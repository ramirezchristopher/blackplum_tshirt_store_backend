package com.business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.business.domain.CatalogItem;

@Repository
public interface CatalogItemRepository extends MongoRepository<CatalogItem, String> {

}
