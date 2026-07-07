package com.mycomp.validation.service.helper;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mycomp.validation.constant.ErrorCodeEnum;
import com.mycomp.validation.exception.ValidationException;
import com.mycomp.validation.http.HttpRequest;
import com.mycomp.validation.pojo.CreatePaymentReq;
import com.mycomp.validation.pojo.InitiatePaymentRequest;
import com.mycomp.validation.pojo.PaymentResponse;
import com.mycomp.validation.processing.ProcessingCreatePaymentReq;
import com.mycomp.validation.processing.ProcessingCreatePaymentRes;
import com.mycomp.validation.processing.ProcessingErrorResponse;
import com.mycomp.validation.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePaymentHelper {

	private final JsonUtil jsonUtil;

	@Value("${processing.create.payment.url}")
	private String createPaymentUrl;
	
	@Value("${processing.initiate.payment.url}")
	private String processingInitiatePaymentUrlTemplate;

	public HttpRequest prepareCreateOrderHttpRequest(
			CreatePaymentReq createPaymentReq) {
		
		log.info("Preparing HttpRequest for processing create payment. "
				+ "createPaymentReq: {}", createPaymentReq);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		
		ProcessingCreatePaymentReq paymentReq = new ProcessingCreatePaymentReq();
		paymentReq.setUserId(createPaymentReq.getUserId());
		paymentReq.setPaymentMethodId(createPaymentReq.getPaymentMethodId());
		paymentReq.setProviderId(createPaymentReq.getProviderId());
		paymentReq.setPaymentTypeId(createPaymentReq.getPaymentTypeId());
		paymentReq.setAmount(createPaymentReq.getAmount());
		paymentReq.setCurrency(createPaymentReq.getCurrency());
		paymentReq.setMerchantTransactionReference(createPaymentReq.getMerchantTransactionReference());
		
		String requestAsJson = jsonUtil.toJson(paymentReq);
		
		
		// create HttpRequest
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(createPaymentUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(requestAsJson);
		
		log.info("Prepared HttpRequest for processing create payment: {}", httpRequest);
		
		return httpRequest;
		
	}
	
	public HttpRequest prepareInitiateOrderHttpRequest(InitiatePaymentRequest initiatePaymentReq,String txnReference) {
		log.info("Preparing HttpRequest for initiating order. "
				+ "initiatePaymentReq: {}", initiatePaymentReq ,"txnReference: {}", txnReference);
		
		//Don't need  to make ProcessingInitiatePaymentReq as it is same as InitiatePaymentrequest  
		String requestAsJson = jsonUtil.toJson(initiatePaymentReq);
		
		String initatePaymentUrl = processingInitiatePaymentUrlTemplate.replace(
				"{txnReference}", txnReference);
		//headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		// create HttpRequest
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(initatePaymentUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(requestAsJson);
		
		log.info("Prepared HttpRequest for initiating order: {}", httpRequest);
		
		return httpRequest;
	}

	
	public PaymentResponse toOrderResponse(ProcessingCreatePaymentRes processingCreatePaymentRes) {
		log.info("Converting processingCreatePaymentRes to PaymentResponse: {}", processingCreatePaymentRes);
		
		PaymentResponse response = new PaymentResponse();
	    response.setTxnReference(processingCreatePaymentRes.getTxnReference());
	    response.setTxnStatusId(processingCreatePaymentRes.getTxnStatusId());
	    response.setProviderReference(processingCreatePaymentRes.getProviderReference());
	    response.setRedirectUrl(processingCreatePaymentRes.getRedirectUrl());

	      
	    log.info("Converted PaypalOrder to OrderResponse: {}", response);

	    return response;
	}

	public PaymentResponse handleProcessingResponse(ResponseEntity<String> httpResponse) {
		log.info("Handling Processing response in PaymentServiceImpl "
				+ "httpResponse:{}", httpResponse);
		
		if(httpResponse.getStatusCode().is2xxSuccessful()) { //success

			ProcessingCreatePaymentRes processingOrder = jsonUtil.fromJson(
					httpResponse.getBody(), ProcessingCreatePaymentRes.class);
			log.info("Converted response body to ProcessingOrder: {}", processingOrder);
			
			PaymentResponse orderResponse = toOrderResponse(processingOrder);
			log.info("Converted OrderResponse: {}", orderResponse);
			
			//checking If getting a valid response with PAYER_ACTION_REQUIRED status & url & id, then only its success else its failed.
			if(orderResponse != null 
					&& orderResponse.getProviderReference() != null
					&& orderResponse.getTxnStatusId() !=0
					&& orderResponse.getTxnReference() != null
					&& orderResponse.getRedirectUrl() != null
					&& !orderResponse.getRedirectUrl().isEmpty()) {
				log.info("Payment created successfully with TxnStatusId = 3 in processing service");
				return orderResponse;
			}
			
			log.error("Order creation failed or incomplete details received. "
					+ "orderResponse: {}", orderResponse);
			
		}
		
		// if 4xx or 5xx then proper error
		if (httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Received 4xx, 5xx error response from Processing service");
			
			ProcessingErrorResponse errorResponse = jsonUtil.fromJson(
					httpResponse.getBody(), ProcessingErrorResponse.class);
			
			throw new ValidationException(
					errorResponse.getErrorCode(),
					errorResponse.getErrorMessage(),
					HttpStatus.valueOf(
							httpResponse.getStatusCode().value()));
		}
		
		log.error("Unexpected response from Processing service. "
				+ "httpResponse: {}", httpResponse);
		throw new ValidationException(
				ErrorCodeEnum.PROCESSING_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PROCESSING_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.BAD_GATEWAY);
	}

}

