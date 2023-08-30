package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class AccountBankSmsInsertDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String smsId;
    private List<AccountBankSmsItemDTO> bankAccounts;

    public AccountBankSmsInsertDTO() {
        super();
    }

    public AccountBankSmsInsertDTO(String smsId, List<AccountBankSmsItemDTO> bankAccounts) {
        this.smsId = smsId;
        this.bankAccounts = bankAccounts;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public List<AccountBankSmsItemDTO> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<AccountBankSmsItemDTO> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

}
