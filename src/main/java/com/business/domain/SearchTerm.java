package com.business.domain;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class SearchTerm {

  @Id
  public String id;
  
  private String term;
  private int count;
  private LocalDate searchDate;
  
  public SearchTerm(String term, int count, LocalDate searchDate) {
    
    this.term = term;
    this.count = count;
    this.searchDate = searchDate;
  }
  
  @Override
  public int hashCode() {
    
    return new HashCodeBuilder().append(this.getTerm()).toHashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    
    if(!(obj instanceof SearchTerm)) {
      
      return false;
    }
    
    SearchTerm that = (SearchTerm) obj;
    
    return new EqualsBuilder().append(this.getTerm(), that.getTerm()).isEquals();
  }
}
