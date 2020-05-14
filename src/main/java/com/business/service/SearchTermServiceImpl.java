package com.business.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.business.domain.SearchTerm;
import com.business.mail.MailSender;
import com.business.repository.SearchTermDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchTermServiceImpl implements SearchTermService {
  
  private static final String DAILY_CRON = "0 0 0 * * *";

  @Autowired
  private SearchTermDao searchTermDao;
  
  @Autowired
  private MailSender mailSender;

  @Override
  public void saveSearchedTerms(Map<String, String> searchData) {
    
    log.info("Storing search terms");
    
    String termsAsCommaSeparatedString = searchData.get("terms");
    
    if(!StringUtils.isBlank(termsAsCommaSeparatedString)) {
      
      List<String> terms = Arrays.asList(termsAsCommaSeparatedString.split("\\s*,\\s*"));
      Map<String, SearchTerm> storedSearchTerms = searchTermDao.getSearchTerms().stream().distinct().collect(Collectors.toMap(SearchTerm::getTerm, Function.identity()));
      
      List<SearchTerm> updatedSearchTerms = terms.stream()
        .map(term -> term.toLowerCase())
        .distinct()
        .map(term -> {
          
          LocalDate searchDate = LocalDate.now();
          SearchTerm updatedSearchTerm = new SearchTerm(term, 1, searchDate);
          SearchTerm storedSearchTerm = storedSearchTerms.get(term);
          
          if(storedSearchTerm != null) {
            
            storedSearchTerm.setCount(storedSearchTerm.getCount() + 1);
            storedSearchTerm.setSearchDate(searchDate);
            
            updatedSearchTerm = storedSearchTerm;
          }
          
          return updatedSearchTerm;
        })
        .collect(Collectors.toList());
    
      searchTermDao.saveAll(updatedSearchTerms);
    }
  }
  
  @Scheduled(cron = DAILY_CRON)
  public void sendSearchedTermsEmail() {
    
    List<SearchTerm> searchTerms = searchTermDao.getSearchTerms();
    
    try {
      if(searchTerms != null && !searchTerms.isEmpty()) {
      
        mailSender.sendSearchedTermsEmail(searchTerms);
        
        // cleanup old search terms
        searchTermDao.deleteAll();
      }
    }
    catch(Exception ex) {
      
      log.error("Error while sending search terms email", ex);
    }
  }
}
