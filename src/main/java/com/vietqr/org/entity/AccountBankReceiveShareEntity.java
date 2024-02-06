package com.vietqr.org.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "AccountBankReceiveShare")
public class AccountBankReceiveShareEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "userId")
    private String userId;
    @Column(name = "bankId")
    private String bankId;
    @Column(name = "isOwner")
    private boolean isOwner;
    @Column(name = "terminalId")
    private String terminalId;
    @Column(name = "qrCode")
    private String qrCode;
    @Column(name = "traceTransfer")
    private String traceTransfer;

    public AccountBankReceiveShareEntity() {
        super();
    }

    public AccountBankReceiveShareEntity(String id, String userId, String bankId, boolean isOwner, String terminalId, String qrCode, String traceTransfer) {
        this.id = id;
        this.userId = userId;
        this.bankId = bankId;
        this.isOwner = isOwner;
        this.terminalId = terminalId;
        this.qrCode = qrCode;
        this.traceTransfer = traceTransfer;
    }

    public String getTraceTransfer() {
        return traceTransfer;
    }

    public void setTraceTransfer(String traceTransfer) {
        this.traceTransfer = traceTransfer;
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

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public String toString() {
        return "AccountBankReceiveShare [id=" + id + ", userId=" + userId + ", bankId=" + bankId
                + ", isOwner=" + isOwner + ", terminalId=" + terminalId + ", qrCode=" + qrCode + "]";
    }
}
