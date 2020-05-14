package com.business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.business.domain.SearchTerm;

@Repository
public interface SearchTermRepository extends MongoRepository<SearchTerm, String>  {

}
