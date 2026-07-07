package com.mycomp.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.mycomp.payments.constant.ErrorCodeEnum;
import com.mycomp.payments.exception.ProcessingServiceException;
import com.mycomp.payments.pojo.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

	private final RestClient restClient;

	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("Making HTTP call in HttpServiceEngine");

		try {
			ResponseEntity<String> httpResponse = restClient
					.method(httpRequest.getHttpMethod())
					.uri(httpRequest.getUrl())
					.headers(
							restClientHeaders -> 
							restClientHeaders.addAll(
									httpRequest.getHttpHeaders()))
					.body(httpRequest.getBody())
					.retrieve()
					.toEntity(String.class);

			log.info("HTTP call completed httpResponse:{}", httpResponse);

			return httpResponse;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			// valid error response from server
			log.error("HTTP error response received: {}", e.getMessage(), e);
			
			// if the error is gateway time or service unavailable, throw PaypalProviderException
			if (e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT ||
					e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
				log.error("Service is unavailable or gateway timed out");
				throw new ProcessingServiceException(
						ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorCode(),
						ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorMessage(),
						HttpStatus.SERVICE_UNAVAILABLE);
			}
			
			// return ResponseEntity with error details 
			String errorResponse = e.getResponseBodyAsString();
			log.info("Error response body: {}", errorResponse);
			
			return ResponseEntity
					.status(e.getStatusCode())
					.body(errorResponse);
			
		} catch (Exception e) { // No Response case.
			log.error("Exception while preparing form data: {}", e.getMessage(), e);

			throw new ProcessingServiceException(
					ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorCode(),
					e.getMessage(),
					//ErrorCodeEnum.PAYPAL_PROVIDER_SERVICE_UNAVAILABLE.getErrorMessage(),
					HttpStatus.SERVICE_UNAVAILABLE);
		
		}
	}
}
