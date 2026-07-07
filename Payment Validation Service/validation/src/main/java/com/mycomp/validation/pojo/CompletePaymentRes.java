package com.mycomp.validation.pojo;

import lombok.Data;

@Data
public class CompletePaymentRes {

	private String txnReference;
	private int txnStatusId;
	
}
