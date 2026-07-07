package com.mycomp.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

	RESOURCE_NOT_FOUND("20001", "Invalid URL. Please check and try again."),
	GENERIC_ERROR("20000", "Something went wrong. Please try again later."),
	PAYPAL_PROVIDER_SERVICE_UNAVAILABLE("20002", "paypal-provider service is currently unavailable. Please try again later."),
	PAYPAL_PROVIDER_UNKNOWN_ERROR("20003","Unknown error occured in paypal-provider service. Please try again later."),
	ERROR_UPDATING_TRANSACTION("20004","Error occurred while updating transaction status."), 
	NO_STATUS_PROCESSOR_FOUND("20005","No processor found for handling transaction status update."); 
	
	 private final String errorCode;
	 private final String errorMessage;

	 ErrorCodeEnum(String errorCode, String errorMessage) {
	        this.errorCode = errorCode;
	        this.errorMessage = errorMessage;
	 }       

}
