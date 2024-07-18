package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BranchMember")
public class BranchMemberEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "branchId")
	private String branchId;

	@Column(name = "businessId")
	private String businessId;

	@Column(name = "userId")
	private String userId;

	@Column(name = "role")
	private int role;

	public BranchMemberEntity() {
		super();
	}

	public BranchMemberEntity(String id, String branchId, String businessId, String userId, int role) {
		this.id = id;
		this.branchId = branchId;
		this.userId = userId;
		this.role = role;
		this.businessId = businessId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

}
