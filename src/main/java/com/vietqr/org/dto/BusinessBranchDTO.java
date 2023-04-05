package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class BusinessBranchDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private String address;
    private int totalMember;
    private MemberDTO manager;
    private List<BusinessBankDTO> banks;

    public BusinessBranchDTO() {
        super();
    }

    public BusinessBranchDTO(String id, String code, String name, String address, int totalMember,
            MemberDTO manager, List<BusinessBankDTO> banks) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.totalMember = totalMember;
        this.manager = manager;
        this.banks = banks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(int totalMember) {
        this.totalMember = totalMember;
    }

    public MemberDTO getManager() {
        return manager;
    }

    public void setManager(MemberDTO manager) {
        this.manager = manager;
    }

    public List<BusinessBankDTO> getBanks() {
        return banks;
    }

    public void setBanks(List<BusinessBankDTO> banks) {
        this.banks = banks;
    }

}
