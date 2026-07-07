package com.mycomp.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.mycomp.payments.constant.ErrorCodeEnum;
import com.mycomp.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(PaypalProviderException.class)
    public ResponseEntity<ErrorResponse> handlePaypalException(
    		PaypalProviderException ex) {
        log.error("Handling PaypalProviderException: {}", ex.getErrorMessage(), ex);
		
		ErrorResponse error = new ErrorResponse(
        		ex.getErrorCode(), ex.getErrorMessage());
        
		return new ResponseEntity<>(error, ex.getHttpStatus());
    }
	
	// NoResourceFoundException
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
			NoResourceFoundException ex) {
		log.error("Handling NoResourceFoundException: {}", ex.getMessage(), ex);
		
		ErrorResponse error = new ErrorResponse(
				ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorCode(), 
				ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorMessage());
		
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); 
	}
	
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorResponse> handleHttpServiceException(HttpClientErrorException ex) {

	    log.error("Handling HttpServiceException: {}", ex.getMessage(), ex);
	    
	    ErrorResponse error = new ErrorResponse(
	            ErrorCodeEnum.ORDER_NOT_APPROVED.getErrorCode(),
	            ex.getMessage()
	    );
	    
	    return new ResponseEntity<>(error, ex.getStatusCode());
	     
	}
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
    		Exception ex) {
		log.error("Handling generic Exception: {}", ex.getMessage(), ex);
		
		ErrorResponse error = new ErrorResponse(
				ErrorCodeEnum.GENERIC_ERROR.getErrorCode(), 
				ex.getMessage());
		        
        
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
