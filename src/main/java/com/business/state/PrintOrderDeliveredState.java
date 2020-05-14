package com.business.state;

import com.business.domain.OrderStatus;

import lombok.Getter;

public class PrintOrderDeliveredState implements State {
  
  private TransactionProcessor transactionProcessor;

  @Getter
  private final OrderStatus orderStatus = OrderStatus.PRINT_ORDER_DELIVERED;
  
  public PrintOrderDeliveredState(TransactionProcessor transactionProcessor) {
    
    this.transactionProcessor = transactionProcessor;
  }

  @Override
  public void process() {

    // TODO: After set number of days from delivery, delete transactionInfo
    
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
