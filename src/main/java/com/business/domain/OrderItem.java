package com.business.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderItem {

  @NotBlank(message = "Order item product id is required")
  public String id;
  
  @NotBlank(message = "External variant id is required")
  public String externalVariantId;
  
  private String name;
  
  @NotBlank(message = "Order item size is required")
  private String size;
  
  @NotBlank(message = "Order item color is required")
  private String color;
  
  @Min(value = 1, message = "Order item quantity is required")
  private Integer quantity;
  
  private BigDecimal price;
  
  private String imageUrl;
  private String imageAltDescription;
}
