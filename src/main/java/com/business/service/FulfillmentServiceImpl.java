package com.business.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.domain.Address;
import com.business.domain.Country;
import com.business.domain.OrderItem;
import com.business.domain.ShippingMethod;
import com.business.domain.TaxRate;
import com.business.repository.FulfillmentDao;
import com.business.repository.FulfillmentDaoImpl.ListResponse;
import com.business.validation.CustomBeanValidator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FulfillmentServiceImpl implements FulfillmentService {

  private static final String UNITED_STATES_COUNTRY_CODE = "US";
  private static final String ARMED_FORCES = "Armed Forces";
  private static final BigDecimal HEADQUARTERS_TAX_RATE = new BigDecimal(0.0975);
  private static final String TENNESSEE = "TN";

  @Autowired
  private FulfillmentDao fulfillmentDao;

  @Autowired
  private CustomBeanValidator beanValidator;

  @Override
  public List<Country.State> getStates() {

    ListResponse<Country> countryCodes = fulfillmentDao.getCountryCodes();

    List<Country.State> states = countryCodes.getResult()
        .stream()
        .filter(country -> UNITED_STATES_COUNTRY_CODE.equals(country.getCode()))
        .map(Country::getStates)
        .flatMap(List::stream)
        .filter(state -> !StringUtils.containsIgnoreCase(state.getName(), ARMED_FORCES))
        .sorted(Comparator.comparing(Country.State::getName))
        .collect(Collectors.toList());

    return states;
  }

  @Override
  public TaxRate getTaxRate(Address address) {

    TaxRate taxRate = new TaxRate();
    List<String> validationErrors = beanValidator.getConstraintViolations(address);

    if(!validationErrors.isEmpty()) {

      log.warn("Address Errors: " + validationErrors);
      
      taxRate.setValidationErrors(validationErrors);
      
      return taxRate;
    }

    if(TENNESSEE.equalsIgnoreCase(address.getState())) {

      log.info("Requested tax rate for {}. Returning {}", address.getState(), HEADQUARTERS_TAX_RATE.doubleValue());

      taxRate.setRate(HEADQUARTERS_TAX_RATE);
      taxRate.setRequired(Boolean.TRUE);
      taxRate.setShippingTaxable(Boolean.TRUE);
    }
    else {
      try {
        Map<String, Object> taxRateResult = fulfillmentDao.getTaxRate(address).getResult();

        if(taxRateResult.get("rate") != null && ((Boolean) taxRateResult.get("required"))) {
  
          taxRate.setRate(new BigDecimal((Double) taxRateResult.get("rate")));
          taxRate.setRequired((Boolean) taxRateResult.get("required"));
          taxRate.setShippingTaxable((Boolean) taxRateResult.get("shipping_taxable"));
        }
      }
      catch(IllegalArgumentException ex) {
        
        validationErrors = new ArrayList<>();
        validationErrors.add("Address invalid");
        
        taxRate.setValidationErrors(validationErrors);
      }
    }

    return taxRate;
  }

  @Override
  public List<ShippingMethod> getShippingMethods(Address address, List<OrderItem> items) {

    List<String> errors = beanValidator.getConstraintViolations(address);

    if(!errors.isEmpty()) {

      log.warn("Address Errors: " + errors);

      return null;
    }

    ListResponse<ShippingMethod> shippingRatesResponse = fulfillmentDao.getShippingRates(address, items);

    return shippingRatesResponse.getResult();
  }

}
