package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountSearchMemberDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String phoneNo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String imgId;
    private int existed;

    public AccountSearchMemberDTO() {
        super();
    }

    public AccountSearchMemberDTO(String id, String phoneNo, String firstName, String middleName, String lastName,
            String imgId, int existed) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.imgId = imgId;
        this.existed = existed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

}
