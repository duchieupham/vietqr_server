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
	private String bankShortName;
	private String imageId;
	private int status;
	private String caiValue;
	private int unlinkedType;

	public BankTypeDTO() {
		super();
	}

	public BankTypeDTO(String id, String bankCode, String bankName, String bankShortName, String imageId, int status,
			String caiValue, int unlinkedType) {
		super();
		this.id = id;
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.bankShortName = bankShortName;
		this.imageId = imageId;
		this.status = status;
		this.caiValue = caiValue;
		this.unlinkedType = unlinkedType;
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

	public String getBankShortName() {
		return bankShortName;
	}

	public void setBankShortName(String bankShortName) {
		this.bankShortName = bankShortName;
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

	public int getUnlinkedType() {
		return unlinkedType;
	}

	public void setUnlinkedType(int unlinkedType) {
		this.unlinkedType = unlinkedType;
	}

}
