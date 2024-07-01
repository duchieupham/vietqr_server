package com.vietqr.org.dto.qrfeed;

import com.vietqr.org.dto.VCardInputDTO;

public class VCardInputExtendDTO extends VCardInputDTO {
    private int style;
    private int theme;

    public VCardInputExtendDTO() {
    }

    public VCardInputExtendDTO(int style, int theme) {
        this.style = style;
        this.theme = theme;
    }

    public VCardInputExtendDTO(String fullname, String phoneNo, String email, String companyName, String website, String address, String userId, String additionalData, int style, int theme) {
        super(fullname, phoneNo, email, companyName, website, address, userId, additionalData);
        this.style = style;
        this.theme = theme;
    }

    public VCardInputExtendDTO(String fullname, String phoneNo, String email, String companyName, String website, String address, String userId, String additionalData) {
        super(fullname, phoneNo, email, companyName, website, address, userId, additionalData);
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }
}
