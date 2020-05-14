package com.business.state;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.business.domain.OrderFulfillmentStatus;
import com.business.domain.OrderStatus;
import com.business.mail.MailSender;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintOrderCreatedState implements State {
  
  private TransactionProcessor transactionProcessor;
  
  @Getter
  private final OrderStatus orderStatus = OrderStatus.PRINT_ORDER_CREATED;

  public PrintOrderCreatedState(TransactionProcessor transactionProcessor) {
    
    this.transactionProcessor = transactionProcessor;
  }

  @Override
  public void process() {
    
    Map<String, Object> printOrder = transactionProcessor.getPrintOrder(transactionProcessor.getTransactionInfo());
    
    if(printOrder == null || StringUtils.isBlank((String) printOrder.get("status"))) {
      
      transactionProcessor.getFulfillmentDao().createPrintOrder(transactionProcessor.getTransactionInfo()).getResult();
    }
    else if(OrderFulfillmentStatus.FULFILLED.equals(OrderFulfillmentStatus.findByDescription((String) printOrder.get("status")))) {
      
      sendPrintOrderShippedEmail(printOrder);
      
      transactionProcessor.setState(transactionProcessor.getPrintOrderFulfilledState());
    }
    else if(OrderFulfillmentStatus.FAILED.equals(OrderFulfillmentStatus.findByDescription((String) printOrder.get("status")))) {
     
      transactionProcessor.setState(transactionProcessor.getPrintOrderFailedState());
    }
    else if(OrderFulfillmentStatus.CANCELED.equals(OrderFulfillmentStatus.findByDescription((String) printOrder.get("status")))) {
     
      transactionProcessor.setState(transactionProcessor.getPrintOrderCanceledState());
    }
    else if(OrderFulfillmentStatus.ON_HOLD.equals(OrderFulfillmentStatus.findByDescription((String) printOrder.get("status")))) {
      
      transactionProcessor.setState(transactionProcessor.getPrintOrderOnHoldState());
    }
    
    
    // transition to delivered state if needed
  }

  @Override
  public void cancel() {

    // TODO Auto-generated method stub
    
  }
  
  private void sendPrintOrderShippedEmail(Map<String, Object> printOrder) {
    
    if(printOrder.get("shipments") != null) {
    
      List<Map<String, Object>> shipments = (List<Map<String, Object>>) printOrder.get("shipments");
      
      if(!shipments.isEmpty()) {
        
        Map<String, Object> shipment = shipments.get(0);
        MailSender mailSender = transactionProcessor.getMailSender();
        
        try {
          mailSender.sendOrderShippedEmail(transactionProcessor.getTransactionInfo(), (String) shipment.get("tracking_url"));
        }
        catch(Exception e) {
          
          log.error("Exception while sending order shipped e-mail", e);
        }
      }
    }
  }
  
  @Override
  public String toString() {
    
    return orderStatus.name();
  }

}
