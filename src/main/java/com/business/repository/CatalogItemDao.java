package com.business.repository;

import java.util.List;

import com.business.domain.CatalogItem;

public interface CatalogItemDao {

  List<CatalogItem> getCatalogItems();
  
  CatalogItem getCatalogItem(String id);
  
  CatalogItem save(CatalogItem item);
  
  void deleteAll();
}
