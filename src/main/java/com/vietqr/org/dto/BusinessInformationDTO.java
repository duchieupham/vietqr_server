package com.vietqr.org.dto;

import java.io.Serializable;

public class BusinessInformationDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String address;
	private String taxCode;

	public BusinessInformationDTO() {
		super();
	}

	public BusinessInformationDTO(String name, String address, String taxCode) {
		super();
		this.name = name;
		this.address = address;
		this.taxCode = taxCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
