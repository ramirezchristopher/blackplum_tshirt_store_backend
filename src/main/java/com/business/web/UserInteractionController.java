package com.business.web;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.business.domain.UserInteraction;
import com.business.domain.UserInteractionType;
import com.business.service.SearchTermService;
import com.business.web.advice.WebControllerErrorAdvice.ErrorInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/observe")
@Api(value = "Interaction", description = "Operations for capturing user interaction")
@Slf4j
public class UserInteractionController {
  
  @Autowired
  private SearchTermService searchTermService;
  
  @RequestMapping(value = "interaction", method = RequestMethod.POST)
  @ApiOperation(value = "Observe user interaction", response = Void.class)
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public void completeOrder(@RequestBody UserInteraction interaction) throws InterruptedException, ExecutionException {

    log.info("User Interaction: {}", interaction);
    
    if(UserInteractionType.SEARCH.equals(interaction.getType())) {
      
      searchTermService.saveSearchedTerms(interaction.getData());
    }
  }

}
