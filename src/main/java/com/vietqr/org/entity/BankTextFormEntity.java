package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BankTextForm")
public class BankTextFormEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "text", columnDefinition = "TEXT")
	private String text;

	@Column(name = "bankId")
	private String bankId;

	public BankTextFormEntity() {
		super();
	}

	public BankTextFormEntity(String id, String text, String bankId) {
		super();
		this.id = id;
		this.text = text;
		this.bankId = bankId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

}
