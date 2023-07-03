package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.TransactionCheckDTO;
import com.vietqr.org.repository.TransactionBankRepository;

@Service
public class TransactionBankServiceImpl implements TransactionBankService {

	@Autowired
	TransactionBankRepository transactionBankRepo;

	@Override
	public int insertTransactionBank(String transactionid, long transactiontime, String referencenumber, int amount,
			String content, String bankaccount, String transType, String reciprocalAccount, String reciprocalBankCode,
			String va, long valueDate, String reftransactionid) {
		return transactionBankRepo.insertTransactionBank(transactionid, transactiontime, referencenumber, amount,
				content, bankaccount, transType, reciprocalAccount, reciprocalBankCode, va, valueDate,
				reftransactionid);
	}

	@Override
	public List<Object> checkTransactionIdInserted(String transactionid, String transType) {
		return transactionBankRepo.checkTransactionIdInserted(transactionid, transType);
	}

	@Override
	public String checkExistedReferenceNumber(String referenceNumber, String transType) {
		return (transType != null && transType.trim().toUpperCase().equals("D"))
				? transactionBankRepo.checkExistedReferenceNumberTypeD(referenceNumber)
				: transactionBankRepo.checkExistedReferenceNumber(referenceNumber);
	}

	@Override
	public List<TransactionCheckDTO> getTransactionsCheck(String fromDate, String toDate, String bankAccount) {
		String fromDateString = fromDate + " " + "00:00:00";
		String toDateString = toDate + " " + "23:59:59";
		return transactionBankRepo.getTransactionsCheck(fromDateString, toDateString, bankAccount);
	}

}
