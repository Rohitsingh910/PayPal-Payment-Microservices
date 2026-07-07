package com.mycomp.payments.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class PaypalProviderException extends RuntimeException {

	private static final long serialVersionUID = 1454052026533375959L;

	private final String errorCode;
    private final String errorMessage;
    
    private final HttpStatus httpStatus;

    public PaypalProviderException(
    		String errorCode, String errorMessage, HttpStatus httpStatus) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }
}
