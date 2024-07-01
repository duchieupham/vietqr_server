package com.vietqr.org.dto.qrfeed;

public class UserInfoLinkOrTextDTO {
    private String email;
    private String content;
    private String phoneNo;
    private String fullName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public UserInfoLinkOrTextDTO(String email, String content, String phoneNo, String fullName) {
        this.email = email;
        this.content = content;
        this.phoneNo = phoneNo;
        this.fullName = fullName;
    }

    public UserInfoLinkOrTextDTO() {
    }
}
