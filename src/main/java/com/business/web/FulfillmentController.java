package com.business.web;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.business.domain.Address;
import com.business.domain.Country;
import com.business.domain.OrderItem;
import com.business.domain.ShippingMethod;
import com.business.domain.TaxRate;
import com.business.service.FulfillmentService;
import com.business.web.advice.WebControllerErrorAdvice.ErrorInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/fulfillment")
@Api(value = "Fulfillment", description = "Operations for fulfilling orders")
@Slf4j
public class FulfillmentController {
  
  @Autowired
  private FulfillmentService fulfillmentService;
	
  @RequestMapping(value = "states", method = RequestMethod.GET)
  @ApiOperation(value = "Get list of states", response = Country.State.class, responseContainer = "List")
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<List<Country.State>> getStates() throws InterruptedException, ExecutionException {

    DeferredResult<List<Country.State>> deferredResult = new DeferredResult<>(5000L);

    CompletableFuture.supplyAsync(() -> {
    	
      try {
        return fulfillmentService.getStates();
      }
      catch(Exception e) {
    	  log.error("Exception occurred", e);
        throw new CompletionException(e);
      }
    })
    .whenCompleteAsync((result, throwable) -> {
    	
      if(throwable != null) {
        deferredResult.setErrorResult(throwable.getCause());
      }
      else {
        deferredResult.setResult(result);
      }
    });

    return deferredResult;
  }
  
  @RequestMapping(value = "taxrate", method = RequestMethod.POST)
  @ApiOperation(value = "Get tax rate for address", response = TaxRate.class)
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<TaxRate> getTaxRate(@RequestBody Address address) throws InterruptedException, ExecutionException {

    DeferredResult<TaxRate> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(() -> {
      
      try {
        return fulfillmentService.getTaxRate(address);
      }
      catch(Exception e) {
        
        throw new CompletionException(e);
      }
    })
    .whenCompleteAsync((result, throwable) -> {
      
      if(throwable != null) {
        deferredResult.setErrorResult(throwable.getCause());
      }
      else {
        deferredResult.setResult(result);
      }
    });

    return deferredResult;
  }

  @RequestMapping(value = "shipping/methods", method = RequestMethod.POST)
  @ApiOperation(value = "Get list of shipping methods", response = ShippingMethod.class, responseContainer = "List")
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<List<ShippingMethod>> getShippingMethods(@RequestBody ShippingRatesRequestData data) throws InterruptedException, ExecutionException {

    DeferredResult<List<ShippingMethod>> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(() -> {
      
      try {
        return fulfillmentService.getShippingMethods(data.getAddress(), data.getOrderItems());
      }
      catch(Exception e) {
        
        throw new CompletionException(e);
      }
    })
    .whenCompleteAsync((result, throwable) -> {
      
      if(throwable != null) {
        deferredResult.setErrorResult(throwable.getCause());
      }
      else {
        deferredResult.setResult(result);
      }
    });

    return deferredResult;
  }
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ShippingRatesRequestData {
    
    Address address;
    List<OrderItem> orderItems;
  }
  
}


