package com.business.repository;

import java.util.List;

import com.business.domain.TransactionInfo;

public interface TransactionInfoDao {

  List<TransactionInfo> getTransactionInfos();
  
  TransactionInfo getTransactionInfo(String id);
  
  TransactionInfo save(TransactionInfo info);
}
