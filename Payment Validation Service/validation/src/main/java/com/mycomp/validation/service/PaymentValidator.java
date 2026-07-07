package com.mycomp.validation.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mycomp.validation.constant.ErrorCodeEnum;
import com.mycomp.validation.exception.ValidationException;
import com.mycomp.validation.pojo.CreatePaymentReq;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentValidator {
	
	/**
	 * This method checks incoming CreateOrderReq for null/invalid values.
	 * If there is any validation error, it throws PaypalProviderException
	 * if no error, then just runs till the end. So void return type.
	 * @param createOrderReq
	 */
	public void validateCreateOrder(CreatePaymentReq createPaymentReq) {
		log.info("Validating create payment request: {}", createPaymentReq);
		
		if (createPaymentReq == null) {
			log.error("Create order request is null");
			
			throw new ValidationException(
					ErrorCodeEnum.INVALID_REQUEST.getErrorCode(), 
					ErrorCodeEnum.INVALID_REQUEST.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		
		//validate userId
		if (createPaymentReq.getUserId() <=0) {
			log.error("User ID is required in create order request");
			
			throw new ValidationException(
					ErrorCodeEnum.USER_ID_REQUIRED.getErrorCode(), 
					ErrorCodeEnum.USER_ID_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		if (createPaymentReq.getPaymentMethodId()
				!=1) {
			log.error("Not a valid payment method");
			
			throw new ValidationException(
					ErrorCodeEnum.PAYMENT_METHOD_REQUIRED.getErrorCode(), 
					ErrorCodeEnum.PAYMENT_METHOD_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		//validate providerId
		if (createPaymentReq.getProviderId() != 1
				) {
			log.error("Not a valid provider");
			
			throw new ValidationException(
					ErrorCodeEnum.PROVIDER_REQUIRED.getErrorCode(), 
					ErrorCodeEnum.PROVIDER_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		//validate paymentTypeId
		if (createPaymentReq.getPaymentTypeId() != 1) {
			log.error("Not a valid payment type");
			
			throw new ValidationException(
					ErrorCodeEnum.PAYMENT_TYPE_REQUIRED.getErrorCode(), 
					ErrorCodeEnum.PAYMENT_TYPE_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}	
		
		if (createPaymentReq.getAmount() <= 0) {
			log.error("Invalid amount in create order request: {}", 
					createPaymentReq.getAmount());
			
			throw new ValidationException(
					ErrorCodeEnum.INVALID_AMOUNT.getErrorCode(), 
					ErrorCodeEnum.INVALID_AMOUNT.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		//validate currency
		if (createPaymentReq.getCurrency() == null 
				|| createPaymentReq.getCurrency().isBlank()) {
			log.error("Currency code is required in create order request");
			
			throw new ValidationException(
					ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getErrorCode(), 
					ErrorCodeEnum.CURRENCY_CODE_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		//validate merchantTransactionReference
		if (createPaymentReq.getMerchantTransactionReference() == null 
				|| createPaymentReq.getMerchantTransactionReference().isBlank()) {
			log.error("Merchant transaction reference is required in create order request");
			
			throw new ValidationException(
					ErrorCodeEnum.MERCHANT_TXN_REF_REQUIRED.getErrorCode(), 
					ErrorCodeEnum.MERCHANT_TXN_REF_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		log.info("Create payment request validation passed");
	}

}
