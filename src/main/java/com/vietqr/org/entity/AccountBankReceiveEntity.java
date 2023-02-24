package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountBankReceive")
public class AccountBankReceiveEntity implements Serializable{

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
	
	@Column(name = "type")
	private int type;


	public AccountBankReceiveEntity() {
		super();
	}

	public AccountBankReceiveEntity(String id, String bankTypeId, String bankAccount, String bankAccountName, int type) {
		super();
		this.id = id;
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.bankAccountName = bankAccountName;
		this.type = type;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
