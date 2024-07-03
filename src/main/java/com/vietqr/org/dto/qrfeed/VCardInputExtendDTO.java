package com.vietqr.org.dto.qrfeed;

import com.vietqr.org.dto.VCardInputDTO;

public class VCardInputExtendDTO extends VCardInputDTO {

    private String qrName;
    private String qrDescription;
    private String isPublic;
    private String style;
    private String theme;

    public VCardInputExtendDTO() {
    }

    public VCardInputExtendDTO(String qrName, String qrDescription, String isPublic, String style, String theme) {
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.isPublic = isPublic;
        this.style = style;
        this.theme = theme;
    }

    public VCardInputExtendDTO(String fullname, String phoneNo, String email, String companyName, String website, String address, String userId, String additionalData, String qrName, String qrDescription, String isPublic, String style, String theme) {
        super(fullname, phoneNo, email, companyName, website, address, userId, additionalData);
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.isPublic = isPublic;
        this.style = style;
        this.theme = theme;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getQrDescription() {
        return qrDescription;
    }

    public void setQrDescription(String qrDescription) {
        this.qrDescription = qrDescription;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
