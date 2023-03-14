package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BusinessInformation")
public class BusinessInformationEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "imgId")
	private String imgId;

	@Column(name = "coverImgId")
	private String coverImgId;

	@Column(name = "address")
	private String address;

	@Column(name = "taxCode")
	private String taxCode;

	@Column(name = "isActive")
	private boolean isActive;


	public BusinessInformationEntity() {
		super();
	}

	public BusinessInformationEntity(String id, String code, String name, String imgId, String coverImgId, String address, String taxCode, boolean isActive) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.imgId = imgId;
		this.coverImgId = coverImgId;
		this.address = address;
		this.taxCode = taxCode;
		this.isActive = isActive;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	public String getCoverImgId() {
		return coverImgId;
	}

	public void setCoverImgId(String coverImgId) {
		this.coverImgId = coverImgId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
