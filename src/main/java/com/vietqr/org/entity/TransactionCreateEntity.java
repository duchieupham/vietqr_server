package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionCreate")
public class TransactionCreateEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id ;

	@Column(name = "bankId")
	private String bankId;

	@Column(name = "amount")
	private Long amount;

	@Column(name = "time")
	private Long time;

	//SMS or the other one.
	@Column(name = "authenType")
	private String authenType;

	//Default = "VND"
	@Column(name = "currency")
	private String currency;

	///OPTIONAL FIELDS///
	//
	//Example: VNPAY
	@Column(name ="merchantId")
	private String merchantId;

	@Column(name ="serviceType")
	private String serviceType;

	@Column(name = "fee")
	private Double fee;

	//Áp dụng khi có thông tin ví điện tử KH , nếu không có thì truyền null
	@Column(name = "paymentDetails")
	private String paymentDetails;

	@Column(name = "deepLinkType")
	private String deppLinkType;

	@Column(name = "deepLink")
	private String deepLink;

	public TransactionCreateEntity() {
		super();
	}

	public TransactionCreateEntity(String id, String bankId, Long amount, Long time, String authenType, String currency,
			String merchantId, String serviceType, Double fee, String paymentDetails, String deppLinkType,
			String deepLink) {
		super();
		this.id = id;
		this.bankId = bankId;
		this.amount = amount;
		this.time = time;
		this.authenType = authenType;
		this.currency = currency;
		this.merchantId = merchantId;
		this.serviceType = serviceType;
		this.fee = fee;
		this.paymentDetails = paymentDetails;
		this.deppLinkType = deppLinkType;
		this.deepLink = deepLink;
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

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getAuthenType() {
		return authenType;
	}

	public void setAuthenType(String authenType) {
		this.authenType = authenType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public String getDeppLinkType() {
		return deppLinkType;
	}

	public void setDeppLinkType(String deppLinkType) {
		this.deppLinkType = deppLinkType;
	}

	public String getDeepLink() {
		return deepLink;
	}

	public void setDeepLink(String deepLink) {
		this.deepLink = deepLink;
	}

}
