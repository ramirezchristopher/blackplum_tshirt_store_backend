package com.business.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@EnableMongoRepositories(basePackages = "com.business.repository")
public class MongoConfiguration extends AbstractMongoConfiguration {
  
  @Value("${spring.data.mongodb.database:catalog}")
  private String databaseName;
  
  @Value("${spring.data.mongodb.host:127.0.0.1}")
  private String host;
  
  @Value("${spring.data.mongodb.password}")
  private String password;
  
  @Value("${spring.data.mongodb.port:27017}")
  private String port;
  
  @Override
  protected String getDatabaseName() {
    
    return databaseName;
  }
  
  @Override
  @Bean
  public MongoClient mongoClient() {
    
    return new MongoClient(
        Collections.singletonList(new ServerAddress(host, Integer.valueOf(port))), 
        MongoCredential.createCredential("catalogsvc", databaseName, password.toCharArray()), 
        MongoClientOptions.builder().build()
    );
  }
  
  @Bean
  public MongoTemplate mongoTemplate() {
    
    return new MongoTemplate(mongoClient(), databaseName);
  }
}
