package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionBankEntity;

@Repository
public interface TransactionBankRepository extends JpaRepository<TransactionBankEntity, Long> {

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO transaction_bank(transactionid, transactiontime, referencenumber, amount, content, bankaccount, trans_type, reciprocal_account, reciprocal_bank_code, va, value_date, id) "
			+ " VALUES(:transactionid, :transactiontime, :referencenumber, :amount, :content, :bankaccount, :transType, :reciprocalAccount, :reciprocalBankCode, :va, :valueDate, :id)", nativeQuery = true)
	int insertTransactionBank(@Param(value = "transactionid") String transactionid,
			@Param(value = "transactiontime") long transactiontime,
			@Param(value = "referencenumber") String referencenumber, @Param(value = "amount") int amount,
			@Param(value = "content") String content, @Param(value = "bankaccount") String bankaccount,
			@Param(value = "transType") String transType, @Param(value = "reciprocalAccount") String reciprocalAccount,
			@Param(value = "reciprocalBankCode") String reciprocalBankCode, @Param(value = "va") String va,
			@Param(value = "valueDate") long valueDate, @Param(value = "id") String id);

	@Query(value = "SELECT transactionid FROM transaction_bank WHERE transactionid = :transactionid", nativeQuery = true)
	List<Object> checkTransactionIdInserted(@Param(value = "transactionid") String transactionid);

	@Query(value = "SELECT referencenumber FROM transaction_bank WHERE referencenumber = :referenceNumber", nativeQuery = true)
	String checkExistedReferenceNumber(@Param(value = "referenceNumber") String referenceNumber);
}
