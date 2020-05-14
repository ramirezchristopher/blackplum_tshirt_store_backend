package com.business.domain;

import java.time.LocalDateTime;

import com.braintreegateway.Transaction.Status;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentTransaction {
 
  private String paymentTransactionId;
  private Status paymentTransactionStatus;
  private String paymentTransactionErrorMessage;
  private LocalDateTime paymentTransactionStatusDate;
}
