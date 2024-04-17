package com.vietqr.org.dto;

import java.io.Serializable;

public class CaiBankDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String bankCode;
	private String caiValue;

	public CaiBankDTO() {
		super();
	}

	public CaiBankDTO(String bankCode, String caiValue) {
		super();
		this.bankCode = bankCode;
		this.caiValue = caiValue;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCaiValue() {
		return caiValue;
	}

	public void setCaiValue(String caiValue) {
		this.caiValue = caiValue;
	}

}
