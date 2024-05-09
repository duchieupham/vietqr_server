package com.vietqr.org.dto;

public class TransWalletVNPTEpayAdminDTO {
    private String id;
    private long timePaid;
    private long amount;
    private String billNumber;
    private String serviceType;
    private String fullName;
    private String phoneNo;
    private String phoneNorc;
    private String email;
    private long timeCreated;
    private int status;

    public TransWalletVNPTEpayAdminDTO() {
    }

    public TransWalletVNPTEpayAdminDTO(long timePaid, long amount, String billNumber,
                                       String serviceType, String fullName, String phoneNo,
                                       String phoneNorc, String email, long timeCreated, int status) {
        this.timePaid = timePaid;
        this.amount = amount;
        this.billNumber = billNumber;
        this.serviceType = serviceType;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.phoneNorc = phoneNorc;
        this.email = email;
        this.timeCreated = timeCreated;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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

    public String getPhoneNorc() {
        return phoneNorc;
    }

    public void setPhoneNorc(String phoneNorc) {
        this.phoneNorc = phoneNorc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
