package com.business.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;

@Configuration
public class BraintreeConfig {

  @Value("${braintree.merchant-id}")
  private String merchantId;

  @Value("${braintree.public-key}")
  private String publicKey;

  @Value("${braintree.private-key}")
  private String privateKey;

  @Bean
  @ConditionalOnProperty(value = "braintree.production.enabled", havingValue = "false")
  BraintreeGateway sandboxGateway() {

    return new BraintreeGateway(
        Environment.SANDBOX, 
        merchantId, 
        publicKey, 
        privateKey
      );
  }
  
  @Bean
  @ConditionalOnProperty(value = "braintree.production.enabled", havingValue = "true")
  BraintreeGateway productionGateway() {

    return new BraintreeGateway(
        Environment.PRODUCTION, 
        merchantId, 
        publicKey, 
        privateKey
      );
  }
}
