package com.business.domain;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInteraction {
  
  private UserInteractionType type;
  private String user;
  private Map<String, String> data;
}
