package com.business.repository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.business.domain.Address;
import com.business.domain.Country;
import com.business.domain.EmailableAddress;
import com.business.domain.OrderItem;
import com.business.domain.ShippingMethod;
import com.business.domain.TransactionInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class FulfillmentDaoImpl implements FulfillmentDao {

  private static final int RETRY_MAX_ATTEMPTS = 3;
  private static final long RETRY_DELAY_MILLISECONDS = 2000;
  private static final String GET_REQUEST_LOGGING_MESSAGE = "REST CALL [GET]: {}";
  private static final String POST_REQUEST_LOGGING_MESSAGE = "REST CALL [POST]: url: {}, data: {}";
  private static final String SUPPORT_EMAIL = "support@blackplumapparel.com";
  private static final String USD_CURRENCY = "USD";
  private static final String PACKING_SLIP_MESSAGE = "Thank you for ordering from Black Plum Apparel.";
  
  @Value("${printful.api-key}")
  private String printfulApiKey;
  
  @Autowired
  @Qualifier("loadBalancedRestTemplate")
  private RestTemplate restTemplate;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Override
  @Cacheable(cacheNames = "COUNTRY_CODES")
  @Retryable(maxAttempts = RETRY_MAX_ATTEMPTS, backoff = @Backoff(delay = RETRY_DELAY_MILLISECONDS), exclude = { HttpClientErrorException.class })
  @HystrixCommand(commandKey = "getCountryCodes", fallbackMethod = "getCountryCodesFallback", threadPoolKey = "fulfillmentThreadPool")
  public ListResponse<Country> getCountryCodes() {

    String authenticationKey = getBase64AuthenticationKey();
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", authenticationKey);

    HttpEntity<String> request = new HttpEntity<>(headers);
    String url = "https://api.printful.com/countries";
    
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

    log.info(GET_REQUEST_LOGGING_MESSAGE, builder.toUriString());

    ParameterizedTypeReference<ListResponse<Country>> typeRef = new ParameterizedTypeReference<ListResponse<Country>>() {};
    ResponseEntity<ListResponse<Country>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, typeRef);

    return response.getBody();
  }
  
  @Override
  @Retryable(maxAttempts = RETRY_MAX_ATTEMPTS, backoff = @Backoff(delay = RETRY_DELAY_MILLISECONDS), exclude = { IllegalArgumentException.class })
  @HystrixCommand(commandKey = "getTaxRates", fallbackMethod = "getTaxRateFallback", threadPoolKey = "fulfillmentThreadPool", ignoreExceptions = { IllegalArgumentException.class })
  public MapResponse getTaxRate(Address address) {

    String authenticationKey = getBase64AuthenticationKey();
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", authenticationKey);
    
    TaxRateRequest body = new TaxRateRequest();
    
    body.getRecipient().setCountryCode(address.getCountry());
    body.getRecipient().setStateCode(address.getState());
    body.getRecipient().setCity(address.getCity());
    body.getRecipient().setZip(address.getZip());
    
    HttpEntity<TaxRateRequest> request = new HttpEntity<>(body, headers);
    String url = "https://api.printful.com/tax/rates";
    
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

    log.info(POST_REQUEST_LOGGING_MESSAGE, builder.toUriString(), getJsonForObject(body));

    ParameterizedTypeReference<MapResponse> typeRef = new ParameterizedTypeReference<MapResponse>() {};
    ResponseEntity<MapResponse> response = null;
    
    try {
      response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, typeRef);
    }
    catch(HttpClientErrorException clientException) {
      
      if(HttpStatus.BAD_REQUEST.equals(clientException.getStatusCode())) {
        
        log.warn("Failed to get tax rate, Address invalid: {}", clientException.getMessage());
        throw new IllegalArgumentException("Address invalid");
      }
    }

    log.info("Got response tax rate of {} for {}", getJsonForObject(response.getBody()), getJsonForObject(body));
    
    return response.getBody();
  }
  
  @Override
  @Retryable(maxAttempts = RETRY_MAX_ATTEMPTS, backoff = @Backoff(delay = RETRY_DELAY_MILLISECONDS), exclude = { HttpClientErrorException.class })
  @HystrixCommand(commandKey = "getShippingRates", fallbackMethod = "getShippingRatesFallback", threadPoolKey = "fulfillmentThreadPool")
  public ListResponse<ShippingMethod> getShippingRates(Address address, List<OrderItem> orderItems) {

    String authenticationKey = getBase64AuthenticationKey();
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", authenticationKey);
    
    ShippingRatesRequest body = new ShippingRatesRequest();
    
    body.getRecipient().setCountryCode(address.getCountry());
    body.getRecipient().setStateCode(address.getState());
    body.getRecipient().setCity(address.getCity());
    body.getRecipient().setZip(address.getZip());
    body.getRecipient().setAddress1(address.getStreet());
    
    List<com.business.repository.FulfillmentDaoImpl.ShippingRatesRequest.Item> items = orderItems.stream()
      .map(orderItem -> {
        
        com.business.repository.FulfillmentDaoImpl.ShippingRatesRequest.Item item = new com.business.repository.FulfillmentDaoImpl.ShippingRatesRequest.Item();
        
        item.setQuantity(orderItem.getQuantity());
        item.setExternalVariantId(orderItem.getExternalVariantId());
        
        return item;
      })
      .collect(Collectors.toList());
    
    body.setItems(items);
    
    HttpEntity<ShippingRatesRequest> request = new HttpEntity<>(body, headers);
    String url = "https://api.printful.com/shipping/rates";
    
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

    log.info(POST_REQUEST_LOGGING_MESSAGE, builder.toUriString(), getJsonForObject(body));

    ParameterizedTypeReference<ListResponse<ShippingMethod>> typeRef = new ParameterizedTypeReference<ListResponse<ShippingMethod>>() {};
    ResponseEntity<ListResponse<ShippingMethod>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, typeRef);

    log.info("Got response shipping rates of {} for {}", getJsonForObject(response.getBody()), getJsonForObject(body));
    
    return response.getBody();
  }
  
  @Override
  @Retryable(maxAttempts = RETRY_MAX_ATTEMPTS, backoff = @Backoff(delay = RETRY_DELAY_MILLISECONDS), exclude = { HttpClientErrorException.class })
  @HystrixCommand(commandKey = "createPrintOrder", fallbackMethod = "createPrintOrderFallback", threadPoolKey = "fulfillmentThreadPool")
  public MapResponse createPrintOrder(TransactionInfo transactionInfo) {

    String authenticationKey = getBase64AuthenticationKey();
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", authenticationKey);

    OrderRequest body = new OrderRequest();
    EmailableAddress shippingAddress = transactionInfo.getOrder().getShippingAddress();
    
    body.setTransactionId(transactionInfo.getId());
    body.setShippingMethod(transactionInfo.getOrder().getShippingMethod());
    
    body.getRecipient().setFullName(String.format("%s %s", shippingAddress.getFirstName(), shippingAddress.getLastName()));
    body.getRecipient().setCountryCode(shippingAddress.getCountry());
    body.getRecipient().setStateCode(shippingAddress.getState());
    body.getRecipient().setCity(shippingAddress.getCity());
    body.getRecipient().setZip(shippingAddress.getZip());
    body.getRecipient().setAddress1(shippingAddress.getStreet());
    body.getRecipient().setEmail(shippingAddress.getEmail());
    
    body.getRetailCosts().setSubtotal(transactionInfo.getTotals().getSubtotal().toString());
    body.getRetailCosts().setShipping(transactionInfo.getTotals().getShipping().toString());
    body.getRetailCosts().setTax(transactionInfo.getTotals().getTax().toString());
    body.getRetailCosts().setTotal(transactionInfo.getTotals().getTotal().toString());
    body.getRetailCosts().setCurrency(USD_CURRENCY);
    
    body.getPackingSlip().setSupportEmail(SUPPORT_EMAIL);
    body.getPackingSlip().setMessage(PACKING_SLIP_MESSAGE);
    
    List<com.business.repository.FulfillmentDaoImpl.OrderRequest.Item> items = transactionInfo.getOrder().getOrderItems().stream()
      .map(orderItem -> {
        
        com.business.repository.FulfillmentDaoImpl.OrderRequest.Item item = new com.business.repository.FulfillmentDaoImpl.OrderRequest.Item();
        
        item.setName(orderItem.getName());
        item.setQuantity(orderItem.getQuantity());
        item.setRetailPrice(orderItem.getPrice().toString());
        item.setExternalVariantId(orderItem.getExternalVariantId());
        
        return item;
      })
      .collect(Collectors.toList());
    
    body.setItems(items);
    
    HttpEntity<OrderRequest> request = new HttpEntity<>(body, headers);
    String url = "https://api.printful.com/orders?confirm=false&update_existing=true";
    
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

    log.info(POST_REQUEST_LOGGING_MESSAGE, builder.toUriString(), getJsonForObject(body));

    ParameterizedTypeReference<MapResponse> typeRef = new ParameterizedTypeReference<MapResponse>() {};
    ResponseEntity<MapResponse> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, typeRef);

    log.info("Got response order of {} for {}", getJsonForObject(response.getBody()), getJsonForObject(body));
    
    return response.getBody();
  }
  
  @Override
  @Retryable(maxAttempts = RETRY_MAX_ATTEMPTS, backoff = @Backoff(delay = RETRY_DELAY_MILLISECONDS), exclude = { HttpClientErrorException.class })
  @HystrixCommand(commandKey = "getPrintOrder", fallbackMethod = "getPrintOrderFallback", threadPoolKey = "fulfillmentThreadPool", ignoreExceptions = { HttpClientErrorException.class })
  public MapResponse getPrintOrder(String transactionId) {

    String authenticationKey = getBase64AuthenticationKey();
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", authenticationKey);

    HttpEntity<String> request = new HttpEntity<>(headers);
    String url = "https://api.printful.com/orders/@" + transactionId;
    
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

    log.info(GET_REQUEST_LOGGING_MESSAGE, builder.toUriString());

    ParameterizedTypeReference<MapResponse> typeRef = new ParameterizedTypeReference<MapResponse>() {};
    ResponseEntity<MapResponse> response = null;
    
    try {
      response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, typeRef);
    }
    catch(HttpClientErrorException clientException) {
      
      if(HttpStatus.NOT_FOUND.equals(clientException.getStatusCode())) {
      
        log.info("Order fulfillment info not found for transaction id {}", transactionId);
      }
      else {
        log.warn("Got exception while getting order fulfillment info for transaction id {}: {}", transactionId, clientException.getMessage());
      }
    }

    return response != null && response.getBody() != null ? response.getBody() : new MapResponse();
  }
  
  private String getJsonForObject(Object object) {
    
    String json = "{}";
    
    try {
      json = objectMapper.writeValueAsString(object);
    }
    catch(JsonProcessingException e) {
      
      log.warn("Got exception while writing object to JSON", e);
    }
    
    return json;
  }
  
  private String getBase64AuthenticationKey() {
    
    return "Basic " + Base64.getEncoder().encodeToString(printfulApiKey.getBytes());
  }
  
  
  /* 
   * HYSTRIX FALLBACKS
   */
  
  public ListResponse<Country> getCountryCodesFallback(Throwable t) throws Throwable {

    log.warn("Got error while retrieving country codes. Will retry... Exception was: {}", !StringUtils.isBlank(t.getMessage()) ? t.getMessage() : t.getClass());
    throw t;
  }
  
  public MapResponse getTaxRateFallback(Address address, Throwable t) throws Throwable {

    log.warn("Got error while retrieving tax rate. Will retry... Exception was: {}", !StringUtils.isBlank(t.getMessage()) ? t.getMessage() : t.getClass());
    throw t;
  }
  
  public ListResponse<ShippingMethod> getShippingRatesFallback(Address address, List<OrderItem> orderItems, Throwable t) throws Throwable {

    log.warn("Got error while retrieving shipping rates. Will retry... Exception was: {}", !StringUtils.isBlank(t.getMessage()) ? t.getMessage() : t.getClass());
    throw t;
  }
  
  public MapResponse createPrintOrderFallback(TransactionInfo transactionInfo, Throwable t) throws Throwable {

    log.warn("Got error while creating order. Will retry... Exception was: {}", !StringUtils.isBlank(t.getMessage()) ? t.getMessage() : t.getClass());
    throw t;
  }
  
  public MapResponse getPrintOrderFallback(String transactionId, Throwable t) throws Throwable {

    log.warn("Got error while getting order. Will retry... Exception was: {}", !StringUtils.isBlank(t.getMessage()) ? t.getMessage() : t.getClass());
    throw t;
  }
  
  
  /* 
   * SPRING RETRY RECOVERY
   */
  
  @Recover
  private ListResponse<Country> recoverFromRetryReturningCountryCodes(Throwable t) {
    
    log.error("All retry attempts have failed to get country codes. Returning an empty response. Exception was: {}", t.getMessage(), t);
    return new ListResponse<>();
  }
  
  @Recover
  private MapResponse recoverFromRetryReturningMap(Throwable t) throws Throwable {
    
    if(t instanceof IllegalArgumentException) {
      
      throw t;
    }
    
    log.error("All retry attempts have failed. Returning an empty response. Exception was: {}", t.getMessage(), t);
    return new MapResponse();
  }
  
  @Recover
  private ListResponse<ShippingMethod> recoverFromRetryReturningShippingMethods(Throwable t) {
    
    log.error("All retry attempts have failed to get shipping methods. Returning an empty response. Exception was: {}", t.getMessage(), t);
    return new ListResponse<>();
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TaxRateRequest {

    private Recipient recipient = new Recipient();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private class Recipient {

      @JsonProperty("country_code")
      private String countryCode;

      @JsonProperty("state_code")
      private String stateCode;

      private String city;
      private String zip;
    }
  }
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ShippingRatesRequest {

    private Recipient recipient = new Recipient();
    private List<Item> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Recipient {

      @JsonProperty("country_code")
      private String countryCode;

      @JsonProperty("state_code")
      private String stateCode;

      private String city;
      private String zip;
      private String address1;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Item {

      @JsonProperty("external_variant_id")
      private String externalVariantId;

      private Integer quantity;
    }
  }
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderRequest {

    @JsonProperty("external_id")
    private String transactionId;
    
    @JsonProperty("shipping")
    private String shippingMethod;
    
    private Recipient recipient = new Recipient();
    private List<Item> items = new ArrayList<>();
    
    @JsonProperty("retail_costs")
    private RetailCosts retailCosts = new RetailCosts();
    
    @JsonProperty("packing_slip")
    private PackingSlip packingSlip = new PackingSlip();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Recipient {

      @JsonProperty("name")
      private String fullName;

      @JsonProperty("state_code")
      private String stateCode;
      
      @JsonProperty("country_code")
      private String countryCode;

      private String city;
      private String zip;
      private String address1;
      private String email;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Item {

      @JsonProperty("external_variant_id")
      private String externalVariantId;

      private String name;
      private Integer quantity;
      
      @JsonProperty("retail_price")
      private String retailPrice;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RetailCosts {
      
      private String subtotal;
      private String shipping;
      private String tax;
      private String total;
      private String currency;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PackingSlip {
      
      @JsonProperty("email")
      private String supportEmail;
      
      private String message;
    }
  }
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ListResponse<T> {
    
    private String code;
    private List<T> result = new ArrayList<>();
  }
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MapResponse {
    
    private String code;
    private Map<String, Object> result = new HashMap<>();
  }
}
