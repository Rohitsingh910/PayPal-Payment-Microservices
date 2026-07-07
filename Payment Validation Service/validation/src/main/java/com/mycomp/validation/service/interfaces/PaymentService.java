package com.mycomp.validation.service.interfaces;

import com.mycomp.validation.pojo.CreatePaymentReq;
import com.mycomp.validation.pojo.InitiatePaymentRequest;
import com.mycomp.validation.pojo.PaymentResponse;

public interface PaymentService {

	public PaymentResponse createPayment(CreatePaymentReq createPaymentReq);
	public PaymentResponse initiatePayment(InitiatePaymentRequest intiatePaymentRequest, String txnReference);
	public PaymentResponse completePayment(String txnReference);
}
