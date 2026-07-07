package com.mycomp.validation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycomp.validation.pojo.CompletePaymentRes;
import com.mycomp.validation.pojo.CreatePaymentReq;
import com.mycomp.validation.pojo.InitiatePaymentRequest;
import com.mycomp.validation.pojo.PaymentResponse;
import com.mycomp.validation.service.PaymentValidator;
import com.mycomp.validation.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/validation")
@RequiredArgsConstructor
public class ValidationController {
	
	private final PaymentValidator paymentValidator;
	private final PaymentService paymentService;
	
	
	@Value("${cancel.url}")
	private String cancelUrl;
	
	@Value("${return.url}")
	private String returnUrl;
	
	@PostMapping("/createPayment")
	public PaymentResponse createPayment(@RequestBody CreatePaymentReq createPaymentReq) {
		log.info("Creating payment inside validation service:{}", createPaymentReq);
		
		//validating request
		paymentValidator.validateCreateOrder(createPaymentReq); 
		
		PaymentResponse createPaymentresponse = paymentService.createPayment(createPaymentReq);
		log.info("Payment created successfully with txnReference: {}", createPaymentresponse.getTxnReference());
		
		//directly calling initiate payment from here instead of making another call from client? 
		String txnReference = createPaymentresponse.getTxnReference();
	
		//making object of  IntiatePaymentRequest 
		InitiatePaymentRequest intiatePaymentRequest = new InitiatePaymentRequest();
		intiatePaymentRequest.setCancelUrl(cancelUrl);
		intiatePaymentRequest.setReturnUrl(returnUrl);
		
		
		//initiating payment
		log.info("Initiating payment for txnReference: {}", txnReference);
		PaymentResponse initiatePaymentResponse = paymentService.initiatePayment(intiatePaymentRequest, txnReference);	
		log.info("Initiate payment response from validation service: {}", initiatePaymentResponse);
		return initiatePaymentResponse;
	}
	
	
	@PostMapping("/{txnReference}/completePayment")
	public CompletePaymentRes completePayment(@PathVariable String txnReference) {
		log.info("Completing payment in validation service for the  txnReference:{}", txnReference);
		
		PaymentResponse  response = paymentService.completePayment(txnReference);
		
		CompletePaymentRes completePaymentRes = new CompletePaymentRes();
		completePaymentRes.setTxnReference(response.getTxnReference());
		completePaymentRes.setTxnStatusId(response.getTxnStatusId());
		
		log.info("Complete payment response from service: {}", completePaymentRes);
		return completePaymentRes;
		
	}
	

}
