package com.business.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.business.domain.SearchTerm;
import com.mongodb.client.MongoClient;

@Repository
public class SearchTermDaoImpl implements SearchTermDao {

  @Autowired
  private SearchTermRepository searchTermRepository;
  
  @Autowired
  private MongoClient mongoClient;
  
  @Value("${spring.data.mongodb.database:catalog}")
  private String databaseName;
  
  @Override
  public List<SearchTerm> getSearchTerms() {
    
    return searchTermRepository.findAll();
  }
  
  @Override
  public SearchTerm getSearchTerm(String id) {
    
    return searchTermRepository.findById(id).get();
  }
  
  @Override
  public SearchTerm save(SearchTerm searchTerm) {
    
    return searchTermRepository.save(searchTerm);
  }
  
  @Override
  public List<SearchTerm> saveAll(List<SearchTerm> searchTerms) {
    
    searchTerms.stream().forEach(searchTerm -> {
      
      if(StringUtils.isBlank(searchTerm.getId())) {
        
        save(searchTerm);
      }
      else {
        MongoOperations mongoOps = new MongoTemplate(new SimpleMongoClientDbFactory(mongoClient, databaseName));
        Update update = new Update();
        
        update.set("count", searchTerm.getCount());
        update.set("searchDate", searchTerm.getSearchDate());
        
        mongoOps.updateFirst(query(where("term").is(searchTerm.getTerm())), update, SearchTerm.class);
      }
    });
    
    return searchTerms;
  }
  
  @Override
  public void deleteAll() {
    
    searchTermRepository.deleteAll();
  }
}
