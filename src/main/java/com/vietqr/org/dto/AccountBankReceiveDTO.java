package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class AccountBankReceiveDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank
	private String bankTypeId;
	@NotBlank
	private String bankAccount;
	@NotBlank
	private String userBankName;
	@NotBlank
	private String userId;
	@NotBlank
	private String nationalId;
	@NotBlank
	private String phoneAuthenticated;
	// for BIDV
	private String ewalletToken;

	public AccountBankReceiveDTO() {
		super();
	}

	public AccountBankReceiveDTO(String bankTypeId, String bankAccount, String userBankName, String userId,
			String nationalId, String phoneAuthenticated) {
		super();
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.userId = userId;
		this.nationalId = nationalId;
		this.phoneAuthenticated = phoneAuthenticated;
	}

	public AccountBankReceiveDTO(String bankTypeId, String bankAccount, String userBankName, String userId,
			String nationalId, String phoneAuthenticated, String ewalletToken) {
		super();
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.userId = userId;
		this.nationalId = nationalId;
		this.phoneAuthenticated = phoneAuthenticated;
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

	public String getEwalletToken() {
		return ewalletToken;
	}

	public void setEwalletToken(String ewalletToken) {
		this.ewalletToken = ewalletToken;
	}

}
