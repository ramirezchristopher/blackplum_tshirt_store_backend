package com.business.validation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomBeanValidator {
  
  @Autowired
  private Validator validator;
  
  public <T> List<String> getConstraintViolations(T targetBean) {
    
    Set<ConstraintViolation<T>> constraintViolations = validator.validate(targetBean);
    
    return constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }
}
