package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class MerchantRequestDTO {
    @NotBlank
    private String name;
    private String address;
    private String vsoCode;
    private int type;
    private String userId;

    public MerchantRequestDTO() {
    }
//
//    public MerchantRequestDTO(String name, String address, String vsoCode,
//                              String type, String userId) {
//        this.name = name;
//        this.address = address;
//        VsoCode = vsoCode;
//        this.type = type;
//        this.userId = userId;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getVsoCode() {
//        return VsoCode;
//    }
//
//    public void setVsoCode(String vsoCode) {
//        VsoCode = vsoCode;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
