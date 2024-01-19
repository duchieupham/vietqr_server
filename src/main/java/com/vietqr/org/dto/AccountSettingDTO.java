package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountSettingDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    // guideline
    private boolean guideWeb;
    private boolean guideMobile;
    // voice
    private boolean voiceWeb;
    private boolean voiceMobile;
    private boolean voiceMobileKiot;
    // status for checking deactive user
    private boolean status;
    // image kiot
    private String edgeImgId;
    private String footerImgId;
    // theme
    private int themeType;
    private String themeImgUrl;
    // logo
    private String logoUrl;
    // keep screen on
    private boolean keepScreenOn;
    // qr show type (rectangular or square)
    private int qrShowType;

    public AccountSettingDTO() {
        super();
    }

    public AccountSettingDTO(String id, String userId, boolean guideWeb, boolean guideMobile, boolean voiceWeb,
            boolean voiceMobile, boolean voiceMobileKiot, boolean status, String edgeImgId, String footerImgId,
            int themeType, String themeImgUrl, String logoUrl, boolean keepScreenOn, int qrShowType) {
        this.id = id;
        this.userId = userId;
        this.guideWeb = guideWeb;
        this.guideMobile = guideMobile;
        this.voiceWeb = voiceWeb;
        this.voiceMobile = voiceMobile;
        this.voiceMobileKiot = voiceMobileKiot;
        this.status = status;
        this.edgeImgId = edgeImgId;
        this.footerImgId = footerImgId;
        this.themeType = themeType;
        this.themeImgUrl = themeImgUrl;
        this.logoUrl = logoUrl;
        this.keepScreenOn = keepScreenOn;
        this.qrShowType = qrShowType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isGuideWeb() {
        return guideWeb;
    }

    public void setGuideWeb(boolean guideWeb) {
        this.guideWeb = guideWeb;
    }

    public boolean isGuideMobile() {
        return guideMobile;
    }

    public void setGuideMobile(boolean guideMobile) {
        this.guideMobile = guideMobile;
    }

    public boolean isVoiceWeb() {
        return voiceWeb;
    }

    public void setVoiceWeb(boolean voiceWeb) {
        this.voiceWeb = voiceWeb;
    }

    public boolean isVoiceMobile() {
        return voiceMobile;
    }

    public void setVoiceMobile(boolean voiceMobile) {
        this.voiceMobile = voiceMobile;
    }

    public boolean isVoiceMobileKiot() {
        return voiceMobileKiot;
    }

    public void setVoiceMobileKiot(boolean voiceMobileKiot) {
        this.voiceMobileKiot = voiceMobileKiot;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEdgeImgId() {
        return edgeImgId;
    }

    public void setEdgeImgId(String edgeImgId) {
        this.edgeImgId = edgeImgId;
    }

    public String getFooterImgId() {
        return footerImgId;
    }

    public void setFooterImgId(String footerImgId) {
        this.footerImgId = footerImgId;
    }

    public int getThemeType() {
        return themeType;
    }

    public void setThemeType(int themeType) {
        this.themeType = themeType;
    }

    public String getThemeImgUrl() {
        return themeImgUrl;
    }

    public void setThemeImgUrl(String themeImgUrl) {
        this.themeImgUrl = themeImgUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }

    public int getQrShowType() {
        return qrShowType;
    }

    public void setQrShowType(int qrShowType) {
        this.qrShowType = qrShowType;
    }

}
