package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FcmToken")
public class FcmTokenEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "token")
	private String token;

	@Column(name = "userId")
	private String userId;

	@Column(name = "platform")
	private String platform;

	@Column(name = "device")
	private String device;


	public FcmTokenEntity() {
		super();
	}

	public FcmTokenEntity(String id, String token, String userId, String platform, String device) {
		super();
		this.id = id;
		this.token = token;
		this.userId = userId;
		this.platform = platform;
		this.device = device;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
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

}
