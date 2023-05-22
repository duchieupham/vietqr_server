package com.vietqr.org.dto;

import java.io.Serializable;

public class BankTypeDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String bankCode;
	private String bankName;
	private String imageId;
	private int status;
	private String caiValue;

	public BankTypeDTO() {
		super();
	}

	public BankTypeDTO(String id, String bankCode, String bankName, String imageId, int status, String caiValue) {
		super();
		this.id = id;
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.imageId = imageId;
		this.status = status;
		this.caiValue = caiValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCaiValue() {
		return caiValue;
	}

	public void setCaiValue(String caiValue) {
		this.caiValue = caiValue;
	}

}
