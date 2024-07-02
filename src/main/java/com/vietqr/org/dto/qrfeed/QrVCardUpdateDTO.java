package com.vietqr.org.dto.qrfeed;

import com.vietqr.org.dto.VCardInputDTO;

import java.io.Serializable;

public class QrVCardUpdateDTO extends VCardInputDTO {
    private String id;
    private String qrTitle;
    private String qrDescription;
    private int isPublic;
    private int style;
    private int theme;

    public QrVCardUpdateDTO() {
    }

    public QrVCardUpdateDTO(String fullname, String phoneNo, String email, String companyName, String website, String address, String userId, String additionalData, int isPublic, int style, int theme) {
        super(fullname, phoneNo, email, companyName, website, address, userId, additionalData);
        this.isPublic = isPublic;
        this.style = style;
        this.theme = theme;
    }

    public String getQrTitle() {
        return qrTitle;
    }

    public void setQrTitle(String qrTitle) {
        this.qrTitle = qrTitle;
    }

    public String getQrDescription() {
        return qrDescription;
    }

    public void setQrDescription(String qrDescription) {
        this.qrDescription = qrDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
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
