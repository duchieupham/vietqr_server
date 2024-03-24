package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class TerminalMemberInsertDTO {
    @NotBlank
    private String terminalId;

    @NotBlank
    private String userId;

    private String merchantId;

    public TerminalMemberInsertDTO() {
    }

    public TerminalMemberInsertDTO(String terminalId, String userId, String merchantId) {
        this.terminalId = terminalId;
        this.userId = userId;
        this.merchantId = merchantId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
