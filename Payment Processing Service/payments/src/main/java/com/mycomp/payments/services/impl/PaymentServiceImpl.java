package com.mycomp.payments.services.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mycomp.payments.dao.interfaces.TransactionDao;
import com.mycomp.payments.dto.TransactionDto;
import com.mycomp.payments.entity.TransactionEntity;
import com.mycomp.payments.http.HttpRequest;
import com.mycomp.payments.http.HttpServiceEngine;
import com.mycomp.payments.paypalprovider.PPOrderResponse;
import com.mycomp.payments.pojo.CreatePaymentRequest;
import com.mycomp.payments.pojo.InitiatePaymentRequest;
import com.mycomp.payments.pojo.PaymentResponse;
import com.mycomp.payments.service.PaymentStatusService;
import com.mycomp.payments.services.helper.PPCaptureOrderHelper;
import com.mycomp.payments.services.helper.PPCreateOrderHelper;
import com.mycomp.payments.services.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	
    private final PPCreateOrderHelper ppCreateOrderHelper;
	
	private final PPCaptureOrderHelper ppCaptureOrderHelper;

	private final HttpServiceEngine httpServiceEngine;

	private final PaymentStatusService paymentStatusService;

	private final ModelMapper modelMapper;
	
	private final TransactionDao transactionDao;

	@Override
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
		log.info("Creating payment in PaymentServiceImpl..."
				+ "||createPaymentRequest:{}",
				createPaymentRequest);

		
		TransactionDto txnDto = modelMapper.map(
				createPaymentRequest, TransactionDto.class);
		log.info("Mapped CreatePaymentRequest to TransactionDto: {}", txnDto);


		int txnStatusId = 1; // CREATED
		String txnReference = UUID.randomUUID().toString(); // for every payment, have unique reference
		
		txnDto.setTxnStatusId(txnStatusId);
		txnDto.setTxnReference(txnReference);
		
		TransactionDto response = paymentStatusService.processPayment(txnDto);
		log.info("Response from TransactionStatusProcessor: {}", response);
		
		PaymentResponse paymentRes = new PaymentResponse();
		paymentRes.setTxnReference(response.getTxnReference());
		paymentRes.setTxnStatusId(response.getTxnStatusId());
		log.info("Prepared PaymentResponse: {}", paymentRes);
		
		return paymentRes;
	}

	@Override
	public PaymentResponse initiatePayment(String txnReference, 
			InitiatePaymentRequest initiatePaymentRequest) {
		log.info("Initiating payment in PaymentServiceImpl... "
				+ "txnReference: {} | initiatePaymentRequest:{}", 
				txnReference, initiatePaymentRequest);
		
		TransactionEntity txnEntity = transactionDao.getTransactionByTxnReference(txnReference);
		log.info("Fetched TransactionEntity from DB: {}", txnEntity);
		
		// use modelMapper to convert Entity to DTO
		TransactionDto txnDto = modelMapper.map(
				txnEntity, TransactionDto.class);
		log.info("Mapped TransactionEntity to TransactionDto: {}", txnDto);
		
		// update txn status to INITIATED
		txnDto.setTxnStatusId(2); // INITIATED
		TransactionDto response = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after updating status to INITIATED: {}", response);
		
		
		// MAKING API CALL To payal-provider to createOrder API
		HttpRequest httpReq = ppCreateOrderHelper.prepareHttpRequest(
				txnReference, initiatePaymentRequest, txnDto);
		log.info("Prepared HttpRequest for PayPalProvider create order: {}", httpReq);

		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpReq);
		log.info("HTTP response from HttpServiceEngine: {}", httpResponse);
		
		PPOrderResponse ppOrderResponse = ppCreateOrderHelper.processResponse(httpResponse);
		log.info("Processed PayPal order response: {}", ppOrderResponse);
		
		// update txn status to PENDING
		txnDto.setTxnStatusId(3); // PENDING
		txnDto.setProviderReference(ppOrderResponse.getOrderId());
		response = paymentStatusService.processPayment(txnDto);
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(txnDto.getTxnReference());
		paymentResponse.setTxnStatusId(txnDto.getTxnStatusId());
		paymentResponse.setProviderReference(ppOrderResponse.getOrderId());
		paymentResponse.setRedirectUrl(ppOrderResponse.getRedirectUrl());

		log.info("Final PaymentResponse to be returned: {}", paymentResponse);
		
		return paymentResponse;
	}
	
	@Override
	public PaymentResponse capturePayment(String txnReference) {
		log.info("Capturing payment in PaymentServiceImpl... "
				+ "txnReference: {}", txnReference);
		
		TransactionEntity txnEntity = transactionDao.getTransactionByTxnReference(
				txnReference);
		log.info("Fetched TransactionEntity from DB: {}", txnEntity);
		
		// use modelMapper to convert Entity to DTO
		TransactionDto txnDto = modelMapper.map(
				txnEntity, TransactionDto.class);
		log.info("Mapped TransactionEntity to TransactionDto: {}", txnDto);
		
		// update txn status to APPROVED
		txnDto.setTxnStatusId(4);  
		txnDto = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after updating status to APPROVED: {}", txnDto);
		
		HttpRequest httpReq = ppCaptureOrderHelper.prepareHttpRequest(
				txnReference, txnDto);
		
		PPOrderResponse ppCaptureOrderSuccessResponse = null;
		try {
			ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpReq);
			log.info("HTTP response from HttpServiceEngine: {}", httpResponse);
			
			ppCaptureOrderSuccessResponse = ppCaptureOrderHelper.processResponse(httpResponse);
			log.info("Processed PayPal order response: {}", ppCaptureOrderSuccessResponse);
		} catch (Exception e) {
			log.error("Error occurred while making captureOrder HTTP call to PayPalProvider: ", e);
			
			// Note, dont change the status to FAILED since user already APPROVED.
			// Let reconciliation job handle such cases.
			// In case reconciliation also resolved it as failed, 
			//then manually back-office can handle this payment..
			// just throw error back
			
			throw e; // rethrow the exception after updating status
		} 
		
		// update txn status to SUCCESS
		txnDto.setTxnStatusId(5); 
		txnDto = paymentStatusService.processPayment(txnDto);
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(txnDto.getTxnReference());
		paymentResponse.setTxnStatusId(txnDto.getTxnStatusId());

		log.info("Final PaymentResponse to be returned: {}", paymentResponse);
		
		return paymentResponse;
	}

}
