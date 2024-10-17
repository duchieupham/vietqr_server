package com.vietqr.org.entity;

import com.vietqr.org.dto.DataTransactionDTO;
import com.vietqr.org.dto.FeeTransactionInfoDTO;
import com.vietqr.org.dto.TransReceiveInvoicesDTO;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "TransactionReceive")
@SqlResultSetMapping(
		name = "TransReceiveInvoicesDTO",
		classes = @ConstructorResult(
				targetClass = TransReceiveInvoicesDTO.class,
				columns = {
						@ColumnResult(name = "id", type = String.class),
						@ColumnResult(name = "amount", type = Long.class),
						@ColumnResult(name = "content", type = String.class),
						@ColumnResult(name = "trans_type", type = String.class),
						@ColumnResult(name = "type", type = Integer.class),
						@ColumnResult(name = "time", type = Long.class),
						@ColumnResult(name = "time_paid", type = Long.class),
						@ColumnResult(name = "status", type = Integer.class)
				}
		)
)
@SqlResultSetMapping(
		name = "FeeTransactionInfoDTO",
		classes = @ConstructorResult(
				targetClass = FeeTransactionInfoDTO.class,
				columns = {
						@ColumnResult(name = "totalCount", type = Integer.class),
						@ColumnResult(name = "totalAmount", type = Long.class),
						@ColumnResult(name = "creditCount", type = Integer.class),
						@ColumnResult(name = "creditAmount", type = Long.class),
						@ColumnResult(name = "debitCount", type = Integer.class),
						@ColumnResult(name = "debitAmount", type = Long.class),
						@ColumnResult(name = "controlCount", type = Integer.class),
						@ColumnResult(name = "controlAmount", type = Long.class),
						@ColumnResult(name = "bankId", type = String.class)
				}
		)
)

