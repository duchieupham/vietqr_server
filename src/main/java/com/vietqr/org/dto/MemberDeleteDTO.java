package com.vietqr.org.dto;

import java.io.Serializable;

public class MemberDeleteDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;

	public MemberDeleteDTO() {
		super();
	}

	public MemberDeleteDTO(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
