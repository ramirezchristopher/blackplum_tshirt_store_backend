package com.business.state;

import com.business.domain.OrderStatus;

import lombok.Getter;

public class PaymentRefundedState implements State {
  
  private TransactionProcessor transactionProcessor;
  
  @Getter
  private final OrderStatus orderStatus = OrderStatus.PAYMENT_REFUNDED;

  public PaymentRefundedState(TransactionProcessor transactionProcessor) {
    
    this.transactionProcessor = transactionProcessor;
  }

  @Override
  public void process() {

    // TODO Auto-generated method stub
    
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
