package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountBankPayment")
public class AccountBankPaymentEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "userId")
	private String userId;

	@Column(name = "bankTypeId")
	private String bankTypeId;

	@Column(name = "bankAccount")
	private String bankAccount;

	@Column(name = "bankAccountName")
	private String bankAccountName;

	@Column(name = "dateOpen")
	private String dateOpen;

	@Column(name = "phoneOtp")
	private String phoneOtp;

	public AccountBankPaymentEntity() {
		super();
	}

	public AccountBankPaymentEntity(String id, String userId, String bankTypeId, String bankAccount,
			String bankAccountName, String dateOpen, String phoneOtp) {
		super();
		this.id = id;
		this.userId = userId;
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.bankAccountName = bankAccountName;
		this.dateOpen = dateOpen;
		this.phoneOtp = phoneOtp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBankTypeId() {
		return bankTypeId;
	}

	public void setBankTypeId(String bankTypeId) {
		this.bankTypeId = bankTypeId;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getDateOpen() {
		return dateOpen;
	}

	public void setDateOpen(String dateOpen) {
		this.dateOpen = dateOpen;
	}

	public String getPhoneOtp() {
		return phoneOtp;
	}

	public void setPhoneOTP(String phoneOtp) {
		this.phoneOtp = phoneOtp;
	}

}
