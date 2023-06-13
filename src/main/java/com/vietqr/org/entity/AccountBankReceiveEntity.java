package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountBankReceive")
public class AccountBankReceiveEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "bankTypeId")
	private String bankTypeId;

	@Column(name = "bankAccount")
	private String bankAccount;

	@Column(name = "bankAccountName")
	private String bankAccountName;

	@Column(name = "nationalId")
	private String nationalId;

	@Column(name = "phoneAuthenticated")
	private String phoneAuthenticated;

	@Column(name = "type")
	private int type;

	@Column(name = "userId")
	private String userId;

	@Column(name = "isAuthenticated")
	private boolean isAuthenticated;

	@Column(name = "isSync")
	private boolean isSync;

	@Column(name = "isWpSync")
	private boolean isWpSync;

	@Column(name = "status")
	private boolean status;

	public AccountBankReceiveEntity() {
		super();
	}

	public AccountBankReceiveEntity(String id, String bankTypeId, String bankAccount, String bankAccountName,
			String nationalId, String phoneAuthenticated, int type, String userId, boolean isAuthenticated,
			boolean isSync, boolean isWpSync, boolean status, String orderId, String sign) {
		super();
		this.id = id;
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.bankAccountName = bankAccountName;
		this.nationalId = nationalId;
		this.phoneAuthenticated = phoneAuthenticated;
		this.type = type;
		this.userId = userId;
		this.isAuthenticated = isAuthenticated;
		this.isSync = isSync;
		this.isWpSync = isWpSync;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankTypeId() {
		return bankTypeId;
	}

	public void setBankTypeId(String bankTypeId) {
		this.bankTypeId = bankTypeId;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getPhoneAuthenticated() {
		return phoneAuthenticated;
	}

	public void setPhoneAuthenticated(String phoneAuthenticated) {
		this.phoneAuthenticated = phoneAuthenticated;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isWpSync() {
		return isWpSync;
	}

	public void setWpSync(boolean isWpSync) {
		this.isWpSync = isWpSync;
	}

	@Override
	public String toString() {
		return "AccountBankReceiveEntity [id=" + id + ", bankTypeId=" + bankTypeId + ", bankAccount=" + bankAccount
				+ ", bankAccountName=" + bankAccountName + ", nationalId=" + nationalId + ", phoneAuthenticated="
				+ phoneAuthenticated + ", type=" + type + ", userId=" + userId + ", isAuthenticated=" + isAuthenticated
				+ ", isSync=" + isSync + ", isWpSync=" + isWpSync + ", status=" + status + "]";
	}

}
