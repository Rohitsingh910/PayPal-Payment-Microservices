package com.mycomp.payments.services.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mycomp.payments.constant.ErrorCodeEnum;
import com.mycomp.payments.dto.TransactionDto;
import com.mycomp.payments.exception.ProcessingServiceException;
import com.mycomp.payments.http.HttpRequest;
import com.mycomp.payments.paypalprovider.PPErrorResponse;
import com.mycomp.payments.paypalprovider.PPOrderResponse;
import com.mycomp.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPCaptureOrderHelper {
	
	private final JsonUtil jsonUtil;
	
	@Value("${paypalprovider.captureorder.url}")
	private String payalProviderCreateOrderUrlTemplate;

	public HttpRequest prepareHttpRequest(
			String txnReference, 
			TransactionDto txnDto) {
		log.info("Preparing HttpRequest for PayPal create order... "
				+ "||txnReference:{} | txnDto:{}",
				txnReference, txnDto);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String captureOrderUrl = payalProviderCreateOrderUrlTemplate.replace(
				"{provider-reference}", txnDto.getProviderReference());

		// create HttpRequest
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(captureOrderUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody("");
		
		log.info("Prepared HttpRequest for PayPal captureOrder: {}", httpRequest);
		return httpRequest;
	}

	/**
	 * Only success valid response is returned back. 
	 * Else we throw exception with valid errorCOde & errorMessage
	 * 
	 * @param httpResponse
	 * @return
	 */
	public PPOrderResponse processResponse(ResponseEntity<String> httpResponse) {
		log.info("Processing PayPal create order response... ||httpResponse:{}",
				httpResponse);
		
		if (httpResponse.getStatusCode().equals(HttpStatus.OK)) {
			log.info("PayPal create order successful. Response body: {}",
					httpResponse.getBody());
			
			PPOrderResponse responseObj = jsonUtil.fromJson(
					httpResponse.getBody(), PPOrderResponse.class);
			
			if(responseObj != null 
					&& responseObj.getOrderId() != null 
					&& responseObj.getPaypalStatus() != null
					&& responseObj.getPaypalStatus().equals("COMPLETED")) {
				log.info("Parsed PayPal order response successfully: {}",
						responseObj);
				return responseObj;
			} else {
				log.error("Failed to parse PayPal order response");
			}
		}
		
		if (httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Received 4xx, 5xx error response from PayPalProvider service");
			
			PPErrorResponse errorResponse = jsonUtil.fromJson(
					httpResponse.getBody(), PPErrorResponse.class);
			
			throw new ProcessingServiceException(
					errorResponse.getErrorCode(),
					errorResponse.getErrorMessage(),
					HttpStatus.valueOf(
							httpResponse.getStatusCode().value()));
		}
		
		log.error("Unexpected response from PayPalProvider service. "
				+ "httpResponse: {}", httpResponse);
		throw new ProcessingServiceException(
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.BAD_GATEWAY);
	}
}
