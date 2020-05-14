package com.business.web;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.business.domain.TransactionInfo;
import com.business.service.OrderProcessingService;
import com.business.web.advice.WebControllerErrorAdvice.ErrorInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transaction")
@Api(value = "Transaction", description = "Operations for working with transactions")
@Slf4j
public class TransactionController {
  
  @Autowired
  private OrderProcessingService orderProcessingService;
	
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  @ApiOperation(value = "Get transaction info", response = TransactionInfo.class)
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<TransactionInfo> getTransactionInfo(@PathVariable(value = "id", required = true) String id) throws InterruptedException, ExecutionException {

    log.info("Getting transaction info for {}", id);

    DeferredResult<TransactionInfo> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(() -> {
    	
      try {
        return orderProcessingService.getTransactionInfo(id);
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
