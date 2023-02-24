package com.vietqr.org.dto;

import java.io.Serializable;

public class LogoutDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private String fcmToken;

	public LogoutDTO() {
		super();
	}

	public LogoutDTO(String fcmToken) {
		super();
		this.fcmToken = fcmToken;
	}

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}
	
	

}
