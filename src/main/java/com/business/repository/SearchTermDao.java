package com.business.repository;

import java.util.List;

import com.business.domain.SearchTerm;

public interface SearchTermDao {

  List<SearchTerm> getSearchTerms();
  
  SearchTerm getSearchTerm(String id);
  
  SearchTerm save(SearchTerm searchTerm);
  
  List<SearchTerm> saveAll(List<SearchTerm> searchTerms);
  
  void deleteAll();
}
