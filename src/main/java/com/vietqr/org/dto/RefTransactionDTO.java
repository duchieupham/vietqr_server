package com.vietqr.org.dto;

import java.io.Serializable;

public class RefTransactionDTO implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String reftransactionid;

	public RefTransactionDTO() {
		super();
	}

	public RefTransactionDTO(String reftransactionid) {
		super();
		this.reftransactionid = reftransactionid;
	}

	public String getReftransactionid() {
		return reftransactionid;
	}

	public void setReftransactionid(String reftransactionid) {
		this.reftransactionid = reftransactionid;
	}
}
