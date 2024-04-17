package com.vietqr.org.dto;

import java.io.Serializable;

public class PasswordUpdateDTO implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String userId;
	public String oldPassword;
	public String newPassword;

	public PasswordUpdateDTO() {
		super();
	}

	public PasswordUpdateDTO(String userId, String oldPassword, String newPassword) {
		super();
		this.userId = userId;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
