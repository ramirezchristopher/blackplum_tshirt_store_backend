package com.business.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.business.repository")
public class MongoConfiguration extends AbstractMongoClientConfiguration {

  @Value("${spring.data.mongodb.database:catalog}")
  private String databaseName;

  @Value("${spring.data.mongodb.host:127.0.0.1}")
  private String host;
  
  @Value("${spring.data.mongodb.username}")
  private String user;

  @Value("${spring.data.mongodb.password}")
  private String password;

  @Value("${spring.data.mongodb.port:27017}")
  private String port;

  @Override
  protected String getDatabaseName() {
      return databaseName;
  }

  @Bean
  public MongoClient mongoClient() {

    MongoCredential credential = MongoCredential.createCredential(user, databaseName, password.toCharArray());

    MongoClientSettings settings = MongoClientSettings.builder()
            .credential(credential)
            .applyToSslSettings(builder -> builder.enabled(false))
            .applyToClusterSettings(builder ->
                builder.hosts(Arrays.asList(new ServerAddress(host, Integer.valueOf(port)))))
            .build();

    MongoClient mongoClient = MongoClients.create(settings);

    return mongoClient;
  }

  @Bean
  public MongoTemplate mongoTemplate() {

    return new MongoTemplate(mongoClient(), databaseName);
  }
}
