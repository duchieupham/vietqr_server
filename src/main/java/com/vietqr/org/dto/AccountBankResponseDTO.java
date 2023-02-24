package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankResponseDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String userId;
	private String bankAccount;
	private String userBankName;
	private String bankCode;
	private String bankName;
	private String imgId;
	private int bankStatus;
	private int role;

	public AccountBankResponseDTO() {
		super();
	}
	public AccountBankResponseDTO(String id, String userId, String bankAccount, String userBankName, String bankCode,
			String bankName, String imgId, int bankStatus, int role) {
		super();
		this.id = id;
		this.userId = userId;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.imgId = imgId;
		this.bankStatus = bankStatus;
		this.role = role;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public int getBankStatus() {
		return bankStatus;
	}
	public void setBankStatus(int bankStatus) {
		this.bankStatus = bankStatus;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getImgId() {
		return imgId;
	}
	public void setImgId(String imgId) {
		this.imgId = imgId;
	}
}
