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
public class ApprovedStatusProcessor implements TransactionStatusProcessor {
	
	private final ModelMapper modelMapper;

	private final TransactionDao transactionDao;

	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("Processing 'APPROVED' status for txnDto: {}", txnDto);
	
		
		// convert DTO to Entity
				TransactionEntity txnEntity = modelMapper.map(
						txnDto, TransactionEntity.class);

				transactionDao.updateTransaction(txnEntity);
				return txnDto;
	}

}
