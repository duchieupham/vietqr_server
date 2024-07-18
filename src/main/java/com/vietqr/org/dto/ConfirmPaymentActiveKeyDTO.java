package com.vietqr.org.dto;

public class ConfirmPaymentActiveKeyDTO {
    private String qr;
    private String billNumber;
    private String bankLogo;
    private long amount;

    public ConfirmPaymentActiveKeyDTO() {
    }

    public ConfirmPaymentActiveKeyDTO(String qr, String billNumber, String bankLogo) {
        this.qr = qr;
        this.billNumber = billNumber;
        this.bankLogo = bankLogo;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getBankLogo() {
        return bankLogo;
    }

    public void setBankLogo(String bankLogo) {
        this.bankLogo = bankLogo;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

}
