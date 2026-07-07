package com.mycomp.payments.service.interfaces;

import com.mycomp.payments.pojo.CreateOrderReq;
import com.mycomp.payments.pojo.OrderResponse;

public interface PaymentService {
	
	public OrderResponse createOrder(CreateOrderReq createOrderReq);

	public OrderResponse captureOrder(String orderId);

}
