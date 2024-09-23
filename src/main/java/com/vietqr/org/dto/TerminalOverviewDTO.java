package com.vietqr.org.dto;

import java.util.List;

public class TerminalOverviewDTO {
    private String merchantId;
    private String merchantName;
    private List<TerminalOverviewV2DTO> terminals;

    public TerminalOverviewDTO() {
    }

    public TerminalOverviewDTO(String merchantId, String merchantName, List<TerminalOverviewV2DTO> terminals) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.terminals = terminals;
    }

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

    public List<TerminalOverviewV2DTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalOverviewV2DTO> terminals) {
        this.terminals = terminals;
    }
}
