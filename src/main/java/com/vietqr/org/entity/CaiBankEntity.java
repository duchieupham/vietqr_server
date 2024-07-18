package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CaiBank")
public class CaiBankEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "bankTypeId")
	private String bankTypeId;

	@Column(name = "cai_value")
	private String caiValue;

	public CaiBankEntity() {
		super();
	}

	public CaiBankEntity(String id, String bankTypeId, String caiValue) {
		super();
		this.id = id;
		this.bankTypeId = bankTypeId;
		this.caiValue = caiValue;
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

	public String getCaiValue() {
		return caiValue;
	}

	public void setCaiValue(String caiValue) {
		this.caiValue = caiValue;
	}

}
