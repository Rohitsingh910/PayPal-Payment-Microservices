package com.mycomp.validation.processing;

import lombok.Data;

@Data
public class ProcessingCreatePaymentReq {

	 private int userId;					 
	    private int paymentMethodId;         
	    private int providerId;              
	    private int paymentTypeId;           

	    private double amount;               
	    private String currency;             

	    private String merchantTransactionReference; 
}