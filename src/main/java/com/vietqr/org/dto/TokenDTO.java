package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TokenDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String access_token;
    private String token_type;
    private int expires_in;
    private List<AccountCustomerMerchantDTO> merchant;

    public TokenDTO() {
        super();
    }

    public TokenDTO(String access_token, String token_type, int expires_in) {
        super();
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
    }

    public TokenDTO(String access_token, String token_type, int expires_in, List<AccountCustomerMerchantDTO> merchant) {
        super();
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.merchant = merchant;
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

    public List<AccountCustomerMerchantDTO> getMerchant() {
        return merchant;
    }

    public void setMerchant(List<AccountCustomerMerchantDTO> merchant) {
        this.merchant = merchant;
    }
}
