package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankResponseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String bankAccount;
	private String userBankName;
	private String bankCode;
	private String bankName;
	private String imgId;
	private int type;
	// for business bank
	private String branchId;
	private String businessId;
	private String branchName;
	private String businessName;
	private String branchCode;
	private String businessCode;

	public AccountBankResponseDTO() {
		super();
	}

	public AccountBankResponseDTO(String id, String bankAccount, String userBankName, String bankCode, String bankName,
			String imgId, int type, String branchId, String businessId, String branchName, String businessName,
			String branchCode,
			String businessCode) {
		this.id = id;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.imgId = imgId;
		this.type = type;
		this.branchId = branchId;
		this.businessId = businessId;
		this.branchName = branchName;
		this.businessName = businessName;
		this.branchCode = branchCode;
		this.businessCode = businessCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserBankName() {
		return userBankName;
	}

	public void setUserBankName(String userBankName) {
		this.userBankName = userBankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
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

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

}
