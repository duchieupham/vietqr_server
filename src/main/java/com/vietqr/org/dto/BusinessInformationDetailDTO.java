package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class BusinessInformationDetailDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String address;
    private String code;
    private String imgId;
    private String coverImgId;
    private String taxCode;
    private boolean isActive;
    private int userRole;
    private List<MemberDTO> managers;
    private List<BusinessBranchDTO> branchs;
    private List<TransactionRelatedDTO> transactions;

    public BusinessInformationDetailDTO() {
        super();
    }

    public BusinessInformationDetailDTO(String id, String name, String address, String code, String imgId,
            String coverImgId, String taxCode, boolean isActive, int userRole, List<MemberDTO> managers,
            List<BusinessBranchDTO> branchs, List<TransactionRelatedDTO> transactions) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.code = code;
        this.imgId = imgId;
        this.coverImgId = coverImgId;
        this.taxCode = taxCode;
        this.isActive = isActive;
        this.userRole = userRole;
        this.managers = managers;
        this.branchs = branchs;
        this.transactions = transactions;
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

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public List<MemberDTO> getManagers() {
        return managers;
    }

    public void setManagers(List<MemberDTO> managers) {
        this.managers = managers;
    }

    public List<BusinessBranchDTO> getBranchs() {
        return branchs;
    }

    public void setBranchs(List<BusinessBranchDTO> branchs) {
        this.branchs = branchs;
    }

    public List<TransactionRelatedDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionRelatedDTO> transactions) {
        this.transactions = transactions;
    }

}