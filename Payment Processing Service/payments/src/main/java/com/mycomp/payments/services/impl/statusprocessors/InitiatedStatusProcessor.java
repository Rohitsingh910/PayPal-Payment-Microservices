package com.mycomp.payments.services.impl.statusprocessors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mycomp.payments.dao.interfaces.TransactionDao;
import com.mycomp.payments.dto.TransactionDto;
import com.mycomp.payments.entity.TransactionEntity;
import com.mycomp.payments.services.interfaces.TransactionStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitiatedStatusProcessor implements TransactionStatusProcessor {
	
	private final TransactionDao transactionDao;
	
	private final ModelMapper modelMapper;

	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("Processing 'INITIATED' status for txnDto: {}", txnDto);
		
		// convert DTO to Entity
		TransactionEntity txnEntity = modelMapper.map(
				txnDto, TransactionEntity.class);
		
		transactionDao.updateTransaction(txnEntity);
		log.info("Updated TransactionEntity in DB for 'INITIATED' "
				+ "status: {}", txnEntity);
		
		return txnDto;
	}

}
