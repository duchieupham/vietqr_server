package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionSms")
public class TransactionSmsEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "bankAccount")
	private String bankAccount;

	@Column(name = "bankId")
	private String bankId;

	@Column(name = "content")
	private String content;

	@Column(name = "address")
	private String address;

	@Column(name = "amount")
	private Long amount;

	@Column(name = "accountBalance")
	private Long accountBalance;

	@Column(name = "time")
	private long time;

	@Column(name = "timePaid")
	private long timePaid;

	@Column(name = "transType")
	private String transType;

	@Column(name = "referenceNumber")
	private String referenceNumber;

	@Column(name = "smsId")
	private String smsId;

	public TransactionSmsEntity() {
		super();
	}

	public TransactionSmsEntity(String id, String bankAccount, String bankId, String content, String address,
			Long amount, Long accountBalance, long time, long timePaid, String transType, String referenceNumber,
			String smsId) {
		this.id = id;
		this.bankAccount = bankAccount;
		this.bankId = bankId;
		this.content = content;
		this.address = address;
		this.amount = amount;
		this.accountBalance = accountBalance;
		this.time = time;
		this.timePaid = timePaid;
		this.transType = transType;
		this.referenceNumber = referenceNumber;
		this.smsId = smsId;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Long accountBalance) {
		this.accountBalance = accountBalance;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public long getTimePaid() {
		return timePaid;
	}

	public void setTimePaid(long timePaid) {
		this.timePaid = timePaid;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

}
