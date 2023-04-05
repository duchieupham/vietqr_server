package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String bankCode;
	private String bankName;
	private String bankAccount;
	private String userBankName;
	private String amount;
	private String content;
	private String qrCode;
	private String imgId;
	private int existing;

	public VietQRDTO() {
		super();
	}

	public VietQRDTO(String bankCode, String bankName, String bankAccount, String userBankName, String amount,
			String content, String qrCode, String imgId, int existing) {
		super();
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.amount = amount;
		this.content = content;
		this.qrCode = qrCode;
		this.imgId = imgId;
		this.existing = existing;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getUserBankName() {
		return userBankName;
	}

	public void setUserBankName(String userBankName) {
		this.userBankName = userBankName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	public int getExisting() {
		return existing;
	}

	public void setExisting(int existing) {
		this.existing = existing;
	}

}
