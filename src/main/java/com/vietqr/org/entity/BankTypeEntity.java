package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BankType")
public class BankTypeEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "bankCode")
	private String bankCode;

	@Column(name = "bankName", columnDefinition = "TEXT")
	private String bankName;

	@Column(name = "bankShortName", columnDefinition = "TEXT")
	private String bankShortName;

	@Column(name = "imgId")
	private String imgId;

	@Column(name = "swiftCode")
	private String swiftCode;

	@Column(name = "status")
	private int status;

	@Column(name = "rpaContainId")
	private boolean rpaContainId;

	@Column(name = "unlinkedType")
	private int unlinkedType;

	public BankTypeEntity() {
		super();
	}

	public BankTypeEntity(String id, String bankCode, String bankName, String bankShortName, String imgId,
			String swiftCode, int status, boolean rpaContainId, int unlinkedType) {
		this.id = id;
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.bankShortName = bankShortName;
		this.imgId = imgId;
		this.swiftCode = swiftCode;
		this.status = status;
		this.rpaContainId = rpaContainId;
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

	public String getBankShortName() {
		return bankShortName;
	}

	public void setBankShortName(String bankShortName) {
		this.bankShortName = bankShortName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isRpaContainId() {
		return rpaContainId;
	}

	public void setRpaContainId(boolean rpaContainId) {
		this.rpaContainId = rpaContainId;
	}

	public int getUnlinkedType() {
		return unlinkedType;
	}

	public void setUnlinkedType(int unlinkedType) {
		this.unlinkedType = unlinkedType;
	}

}
