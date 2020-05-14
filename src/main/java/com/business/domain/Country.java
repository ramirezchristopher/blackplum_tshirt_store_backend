package com.business.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Country {

  private String code;
  private String name;
  private List<State> states;
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class State {
    
    private String code;
    private String name;
  }
}
