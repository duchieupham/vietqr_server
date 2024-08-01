package com.vietqr.org.dto;

import java.util.List;

public class MerchantTerminalV2DTO {
    private String merchantId;
    private String merchantName;
    private List<TerminalResponseV2DTO> terminals;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public List<TerminalResponseV2DTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalResponseV2DTO> terminals) {
        this.terminals = terminals;
    }
}
