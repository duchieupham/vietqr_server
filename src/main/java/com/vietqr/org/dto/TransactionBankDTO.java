package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionBankDTO implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String transactionid;
	private long transactiontime;
	private String referencenumber;
	private int amount;
	private String content;
	private String bankaccount;

	// C or D
	private String transType;
	private String reciprocalAccount;
	private String reciprocalBankCode;
	private String va;
	private long valueDate;

	public TransactionBankDTO() {
		super();
	}

	public TransactionBankDTO(String transactionid, int transactiontime, String referencenumber, int amount,
			String content, String bankaccount, String transType, String reciprocalAccount, String reciprocalBankCode,
			String va, int valueDate) {
		super();
		this.transactionid = transactionid;
		this.transactiontime = transactiontime;
		this.referencenumber = referencenumber;
		this.amount = amount;
		this.content = content;
		this.bankaccount = bankaccount;
		this.transType = transType;
		this.reciprocalAccount = reciprocalAccount;
		this.reciprocalBankCode = reciprocalBankCode;
		this.va = va;
		this.valueDate = valueDate;
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

	@Override
	public String toString() {
		return "TransactionBankDTO [transactionid=" + transactionid + ", transactiontime=" + transactiontime
				+ ", referencenumber=" + referencenumber + ", amount=" + amount + ", content=" + content
				+ ", bankaccount=" + bankaccount + ", transType=" + transType + ", reciprocalAccount="
				+ reciprocalAccount + ", reciprocalBankCode=" + reciprocalBankCode + ", va=" + va + ", valueDate="
				+ valueDate + "]";
	}

}
