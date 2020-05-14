package com.business.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Configuration class for logging incoming requests.
 */
@Configuration
public class RequestLoggingFilter {

  /**
   * Initializes {@link CommonsRequestLoggingFilter} to log incoming requests.
   *
   * @return configured {@link CommonsRequestLoggingFilter}.
   */
  @Bean
  public CommonsRequestLoggingFilter logFilter() {

    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

    filter.setBeforeMessagePrefix("REQUEST BEGIN: ");
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    filter.setAfterMessagePrefix("REQUEST END: ");
    
    return filter;
  }
  
  @Bean
  public FilterRegistrationBean loggingFilterRegistration(CommonsRequestLoggingFilter logFilter) {
    
      FilterRegistrationBean registration = new FilterRegistrationBean(logFilter);
      registration.addUrlPatterns(
          "/api-docs/*",
          "/v1/catalog/*", 
          "/v1/checkout/*",
          "/v1/fulfillment/*", 
          "/v1/transaction/*", 
          "/v1/webhook/*");
      
      return registration;
  }
}
