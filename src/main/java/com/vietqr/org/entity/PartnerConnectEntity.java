package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PartnerConnect")
public class PartnerConnectEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "service")
    private String service;

    @Column(name = "url1")
    private String url1;

    @Column(name = "url2")
    private String url2;

    @Column(name = "url3")
    private String url3;

    @Column(name = "url4")
    private String url4;

    @Column(name = "url5")
    private String url5;

    @Column(name = "usernameBasic")
    private String usernameBasic;

    @Column(name = "passwordBasic")
    private String passwordBasic;

    public PartnerConnectEntity() {
        super();
    }

    public PartnerConnectEntity(String id, String service, String url1, String url2, String url3, String url4,
            String url5, String usernameBasic, String passwordBasic) {
        this.id = id;
        this.service = service;
        this.url1 = url1;
        this.url2 = url2;
        this.url3 = url3;
        this.url4 = url4;
        this.url5 = url5;
        this.usernameBasic = usernameBasic;
        this.passwordBasic = passwordBasic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    public String getUrl3() {
        return url3;
    }

    public void setUrl3(String url3) {
        this.url3 = url3;
    }

    public String getUrl4() {
        return url4;
    }

    public void setUrl4(String url4) {
        this.url4 = url4;
    }

    public String getUrl5() {
        return url5;
    }

    public void setUrl5(String url5) {
        this.url5 = url5;
    }

    public String getUsernameBasic() {
        return usernameBasic;
    }

    public void setUsernameBasic(String usernameBasic) {
        this.usernameBasic = usernameBasic;
    }

    public String getPasswordBasic() {
        return passwordBasic;
    }

    public void setPasswordBasic(String passwordBasic) {
        this.passwordBasic = passwordBasic;
    }

}
