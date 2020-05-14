package com.business.state;

import java.util.Optional;

import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.Transaction.Type;
import com.business.domain.OrderStatus;

import lombok.Getter;

public class PaymentSubmittedForSettlementState implements State {
  
  private TransactionProcessor transactionProcessor;
  
  @Getter
  private final OrderStatus orderStatus = OrderStatus.PAYMENT_SUBMITTED_FOR_SETTLEMENT;
  
  public PaymentSubmittedForSettlementState(TransactionProcessor transactionProcessor) {
    
    this.transactionProcessor = transactionProcessor;
  }

  @Override
  public void process() {
    
    Optional<Transaction> paymentTransactionOptional = transactionProcessor.getBraintreeService().getPaymentTransactionStatus(transactionProcessor.getTransactionInfo().getPaymentTransaction().getPaymentTransactionId());

    if(paymentTransactionOptional.isPresent()) {
      
      Transaction paymentTransaction = paymentTransactionOptional.orElse(null);
      Status paymentTransactionStatus = paymentTransaction.getStatus();
      Type paymentTransactionType = paymentTransaction.getType();
      
      if(Type.CREDIT.equals(paymentTransactionType)) {
        
        transactionProcessor.setState(transactionProcessor.getPaymentRefundedState());
        transactionProcessor.process();
      }
      else {
        if(Transaction.Status.SETTLED.equals(paymentTransactionStatus) || Transaction.Status.SETTLEMENT_CONFIRMED.equals(paymentTransactionStatus)) {
          
          transactionProcessor.setState(transactionProcessor.getPaymentSettledState());
          transactionProcessor.process();
        }
        else if(Transaction.Status.VOIDED.equals(paymentTransactionStatus)) {
          
          transactionProcessor.setState(transactionProcessor.getPaymentVoidedState());
        }
        else if(Transaction.Status.FAILED.equals(paymentTransactionStatus)) {
          
          transactionProcessor.setState(transactionProcessor.getPaymentFailedState());
        }
        else if(Transaction.Status.AUTHORIZATION_EXPIRED.equals(paymentTransactionStatus)) {
          
          transactionProcessor.setState(transactionProcessor.getPaymentAuthorizationExpiredState());
        }
        else if(Transaction.Status.PROCESSOR_DECLINED.equals(paymentTransactionStatus)) {
          
          transactionProcessor.setState(transactionProcessor.getPaymentProcessorDeclinedState());
        }
        else if(Transaction.Status.GATEWAY_REJECTED.equals(paymentTransactionStatus)) {
          
          transactionProcessor.setState(transactionProcessor.getPaymentGatewayRejectedState());
        }
        else if(Transaction.Status.SETTLEMENT_DECLINED.equals(paymentTransactionStatus)) {
          
          transactionProcessor.setState(transactionProcessor.getPaymentSettlementDeclinedState());
        }
      }
    }
  }

  @Override
  public void cancel() {

    // TODO Auto-generated method stub
    
  }
  
  @Override
  public String toString() {
    
    return orderStatus.name();
  }

}
