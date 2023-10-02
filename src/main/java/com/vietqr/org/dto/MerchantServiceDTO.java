package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class MerchantServiceDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String customerSyncId;
    private String merchant;
    private List<AccountBankReceiveServiceItemDTO> bankAccounts;

    public MerchantServiceDTO() {
        super();
    }

    public MerchantServiceDTO(String customerSyncId, String merchant,
            List<AccountBankReceiveServiceItemDTO> bankAccounts) {
        this.customerSyncId = customerSyncId;
        this.merchant = merchant;
        this.bankAccounts = bankAccounts;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getCustomerSyncId() {
        return customerSyncId;
    }

    public void setCustomerSyncId(String customerSyncId) {
        this.customerSyncId = customerSyncId;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public List<AccountBankReceiveServiceItemDTO> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<AccountBankReceiveServiceItemDTO> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

}
