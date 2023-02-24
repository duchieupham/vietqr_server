package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankReceiveDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String bankTypeId;
	private String bankAccount;
	private String userBankName;
	private int type;


	public AccountBankReceiveDTO() {
		super();
	}


	public AccountBankReceiveDTO(String bankTypeId, String bankAccount, String userBankName, int type) {
		super();
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.type = type;
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


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

}
