package com.vietqr.org.dto;

import java.io.Serializable;

public class ContactScanResultDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String nickname;

    public ContactScanResultDTO() {
        super();
    }

    public ContactScanResultDTO(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
