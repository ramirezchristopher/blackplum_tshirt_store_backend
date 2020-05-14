package com.business.domain;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TransactionTotals {
  
  public BigDecimal subtotal;
  public BigDecimal shipping;
  public BigDecimal tax;
  public BigDecimal total;
}
