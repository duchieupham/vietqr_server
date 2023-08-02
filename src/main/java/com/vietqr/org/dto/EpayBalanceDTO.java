package com.vietqr.org.dto;

import java.io.Serializable;

public class EpayBalanceDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String cashBalance;
    private String bonusBalance;
    private String holdingBalance;
    private String availableBalance;

    public EpayBalanceDTO() {
        super();
    }

    public EpayBalanceDTO(String cashBalance, String bonusBalance, String holdingBalance, String availableBalance) {
        this.cashBalance = cashBalance;
        this.bonusBalance = bonusBalance;
        this.holdingBalance = holdingBalance;
        this.availableBalance = availableBalance;
    }

    public String getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(String cashBalance) {
        this.cashBalance = cashBalance;
    }

    public String getBonusBalance() {
        return bonusBalance;
    }

    public void setBonusBalance(String bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    public String getHoldingBalance() {
        return holdingBalance;
    }

    public void setHoldingBalance(String holdingBalance) {
        this.holdingBalance = holdingBalance;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    @Override
    public String toString() {
        return "EpayBalanceDTO [cashBalance=" + cashBalance + ", bonusBalance=" + bonusBalance + ", holdingBalance="
                + holdingBalance + ", availableBalance=" + availableBalance + "]";
    }

}
