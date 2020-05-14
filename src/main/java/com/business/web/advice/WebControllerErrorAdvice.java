package com.business.web.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class WebControllerErrorAdvice {
  
  private static final String ERROR_MESSAGE = "Error occurred: {}; URI={}";

  @ExceptionHandler(value = { RuntimeException.class, Exception.class })
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorInfo handleRuntimeException(HttpServletRequest req, Exception ex) {

    log.error(ERROR_MESSAGE, ex.getMessage(), req.getRequestURI(), ex);

    return new ErrorInfo(req.getRequestURI(), ex.getMessage());
  }
  
  public class ErrorInfo {

    @Getter
    private String url;

    @Getter
    private String errorMessage;

    public ErrorInfo(String url, String errorMessage) {
      
      this.url = url;
      this.errorMessage = errorMessage;
    }
  }

}
