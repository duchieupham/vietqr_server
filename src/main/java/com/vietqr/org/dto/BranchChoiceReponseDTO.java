package com.vietqr.org.dto;

import java.io.Serializable;

public class BranchChoiceReponseDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String branchId;
    private String name;
    private String address;

    public BranchChoiceReponseDTO() {
        super();
    }

    public BranchChoiceReponseDTO(String branchId, String name, String address) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
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

}
