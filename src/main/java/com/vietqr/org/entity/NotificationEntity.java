package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Notification")
public class NotificationEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id ;

	@Column(name = "isRead")
	private boolean isRead;

	@Column(name = "message")
	private String message;

	@Column(name = "time")
	private long time;

	@Column(name = "type")
	private String type;

	@Column(name = "userId")
	private String userId;

	@Column(name = "data")
	private String data;

	public NotificationEntity() {
		super();
	}

	public NotificationEntity(String id, boolean isRead, String message, long time, String type, String userId, String data) {
		super();
		this.id = id;
		this.isRead = isRead;
		this.message = message;
		this.time = time;
		this.type = type;
		this.userId = userId;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
