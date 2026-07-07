package com.mycomp.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mycomp.payments.constant.ErrorCodeEnum;
import com.mycomp.payments.dto.TransactionDto;
import com.mycomp.payments.exception.ProcessingServiceException;
import com.mycomp.payments.services.factory.PaymentStatusFactory;
import com.mycomp.payments.services.interfaces.TransactionStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusService {
	
	private final PaymentStatusFactory paymentStatusFactory;
	
	public TransactionDto processPayment(TransactionDto txnDto) {
		log.info("Processing payment status for txnDto: {}", txnDto);
		
		int statusId = txnDto.getTxnStatusId();
		
		TransactionStatusProcessor processor = 
				paymentStatusFactory.getStatusProcessor(statusId);
		
		if(processor == null) { 
			log.error("No TransactionStatusProcessor found for statusId: {}", 
					statusId);
			
			throw new ProcessingServiceException( 
					ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorCode(), 
					ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorMessage(), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		TransactionDto response = processor.processStatus(txnDto);
		log.info("Processed payment status with response: {}", response);
		
		return response;
	}

}
