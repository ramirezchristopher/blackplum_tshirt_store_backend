package com.business.state;

import com.business.domain.OrderStatus;

import lombok.Getter;

public class PaymentSettledState implements State {
  
  private TransactionProcessor transactionProcessor;
  
  @Getter
  private final OrderStatus orderStatus = OrderStatus.PAYMENT_SETTLED;

  public PaymentSettledState(TransactionProcessor transactionProcessor) {
    
    this.transactionProcessor = transactionProcessor;
  }

  @Override
  public void process() {
    
    transactionProcessor.setState(transactionProcessor.getPrintOrderCreatedState());
    transactionProcessor.process();
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
