package com.business.domain;

import com.braintreegateway.CreditCardVerification.Status;
import com.braintreegateway.ProcessorResponseType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentVerification {

  Status status;
  ProcessorResponseType processorResponseType;
  String processorResponseCode;
  String processorResponseText;
  String paymentToken;

}
