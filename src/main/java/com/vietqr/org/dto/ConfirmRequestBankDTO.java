package com.vietqr.org.dto;

import java.io.Serializable;

public class ConfirmRequestBankDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String clientMessageId;
    private Data data;
    private String errorCode;

    public String getClientMessageId() {
        return clientMessageId;
    }

    public ConfirmRequestBankDTO(String clientMessageId, Data data, String errorCode) {
        this.clientMessageId = clientMessageId;
        this.data = data;
        this.errorCode = errorCode;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public static class Data implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private String requestId;
        // private String status;

        public Data() {
            super();
        }

        public Data(String requestId) {
            this.requestId = requestId;
            // this.status = status;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        // public String getStatus() {
        // return status;
        // }

        // public void setStatus(String status) {
        // this.status = status;
        // }

    }
}