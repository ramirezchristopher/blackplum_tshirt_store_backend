package com.business.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import com.google.common.base.Predicates;

import io.swagger.annotations.Api;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  private static final String API_KEY_NAME = "api_key";

  /**
   * @return {@link ApiKey}
   */
  private ApiKey apiKey() {

    return new ApiKey(API_KEY_NAME, API_KEY_NAME, "header");
  }

  /**
   * @return list of {@link SecurityReference}
   */
  private List<SecurityReference> defaultAuth() {

    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Collections.singletonList(new SecurityReference(API_KEY_NAME, authorizationScopes));
  }

  /**
   * @return {@link SecurityContext}
   */
  private SecurityContext securityContext() {

    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.any())
        .build();
  }

  /**
   * Initialized {@link Docket} bean.
   * 
   * @return {@link Docket}
   */
  @Bean
  @SuppressWarnings("unchecked")
  public Docket api() {

    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
        .paths(Predicates.or(
            PathSelectors.regex(".*v1/catalog.*"), 
            PathSelectors.regex(".*v1/checkout.*"), 
            PathSelectors.regex(".*v1/observe.*"), 
            PathSelectors.regex(".*v1/fulfillment.*"), 
            PathSelectors.regex(".*v1/transaction.*"), 
            PathSelectors.regex(".*v1/webhook.*")
        ))
        .build()
        .apiInfo(apiInfo())
        .produces(new HashSet<>(Collections.singletonList(MediaType.APPLICATION_JSON_VALUE)))
        .securitySchemes(Collections.singletonList(apiKey()))
        .securityContexts(Collections.singletonList(securityContext()))
        .useDefaultResponseMessages(false);
  }

  private ApiInfo apiInfo() {

    return new ApiInfoBuilder()
        .title("Store Catalog Service")
        .description("Maintain Store Catalog")
        .version("1.0")
        .contact(new Contact("Chris Ramirez", "", "chris80.ramirez@gmail.com"))
        .build();
  }

}
