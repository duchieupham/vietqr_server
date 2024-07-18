package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BankReceiveBranch")
public class BankReceiveBranchEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "bankId")
	private String bankId;

	@Column(name = "branchId")
	private String branchId;

	@Column(name = "businessId")
	private String businessId;

	public BankReceiveBranchEntity() {
		super();
	}

	public BankReceiveBranchEntity(String id, String bankId, String branchId, String businessId) {
		super();
		this.id = id;
		this.bankId = bankId;
		this.branchId = branchId;
		this.businessId = businessId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

}
