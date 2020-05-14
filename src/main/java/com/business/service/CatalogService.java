package com.business.service;

import java.util.List;

import com.business.domain.CatalogItem;
import com.business.domain.CategoryType;

public interface CatalogService {

  List<CatalogItem> getCatalogItemsByCategory(CategoryType category);
  
  List<CatalogItem> initializeCatalog();
}
