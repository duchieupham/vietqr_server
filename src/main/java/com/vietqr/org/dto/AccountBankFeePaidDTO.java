package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankFeePaidDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String month;

    public static class AccountFeeDTO implements Serializable {

        /**
        *
        */
        private static final long serialVersionUID = 1L;

        private String bankId;
        private String serviceFeeId;

        public AccountFeeDTO() {
            super();
        }

        public AccountFeeDTO(String bankId, String serviceFeeId) {
            this.bankId = bankId;
            this.serviceFeeId = serviceFeeId;
        }

        public String getBankId() {
            return bankId;
        }

        public void setBankId(String bankId) {
            this.bankId = bankId;
        }

        public String getServiceFeeId() {
            return serviceFeeId;
        }

        public void setServiceFeeId(String serviceFeeId) {
            this.serviceFeeId = serviceFeeId;
        }

    }
}
