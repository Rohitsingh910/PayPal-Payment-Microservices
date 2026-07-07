package com.mycomp.payments.service.helper;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mycomp.payments.constant.Constant;
import com.mycomp.payments.constant.ErrorCodeEnum;
import com.mycomp.payments.exception.PaypalProviderException;
import com.mycomp.payments.http.HttpRequest;
import com.mycomp.payments.paypal.res.PaypalOrderRes;
import com.mycomp.payments.paypal.res.error.PaypalErrorResponse;
import com.mycomp.payments.pojo.OrderResponse;
import com.mycomp.payments.util.JsonUtil;
import com.mycomp.payments.util.PaypalOrderUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptureOrderHelper {

	private static final String ORDER_ID_REF = "{orderId}";

	private final JsonUtil jsonUtil;

	@Value("${paypal.capture.order.url}")
	private String captureOrderUrlTemplate;

	public HttpRequest prepareCaptureOrderHttpRequest(
			String orderId, String accessToken) {
		log.info("Preparing capture order HttpRequest "
				+ "|| orderId: {}, accessToken: {}",
				orderId, accessToken);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		// set header PayPal-Request-Id => UUID
		String uuid = UUID.randomUUID().toString();
		log.info("Generated UUID for PayPal-Request-Id: {}", uuid);

		headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);

		String requestAsJson = "";
		
		String captureOrderUrl = captureOrderUrlTemplate.replace(ORDER_ID_REF, orderId);
		log.info("Prepared capture order URL: {}", captureOrderUrl);
		
		// create HttpRequest
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(captureOrderUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(requestAsJson);
		
		log.info("Prepared HttpRequest for capture order: {}", httpRequest);
		return httpRequest;
	}

	
	public OrderResponse toOrderResponse(PaypalOrderRes paypalOrder) {
		log.info("Converting PaypalOrder to OrderResponse: {}", paypalOrder);
		
	    OrderResponse response = new OrderResponse();
	    response.setOrderId(paypalOrder.getId());
	    response.setPaypalStatus(paypalOrder.getStatus());
	    	    
	    log.info("Converted PaypalOrder to OrderResponse: {}", response);
	    return response;
	}
	
	public OrderResponse handlePaypalResponse(ResponseEntity<String> httpResponse) {
		log.info("Handling PayPal response in PaymentServiceImpl "
				+ "httpResponse:{}", httpResponse);
		
		if(httpResponse.getStatusCode().is2xxSuccessful()) { //success

			PaypalOrderRes paypalOrder = jsonUtil.fromJson(
					httpResponse.getBody(), PaypalOrderRes.class);
			log.info("Converted response body to PaypalOrder: {}", paypalOrder);
			
			OrderResponse orderResponse = toOrderResponse(paypalOrder);
			log.info("Converted OrderResponse: {}", orderResponse);
			
			if(orderResponse != null 
					&& orderResponse.getOrderId() != null
					&& !orderResponse.getOrderId().isEmpty()
					&& orderResponse.getPaypalStatus() != null
					&& !orderResponse.getPaypalStatus().isEmpty()) {
				log.info("Order capture successful. "
						+ "orderResponse: {}", orderResponse);
				return orderResponse;
			}
			
			log.error("Order creation failed or incomplete details received. "
					+ "orderResponse: {}", orderResponse);
		}
		
		// if 4xx or 5xx then proper error
		if(httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Received 4xx, 5xx error response from PayPal service");
			
			PaypalErrorResponse paypalErrorRes = jsonUtil.fromJson(
					httpResponse.getBody(), PaypalErrorResponse.class);
			log.info("PayPal error response details: {}", paypalErrorRes);
			
			String errorCode = ErrorCodeEnum.PAYPAL_ERROR.getErrorCode();
			String errorMessage = PaypalOrderUtil.getPaypalErrorSummary(
					paypalErrorRes);
			log.info("Generated PayPal error summary: {}", errorMessage);
			
			throw new PaypalProviderException(
					errorCode,
					errorMessage,
					HttpStatus.valueOf(
							httpResponse.getStatusCode().value()));
		}
		

		log.error("Unexpected response from PayPal service. "
				+ "httpResponse: {}", httpResponse);
		
		throw new PaypalProviderException(
				ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.BAD_GATEWAY);
	}

}
