package com.vietqr.org.dto;

import java.util.List;

public class MerchantMemberResponseDTO {
    private String merchantId;
    private String userId;
    private String level;
    private String phoneNo;
    private String fullName;
    private List<TerminalMapperDTO> terminals;
    private List<IRoleMemberDTO> transReceiveRoles;
    private List<IRoleMemberDTO> transRefundRoles;
    private String imgId;

    public MerchantMemberResponseDTO(String merchantId, String userId,
                                     String level, String phoneNo,
                                     String fullName, List<TerminalMapperDTO> terminals,
                                     List<IRoleMemberDTO> transReceiveRoles,
                                     List<IRoleMemberDTO> transRefundRoles, String imgId) {
        this.merchantId = merchantId;
        this.userId = userId;
        this.level = level;
        this.phoneNo = phoneNo;
        this.fullName = fullName;
        this.terminals = terminals;
        this.transReceiveRoles = transReceiveRoles;
        this.transRefundRoles = transRefundRoles;
        this.imgId = imgId;
    }

    public MerchantMemberResponseDTO() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    public List<TerminalMapperDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalMapperDTO> terminals) {
        this.terminals = terminals;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public List<IRoleMemberDTO> getTransReceiveRoles() {
        return transReceiveRoles;
    }

    public void setTransReceiveRoles(List<IRoleMemberDTO> transReceiveRoles) {
        this.transReceiveRoles = transReceiveRoles;
    }

    public List<IRoleMemberDTO> getTransRefundRoles() {
        return transRefundRoles;
    }

    public void setTransRefundRoles(List<IRoleMemberDTO> transRefundRoles) {
        this.transRefundRoles = transRefundRoles;
    }
}
