package com.vietqr.org.dto;

import java.io.Serializable;

import java.util.List;

public class BusinessItemResponseDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String businessId;
    // private String code;
    private int role;
    private String imgId;
    private String coverImgId;
    private String name;
    // private String address;
    // private String taxCode;
    private List<TransactionRelatedDTO> transactions;
    private int totalMember;
    private int totalBranch;

    public BusinessItemResponseDTO() {
        super();
    }

    public BusinessItemResponseDTO(String businessId,
            // String code,
            int role, String imgId, String coverImgId,
            String name,
            // String address, String taxCode,
            List<TransactionRelatedDTO> transactions, int totalMember,
            int totalBranch) {
        this.businessId = businessId;
        // this.code = code;
        this.role = role;
        this.imgId = imgId;
        this.coverImgId = coverImgId;
        this.name = name;
        // this.address = address;
        // this.taxCode = taxCode;
        this.transactions = transactions;
        this.totalMember = totalMember;
        this.totalBranch = totalBranch;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    // public String getCode() {
    // return code;
    // }

    // public void setCode(String code) {
    // this.code = code;
    // }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getCoverImgId() {
        return coverImgId;
    }

    public void setCoverImgId(String coverImgId) {
        this.coverImgId = coverImgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public String getAddress() {
    // return address;
    // }

    // public void setAddress(String address) {
    // this.address = address;
    // }

    // public String getTaxCode() {
    // return taxCode;
    // }

    // public void setTaxCode(String taxCode) {
    // this.taxCode = taxCode;
    // }

    public List<TransactionRelatedDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionRelatedDTO> transactions) {
        this.transactions = transactions;
    }

    public int getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(int totalMember) {
        this.totalMember = totalMember;
    }

    public int getTotalBranch() {
        return totalBranch;
    }

    public void setTotalBranch(int totalBranch) {
        this.totalBranch = totalBranch;
    }

}