@SqlResultSetMapping(
		name = "DataTransactionDTO",
		classes = @ConstructorResult(
				targetClass = DataTransactionDTO.class,
				columns = {
						@ColumnResult(name = "bank_account", type = String.class),
						@ColumnResult(name = "content", type = String.class),
						@ColumnResult(name = "amount", type = Long.class),
						@ColumnResult(name = "time", type = Long.class),
						@ColumnResult(name = "time_paid", type = Long.class),
						@ColumnResult(name = "type", type = Integer.class),
						@ColumnResult(name = "status", type = Integer.class),
						@ColumnResult(name = "trans_type", type = String.class),
						@ColumnResult(name = "reference_number", type = String.class),
						@ColumnResult(name = "order_id", type = String.class),
						@ColumnResult(name = "terminal_code", type = String.class),
						@ColumnResult(name = "note", type = String.class)
				}
		)
)
public class TransactionReceiveEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "bankAccount")
	private String bankAccount;

	@Column(name = "bankId")
	private String bankId;

	@Column(name = "content")
	private String content;

	@Column(name = "amount")
	private long amount;

	@Column(name = "time")
	private long time;

	@Column(name = "timePaid")
	private long timePaid;

	// ref id from transaction_bank/(transaction_sms:removed)
	@Column(name = "refId")
	private String refId;

	// transaction type: From Bank/(SMS:removed)
	@Column(name = "type")
	private int type;

	// transaction status: PAID/UNPAID
	@Column(name = "status")
	private int status;

	@Column(name = "traceId")
	private String traceId;

	@Column(name = "transType")
	private String transType;

	@Column(name = "referenceNumber")
	private String referenceNumber;

	// for customers
	@Column(name = "orderId")
	private String orderId = "";

	@Column(name = "sign")
	private String sign = "";

	@Column(name = "customerBankAccount")
	private String customerBankAccount;

	@Column(name = "customerBankCode")
	private String customerBankCode;

	@Column(name = "customerName")
	private String customerName;

	@Column(name = "terminalCode")
	private String terminalCode;

	@Column(name = "serviceCode")
	private String serviceCode = "";

	@Column(name = "qrCode")
	private String qrCode;

	@Column(name = "userId")
	private String userId;

	@Column(name = "note")
	private String note;

	// 0: init
	// 1: success - Thanh cong
	// 2: pending - Cho xử lý
	// 3: failed - That bai
	// 4: error - Loi
	@Column(name = "statusResponse")
	private Integer statusResponse;

	@Column(name = "urlLink")
	private String urlLink = "";

	@Column(name = "billId")
	private String billId = "";

	@Column(name = "hashTag")
	private String hashTag = "";

	@Column(name = "additionalData", columnDefinition = "JSON")
	private String additionalData = "[]";

	@Column(name = "subCode")
	private String subCode = "";

	public TransactionReceiveEntity() {
		super();
		this.hashTag = "";
		this.additionalData = "[]";
	}

	public TransactionReceiveEntity(String id, String bankAccount, String bankId, String content, long amount,
			long time, String refId, int type, int status, String traceId, String transType, String referenceNumber,
			String sign, String orderId, String terminalCode, String qrCode, String userId, String note) {
		this.id = id;
		this.bankAccount = bankAccount;
		this.bankId = bankId;
		this.content = content;
		this.amount = amount;
		this.time = time;
		this.refId = refId;
		this.type = type;
		this.status = status;
		this.traceId = traceId;
		this.transType = transType;
		this.referenceNumber = referenceNumber;
		this.sign = sign;
		this.orderId = orderId;
		this.terminalCode = terminalCode;
		this.qrCode = qrCode;
		this.userId = userId;
		this.note = note;
	}

	public TransactionReceiveEntity(String id, String bankAccount, String bankId, String content, long amount,
			long time, String refId, int type, int status, String traceId, String transType, String referenceNumber,
			String orderId, String sign, String customerBankAccount, String customerBankCode, String customerName,
			String terminalCode, String qrCode, String userId, String note) {
		this.id = id;
		this.bankAccount = bankAccount;
		this.bankId = bankId;
		this.content = content;
		this.amount = amount;
		this.time = time;
		this.refId = refId;
		this.type = type;
		this.status = status;
		this.traceId = traceId;
		this.transType = transType;
		this.referenceNumber = referenceNumber;
		this.orderId = orderId;
		this.sign = sign;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
		this.terminalCode = terminalCode;
		this.qrCode = qrCode;
		this.userId = userId;
		this.note = note;
	}

	public TransactionReceiveEntity(String id, String bankAccount, String bankId, String content, long amount,
			long time, long timePaid, String refId, int type, int status, String traceId, String transType,
			String referenceNumber, String orderId, String sign, String customerBankAccount, String customerBankCode,
			String customerName, String terminalCode, String qrCode, String userId, String note) {
		this.id = id;
		this.bankAccount = bankAccount;
		this.bankId = bankId;
		this.content = content;
		this.amount = amount;
		this.time = time;
		this.timePaid = timePaid;
		this.refId = refId;
		this.type = type;
		this.status = status;
		this.traceId = traceId;
		this.transType = transType;
		this.referenceNumber = referenceNumber;
		this.orderId = orderId;
		this.sign = sign;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
		this.terminalCode = terminalCode;
		this.qrCode = qrCode;
		this.userId = userId;
		this.note = note;
	}

	public TransactionReceiveEntity(String id, String bankAccount, String bankId, String content, long amount, long time, long timePaid, String refId, int type, int status, String traceId, String transType, String referenceNumber, String orderId, String sign, String customerBankAccount, String customerBankCode, String customerName, String terminalCode, String serviceCode, String qrCode, String userId, String note, Integer statusResponse, String urlLink, String billId, String hashTag, String additionalData) {
		this.id = id;
		this.bankAccount = bankAccount;
		this.bankId = bankId;
		this.content = content;
		this.amount = amount;
		this.time = time;
		this.timePaid = timePaid;
		this.refId = refId;
		this.type = type;
		this.status = status;
		this.traceId = traceId;
		this.transType = transType;
		this.referenceNumber = referenceNumber;
		this.orderId = orderId;
		this.sign = sign;
		this.customerBankAccount = customerBankAccount;
		this.customerBankCode = customerBankCode;
		this.customerName = customerName;
		this.terminalCode = terminalCode;
		this.serviceCode = serviceCode;
		this.qrCode = qrCode;
		this.userId = userId;
		this.note = note;
		this.statusResponse = statusResponse;
		this.urlLink = urlLink;
		this.billId = billId;
		this.hashTag = hashTag;
		this.additionalData = additionalData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getCustomerBankAccount() {
		return customerBankAccount;
	}

	public void setCustomerBankAccount(String customerBankAccount) {
		this.customerBankAccount = customerBankAccount;
	}

	public String getCustomerBankCode() {
		return customerBankCode;
	}

	public void setCustomerBankCode(String customerBankCode) {
		this.customerBankCode = customerBankCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public long getTimePaid() {
		return timePaid;
	}

	public void setTimePaid(long timePaid) {
		this.timePaid = timePaid;
	}

	public String getTerminalCode() {
		return terminalCode;
	}

	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getStatusResponse() {
		return statusResponse;
	}

	public void setStatusResponse(Integer transStatus) {
		this.statusResponse = transStatus;
	}

	public String getUrlLink() {
		return urlLink;
	}

	public void setUrlLink(String urlLink) {
		this.urlLink = urlLink;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getHashTag() {
		return hashTag;
	}

	public void setHashTag(String hashTag) {
		this.hashTag = hashTag;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}
}
