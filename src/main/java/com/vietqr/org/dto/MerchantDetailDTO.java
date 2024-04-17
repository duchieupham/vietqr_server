package com.vietqr.org.dto;

import java.util.List;

public class MerchantDetailDTO {
    private String merchantId;
    private String merchantName;
    private String merchantAddress;

    private int totalTerminals;
    private List<TerminalDetailWebDTO> terminals;

    public MerchantDetailDTO() {
    }

    public MerchantDetailDTO(String merchantId, String merchantName, String merchantAddress, int totalTerminals, List<TerminalDetailWebDTO> terminals) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.merchantAddress = merchantAddress;
        this.totalTerminals = totalTerminals;
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

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public int getTotalTerminals() {
        return totalTerminals;
    }

    public void setTotalTerminals(int totalTerminals) {
        this.totalTerminals = totalTerminals;
    }

    public List<TerminalDetailWebDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalDetailWebDTO> terminals) {
        this.terminals = terminals;
    }
}
