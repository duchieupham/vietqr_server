package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class ServiceFeeBankItemDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankId;
    private String bankAccount;
    private String bankCode;
    private String bankShortName;
    private List<ServiceFeeMonthItemDTO> fees;

    public ServiceFeeBankItemDTO() {
        super();
    }

    public ServiceFeeBankItemDTO(String bankId, String bankAccount, String bankCode, String bankShortName,
            List<ServiceFeeMonthItemDTO> fees) {
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.bankShortName = bankShortName;
        this.fees = fees;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public List<ServiceFeeMonthItemDTO> getFees() {
        return fees;
    }

    public void setFees(List<ServiceFeeMonthItemDTO> fees) {
        this.fees = fees;
    }

}
