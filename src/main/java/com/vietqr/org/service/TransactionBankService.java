package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface TransactionBankService {
	public int insertTransactionBank(String transactionid, long transactiontime, String referencenumber, int amount,
			String content, String bankaccount, String transType, String reciprocalAccount, String reciprocalBankCode,
			String va, long valueDate, String reftransactionid);

	public List<Object> checkTransactionIdInserted(String transactionid);

	public String checkExistedReferenceNumber(String referenceNumber);
}
