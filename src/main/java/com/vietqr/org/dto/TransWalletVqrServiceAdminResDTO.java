package com.vietqr.org.dto;

public class TransWalletVqrServiceAdminResDTO {
    private String id;
    private long amount;
    private String billNumber;
    private int status;
    private long timeCreated;
    private long timePaid;
    private String additionData;
    private String additionData2;
    private String additionData3;
    private String additionData4;
    private String additionData5;
    private String fullName;
    private String phoneNo;

    public TransWalletVqrServiceAdminResDTO() {
    }

    public TransWalletVqrServiceAdminResDTO(String id, long amount, String billNumber, int status,
                                            long timeCreated, long timePaid, String additionData,
                                            String additionData2, String additionData3, String additionData4,
                                            String additionData5, String fullName, String phoneNo) {
        this.id = id;
        this.amount = amount;
        this.billNumber = billNumber;
        this.status = status;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.additionData = additionData;
        this.additionData2 = additionData2;
        this.additionData3 = additionData3;
        this.additionData4 = additionData4;
        this.additionData5 = additionData5;
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

    public String getAdditionData3() {
        return additionData3;
    }

    public void setAdditionData3(String additionData3) {
        this.additionData3 = additionData3;
    }

    public String getAdditionData4() {
        return additionData4;
    }

    public void setAdditionData4(String additionData4) {
        this.additionData4 = additionData4;
    }

    public String getAdditionData5() {
        return additionData5;
    }

    public void setAdditionData5(String additionData5) {
        this.additionData5 = additionData5;
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
