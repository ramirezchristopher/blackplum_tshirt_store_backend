package com.business.repository;

import java.util.List;

import com.business.domain.Address;
import com.business.domain.Country;
import com.business.domain.OrderItem;
import com.business.domain.ShippingMethod;
import com.business.domain.TransactionInfo;
import com.business.repository.FulfillmentDaoImpl.ListResponse;
import com.business.repository.FulfillmentDaoImpl.MapResponse;

public interface FulfillmentDao {

  ListResponse<Country> getCountryCodes();
  
  MapResponse getTaxRate(Address address);
  
  ListResponse<ShippingMethod> getShippingRates(Address address, List<OrderItem> items);
  
  MapResponse createPrintOrder(TransactionInfo transactionInfo);
  
  MapResponse getPrintOrder(String transactionId);
}
