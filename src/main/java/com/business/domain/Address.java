package com.business.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
public class Address {

  @NotBlank(message = "First Name is required")
  private String firstName;
  
  @NotBlank(message = "Last Name is required")
  private String lastName;
  
  @NotBlank(message = "Street is required")
  private String street;
  
  @NotBlank(message = "City is required")
  private String city;
  
  @Size(min = 2, max = 2, message = "State is required")
  private String state;
  
  @Pattern(regexp = "[\\d]{5}", message = "Zip Code is required")
  private String zip;
  
  @Size(min = 2, max = 2, message = "Country is required")
  private String country;
  
}
