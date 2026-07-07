package com.mycomp.payments.http;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
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
		} catch (Exception e) { 
			log.error("Exception while making a HTTP call: {}", e.getMessage(), e);

			throw new RuntimeException("HTTP call failed in HttpServiceEngine"
					+ ": " + e.getMessage());
		}
	} 

}
