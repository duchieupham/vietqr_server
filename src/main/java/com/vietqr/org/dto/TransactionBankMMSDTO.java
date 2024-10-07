package com.vietqr.org.dto;

public class TransactionBankMMSDTO {
    private String traceTransfer;
    private String ftCode;
    private String storeLabel;
    private String terminalLabel;
    private String debitAmount;
    private String realAmount;
    private String payDate;
    private String respCode;
    private String respDesc;
    private String checkSum;
    private String qrCodeId;
    private String rate;

    public TransactionBankMMSDTO() {
    }

    public TransactionBankMMSDTO(String traceTransfer, String ftCode, String storeLabel, String terminalLabel, String debitAmount,
                                 String realAmount, String payDate, String respCode, String respDesc, String checkSum, String qrCodeId) {
        this.traceTransfer = traceTransfer;
        this.ftCode = ftCode;
        this.storeLabel = storeLabel;
        this.terminalLabel = terminalLabel;
        this.debitAmount = debitAmount;
        this.realAmount = realAmount;
        this.payDate = payDate;
        this.respCode = respCode;
        this.respDesc = respDesc;
        this.checkSum = checkSum;
        this.qrCodeId = qrCodeId;
    }

    public String getTraceTransfer() {
        return traceTransfer;
    }

    public void setTraceTransfer(String traceTransfer) {
        this.traceTransfer = traceTransfer;
    }

    public String getFtCode() {
        return ftCode;
    }

    public void setFtCode(String ftCode) {
        this.ftCode = ftCode;
    }

    public String getStoreLabel() {
        return storeLabel;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setStoreLabel(String storeLabel) {
        this.storeLabel = storeLabel;
    }

    public String getTerminalLabel() {
        return terminalLabel;
    }

    public void setTerminalLabel(String terminalLabel) {
        this.terminalLabel = terminalLabel;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(String realAmount) {
        this.realAmount = realAmount;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }
}
