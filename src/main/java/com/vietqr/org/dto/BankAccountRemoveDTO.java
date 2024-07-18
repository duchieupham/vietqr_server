package com.vietqr.org.dto;

import java.io.Serializable;

public class BankAccountRemoveDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String bankId;
	private boolean authenticated;

	public BankAccountRemoveDTO() {
		super();
	}

	public BankAccountRemoveDTO(String bankId, int type, boolean authenticated) {
		super();
		this.bankId = bankId;
		this.authenticated = authenticated;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

}
