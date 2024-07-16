package com.vietqr.org.dto;

public class TokenMBResponseDTO {

    private String access_token;
    private int expires_in;
    private String scope;
    private String issued_at;
    private String token_type;

    public TokenMBResponseDTO() {
    }

    public TokenMBResponseDTO(String access_token, int expires_in, String scope, String issued_at, String token_type) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.scope = scope;
        this.issued_at = issued_at;
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

    public String getIssued_at() {
        return issued_at;
    }

    public void setIssued_at(String issued_at) {
        this.issued_at = issued_at;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
