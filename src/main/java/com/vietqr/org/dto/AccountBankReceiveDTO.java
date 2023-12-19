package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankReceiveDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String bankTypeId;
	private String bankAccount;
	private String userBankName;
	private String userId;
	private int type;
	private String nationalId;
	private String phoneAuthenticated;
	// for business
	private String branchId;
	// for BIDV
	private String ewalletToken;

	public AccountBankReceiveDTO() {
		super();
	}

	public AccountBankReceiveDTO(String bankTypeId, String bankAccount, String userBankName, String userId,
			String nationalId, String phoneAuthenticated, int type,
			String branchId) {
		super();
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.userId = userId;
		this.nationalId = nationalId;
		this.phoneAuthenticated = phoneAuthenticated;
		this.type = type;
		this.branchId = branchId;
	}

	public AccountBankReceiveDTO(String bankTypeId, String bankAccount, String userBankName, String userId,
			String nationalId, String phoneAuthenticated, int type,
			String branchId, String ewalletToken) {
		super();
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.userId = userId;
		this.nationalId = nationalId;
		this.phoneAuthenticated = phoneAuthenticated;
		this.type = type;
		this.branchId = branchId;
		this.ewalletToken = ewalletToken;
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

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getPhoneAuthenticated() {
		return phoneAuthenticated;
	}

	public void setPhoneAuthenticated(String phoneAuthenticated) {
		this.phoneAuthenticated = phoneAuthenticated;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getEwalletToken() {
		return ewalletToken;
	}

	public void setEwalletToken(String ewalletToken) {
		this.ewalletToken = ewalletToken;
	}

}
