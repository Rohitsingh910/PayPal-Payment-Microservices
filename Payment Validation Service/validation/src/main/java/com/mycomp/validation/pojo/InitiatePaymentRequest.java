package com.mycomp.validation.pojo;

import lombok.Data;

@Data
public class InitiatePaymentRequest {
	private String returnUrl; 
	private String cancelUrl; 
}
