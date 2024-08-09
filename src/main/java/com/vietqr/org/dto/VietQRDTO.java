package com.vietqr.org.dto;

import com.vietqr.org.service.mqtt.AdditionalData;

import java.io.Serializable;
import java.util.List;

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
	// transactionId to upload bill image
	private String transactionId;
	// transactionRefId to show QR Link
	private String transactionRefId;
	private String qrLink;
	// terminalCode to show what terminal is
	private String terminalCode;

	private String serviceCode;
	private String orderId;

	private List<AdditionalData> additionalData;

	public VietQRDTO() {
		super();
	}

	public VietQRDTO(String bankCode, String bankName, String bankAccount, String userBankName, String amount,
			String content, String qrCode, String imgId, int existing, String transactionId,
			String transactionRefId, String terminalCode, String qrLink) {
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
		this.transactionId = transactionId;
		this.transactionRefId = transactionRefId;
		this.terminalCode = terminalCode;
		this.qrLink = qrLink;
	}


	public VietQRDTO(String bankCode, String bankName,
					 String bankAccount, String userBankName, String amount, String content, String qrCode, String imgId, int existing, String transactionId, String transactionRefId, String qrLink, String terminalCode, String serviceCode, String orderId, List<AdditionalData> additionalData) {
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.bankAccount = bankAccount;
		this.userBankName = userBankName;
		this.amount = amount;
		this.content = content;
		this.qrCode = qrCode;
		this.imgId = imgId;
		this.existing = existing;
		this.transactionId = transactionId;
		this.transactionRefId = transactionRefId;
		this.qrLink = qrLink;
		this.terminalCode = terminalCode;
		this.serviceCode = serviceCode;
		this.orderId = orderId;
		this.additionalData =additionalData;
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionRefId() {
		return transactionRefId;
	}

	public void setTransactionRefId(String transactionRefId) {
		this.transactionRefId = transactionRefId;
	}

	public String getTerminalCode() {
		return terminalCode;
	}

	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}

	public String getQrLink() {
		return qrLink;
	}

	public void setQrLink(String qrLink) {
		this.qrLink = qrLink;
	}


	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public List<AdditionalData> getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(List<AdditionalData> additionalData) {
		this.additionalData = additionalData;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "VietQRDTO [bankCode=" + bankCode + ", bankName=" + bankName + ", bankAccount=" + bankAccount
				+ ", userBankName=" + userBankName + ", amount=" + amount + ", content=" + content + ", qrCode="
				+ qrCode + ", imgId=" + imgId + ", existing=" + existing + "]";
	}

}
