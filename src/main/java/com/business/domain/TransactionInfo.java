package com.business.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class TransactionInfo {

  @Id
  public String id;
  
  public Order order;
  public TransactionTotals totals;
  public PaymentTransaction paymentTransaction;
  public LocalDateTime orderDate;
  public OrderStatus orderStatus;
  public LocalDateTime orderStatusDate;
  public String orderStatusSimpleDescription;
  public List<String> validationErrors;
}
