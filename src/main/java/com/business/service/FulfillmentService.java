package com.business.service;

import java.util.List;

import com.business.domain.Address;
import com.business.domain.Country;
import com.business.domain.OrderItem;
import com.business.domain.ShippingMethod;
import com.business.domain.TaxRate;

public interface FulfillmentService {

  List<Country.State> getStates();
  
  TaxRate getTaxRate(Address address);
  
  List<ShippingMethod> getShippingMethods(Address address, List<OrderItem> items);
}
