package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AccountSetting")
public class AccountSettingEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "userId")
    private String userId;

    @Column(name = "guideWeb")
    private boolean guideWeb;

    @Column(name = "guideMobile")
    private boolean guideMobile;

    @Column(name = "voiceWeb")
    private boolean voiceWeb;

    @Column(name = "voiceMobile")
    private boolean voiceMobile;

    @Column(name = "voiceMobileKiot")
    private boolean voiceMobileKiot;

    @Column(name = "status")
    private boolean status;

    @Column(name = "lastLogin")
    private long lastLogin;

    @Column(name = "accessCount")
    private long accessCount;

    @Column(name = "edgeImgId")
    private String edgeImgId;

    @Column(name = "footerImgId")
    private String footerImgId;

    @Column(name = "themeType")
    private int themeType;

    @Column(name = "keepScreenOn")
    private boolean keepScreenOn;

    // 0: rectangular
    // 1: square
    @Column(name = "qrShowType")
    private int qrShowType;

    @Column(name = "notificationMobile")
    private boolean notificationMobile;

    @Column(name = "notificationMessage")
    private String notificationMessage;

    @Column(name = "dataConfig", columnDefinition = "JSON")
    private String dataConfig;

    public AccountSettingEntity() {
        super();
    }

    public AccountSettingEntity(String id, String userId, boolean guideWeb, boolean guideMobile, boolean voiceWeb,
            boolean voiceMobile, boolean voiceMobileKiot, boolean status, long lastLogin, long accessCount,
            String edgeImgId, String footerImgId,
            int themeType, boolean keepScreenOn, int qrShowType) {
        this.id = id;
        this.userId = userId;
        this.guideWeb = guideWeb;
        this.guideMobile = guideMobile;
        this.voiceWeb = voiceWeb;
        this.voiceMobile = voiceMobile;
        this.voiceMobileKiot = voiceMobileKiot;
        this.status = status;
        this.lastLogin = lastLogin;
        this.accessCount = accessCount;
        this.edgeImgId = edgeImgId;
        this.footerImgId = footerImgId;
        this.themeType = themeType;
        this.keepScreenOn = keepScreenOn;
        this.qrShowType = qrShowType;
    }

    public AccountSettingEntity(String id, String userId, boolean guideWeb, boolean guideMobile, boolean voiceWeb, boolean voiceMobile, boolean voiceMobileKiot, boolean status, long lastLogin, long accessCount, String edgeImgId, String footerImgId, int themeType, boolean keepScreenOn, int qrShowType, boolean notificationMobile, String notificationMessage) {
        this.id = id;
        this.userId = userId;
        this.guideWeb = guideWeb;
        this.guideMobile = guideMobile;
        this.voiceWeb = voiceWeb;
        this.voiceMobile = voiceMobile;
        this.voiceMobileKiot = voiceMobileKiot;
        this.status = status;
        this.lastLogin = lastLogin;
        this.accessCount = accessCount;
        this.edgeImgId = edgeImgId;
        this.footerImgId = footerImgId;
        this.themeType = themeType;
        this.keepScreenOn = keepScreenOn;
        this.qrShowType = qrShowType;
        this.notificationMobile = notificationMobile;
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
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

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
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

    public boolean isNotificationMobile() {
        return notificationMobile;
    }

    public void setNotificationMobile(boolean notificationMobile) {
        this.notificationMobile = notificationMobile;
    }

    public String getDataConfig() {
        return dataConfig;
    }

    public void setDataConfig(String dataConfig) {
        this.dataConfig = dataConfig;
    }
}
