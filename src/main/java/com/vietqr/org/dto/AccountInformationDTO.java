package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountInformationDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String firstName;
	private String middleName;
	private String lastName;
	private String birthDate;
	private String address;
	private int gender;
	private String email;
	private String userId;
	private String nationalId;
	private String oldNationalId;
	private String nationalDate;

	public AccountInformationDTO() {
		super();
	}

	public AccountInformationDTO(String firstName, String middleName, String lastName, String birthDate, String address,
			int gender, String email, String userId) {
		super();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.address = address;
		this.gender = gender;
		this.email = email;
		this.userId = userId;
	}

	public AccountInformationDTO(String firstName, String middleName, String lastName, String birthDate, String address,
			int gender, String email, String nationalId, String oldNationalId, String nationalDate, String userId) {
		super();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.address = address;
		this.gender = gender;
		this.email = email;
		this.nationalId = nationalId;
		this.oldNationalId = oldNationalId;
		this.nationalDate = nationalDate;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

}
