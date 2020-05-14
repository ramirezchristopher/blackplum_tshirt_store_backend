package com.business.web;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.business.domain.Order;
import com.business.domain.TransactionInfo;
import com.business.service.BraintreeService;
import com.business.service.OrderProcessingService;
import com.business.web.advice.WebControllerErrorAdvice.ErrorInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/checkout")
@Api(value = "Checkout", description = "Operations for checking out")
@Slf4j
public class CheckoutController {
  
  @Autowired
  private BraintreeService braintreeService;
  
  @Autowired
  private OrderProcessingService orderProcessingService;
	
  @RequestMapping(value = "token", method = RequestMethod.GET)
  @ApiOperation(value = "Get token for client", response = String.class)
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<String> getClientToken() throws InterruptedException, ExecutionException {

    log.info("Getting client token");

    DeferredResult<String> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(() -> {
    	
      try {
        return braintreeService.getClientToken();
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
  
  @RequestMapping(value = "complete", method = RequestMethod.POST)
  @ApiOperation(value = "Complete order", response = TransactionInfo.class)
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<TransactionInfo> completeOrder(@RequestBody Order order) throws InterruptedException, ExecutionException {

    log.info("Completing Order: {}", order);
    
    DeferredResult<TransactionInfo> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(() -> {
      
      try {
        return orderProcessingService.completeOrder(order);
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

}
