package com.vietqr.org.dto;

public class TerminalWebDetailResponseDTO {
    private String id;
    private String name;
    private String address;
    private String code;

    private int totalTrans;

    private long totalAmount;

    private int revGrowthPrevDate;

    private int revGrowthPrevMonth;

    private TerminalBankResponseDTO bank;

    public TerminalWebDetailResponseDTO() {
    }

    public TerminalWebDetailResponseDTO(String id, String name, String address, String code, int totalTrans, long totalAmount, int revGrowthPrevDate, int revGrowthPrevMonth, TerminalBankResponseDTO bank) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.code = code;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.revGrowthPrevDate = revGrowthPrevDate;
        this.revGrowthPrevMonth = revGrowthPrevMonth;
        this.bank = bank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getRevGrowthPrevDate() {
        return revGrowthPrevDate;
    }

    public void setRevGrowthPrevDate(int revGrowthPrevDate) {
        this.revGrowthPrevDate = revGrowthPrevDate;
    }

    public int getRevGrowthPrevMonth() {
        return revGrowthPrevMonth;
    }

    public void setRevGrowthPrevMonth(int revGrowthPrevMonth) {
        this.revGrowthPrevMonth = revGrowthPrevMonth;
    }

    public TerminalBankResponseDTO getBank() {
        return bank;
    }

    public void setBank(TerminalBankResponseDTO bank) {
        this.bank = bank;
    }
}
