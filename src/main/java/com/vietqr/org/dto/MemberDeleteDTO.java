package com.vietqr.org.dto;

import java.io.Serializable;

public class MemberDeleteDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String userId;
	private String businessId;

	public MemberDeleteDTO() {
		super();
	}

	public MemberDeleteDTO(String userId, String businessId) {
		super();
		this.userId = userId;
		this.businessId = businessId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

}
