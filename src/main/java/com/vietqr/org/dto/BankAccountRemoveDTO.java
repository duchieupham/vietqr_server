package com.vietqr.org.dto;

import java.io.Serializable;

public class BankAccountRemoveDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String bankId;
	private int role;
	private String userId;

	public BankAccountRemoveDTO() {
		super();
	}

	public BankAccountRemoveDTO(String bankId, int role, String userId) {
		super();
		this.bankId = bankId;
		this.role = role;
		this.userId = userId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
