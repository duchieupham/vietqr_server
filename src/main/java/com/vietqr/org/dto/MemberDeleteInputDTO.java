package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class MemberDeleteInputDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String userId;
    @NotBlank
    private String bankId;

    public MemberDeleteInputDTO() {
        super();
    }

    public MemberDeleteInputDTO(String userId, String bankId) {
        this.userId = userId;
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

}
