package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class AnnualFeeMerchantItemDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String customerSyncId;
    private String merchant;
    private Long totalPayment;
    private Integer status;
    private List<AnnualFeeBankItemDTO> bankAccounts;

    public AnnualFeeMerchantItemDTO() {
        super();
    }

    public AnnualFeeMerchantItemDTO(String customerSyncId, String merchant, Long totalPayment, Integer status,
            List<AnnualFeeBankItemDTO> bankAccounts) {
        this.customerSyncId = customerSyncId;
        this.merchant = merchant;
        this.totalPayment = totalPayment;
        this.status = status;
        this.bankAccounts = bankAccounts;
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

    public Long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<AnnualFeeBankItemDTO> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<AnnualFeeBankItemDTO> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

}
