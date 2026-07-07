package com.mycomp.payments.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mycomp.payments.constant.Constant;
import com.mycomp.payments.http.HttpRequest;
import com.mycomp.payments.http.HttpServiceEngine;
import com.mycomp.payments.paypal.res.PaypalOAuthToken;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
	
	private final HttpServiceEngine httpServiceEngine;
	private final ObjectMapper objectMapper;
	private final StringRedisTemplate redisTemplate;
	
	private static final String REDIS_KEY = "paypal:access_token";
	
	@Value("${mytestkey:NOT_FOUND}")
	private String testKey;

	@Value("${paypal.client.id}")
	private String clientId;
	
	@Value("${paypal.client.secret}")
	private String clientSecret;

	@Value("${paypal.oauth.url}")
	private String outhUrl;
	
	public String getAccessToken() {
		log.info("Retrieving access token from TokenService");
		
		String cachedToken = redisTemplate.opsForValue().get(REDIS_KEY);
		if (cachedToken != null) {
			log.info("Returning cached access token from Redis");
			return cachedToken;
		}
		
		log.info("No cached access token found in Redis, calling OAuth service");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(clientId, clientSecret);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(outhUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(formData);
		
		log.info("Prepared HttpRequest for OAuth call: {}", httpRequest);
		
		ResponseEntity<String> response = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP response from HttpServiceEngine: {}", response);
		
		String tokenBody = response.getBody();
		
		PaypalOAuthToken token = null;
		try {
			token = objectMapper.readValue(tokenBody, PaypalOAuthToken.class);
		} catch (JsonProcessingException e) {
			log.error("Failed to parse PayPal OAuth token response: ", e);
			throw new RuntimeException("Failed to retrieve PayPal access token", e);
		}
		
		log.info("Parsed OAuth token response: {}", token);
		
		if (token != null && token.getAccessToken() != null) {
			int ttlSeconds = token.getExpiresIn();
			int cacheTtl = Math.max(ttlSeconds - 60, 60);
			redisTemplate.opsForValue().set(REDIS_KEY, token.getAccessToken(), cacheTtl, TimeUnit.SECONDS);
			log.info("Cached access token in Redis with TTL: {} seconds", cacheTtl);
			return token.getAccessToken();
		}
		
		throw new RuntimeException("Paypal OAuth Token is null or empty");
	}

	@PostConstruct
	public void init() {
	    System.out.println("TEST KEY = " + testKey);
	}
}
