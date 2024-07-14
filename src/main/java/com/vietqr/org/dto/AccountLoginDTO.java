package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountLoginDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String phoneNo;
	private String email;
	private String password;
	private String fcmToken;
	private String platform;
	private String device;
	private String hosting;
	private String sharingCode;

	public AccountLoginDTO() {
		super();
	}

	public AccountLoginDTO(String phoneNo, String email, String password, String fcmToken, String platform,
			String device, String hosting, String sharingCode) {
		this.phoneNo = phoneNo;
		this.email = email;
		this.password = password;
		this.fcmToken = fcmToken;
		this.platform = platform;
		this.device = device;
		this.hosting = hosting;
		this.sharingCode = sharingCode;
	}

	public AccountLoginDTO(String phoneNo, String password, String fcmToken, String platform, String device) {
		super();
		this.phoneNo = phoneNo;
		this.password = password;
		this.fcmToken = fcmToken;
		this.platform = platform;
		this.device = device;
	}

	public AccountLoginDTO(String phoneNo, String password, String fcmToken, String platform, String device,
			String hosting) {
		super();
		this.phoneNo = phoneNo;
		this.password = password;
		this.fcmToken = fcmToken;
		this.platform = platform;
		this.device = device;
		this.hosting = hosting;
	}

	public AccountLoginDTO(String phoneNo, String email, String password, String fcmToken, String platform,
			String device, String hosting) {
		super();
		this.phoneNo = phoneNo;
		this.email = email;
		this.password = password;
		this.fcmToken = fcmToken;
		this.platform = platform;
		this.device = device;
		this.hosting = hosting;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHosting() {
		return hosting;
	}

	public void setHosting(String hosting) {
		this.hosting = hosting;
	}

	public String getSharingCode() {
		return sharingCode;
	}

	public void setSharingCode(String sharingCode) {
		this.sharingCode = sharingCode;
	}

	@Override
	public String toString() {
		return "AccountLoginDTO{" +
				"phoneNo='" + phoneNo + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", fcmToken='" + fcmToken + '\'' +
				", platform='" + platform + '\'' +
				", device='" + device + '\'' +
				", hosting='" + hosting + '\'' +
				", sharingCode='" + sharingCode + '\'' +
				'}';
	}
}
