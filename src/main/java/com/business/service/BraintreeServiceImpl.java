package com.business.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionLineItem;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.business.domain.Order;
import com.business.domain.PaymentTransactionBuilder;
import com.business.domain.TransactionInfo;
import com.business.domain.TransactionTotals;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BraintreeServiceImpl implements BraintreeService {
  
  @Autowired
  private BraintreeGateway gateway;

  @Override
  public String getClientToken() {

    ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
    String clientToken = gateway.clientToken().generate(clientTokenRequest);

    return clientToken;
  }

  @Override
  public Optional<Transaction> getPaymentTransactionStatus(String transactionId) {
    
    Transaction transaction = null;
    
    try {
      transaction = gateway.transaction().find(transactionId);
    }
    catch(NotFoundException ex) {
     
      log.warn("Transaction {} was not found in Payment Gateway.", transactionId);
    }
    
    return Optional.ofNullable(transaction);
  }
  
  @Override
  public PaymentTransactionBuilder processPaymentTransaction(TransactionInfo transactionInfo) {

    log.info("Beginning payment processing");
    
    PaymentTransactionBuilder resultBuilder = new PaymentTransactionBuilder()
        .paymentTransactionStatusDate(LocalDateTime.now());
    
    TransactionRequest request = new TransactionRequest()
        .shippingAmount(transactionInfo.getTotals().getShipping())
        .taxAmount(transactionInfo.getTotals().getTax())
        .amount(transactionInfo.getTotals().getTotal())
        .paymentMethodNonce(transactionInfo.getOrder().getPaymentMethod().getPaymentMethodNonce())
        .customer()
          .firstName(transactionInfo.getOrder().getShippingAddress().getFirstName())
          .lastName(transactionInfo.getOrder().getShippingAddress().getLastName())
          .email(transactionInfo.getOrder().getShippingAddress().getEmail())
          .done()
        .billingAddress()
          .firstName(transactionInfo.getOrder().getBillingAddress().getFirstName())
          .lastName(transactionInfo.getOrder().getBillingAddress().getLastName())
          .streetAddress(transactionInfo.getOrder().getBillingAddress().getStreet())
          .locality(transactionInfo.getOrder().getBillingAddress().getCity())
          .region(transactionInfo.getOrder().getBillingAddress().getState())
          .postalCode(transactionInfo.getOrder().getBillingAddress().getZip())
          .countryCodeAlpha2(transactionInfo.getOrder().getBillingAddress().getCountry())
          .done()
        .shippingAddress()
          .firstName(transactionInfo.getOrder().getShippingAddress().getFirstName())
          .lastName(transactionInfo.getOrder().getShippingAddress().getLastName())
          .streetAddress(transactionInfo.getOrder().getShippingAddress().getStreet())
          .locality(transactionInfo.getOrder().getShippingAddress().getCity())
          .region(transactionInfo.getOrder().getShippingAddress().getState())
          .postalCode(transactionInfo.getOrder().getShippingAddress().getZip())
          .countryCodeAlpha2(transactionInfo.getOrder().getShippingAddress().getCountry())
          .done()
        .options()
          .submitForSettlement(true)
          .done();
    
    transactionInfo.getOrder().getOrderItems().stream()
      .forEach(orderItem -> {
        
        request.lineItem()
          .kind(TransactionLineItem.Kind.DEBIT)
          .name(orderItem.getName())
          .description(orderItem.getSize() + " / " + orderItem.getColor())
          .quantity(new BigDecimal(orderItem.getQuantity()))
          .unitAmount(orderItem.getPrice())
          .totalAmount(new BigDecimal(orderItem.getQuantity()).multiply(orderItem.getPrice()))
          .done();
      });

    Result<Transaction> result = gateway.transaction().sale(request);
    
    if(result.isSuccess()) {
      Transaction transaction = result.getTarget();
      
      log.info("Payment Transaction {} was successful. Status: {}", transaction.getId(), transaction.getStatus());
      
      resultBuilder
        .paymentTransactionId(transaction.getId())
        .paymentTransactionStatus(transaction.getStatus());

    }
    else if(result.getTransaction() != null) {
      Transaction transaction = result.getTransaction();
      
      log.info("Payment Transaction {} was not successful. Status: {}", transaction.getId(), transaction.getStatus());
      
      resultBuilder
        .paymentTransactionId(transaction.getId())
        .paymentTransactionStatus(transaction.getStatus());
    }
    else {
      String errorString = result.getErrors().getAllDeepValidationErrors().stream()
          .map(error -> {
            
            return "Error [" + error.getCode() + ": " + error.getMessage() + "]";
          })
          .collect(Collectors.joining("; "));
      
      log.info("Payment Transaction has errors: {}", errorString);
      
      resultBuilder.paymentTransactionErrorMessage(errorString);
    }
    
    log.info("Completed payment processing");
    
    return resultBuilder;
  }
  
}

