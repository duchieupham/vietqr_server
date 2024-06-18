package com.vietqr.org.dto;

import java.util.List;

public class UserDetailResponseDTO {
    private IUserInfoDTO userInfo;
    private List<BankInfoDTO> bankInfo;
    private List<BankShareDTO> bankShareInfo;
    private List<SocialMediaDTO> socalMedia;
    private long balance;
    private long score;

    public UserDetailResponseDTO() {
    }

    public UserDetailResponseDTO(IUserInfoDTO userInfo, List<BankInfoDTO> bankInfo, List<BankShareDTO> bankShareInfo, List<SocialMediaDTO> socalMedia, long balance, long score) {
        this.userInfo = userInfo;
        this.bankInfo = bankInfo;
        this.bankShareInfo = bankShareInfo;
        this.socalMedia = socalMedia;
        this.balance = balance;
        this.score = score;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public IUserInfoDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(IUserInfoDTO userInfo) {
        this.userInfo = userInfo;
    }

    public List<BankInfoDTO> getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(List<BankInfoDTO> bankInfo) {
        this.bankInfo = bankInfo;
    }

    public List<BankShareDTO> getBankShareInfo() {
        return bankShareInfo;
    }

    public void setBankShareInfo(List<BankShareDTO> bankShareInfo) {
        this.bankShareInfo = bankShareInfo;
    }

    public List<SocialMediaDTO> getSocalMedia() {
        return socalMedia;
    }

    public void setSocalMedia(List<SocialMediaDTO> socalMedia) {
        this.socalMedia = socalMedia;
    }
}
