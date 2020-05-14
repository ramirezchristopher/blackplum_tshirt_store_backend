package com.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableHystrix
@Slf4j
public class StoreCatalogApplication {

  @LoadBalanced
  @Bean(name = "loadBalancedRestTemplate")
  public RestTemplate restTemplate() {

    return new RestTemplate();
  }
  
  public static void main(String[] args) {

    SpringApplication.run(StoreCatalogApplication.class, args);
  }
}
