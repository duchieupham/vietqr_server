package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountWalletDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String amount;
    private String point;
    private String sharingCode;
    private String walletId;
    private boolean enableService;

    public AccountWalletDTO() {
        super();
    }

    public AccountWalletDTO(String amount, String point, String sharingCode, String walletId, boolean enableService) {
        this.amount = amount;
        this.point = point;
        this.sharingCode = sharingCode;
        this.walletId = walletId;
        this.enableService = enableService;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getSharingCode() {
        return sharingCode;
    }

    public void setSharingCode(String sharingCode) {
        this.sharingCode = sharingCode;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public boolean isEnableService() {
        return enableService;
    }

    public void setEnableService(boolean enableService) {
        this.enableService = enableService;
    }

}
