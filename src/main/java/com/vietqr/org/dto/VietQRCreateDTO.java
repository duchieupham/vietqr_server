package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRCreateDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String bankId;
	private String amount;
	private String content;
	private String userId;
	private String transType;
	private String customerBankAccount;
	private String customerBankCode;
	private String customerName;
	private String terminalCode;
	private String orderId;
	private String note;

	public VietQRCreateDTO() {
		super();
	}

	public VietQRCreateDTO(String bankId, String amount, String content,
			String userId) {
		super();
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.userId = userId;
	}

	public VietQRCreateDTO(String bankId, String amount, String content,
			String userId, String terminalCode, String note) {
		super();
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.userId = userId;
		this.terminalCode = terminalCode;
		this.note = note;
	}

	public VietQRCreateDTO(String bankId, String amount, String content,
			String userId, String transType, String customerBankAccount, String customerBankCode, String customerName) {
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.userId = userId;
		this.transType = transType;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
	}

	public VietQRCreateDTO(String bankId, String amount, String content,
			String userId, String transType, String customerBankAccount, String customerBankCode, String customerName,
			String note) {
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.userId = userId;
		this.transType = transType;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
		this.note = note;
	}

	public VietQRCreateDTO(String bankId, String amount, String content,
			String userId, String transType, String customerBankAccount, String customerBankCode, String customerName,
			String terminalCode, String note) {
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.userId = userId;
		this.transType = transType;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
		this.terminalCode = terminalCode;
		this.note = note;
	}

	public VietQRCreateDTO(String bankId, String amount, String content,
			String userId, String transType, String customerBankAccount, String customerBankCode, String customerName,
			String terminalCode, String orderId, String note) {
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.userId = userId;
		this.transType = transType;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
		this.terminalCode = terminalCode;
		this.orderId = orderId;
		this.note = note;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getCustomerBankAccount() {
		return customerBankAccount;
	}

	public void setCustomerBankAccount(String customerBankAccount) {
		this.customerBankAccount = customerBankAccount;
	}

	public String getCustomerBankCode() {
		return customerBankCode;
	}

	public void setCustomerBankCode(String customerBankCode) {
		this.customerBankCode = customerBankCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTerminalCode() {
		return terminalCode;
	}

	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
