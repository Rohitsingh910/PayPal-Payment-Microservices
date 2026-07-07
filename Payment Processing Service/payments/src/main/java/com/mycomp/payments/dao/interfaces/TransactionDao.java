package com.mycomp.payments.dao.interfaces;

import com.mycomp.payments.entity.TransactionEntity;

public interface TransactionDao {
	
	public TransactionEntity createTransaction(TransactionEntity transaction);
	public TransactionEntity getTransactionByTxnReference(String txnReference);
	
	public void updateTransaction(TransactionEntity transaction);

}
