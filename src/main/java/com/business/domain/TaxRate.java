package com.business.domain;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaxRate {

  private Boolean required = Boolean.FALSE;
  private BigDecimal rate = BigDecimal.ZERO;
  private Boolean shippingTaxable = Boolean.FALSE;
  private List<String> validationErrors;
  
}
