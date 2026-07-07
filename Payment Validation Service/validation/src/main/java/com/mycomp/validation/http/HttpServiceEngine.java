package com.mycomp.validation.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.mycomp.validation.constant.ErrorCodeEnum;
import com.mycomp.validation.exception.ValidationException;

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
			
			// return ResponseEntity with error details 
			String errorResponse = e.getResponseBodyAsString();
			log.info("Error response body: {}", errorResponse);
			
			return ResponseEntity
					.status(e.getStatusCode())
					.body(errorResponse);
			
			
		} catch (ResourceAccessException e) {

		    log.error("Processing Service not reachable", e);

		    throw new ValidationException(
		            ErrorCodeEnum.PAYMENT_PROCESSING_SERVICE_UNAVAILABLE.getErrorCode(),
		            ErrorCodeEnum.PAYMENT_PROCESSING_SERVICE_UNAVAILABLE.getErrorMessage(),
		            HttpStatus.SERVICE_UNAVAILABLE);
		    
		}catch (Exception e) { // No Response case.
			log.error("Exception while preparing form data: {}", e.getMessage(), e);
	
			throw new ValidationException(
					ErrorCodeEnum.PROCESSING_UNKNOWN_ERROR.getErrorCode(),
					ErrorCodeEnum.PROCESSING_UNKNOWN_ERROR.getErrorMessage(),
					HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
}
