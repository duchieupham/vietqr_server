package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankSyncWpDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String bankId;
    private boolean syncWp;

    public AccountBankSyncWpDTO() {
        super();
    }

    public AccountBankSyncWpDTO(String bankId, boolean syncWp) {
        this.bankId = bankId;
        this.syncWp = syncWp;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public boolean isSyncWp() {
        return syncWp;
    }

    public void setSyncWp(boolean syncWp) {
        this.syncWp = syncWp;
    }

    @Override
    public String toString() {
        return "AccountBankSyncWpDTO [bankId=" + bankId + ", syncWp=" + syncWp + "]";
    }

}
