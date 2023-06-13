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
	private String branchId;
	private String businessId;
	private String userId;
	private String transType;
	private String customerBankAccount;
	private String customerBankCode;
	private String customerName;

	public VietQRCreateDTO() {
		super();
	}

	public VietQRCreateDTO(String bankId, String amount, String content, String branchId, String businessId,
			String userId) {
		super();
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.branchId = branchId;
		this.businessId = businessId;
		this.userId = userId;
	}

	public VietQRCreateDTO(String bankId, String amount, String content, String branchId, String businessId,
			String userId, String transType, String customerBankAccount, String customerBankCode, String customerName) {
		this.bankId = bankId;
		this.amount = amount;
		this.content = content;
		this.branchId = branchId;
		this.businessId = businessId;
		this.userId = userId;
		this.transType = transType;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
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

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
