package com.vietqr.org.dto;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class VCardInputDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // for generate Vcard QR
    private String fullname;
    private String phoneNo;
    private String email;
    private String companyName;
    private String website;
    private String address;

    // for insert from API
    private String userId;
    private String additionalData;

    public VCardInputDTO() {
        super();
    }

    public VCardInputDTO(String fullname, String phoneNo, String email, String companyName, String website,
            String address, String userId, String additionalData) {
        this.fullname = fullname;
        this.phoneNo = phoneNo;
        this.email = email;
        this.companyName = companyName;
        this.website = website;
        this.address = address;
        this.userId = userId;
        this.additionalData = additionalData;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }


    public boolean isNull() {
        Field fields[] = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            try {
                Object value = f.get(this);
                if (value == null) {
                    return true;
                }
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
