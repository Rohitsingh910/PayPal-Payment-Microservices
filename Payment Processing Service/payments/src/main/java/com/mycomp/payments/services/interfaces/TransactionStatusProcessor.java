package com.mycomp.payments.services.interfaces;

import com.mycomp.payments.dto.TransactionDto;

public interface TransactionStatusProcessor {
	
	public TransactionDto processStatus(TransactionDto txnDto);

}
