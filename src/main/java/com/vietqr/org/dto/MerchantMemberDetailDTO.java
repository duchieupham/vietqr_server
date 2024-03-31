package com.vietqr.org.dto;

import java.util.List;

public class MerchantMemberDetailDTO {
    private String userId;
    private String phoneNo;
    private String fullName;
    private String imgId;
    private int existed;
    private int level;
    private List<TerminalMapperDTO> terminals;
    private List<String> transReceiveRoles;
    private List<String> transRefundRoles;

    public MerchantMemberDetailDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public int getExisted() {
        return existed;
    }

    public void setExisted(int existed) {
        this.existed = existed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<String> getTransReceiveRoles() {
        return transReceiveRoles;
    }

    public void setTransReceiveRoles(List<String> transReceiveRoles) {
        this.transReceiveRoles = transReceiveRoles;
    }

    public List<TerminalMapperDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalMapperDTO> terminals) {
        this.terminals = terminals;
    }

    public List<String> getTransRefundRoles() {
        return transRefundRoles;
    }

    public void setTransRefundRoles(List<String> transRefundRoles) {
        this.transRefundRoles = transRefundRoles;
    }
}