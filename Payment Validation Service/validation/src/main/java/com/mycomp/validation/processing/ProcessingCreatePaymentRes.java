package com.mycomp.validation.processing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycomp.validation.pojo.PaymentResponse;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingCreatePaymentRes {
	private String txnReference;
	private int txnStatusId;
	
	private String redirectUrl;
	private String providerReference;
}
