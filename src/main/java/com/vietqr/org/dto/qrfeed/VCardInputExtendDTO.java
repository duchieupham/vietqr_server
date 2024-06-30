package com.vietqr.org.dto.qrfeed;

import com.vietqr.org.dto.VCardInputDTO;

public class VCardInputExtendDTO extends VCardInputDTO {
    private String userId;

    public VCardInputExtendDTO(String userId) {
        this.userId = userId;
    }

    public VCardInputExtendDTO(String fullname, String phoneNo, String email, String companyName, String website, String address, String userId, String additionalData, String userId1) {
        super(fullname, phoneNo, email, companyName, website, address, userId, additionalData);
        this.userId = userId1;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
