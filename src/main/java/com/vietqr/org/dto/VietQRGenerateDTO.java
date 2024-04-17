package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRGenerateDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String caiValue;
	private String amount;
	private String content;
	private String bankAccount;

	public VietQRGenerateDTO() {
		super();
	}

	public VietQRGenerateDTO(String caiValue, String amount, String content, String bankAccount) {
		super();
		this.caiValue = caiValue;
		this.amount = amount;
		this.content = content;
		this.bankAccount = bankAccount;
	}

	public String getCaiValue() {
		return caiValue;
	}

	public void setCaiValue(String caiValue) {
		this.caiValue = caiValue;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

}
