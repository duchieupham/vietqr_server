package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

public class BusinessInformationInsertDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;
    private String address;
    private String taxCode;
    private MultipartFile image;
    private MultipartFile coverImage;
    private ArrayList<BusinessMemberInsertDTO> members;
    private ArrayList<BusinessBranchInsertDTO> branchs;

    public BusinessInformationInsertDTO() {
        super();
    }

    public BusinessInformationInsertDTO(String userId, String name, String address, String taxCode, MultipartFile image,
            MultipartFile coverImage, ArrayList<BusinessMemberInsertDTO> members,
            ArrayList<BusinessBranchInsertDTO> branchs) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.taxCode = taxCode;
        this.image = image;
        this.coverImage = coverImage;
        this.members = members;
        this.branchs = branchs;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public MultipartFile getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(MultipartFile coverImage) {
        this.coverImage = coverImage;
    }

    public ArrayList<BusinessMemberInsertDTO> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<BusinessMemberInsertDTO> members) {
        this.members = members;
    }

    public ArrayList<BusinessBranchInsertDTO> getBranchs() {
        return branchs;
    }

    public void setBranchs(ArrayList<BusinessBranchInsertDTO> branchs) {
        this.branchs = branchs;
    }

}
