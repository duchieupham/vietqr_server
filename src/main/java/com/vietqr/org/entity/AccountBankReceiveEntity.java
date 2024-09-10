package com.vietqr.org.entity;

import com.vietqr.org.util.DateTimeUtil;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountBankReceive")
public class AccountBankReceiveEntity implements Serializable {

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

	@Column(name = "nationalId")
	private String nationalId;

	@Column(name = "phoneAuthenticated")
	private String phoneAuthenticated;

	@Column(name = "type")
	private int type;

	@Column(name = "userId")
	private String userId;

	@Column(name = "isAuthenticated")
	private boolean isAuthenticated;

	@Column(name = "isSync")
	private boolean isSync;

	@Column(name = "isWpSync")
	private boolean isWpSync;

	@Column(name = "status")
	private boolean status;

	@Column(name = "mmsActive")
	private boolean mmsActive;

	@Column(name = "isRpaSync")
	private boolean isRpaSync;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "ewalletToken")
	private String ewalletToken;

	@Column(name = "terminalLength")
	private int terminalLength;

	/*
	* 1: enable
	* 0: disable
	* */
	@Column(name = "pushNotification")
	private Integer pushNotification = 1;

	@Column(name = "enableVoice")
	private Boolean enableVoice = true;

	@Column(name = "isValidService")
	private Boolean isValidService;

	@Column(name = "validFeeFrom")
	private Long validFeeFrom;

	@Column(name = "validFeeTo")
	private Long validFeeTo;

	@Column(name = "customerId")
	private String customerId = "";

	@Column(name = "timeCreated")
	private Long timeCreated = DateTimeUtil.getCurrentDateTimeUTC();
	@Column(name = "vso")
	private String vso = "";

	public AccountBankReceiveEntity() {
		super();
	}

	public AccountBankReceiveEntity(String id, String bankTypeId, String bankAccount, String bankAccountName,
			String nationalId, String phoneAuthenticated, int type, String userId, boolean isAuthenticated,
			boolean isSync, boolean isWpSync, boolean status, String orderId, String sign, boolean mmsActive,
			String ewalletToken) {
		super();
		this.id = id;
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.bankAccountName = bankAccountName;
		this.nationalId = nationalId;
		this.phoneAuthenticated = phoneAuthenticated;
		this.type = type;
		this.userId = userId;
		this.isAuthenticated = isAuthenticated;
		this.isSync = isSync;
		this.isWpSync = isWpSync;
		this.status = status;
		this.mmsActive = mmsActive;
		this.ewalletToken = ewalletToken;
	}

	public AccountBankReceiveEntity(String id, String bankTypeId, String bankAccount, String bankAccountName, String nationalId, String phoneAuthenticated, int type, String userId, boolean isAuthenticated, boolean isSync, boolean isWpSync, boolean status, boolean mmsActive, boolean isRpaSync, String username, String password, String ewalletToken, int terminalLength, Boolean isValidService, Long validFeeFrom, Long validFeeTo, String customerId, Long timeCreated, String vso) {
		this.id = id;
		this.bankTypeId = bankTypeId;
		this.bankAccount = bankAccount;
		this.bankAccountName = bankAccountName;
		this.nationalId = nationalId;
		this.phoneAuthenticated = phoneAuthenticated;
		this.type = type;
		this.userId = userId;
		this.isAuthenticated = isAuthenticated;
		this.isSync = isSync;
		this.isWpSync = isWpSync;
		this.status = status;
		this.mmsActive = mmsActive;
		this.isRpaSync = isRpaSync;
		this.username = username;
		this.password = password;
		this.ewalletToken = ewalletToken;
		this.terminalLength = terminalLength;
		this.isValidService = isValidService;
		this.validFeeFrom = validFeeFrom;
		this.validFeeTo = validFeeTo;
		this.customerId = customerId;
		this.timeCreated = timeCreated;
		this.vso = vso;
	}

	public int getTerminalLength() {
		return terminalLength;
	}

	public void setTerminalLength(int terminalLength) {
		this.terminalLength = terminalLength;
	}

	public boolean isRpaSync() {
		return isRpaSync;
	}

	public void setRpaSync(boolean isRpaSync) {
		this.isRpaSync = isRpaSync;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getPhoneAuthenticated() {
		return phoneAuthenticated;
	}

	public void setPhoneAuthenticated(String phoneAuthenticated) {
		this.phoneAuthenticated = phoneAuthenticated;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isWpSync() {
		return isWpSync;
	}

	public void setWpSync(boolean isWpSync) {
		this.isWpSync = isWpSync;
	}

	public boolean isMmsActive() {
		return mmsActive;
	}

	public void setMmsActive(boolean mmsActive) {
		this.mmsActive = mmsActive;
	}

	public String getEwalletToken() {
		return ewalletToken;
	}

	public void setEwalletToken(String ewalletToken) {
		this.ewalletToken = ewalletToken;
	}

	public boolean isValidService() {
		return isValidService;
	}

	public void setValidService(boolean validService) {
		isValidService = validService;
	}

	public long getValidFeeFrom() {
		return validFeeFrom;
	}

	public void setValidFeeFrom(long validFeeFrom) {
		this.validFeeFrom = validFeeFrom;
	}

	public Long getValidFeeTo() {
		return validFeeTo;
	}

	public void setValidFeeTo(Long validFeeTo) {
		this.validFeeTo = validFeeTo;
	}

	public void setValidFeeFrom(Long validFeeFrom) {
		this.validFeeFrom = validFeeFrom;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getVso() {
		return vso;
	}

	public void setVso(String vso) {
		this.vso = vso;
	}

	public Long getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Long timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Integer getPushNotification() {
		return pushNotification;
	}

	public void setPushNotification(Integer pushNotification) {
		this.pushNotification = pushNotification;
	}

	public Boolean getEnableVoice() {
		return enableVoice;
	}

	public void setEnableVoice(Boolean enableVoice) {
		this.enableVoice = enableVoice;
	}

	@Override
	public String toString() {
		return "AccountBankReceiveEntity [id=" + id + ", bankTypeId=" + bankTypeId + ", bankAccount=" + bankAccount
				+ ", bankAccountName=" + bankAccountName + ", nationalId=" + nationalId + ", phoneAuthenticated="
				+ phoneAuthenticated + ", type=" + type + ", userId=" + userId + ", isAuthenticated=" + isAuthenticated
				+ ", isSync=" + isSync + ", isWpSync=" + isWpSync + ", status=" + status + ", mmsActive=" + mmsActive
				+ "]";
	}

}
