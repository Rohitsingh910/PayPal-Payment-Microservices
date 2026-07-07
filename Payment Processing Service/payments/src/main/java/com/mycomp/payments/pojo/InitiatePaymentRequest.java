package com.mycomp.payments.pojo;

import lombok.Data;

@Data
public class InitiatePaymentRequest {
	private String returnUrl; 
	private String cancelUrl; 
}
