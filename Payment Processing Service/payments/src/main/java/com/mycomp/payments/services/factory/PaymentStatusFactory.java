package com.mycomp.payments.services.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.mycomp.payments.services.impl.statusprocessors.ApprovedStatusProcessor;
import com.mycomp.payments.services.impl.statusprocessors.CreatedStatusProcessor;
import com.mycomp.payments.services.impl.statusprocessors.FailedStatusProcessor;
import com.mycomp.payments.services.impl.statusprocessors.InitiatedStatusProcessor;
import com.mycomp.payments.services.impl.statusprocessors.PendingStatusProcessor;
import com.mycomp.payments.services.impl.statusprocessors.SuccessStatusProcessor;
import com.mycomp.payments.services.interfaces.TransactionStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusFactory {
	
	private final ApplicationContext applicationContext;
	
	public TransactionStatusProcessor getStatusProcessor(int statusId) {
		log.info("Getting status processor for statusId: {}", statusId);
		
		//switch based on statusId. If statusId = 1, return CreatedStatusProcessor object.
		switch (statusId) {
		case 1:
			log.info("Returning CreatedStatusProcessor for statusId: {}", statusId);
			return applicationContext.getBean(CreatedStatusProcessor.class);
		case 2:
			log.info("Returning InitiatedStatusProcessor for statusId: {}", statusId);
			return applicationContext.getBean(InitiatedStatusProcessor.class);
			
		case 3:
			log.info("Returning PendingStatusProcessor for statusId: {}", statusId);
			return applicationContext.getBean(PendingStatusProcessor.class);
		
		case 4:
			log.info("Returning ApprovedStatusProcessor for statusId: {}", statusId);
			return applicationContext.getBean(ApprovedStatusProcessor.class);
			
		case 5:
			log.info("Returning SuccessStatusProcessor for statusId: {}", statusId);
			return applicationContext.getBean(SuccessStatusProcessor.class);
			
		case 6:
			log.info("Returning FailedStatusProcessor for statusId: {}", statusId);
			return applicationContext.getBean(FailedStatusProcessor.class);
		
		default:
			log.warn("No processor found for statusId: {}", statusId);
			return null;
		}
		
	}

}
