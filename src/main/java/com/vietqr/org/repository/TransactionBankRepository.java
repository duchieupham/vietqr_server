package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.TransactionCheckDTO;
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

	@Query(value = "SELECT transactionid FROM transaction_bank WHERE transactionid = :transactionid AND trans_type = :transType", nativeQuery = true)
	List<Object> checkTransactionIdInserted(@Param(value = "transactionid") String transactionid,
			@Param(value = "transType") String transType);

	@Query(value = "SELECT "
			+ "CASE "
			+ "WHEN EXISTS(SELECT 1 FROM transaction_bank WHERE referencenumber = :referenceNumber AND trans_type = 'C' ) OR "
			+ "EXISTS(SELECT 1 FROM transactionmms WHERE ft_code = :referenceNumber) "
			+ "THEN :referenceNumber "
			+ "ELSE '' "
			+ "END AS referencenumber", nativeQuery = true)
	String checkExistedReferenceNumber(@Param(value = "referenceNumber") String referenceNumber);

	@Query(value = "SELECT referencenumber FROM transaction_bank WHERE referencenumber = :referenceNumber AND trans_type = 'D'", nativeQuery = true)
	String checkExistedReferenceNumberTypeD(@Param(value = "referenceNumber") String referenceNumber);

	@Query(value = "SELECT transactionid as transactionId, referencenumber as referenceNumber, bankaccount as bankAccount, amount, trans_type as transType, content, "
			+ "DATE_FORMAT(CONVERT_TZ(FROM_UNIXTIME(transactiontime/1000), '+00:00', '+07:00'), '%Y-%m-%d %H:%i:%s') AS timeReceived "
			+ "FROM transaction_bank "
			+ "WHERE CONVERT_TZ(FROM_UNIXTIME(transactiontime/1000), '+00:00', '+07:00') "
			+ "BETWEEN :fromDate AND :toDate AND bankaccount = :bankAccount "
			+ "ORDER BY transactiontime DESC", nativeQuery = true)
	List<TransactionCheckDTO> getTransactionsCheck(@Param(value = "fromDate") String fromDate,
			@Param(value = "toDate") String toDate, @Param(value = "bankAccount") String bankAccount);
}
