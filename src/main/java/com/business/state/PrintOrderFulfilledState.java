package com.business.state;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.business.domain.OrderFulfillmentStatus;
import com.business.domain.OrderStatus;

import lombok.Getter;

public class PrintOrderFulfilledState implements State {
  
  private TransactionProcessor transactionProcessor;
  
  @Getter
  private final OrderStatus orderStatus = OrderStatus.PRINT_ORDER_FULFILLED;

  public PrintOrderFulfilledState(TransactionProcessor transactionProcessor) {
    
    this.transactionProcessor = transactionProcessor;
  }

  @Override
  public void process() {

    Map<String, Object> printOrder = transactionProcessor.getPrintOrder(transactionProcessor.getTransactionInfo());
        
    //if(printOrder != null && OrderFulfillmentStatus.DELIVERED.equals(OrderFulfillmentStatus.findByDescription((String) printOrder.get("status")))) {
      
    //}
    
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
