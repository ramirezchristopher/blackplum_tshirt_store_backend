package com.business.domain;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class Order {

  @NotEmpty(message = "At least 1 item must be included in the order")
  @Valid
  private List<OrderItem> orderItems;
  
  @NotNull(message = "Payment Method is required")
  @Valid
  private PaymentMethod paymentMethod;
  
  @NotNull(message = "Shipping Address is required")
  @Valid
  private EmailableAddress shippingAddress;
  
  @NotBlank(message = "Shipping Method is required")
  private String shippingMethod;
  
  private String shippingMethodDescription;
  
  // This value is recalculated at order processing time
  private BigDecimal shippingRate;
  
  // This value is recalculated at order processing time
  private TaxRate taxRate;
  
  @NotNull(message = "Shipping Address is required")
  @Valid
  private Address billingAddress;
}
