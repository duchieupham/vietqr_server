package com.vietqr.org.dto;

import java.util.Objects;

public class MerchantBankDTO {
    private String merchantId;
    private String bankId;

    public MerchantBankDTO() {
    }

    public MerchantBankDTO(String merchantId, String bankId) {
        this.merchantId = merchantId;
        this.bankId = bankId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchantId, bankId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MerchantBankDTO that = (MerchantBankDTO) obj;
        return Objects.equals(merchantId, that.merchantId) &&
                Objects.equals(bankId, that.bankId);
    }
}
