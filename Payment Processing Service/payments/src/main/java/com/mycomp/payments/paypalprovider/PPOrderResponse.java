package com.mycomp.payments.paypalprovider;

import lombok.Data;

@Data
public class PPOrderResponse {
	
	private String orderId;
    private String paypalStatus;
    private String redirectUrl;

}
