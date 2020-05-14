package com.business.domain;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

public enum OrderFulfillmentStatus {

  DRAFT("draft"),
  FAILED("failed"),
  PENDING("pending"),
  CANCELED("canceled"), 
  ON_HOLD("onhold"), 
  IN_PROCESS("inprocess"), 
  PARTIAL("partial"), 
  FULFILLED("fulfilled");
  
  @Getter
  private final String description;
  
  private OrderFulfillmentStatus(String description) {
    
    this.description = description;
  }
  
  public static OrderFulfillmentStatus findByDescription(String description) {
    
    if(StringUtils.isBlank(description)) {
      
      return null;
    }
    
    return Arrays.asList(OrderFulfillmentStatus.values()).stream()
      .filter(status -> status.getDescription().equals(description))
      .findFirst()
      .orElse(null);
  }
}
