package com.business.service;

import java.util.Optional;

import com.braintreegateway.Transaction;
import com.business.domain.PaymentTransactionBuilder;
import com.business.domain.TransactionInfo;

public interface BraintreeService {

  String getClientToken();
  
  Optional<Transaction> getPaymentTransactionStatus(String transactionId);
  
  PaymentTransactionBuilder processPaymentTransaction(TransactionInfo transactionInfo);
}
