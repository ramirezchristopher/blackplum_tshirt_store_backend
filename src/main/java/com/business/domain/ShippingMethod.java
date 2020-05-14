package com.business.domain;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShippingMethod {

  private String id;
  private String name;
  private BigDecimal rate;
  private String currency;
}
