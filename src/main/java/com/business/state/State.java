package com.business.state;

import com.business.domain.OrderStatus;

public interface State {
  
  void process();
  
  void cancel();
  
  OrderStatus getOrderStatus();
}
