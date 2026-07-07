package com.mycomp.payments.services.interfaces;

import com.mycomp.payments.pojo.CreatePaymentRequest;
import com.mycomp.payments.pojo.InitiatePaymentRequest;
import com.mycomp.payments.pojo.PaymentResponse;


public interface PaymentService {
	
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);
	
	public PaymentResponse initiatePayment( 
			String txnReference,InitiatePaymentRequest initiatePaymentRequest);
	
	public PaymentResponse capturePayment(String txnReference);

}
