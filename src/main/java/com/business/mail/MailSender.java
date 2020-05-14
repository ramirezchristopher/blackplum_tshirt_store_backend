package com.business.mail;

import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.business.domain.SearchTerm;
import com.business.domain.TransactionInfo;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MailSender {
  
  @Value("${mailgun.api-key}")
  private String apiKey;

  @Value("${mailgun.base-url}")
  private String apiUrl;
  
  @Value("${personal-email}")
  private String personalEmail;
  
  @Autowired
  private Configuration freemarkerConfig;

  public String sendPurchaseConfirmationEmail(TransactionInfo transactionInfo) throws Exception {
    
    log.info("Sending purchase confirmation e-mail to {}", transactionInfo.getOrder().getShippingAddress().getEmail());
    
    HttpResponse<String> response = Unirest.post(apiUrl + "/messages")
        .basicAuth("api", apiKey)
        .field("from", "Black Plum Apparel <support@blackplumapparel.com>")
        .field("to", transactionInfo.getOrder().getShippingAddress().getEmail())
        .field("subject", "Your Black Plum Apparel Purchase Confirmation")
        .field("html", composePurchaseConfirmationEmail(transactionInfo))
        .asString();
      
    log.info("Finished sending purchase confirmation e-mail to {}. Response: {}", transactionInfo.getOrder().getShippingAddress().getEmail(), response.getBody());
    
    return response.getBody();
  }
  
  private String composePurchaseConfirmationEmail(TransactionInfo transactionInfo) throws Exception {
    
    Template template = freemarkerConfig.getTemplate("/email/purchase-confirmation.ftl");
    Map<String, Object> dataModel = new HashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a");
    
    dataModel.put("transactionInfo", transactionInfo);
    dataModel.put("orderDate", transactionInfo.getOrderDate().format(formatter));
    
    Writer out = new StringWriter();
    template.process(dataModel, out);
    
    return out.toString();
  }
  
  public String sendOrderShippedEmail(TransactionInfo transactionInfo, String trackingUrl) throws Exception {
    
    log.info("Sending purchase confirmation e-mail to {}", transactionInfo.getOrder().getShippingAddress().getEmail());
    
    HttpResponse<String> response = Unirest.post(apiUrl + "/messages")
        .basicAuth("api", apiKey)
        .field("from", "Black Plum Apparel <support@blackplumapparel.com>")
        .field("to", transactionInfo.getOrder().getShippingAddress().getEmail())
        .field("subject", "Your Black Plum Apparel Purchase Has Shipped")
        .field("html", composeOrderShippedEmail(transactionInfo, trackingUrl))
        .asString();
      
    log.info("Finished sending order shipped e-mail to {} with tracking url {}. Response: {}", transactionInfo.getOrder().getShippingAddress().getEmail(), trackingUrl, response.getBody());
    
    return response.getBody();
  }
  
  private String composeOrderShippedEmail(TransactionInfo transactionInfo, String trackingUrl) throws Exception {
    
    Template template = freemarkerConfig.getTemplate("/email/order-shipped.ftl");
    Map<String, Object> dataModel = new HashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a");
    
    dataModel.put("transactionInfo", transactionInfo);
    dataModel.put("orderDate", transactionInfo.getOrderDate().format(formatter));
    dataModel.put("trackingUrl", trackingUrl);
    
    Writer out = new StringWriter();
    template.process(dataModel, out);
    
    return out.toString();
  }
  
  public String sendSearchedTermsEmail(List<SearchTerm> searchTerms) throws Exception {
    
    log.info("Sending searched terms e-mail to {}", personalEmail);
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a");
    String now = LocalDateTime.now().format(formatter);
    
    HttpResponse<String> response = Unirest.post(apiUrl + "/messages")
        .basicAuth("api", apiKey)
        .field("from", "Black Plum Apparel <support@blackplumapparel.com>")
        .field("to", personalEmail)
        .field("subject", String.format("Black Plum Apparel - Search Terms %s", now))
        .field("html", composeSearchedTermsEmail(searchTerms, now))
        .asString();
      
    log.info("Finished sending searched terms e-mail to {}. Response: {}", personalEmail, response.getBody());
    
    return response.getBody();
  }
  
  private String composeSearchedTermsEmail(List<SearchTerm> searchTerms, String formattedDate) throws Exception {
    
    Template template = freemarkerConfig.getTemplate("/email/searched-terms.ftl");
    Map<String, Object> dataModel = new HashMap<>();
    
    dataModel.put("searchTerms", searchTerms);
    dataModel.put("now", formattedDate);
    
    Writer out = new StringWriter();
    template.process(dataModel, out);
    
    return out.toString();
  }

}
