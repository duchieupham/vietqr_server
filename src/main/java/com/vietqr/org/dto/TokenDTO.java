package com.vietqr.org.dto;

import java.io.Serializable;

public class TokenDTO implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String access_token;
	private String token_type;
	private int expires_in;

	public TokenDTO() {
		super();
	}

	public TokenDTO(String access_token, String token_type, int expires_in) {
		super();
		this.access_token = access_token;
		this.token_type = token_type;
		this.expires_in = expires_in;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}




}
