package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionReceive")
public class TransactionReceiveEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id ;

	@Column(name = "bankAccount")
	private String bankAccount;

	@Column(name = "bankId")
	private String bankId;

	@Column(name = "content")
	private String content;

	@Column(name = "amount")
	private String amount;

	@Column(name = "time")
	private long time;

	@Column(name = "transactionBankId")
	private String transactionBankId;

	@Column(name = "transactionSmsId")
	private String transactionSmsId;

	@Column(name = "transType")
	private String transType;

	@Column(name = "status")
	private int status;

	public TransactionReceiveEntity() {
		super();
	}

	public TransactionReceiveEntity(String id, String bankAccount, String bankId, String content, String amount, long time,
			String transactionBankId, String transactionSmsId, String transType, int status) {
		super();
		this.id = id;
		this.bankAccount = bankAccount;
		this.bankId = bankId;
		this.content = content;
		this.amount = amount;
		this.time = time;
		this.transactionBankId = transactionBankId;
		this.transactionSmsId = transactionSmsId;
		this.transType = transType;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTransactionBankId() {
		return transactionBankId;
	}

	public void setTransactionBankId(String transactionBankId) {
		this.transactionBankId = transactionBankId;
	}

	public String getTransactionSmsId() {
		return transactionSmsId;
	}

	public void setTransactionSmsId(String transactionSmsId) {
		this.transactionSmsId = transactionSmsId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
