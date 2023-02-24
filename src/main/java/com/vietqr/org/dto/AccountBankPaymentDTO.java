package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankPaymentDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String bankTypeId;
	private String bankAccount;
	private String userBankName;
	private String dateOpen;
	private String phoneOtp;
	private int role;


	public AccountBankPaymentDTO() {
		super();
	}


	public AccountBankPaymentDTO(String userId, String bankTypeId, String bankAccount, String dateOpen, String phoneOtp, String userBankName, int role) {
		super();
		this.userId = userId;
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.dateOpen = dateOpen;
		this.phoneOtp = phoneOtp;
		this.role = role;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getBankTypeId() {
		return bankTypeId;
	}


	public void setBankTypeId(String bankTypeId) {
		this.bankTypeId = bankTypeId;
	}


	public String getUserBankName() {
		return userBankName;
	}


	public void setUserBankName(String userBankName) {
		this.userBankName = userBankName;
	}


	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getDateOpen() {
		return dateOpen;
	}

	public void setDateOpen(String dateOpen) {
		this.dateOpen = dateOpen;
	}

	public String getPhoneOtp() {
		return phoneOtp;
	}

	public void setPhoneOtp(String phoneOtp) {
		this.phoneOtp = phoneOtp;
	}

}
