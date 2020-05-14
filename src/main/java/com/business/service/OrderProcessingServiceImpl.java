package com.business.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.business.domain.Address;
import com.business.domain.CatalogItem;
import com.business.domain.Order;
import com.business.domain.OrderStatus;
import com.business.domain.ShippingMethod;
import com.business.domain.TaxRate;
import com.business.domain.TransactionInfo;
import com.business.domain.TransactionTotals;
import com.business.mail.MailSender;
import com.business.repository.CatalogItemDao;
import com.business.repository.FulfillmentDao;
import com.business.repository.TransactionInfoDao;
import com.business.state.TransactionProcessor;
import com.business.validation.CustomBeanValidator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderProcessingServiceImpl implements OrderProcessingService {
  
  private static final String SIX_HOUR_CRON = "0 0 0/6 * * *";
  
  @Autowired
  private BraintreeService braintreeService;
  
  @Autowired
  private FulfillmentService fulfillmentService;
  
  @Autowired
  private FulfillmentDao fulfillmentDao;
  
  @Autowired
  private CatalogItemDao catalogItemDao;
  
  @Autowired
  private TransactionInfoDao transactionInfoDao;
  
  @Autowired
  private CustomBeanValidator beanValidator;
  
  @Autowired
  private MailSender mailSender;
  
  private List<Status> PAYMENT_SUCCESS_STATUSES = Arrays.asList(
      Transaction.Status.AUTHORIZING, 
      Transaction.Status.AUTHORIZED, 
      Transaction.Status.SUBMITTED_FOR_SETTLEMENT, 
      Transaction.Status.SETTLEMENT_PENDING, 
      Transaction.Status.SETTLING, 
      Transaction.Status.SETTLED, 
      Transaction.Status.SETTLEMENT_CONFIRMED
  );
  
  @Override
  public TransactionInfo getTransactionInfo(String id) {
    
    TransactionInfo transactionInfo = new TransactionInfo();
    
    if(!StringUtils.isBlank(id)) {
    
      transactionInfo = transactionInfoDao.getTransactionInfo(StringUtils.trim(id));
      
      if(transactionInfo != null && transactionInfo.getOrderStatus() != null) {
      
        transactionInfo.setOrderStatusSimpleDescription(transactionInfo.getOrderStatus().getSimpleDescription());
      }
    }
    
    return transactionInfo;
  }
  
  @Scheduled(cron = SIX_HOUR_CRON)
  public void updateTransactionState() {
    
    log.info("Start processing transactions");
    
    List<TransactionInfo> transactionsInfoList = transactionInfoDao.getTransactionInfos();
    
    transactionsInfoList.stream()
      .forEach(transactionInfo -> {
        
        TransactionProcessor transactionProcessor = new TransactionProcessor(transactionInfo, braintreeService, fulfillmentDao, transactionInfoDao, mailSender);
        transactionProcessor.process();
      });
    
    log.info("Finished processing transactions");
  }
  
  @Override
  public TransactionInfo completeOrder(Order order) {
    
    TransactionInfo transactionInfo = new TransactionInfo();
    List<String> errors = beanValidator.getConstraintViolations(order);
    
    transactionInfo.setOrder(order);
    transactionInfo.setOrderDate(LocalDateTime.now());
    
    if(!errors.isEmpty()) {
    
      log.warn("Order Errors: " + errors);
      
      transactionInfo.setValidationErrors(errors);
      transactionInfo.setOrderStatus(OrderStatus.TRANSACTION_HAS_VALIDATION_ERRORS);
      transactionInfo.setOrderStatusDate(LocalDateTime.now());
      
      return transactionInfo;
    }
    
    transactionInfo.setTotals(getTransactionTotals(order));
    transactionInfo.setPaymentTransaction(braintreeService.processPaymentTransaction(transactionInfo).build());
    transactionInfo.setOrderStatus(OrderStatus.PAYMENT_SUBMITTED_FOR_SETTLEMENT);
    transactionInfo.setOrderStatusDate(LocalDateTime.now());
    
    TransactionInfo savedTransactionInfo = transactionInfoDao.save(transactionInfo);
    
    try {
      mailSender.sendPurchaseConfirmationEmail(savedTransactionInfo);
    }
    catch(Exception e) {
      
      log.error("Exception while sending confirmation e-mail", e);
    }
    
    return savedTransactionInfo;
  }
  
  private TransactionTotals getTransactionTotals(Order order) {
    
    TransactionTotals transactionTotals = new TransactionTotals();
    
    BigDecimal subtotal = calcSubTotal(order);
    BigDecimal shipping = calcShippingRate(order);
    BigDecimal tax = calcTax(order.getShippingAddress(), subtotal, shipping);
    BigDecimal total = subtotal.add(shipping).add(tax);
    
    transactionTotals.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
    transactionTotals.setShipping(shipping.setScale(2, RoundingMode.HALF_UP));
    transactionTotals.setTax(tax.setScale(2, RoundingMode.HALF_UP));
    transactionTotals.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    
    log.info("Calculated Transaction Totals: {}", transactionTotals);
    
    return transactionTotals;
  }
  
  private BigDecimal calcSubTotal(Order order) {

    return order.getOrderItems().stream()
      .map(orderItem -> {

        CatalogItem catalogItem = catalogItemDao.getCatalogItem(orderItem.getId());

        return catalogItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
      })
      .reduce(BigDecimal.ZERO, (acc, curr) -> acc.add(curr));
  }
  
  private BigDecimal calcShippingRate(Order order) {
    
    ShippingMethod shippingMethod = fulfillmentService.getShippingMethods(order.getShippingAddress(), order.getOrderItems()).stream()
      .filter(method -> method.getId().equalsIgnoreCase(order.getShippingMethod()))
      .findFirst()
      .orElse(null);
    
    return shippingMethod != null ? shippingMethod.getRate() : BigDecimal.ZERO;
  }
  
  private BigDecimal calcTax(Address address, BigDecimal subtotal, BigDecimal shippingRate) {
    
    BigDecimal tax = BigDecimal.ZERO;
    TaxRate taxRate = fulfillmentService.getTaxRate(address);
    
    if(taxRate != null && taxRate.getRequired()) {
      
      if(taxRate.getShippingTaxable()) {
        
        tax = taxRate.getRate().multiply(subtotal.add(shippingRate));
      }
      else {
        tax = taxRate.getRate().multiply(subtotal);
      }
    }
    
    return tax;
  }

}

