package com.business.state;

import java.time.LocalDateTime;
import java.util.Map;

import com.business.domain.OrderStatus;
import com.business.domain.TransactionInfo;
import com.business.mail.MailSender;
import com.business.repository.FulfillmentDao;
import com.business.repository.TransactionInfoDao;
import com.business.service.BraintreeService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@NoArgsConstructor
public class TransactionProcessor {

  private BraintreeService braintreeService;
  private FulfillmentDao fulfillmentDao;
  private TransactionInfoDao transactionInfoDao;
  private MailSender mailSender;
  
  private State transactionHasValidationErrorsState;
  private State paymentSubmittedForSettlementState;
  private State paymentSettledState;
  private State paymentRefundedState;
  private State paymentVoidedState;
  private State paymentFailedState;
  private State paymentAuthorizationExpiredState;
  private State paymentProcessorDeclinedState;
  private State paymentGatewayRejectedState;
  private State paymentSettlementDeclinedState;
  private State printOrderCreatedState;
  private State printOrderFulfilledState;
  private State printOrderFailedState;
  private State printOrderCanceledState;
  private State printOrderOnHoldState;
  private State printOrderDeliveredState;
  
  private TransactionInfo transactionInfo;
  private State state;
  
  public TransactionProcessor(TransactionInfo transactionInfo, BraintreeService braintreeService, FulfillmentDao fulfillmentDao, TransactionInfoDao transactionInfoDao, MailSender mailSender) {
    
    this.braintreeService = braintreeService;
    this.fulfillmentDao = fulfillmentDao;
    this.transactionInfoDao = transactionInfoDao;
    this.mailSender = mailSender;
    
    this.transactionHasValidationErrorsState = new TransactionHasValidationErrorsState(this);
    this.paymentSubmittedForSettlementState = new PaymentSubmittedForSettlementState(this);
    this.paymentSettledState = new PaymentSettledState(this);
    this.paymentRefundedState = new PaymentRefundedState(this);
    this.paymentVoidedState = new PaymentVoidedState(this);
    this.paymentFailedState = new PaymentFailedState(this);
    this.paymentAuthorizationExpiredState = new PaymentAuthorizationExpiredState(this);
    this.paymentProcessorDeclinedState = new PaymentProcessorDeclinedState(this);
    this.paymentGatewayRejectedState = new PaymentGatewayRejectedState(this);
    this.paymentSettlementDeclinedState = new PaymentSettlementDeclinedState(this);
    this.printOrderCreatedState = new PrintOrderCreatedState(this);
    this.printOrderFulfilledState = new PrintOrderFulfilledState(this);
    this.printOrderFailedState = new PrintOrderFailedState(this);
    this.printOrderCanceledState = new PrintOrderCanceledState(this);
    this.printOrderOnHoldState = new PrintOrderOnHoldState(this);
    this.printOrderDeliveredState = new PrintOrderDeliveredState(this);
    
    this.transactionInfo = transactionInfo;
    this.state = initializeState();
  }
  
  private State initializeState() {
    
    State state = getPaymentSubmittedForSettlementState();
    OrderStatus orderStatus = getTransactionInfo().getOrderStatus();
    
    if(orderStatus != null) {
      
      switch(orderStatus) {
        
        case TRANSACTION_HAS_VALIDATION_ERRORS:
          state = getTransactionHasValidationErrorsState();
          break;
        
        case PAYMENT_SUBMITTED_FOR_SETTLEMENT:
          state = getPaymentSubmittedForSettlementState();
          break;
          
        case PAYMENT_SETTLED:
          state = getPaymentSettledState();
          break;
          
        case PAYMENT_REFUNDED: 
          state = getPaymentRefundedState();
          break;
          
        case PAYMENT_VOIDED:
          state = getPaymentVoidedState();
          break;
          
        case PAYMENT_FAILED:
          state = getPaymentFailedState();
          break;
          
        case PAYMENT_AUTHORIZATION_EXPIRED:
          state = getPaymentAuthorizationExpiredState();
          break;
          
        case PAYMENT_PROCESSOR_DECLINED:
          state = getPaymentProcessorDeclinedState();
          break;
          
        case PAYMENT_GATEWAY_REJECTED:
          state = getPaymentGatewayRejectedState();
          break;
          
        case PAYMENT_SETTLEMENT_DECLINED:
          state = getPaymentSettlementDeclinedState();
          break;
          
        case PRINT_ORDER_CREATED:
          state = getPrintOrderCreatedState();
          break;
          
        case PRINT_ORDER_FULFILLED: 
          state = getPrintOrderFulfilledState();
          break;
          
        case PRINT_ORDER_FAILED:
          state = getPrintOrderFailedState();
          break;
          
        case PRINT_ORDER_CANCELED:
          state = getPrintOrderCanceledState();
          break;
          
        case PRINT_ORDER_ON_HOLD:
          state = getPrintOrderOnHoldState();
          break;
          
        case PRINT_ORDER_DELIVERED:
          state = getPrintOrderDeliveredState();
          break;
          
        default:
          state = getPaymentSubmittedForSettlementState();
          break;
      }
    }
    
    return state;
  }

  public void setState(State state) {
    
    saveStateChange(state);
    
    this.state = state;
  }
  
  public void process() {
    
    state.process();
  }
  
  public void cancel() {
    
    state.cancel();
  }
  
  public Map<String, Object> getPrintOrder(TransactionInfo transactionInfo) {
    
    return fulfillmentDao.getPrintOrder(transactionInfo.getId()).getResult();
  }
  
  private void saveStateChange(State state) {
    
    getTransactionInfo().setOrderStatus(state.getOrderStatus());
    getTransactionInfo().setOrderStatusDate(LocalDateTime.now());
    
    TransactionInfo savedTransactionInfo = transactionInfoDao.save(getTransactionInfo());
    
    log.info("Transaction {} status updated: {}", savedTransactionInfo.getId(), savedTransactionInfo.getOrderStatus());
  }
  
}
