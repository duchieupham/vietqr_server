package com.vietqr.org.dto;

public class ConfirmActiveBankReceiveDTO {
    private String otp;
    private String userId;
    private String bankId;
    private String password;
    private String key;

    public ConfirmActiveBankReceiveDTO() {
    }

    public ConfirmActiveBankReceiveDTO(String otp, String userId, String bankId) {
        this.otp = otp;
        this.userId = userId;
        this.bankId = bankId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ConfirmActiveBankReceiveDTO [otp=" + otp + ", bankId=" + bankId + ", userId=" + userId + ", key=" + key + "]";
    }
}
