package com.business.service;

import com.business.domain.Order;
import com.business.domain.TransactionInfo;

public interface OrderProcessingService {

  TransactionInfo getTransactionInfo(String id);
  
  void updateTransactionState();
  
  TransactionInfo completeOrder(Order order);
}
