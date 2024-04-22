package com.vietqr.org.dto;

public class TransactionWalletAdminResDTO {
    private String id;
    private long amount;
    private String billNumber;
    private int status;
    private long timeCreated;
    private long timePaid;
    private String transType;
    private int paymentType;
    private String additionData;
    private String additionData2;
    private String userId;
    private String fullName;
    private String phoneNo;

    public TransactionWalletAdminResDTO() {
    }

    public TransactionWalletAdminResDTO(String id, long amount, String billNumber, int status, long timeCreated,
                                        long timePaid, String transType, int paymentType, String additionData,
                                        String userId, String fullName, String phoneNo) {
        this.id = id;
        this.amount = amount;
        this.billNumber = billNumber;
        this.status = status;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.transType = transType;
        this.paymentType = paymentType;
        this.additionData = additionData;
        this.userId = userId;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAdditionData() {
        return additionData;
    }

    public String getAdditionData2() {
        return additionData2;
    }

    public void setAdditionData2(String additionData2) {
        this.additionData2 = additionData2;
    }

    public void setAdditionData(String additionData) {
        this.additionData = additionData;
    }
}
