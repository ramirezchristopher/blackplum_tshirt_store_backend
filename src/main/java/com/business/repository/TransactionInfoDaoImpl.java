package com.business.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.business.domain.TransactionInfo;

@Repository
public class TransactionInfoDaoImpl implements TransactionInfoDao {

  @Autowired
  TransactionInfoRepository transactionInfoRepository;
  
  @Override
  public List<TransactionInfo> getTransactionInfos() {
    
    return transactionInfoRepository.findAll();
  }
  
  @Override
  public TransactionInfo getTransactionInfo(String id) {
    
    return transactionInfoRepository.findById(id).orElseGet(() -> new TransactionInfo());
  }
  
  @Override
  public TransactionInfo save(TransactionInfo info) {
    
    return transactionInfoRepository.save(info);
  }
}
