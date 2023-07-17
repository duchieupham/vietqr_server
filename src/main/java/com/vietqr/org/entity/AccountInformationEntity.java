package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountInformation")
public class AccountInformationEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "userId")
	private String userId;

	@Column(name = "firstName")
	private String firstName;

	@Column(name = "middleName")
	private String middleName;

	@Column(name = "lastName")
	private String lastName;

	@Column(name = "birthDate")
	private String birthDate;

	@Column(name = "address")
	private String address;

	@Column(name = "gender")
	private int gender;

	@Column(name = "email")
	private String email;

	@Column(name = "imgId")
	private String imgId;

	@Column(name = "registerPlatform")
	private String registerPlatform;

	@Column(name = "userIp")
	private String userIp;

	@Column(name = "nationalId")
	private String nationalId;

	@Column(name = "oldNationalId")
	private String oldNationalId;

	@Column(name = "nationalDate")
	private String nationalDate;

	@Column(name = "status")
	private boolean status;

	public AccountInformationEntity() {
		super();
	}

	public AccountInformationEntity(String id, String userId, String firstName, String middleName, String lastName,
			String birthDate, String address, int gender, String email, String imgId, String registerPlatform,
			String userIp, String nationalId, String nationalDate, String oldNationalId, boolean status) {
		super();
		this.id = id;
		this.userId = userId;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.address = address;
		this.gender = gender;
		this.email = email;
		this.imgId = imgId;
		this.registerPlatform = registerPlatform;
		this.userIp = userIp;
		this.nationalId = nationalId;
		this.nationalDate = nationalDate;
		this.oldNationalId = oldNationalId;
		this.status = status;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	public String getRegisterPlatform() {
		return registerPlatform;
	}

	public void setRegisterPlatform(String registerPlatform) {
		this.registerPlatform = registerPlatform;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getOldNationalId() {
		return oldNationalId;
	}

	public void setOldNationalId(String oldNationalId) {
		this.oldNationalId = oldNationalId;
	}

	public String getNationalDate() {
		return nationalDate;
	}

	public void setNationalDate(String nationalDate) {
		this.nationalDate = nationalDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
