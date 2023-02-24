package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionResponseDTO implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private boolean error;
	private String errorReason;
	private String toastMessage;
	private RefTransactionDTO object;

	public TransactionResponseDTO() {
		super();
	}

	public TransactionResponseDTO(boolean error, String errorReason, String toastMessage) {
		super();
		this.error = error;
		this.errorReason = errorReason;
		this.toastMessage = toastMessage;
	}

	public TransactionResponseDTO(boolean error, String errorReason, String toastMessage, RefTransactionDTO object) {
		super();
		this.error = error;
		this.errorReason = errorReason;
		this.toastMessage = toastMessage;
		this.object = object;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorReason() {
		return errorReason;
	}
	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}
	public String getToastMessage() {
		return toastMessage;
	}
	public void setToastMessage(String toastMessage) {
		this.toastMessage = toastMessage;
	}
	public RefTransactionDTO getObject() {
		return object;
	}
	public void setObject(RefTransactionDTO object) {
		this.object = object;
	}
}
