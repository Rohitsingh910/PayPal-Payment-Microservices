package com.mycomp.validation.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mycomp.validation.constant.ErrorCodeEnum;
import com.mycomp.validation.exception.ValidationException;
import com.mycomp.validation.http.HttpRequest;
import com.mycomp.validation.pojo.PaymentResponse;
import com.mycomp.validation.processing.ProcessingCreatePaymentRes;
import com.mycomp.validation.processing.ProcessingErrorResponse;
import com.mycomp.validation.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class CompletePaymentHelper {
	
private final JsonUtil jsonUtil;
	
	@Value("${processing.capture.payment.url}")
	private String processingCaptureOrderUrlTemplate;
	

	public HttpRequest prepareHttpRequest(
			String txnReference
			) {
		log.info("Preparing HttpRequest to call processing service for completing payment..:"
				+ "||txnReference:{}",
				txnReference);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String captureOrderUrl = processingCaptureOrderUrlTemplate.replace(
				"{txnReference}", txnReference);

		// create HttpRequest
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(captureOrderUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody("");
		
		log.info("Prepared HttpRequest for Processing captureOrder: {}", httpRequest);
		return httpRequest;
	}

	public PaymentResponse handlePaypalResponse(ResponseEntity<String> httpResponse) {
		log.info("Handling Processing response in PaymentServiceImpl "
				+ "httpResponse:{}", httpResponse);
		
		if(httpResponse.getStatusCode().is2xxSuccessful()) { //success

			PaymentResponse processingOrder = jsonUtil.fromJson(
					httpResponse.getBody(), PaymentResponse.class);
			log.info("Converted response body to ProcessingOrder: {}", processingOrder);
			
			//PaymentResponse orderResponse = toOrderResponse(processingOrder);
			log.info("Converted OrderResponse: {}", processingOrder);
			
			// If we get a valid response with PAYER_ACTION_REQUIRED status & url & id, then only its success else its failed.
			if(processingOrder != null 					
					&& processingOrder.getTxnStatusId() !=0
					&& processingOrder.getTxnReference() != null) {
				log.info("Payment created successfully with TxnStatusId = 5 in processing service");
				return processingOrder;
			}
			
			log.error("Order creation failed or incomplete details received. "
					+ "orderResponse: {}", processingOrder);
			
		}
		
		// if 4xx or 5xx then proper error
		if (httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Received 4xx, 5xx error response from PayPalProvider service");
			
			ProcessingErrorResponse errorResponse = jsonUtil.fromJson(
					httpResponse.getBody(), ProcessingErrorResponse.class);
			
			throw new ValidationException(
					errorResponse.getErrorCode(),
					errorResponse.getErrorMessage(),
					HttpStatus.valueOf(
							httpResponse.getStatusCode().value()));
		}
		
		log.error("Unexpected response from PayPalProvider service. "
				+ "httpResponse: {}", httpResponse);
		throw new ValidationException(
				ErrorCodeEnum.PROCESSING_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PROCESSING_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.BAD_GATEWAY);
	}
	
}
