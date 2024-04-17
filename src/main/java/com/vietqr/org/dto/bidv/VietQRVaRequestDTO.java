package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class VietQRVaRequestDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String billId;
    private String userBankName;
    private String amount;
    private String description;

    public VietQRVaRequestDTO() {
        super();
    }

    public VietQRVaRequestDTO(
            String billId,
            String userBankName,
            String amount,
            String description) {
        this.billId = billId;
        this.userBankName = userBankName;
        this.amount = amount;
        this.description = description;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
