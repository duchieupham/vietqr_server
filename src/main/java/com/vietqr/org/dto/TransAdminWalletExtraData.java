package com.vietqr.org.dto;

public class TransAdminWalletExtraData {
    private String time;
    private int total;

    public TransAdminWalletExtraData() {
        time = "";
        total = 0;
    }

    public TransAdminWalletExtraData(String time, int total) {
        this.time = time;
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
