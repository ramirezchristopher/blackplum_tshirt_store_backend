package com.business.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.domain.CatalogItem;
import com.business.domain.CategoryType;
import com.business.repository.CatalogItemDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CatalogServiceImpl implements CatalogService {
  
  @Autowired
  private CatalogItemDao catalogItemDao;
  
  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public List<CatalogItem> getCatalogItemsByCategory(CategoryType categoryType) {
    
    List<CatalogItem> catalogItems = catalogItemDao.getCatalogItems();
    
    return catalogItems.stream()
      .filter(item -> categoryType == CategoryType.ALL || categoryType == item.getCategoryType())
      .collect(Collectors.toList());
  }
  
  @Override
  public List<CatalogItem> initializeCatalog() {
    
    List<CatalogItem> savedItems = new ArrayList<>();
    
    try {
      List<CatalogItem> catalog = objectMapper.readValue(getClass().getResourceAsStream("/catalog.json"), TypeFactory.defaultInstance().constructCollectionType(List.class, CatalogItem.class));
      
      catalogItemDao.deleteAll();
      
      savedItems = catalog.stream()
        .map(catalogItem -> {
          
          return catalogItemDao.save(catalogItem);
        })
        .collect(Collectors.toList());
    }
    catch(IOException e) {
      
      log.error("Exception while reading JSON file.", e);
    }
    
    return savedItems;
  }
}
