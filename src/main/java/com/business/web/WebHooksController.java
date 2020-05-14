package com.business.web;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.business.service.OrderProcessingService;
import com.business.web.advice.WebControllerErrorAdvice.ErrorInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/webhook")
@Api(value = "Webhook", description = "Operations for working with webhooks")
@Slf4j
public class WebHooksController {
  
  @Autowired
  private OrderProcessingService orderProcessingService;

  @RequestMapping(value = "transaction/state", method = RequestMethod.POST)
  @ApiOperation(value = "Check can update transaction state", response = Void.class)
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public void updateTransactionState() throws InterruptedException, ExecutionException {

    log.info("Checking if can update transaction state");

    orderProcessingService.updateTransactionState();
  }

}
