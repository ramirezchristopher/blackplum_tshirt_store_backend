package com.business.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.business.domain.CatalogItem;

@Repository
public class CatalogItemDaoImpl implements CatalogItemDao {

  @Autowired
  CatalogItemRepository catalogItemRepository;
  
  @Override
  public List<CatalogItem> getCatalogItems() {
    
    return catalogItemRepository.findAll();
  }
  
  @Override
  public CatalogItem getCatalogItem(String id) {
    
    return catalogItemRepository.findById(id).get();
  }
  
  @Override
  public CatalogItem save(CatalogItem item) {
    
    return catalogItemRepository.save(item);
  }
  
  @Override
  public void deleteAll() {
    
    catalogItemRepository.deleteAll();
  }
}
