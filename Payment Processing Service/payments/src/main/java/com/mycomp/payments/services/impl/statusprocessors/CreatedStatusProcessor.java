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
public class CreatedStatusProcessor implements TransactionStatusProcessor {
	
	private final ModelMapper modelMapper;
	
	private final TransactionDao transactionDao;

	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("Processing 'CREATED' status for txnDto: {}", txnDto);
		
		TransactionEntity txnEntity = modelMapper.map(txnDto, TransactionEntity.class);
		log.info("Mapped TransactionEntity: {}", txnEntity);
		
		TransactionEntity responseEntity = transactionDao.createTransaction(txnEntity);
		log.info("Created TransactionEntity in DB: {}", responseEntity);
		
		txnDto.setId(responseEntity.getId());
		
		log.info("Updated TransactionDto with ID: {}", txnDto);
		return txnDto;
	}

}
