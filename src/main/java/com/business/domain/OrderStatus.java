package com.business.domain;

import lombok.Getter;

public enum OrderStatus {
  
  TRANSACTION_HAS_VALIDATION_ERRORS("problem processing order"), 
  PAYMENT_SUBMITTED_FOR_SETTLEMENT("processing payment"), 
  PAYMENT_SETTLED("payment complete"), 
  PAYMENT_REFUNDED("payment refunded"), 
  PAYMENT_VOIDED("payment voided"), 
  PAYMENT_FAILED("payment failed"), 
  PAYMENT_AUTHORIZATION_EXPIRED("payment auth expired"), 
  PAYMENT_PROCESSOR_DECLINED("payment declined"), 
  PAYMENT_GATEWAY_REJECTED("payment rejected"), 
  PAYMENT_SETTLEMENT_DECLINED("payment declined"), 
  PRINT_ORDER_CREATED("preparing order"), 
  PRINT_ORDER_FAILED("print order failed"), 
  PRINT_ORDER_CANCELED("print order canceled"), 
  PRINT_ORDER_ON_HOLD("print order on hold"), 
  PRINT_ORDER_FULFILLED("order shipped"), 
  PRINT_ORDER_DELIVERED("order delivered");
  
  @Getter
  private final String simpleDescription;
  
  private OrderStatus(String simpleDescription) {
    
    this.simpleDescription = simpleDescription;
  }
}
