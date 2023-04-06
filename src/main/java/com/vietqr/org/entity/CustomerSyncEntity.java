package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CustomerSync")
public class CustomerSyncEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "ipAddress")
    private String ipAddress;

    @Column(name = "port")
    private String port;

    @Column(name = "suffixUrl")
    private String suffixUrl;

    @Column(name = "information")
    private String information;

    @Column(name = "active")
    private boolean active;

    public CustomerSyncEntity() {
        super();
    }

    public CustomerSyncEntity(String id, String username, String password, String ipAddress, String port,
            String suffixUrl, String information, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.ipAddress = ipAddress;
        this.port = port;
        this.suffixUrl = suffixUrl;
        this.information = information;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSuffixUrl() {
        return suffixUrl;
    }

    public void setSuffixUrl(String suffixUrl) {
        this.suffixUrl = suffixUrl;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
