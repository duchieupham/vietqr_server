package com.vietqr.org.dto;

import java.io.Serializable;

public class BusinessStatusDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private boolean status;
	
	public BusinessStatusDTO() {
		super();
	}

	public BusinessStatusDTO(String id, boolean status) {
		super();
		this.id = id;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
