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

    public AccountSettingEntity() {
        super();
    }

    public AccountSettingEntity(String id, String userId, boolean guideWeb, boolean guideMobile, boolean voiceWeb,
            boolean voiceMobile, boolean voiceMobileKiot, boolean status) {
        this.id = id;
        this.userId = userId;
        this.guideWeb = guideWeb;
        this.guideMobile = guideMobile;
        this.voiceWeb = voiceWeb;
        this.voiceMobile = voiceMobile;
        this.voiceMobileKiot = voiceMobileKiot;
        this.status = status;
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

}
