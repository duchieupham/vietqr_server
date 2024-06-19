package com.vietqr.org.dto;

public class AccountCountDTO {
    private long totalUsers;
    private long totalUserRegisterToday;

    public AccountCountDTO(long totalUsers, long totalUserRegisterToday) {
        this.totalUsers = totalUsers;
        this.totalUserRegisterToday = totalUserRegisterToday;
    }

    public AccountCountDTO() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalUserRegisterToday() {
        return totalUserRegisterToday;
    }

    public void setTotalUserRegisterToday(long totalUserRegisterToday) {
        this.totalUserRegisterToday = totalUserRegisterToday;
    }
}
