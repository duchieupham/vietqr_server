package com.vietqr.org.dto;

public class TransactionImgDeleteDTO {
    private String imgId;
    private String transactionId;

    public TransactionImgDeleteDTO() {
    }

    public TransactionImgDeleteDTO(String imgId, String transactionId) {
        this.imgId = imgId;
        this.transactionId = transactionId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
