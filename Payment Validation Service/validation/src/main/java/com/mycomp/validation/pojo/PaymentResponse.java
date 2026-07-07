package com.mycomp.validation.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

	private String txnReference;
	private int txnStatusId;
	
	private String redirectUrl;
	private String providerReference;
}
