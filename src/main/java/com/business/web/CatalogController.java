package com.business.web;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.business.domain.CatalogItem;
import com.business.domain.CategoryType;
import com.business.service.CatalogService;
import com.business.web.advice.WebControllerErrorAdvice.ErrorInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/catalog")
@Api(value = "Catalog", description = "Operations for getting store catalog")
@Slf4j
public class CatalogController {
  
  @Autowired
  private CatalogService catalogService;
	
  @RequestMapping(value = "", method = RequestMethod.GET)
  @ApiOperation(value = "Get catalog items by category type", response = CatalogItem.class, responseContainer = "List")
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<List<CatalogItem>> getCatalogItemsByCategory(@RequestParam(value = "category", required = true) CategoryType category) throws InterruptedException, ExecutionException {

    log.info("Getting catalog items for {}", category);

    DeferredResult<List<CatalogItem>> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(() -> {
    	
      try {
        return catalogService.getCatalogItemsByCategory(category);
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
  
  @RequestMapping(value = "init", method = RequestMethod.GET)
  @ApiOperation(value = "Initialize the catalog.catalogItem database collection", response = CatalogItem.class, responseContainer = "List")
  @ApiResponses({
      @ApiResponse(code = 200, message = "OK"), 
      @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorInfo.class)
  })
  public DeferredResult<List<CatalogItem>> initializeCatalog() throws InterruptedException, ExecutionException {

    log.info("Initializing catalog items");

    DeferredResult<List<CatalogItem>> deferredResult = new DeferredResult<>();

    CompletableFuture.supplyAsync(() -> {
      
      try {
        return catalogService.initializeCatalog();
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
