package com.vietqr.org.dto;

public class AccountSettingBackUpDTO extends AccountSettingDTO{
    private boolean isVerifyEmail;
    private String notificationMessage;

    public AccountSettingBackUpDTO() {
    }

    public AccountSettingBackUpDTO(boolean isVerifyEmail) {
        this.isVerifyEmail = isVerifyEmail;
    }

    public AccountSettingBackUpDTO(String id, String userId, boolean guideWeb, boolean guideMobile, boolean voiceWeb, boolean voiceMobile, boolean voiceMobileKiot, boolean status, String edgeImgId, String footerImgId, int themeType, String themeImgUrl, String logoUrl, boolean keepScreenOn, int qrShowType, boolean isVerifyEmail) {
        super(id, userId, guideWeb, guideMobile, voiceWeb, voiceMobile, voiceMobileKiot, status, edgeImgId, footerImgId, themeType, themeImgUrl, logoUrl, keepScreenOn, qrShowType);
        this.isVerifyEmail = isVerifyEmail;
    }

    public boolean isVerifyEmail() {
        return isVerifyEmail;
    }

    public void setVerifyEmail(boolean verifyEmail) {
        isVerifyEmail = verifyEmail;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public AccountSettingBackUpDTO(boolean isVerifyEmail, String notificationMessage) {
        this.isVerifyEmail = isVerifyEmail;
        this.notificationMessage = notificationMessage;
    }

    public AccountSettingBackUpDTO(String id, String userId, boolean guideWeb, boolean guideMobile, boolean voiceWeb, boolean voiceMobile, boolean voiceMobileKiot, boolean status, String edgeImgId, String footerImgId, int themeType, String themeImgUrl, String logoUrl, boolean keepScreenOn, int qrShowType, boolean isVerifyEmail, String notificationMessage) {
        super(id, userId, guideWeb, guideMobile, voiceWeb, voiceMobile, voiceMobileKiot, status, edgeImgId, footerImgId, themeType, themeImgUrl, logoUrl, keepScreenOn, qrShowType);
        this.isVerifyEmail = isVerifyEmail;
        this.notificationMessage = notificationMessage;
    }
}
