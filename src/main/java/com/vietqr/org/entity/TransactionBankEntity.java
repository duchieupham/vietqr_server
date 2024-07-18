package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionBank")
public class TransactionBankEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id ;

	@Column(name = "transactionid")
	private String transactionid;

	@Column(name = "transactiontime")
	private long transactiontime;

	@Column(name = "referencenumber")
	private String referencenumber;

	@Column(name = "amount")
	private int amount;

	@Column(name = "content")
	private String content;

	@Column(name = "bankaccount")
	private String bankaccount;

	@Column(name = "transType")
	private String transType;

	@Column(name = "reciprocalAccount")
	private String reciprocalAccount;

	@Column(name = "reciprocalBankCode")
	private String reciprocalBankCode;

	@Column(name = "va")
	private String va;

	@Column(name = "valueDate")
	private long valueDate;

	public TransactionBankEntity() {
		super();
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public long getTransactiontime() {
		return transactiontime;
	}

	public void setTransactiontime(long transactiontime) {
		this.transactiontime = transactiontime;
	}

	public String getReferencenumber() {
		return referencenumber;
	}

	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getReciprocalAccount() {
		return reciprocalAccount;
	}

	public void setReciprocalAccount(String reciprocalAccount) {
		this.reciprocalAccount = reciprocalAccount;
	}

	public String getReciprocalBankCode() {
		return reciprocalBankCode;
	}

	public void setReciprocalBankCode(String reciprocalBankCode) {
		this.reciprocalBankCode = reciprocalBankCode;
	}

	public String getVa() {
		return va;
	}

	public void setVa(String va) {
		this.va = va;
	}

	public long getValueDate() {
		return valueDate;
	}

	public void setValueDate(long valueDate) {
		this.valueDate = valueDate;
	}

}
