package com.mycomp.validation.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("10000", "Something went wrong. Please try again later."),
    CURRENCY_CODE_REQUIRED("10001", "Currency code is required field and cannot be null/blank"),
    
    INVALID_REQUEST("10002", "Invalid request payload"), 
    INVALID_AMOUNT("10003", "Amount must be greater than zero"),  
    RESOURCE_NOT_FOUND("10004", "Invalid URL. Please check and try again."),
	ORDER_NOT_APPROVED("10005","Order request is not approved by user.go to the redirect url and click on complete payment"), 
	USER_ID_REQUIRED("10006","User ID is required field and cannot be null/blank"), 
	PAYMENT_METHOD_REQUIRED("10007","payment method is required field and cannot be null/blank"), 
	PROVIDER_REQUIRED("10008","payment provider is required field and cannot be null/blank"), 
	PAYMENT_TYPE_REQUIRED("10009","payment type is required field and cannot be null/blank"),
	MERCHANT_TXN_REF_REQUIRED("10010","Merchant transaction reference is required field and cannot be null/blank"),
	PAYMENT_PROCESSING_SERVICE_UNAVAILABLE("10011","Processing service is currently unavailable. Please try again later."),
	PROCESSING_UNKNOWN_ERROR("10012", "Unknown error occurred while processing request in processing service");

    private final String errorCode;
    private final String errorMessage;

    ErrorCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}