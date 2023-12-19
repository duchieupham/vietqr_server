package com.vietqr.org.dto;

import java.io.Serializable;

public class TokenBankBIDVDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String token_type;
    private String access_token;
    private int expires_in;
    private String scope;
    private String consented_on;

    public TokenBankBIDVDTO() {
        super();
    }

    public TokenBankBIDVDTO(String token_type, String access_token, int expires_in, String scope, String consented_on) {
        this.token_type = token_type;
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.scope = scope;
        this.consented_on = consented_on;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getConsented_on() {
        return consented_on;
    }

    public void setConsented_on(String consented_on) {
        this.consented_on = consented_on;
    }

}
