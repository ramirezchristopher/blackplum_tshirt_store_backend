package com.business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.business.domain.TransactionInfo;

@Repository
public interface TransactionInfoRepository extends MongoRepository<TransactionInfo, String> {

}
