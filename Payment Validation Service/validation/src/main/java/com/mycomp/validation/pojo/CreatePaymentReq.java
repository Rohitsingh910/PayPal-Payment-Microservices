package com.mycomp.validation.pojo;

import lombok.Data;

@Data
public class CreatePaymentReq {

	 private int userId;					 
	    private int paymentMethodId;         
	    private int providerId;              
	    private int paymentTypeId;           

	    private double amount;               
	    private String currency;             

	    private String merchantTransactionReference; 
}