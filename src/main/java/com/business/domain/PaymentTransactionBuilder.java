package com.business.domain;

import java.time.LocalDateTime;

import com.braintreegateway.Transaction.Status;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class PaymentTransactionBuilder {
  
  public String paymentTransactionId;
  public Status paymentTransactionStatus;
  public String paymentTransactionErrorMessage;
  public LocalDateTime paymentTransactionStatusDate;
  
  public PaymentTransactionBuilder paymentTransactionId(String value) {
    
    this.paymentTransactionId = value;
    return this;
  }
  
  public PaymentTransactionBuilder paymentTransactionStatus(Status value) {
    
    this.paymentTransactionStatus = value;
    return this;
  }
  
  public PaymentTransactionBuilder paymentTransactionStatusDate(LocalDateTime value) {
    
    this.paymentTransactionStatusDate = value;
    return this;
  }
  
  public PaymentTransactionBuilder paymentTransactionErrorMessage(String value) {
    
    this.paymentTransactionErrorMessage = value;
    return this;
  }
  
  public PaymentTransaction build() {
    
    return new PaymentTransaction(
        this.paymentTransactionId, 
        this.paymentTransactionStatus, 
        this.paymentTransactionErrorMessage, 
        this.paymentTransactionStatusDate);
  }

}
