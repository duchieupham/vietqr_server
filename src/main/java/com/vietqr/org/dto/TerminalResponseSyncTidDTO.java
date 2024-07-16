package com.vietqr.org.dto;

import javax.xml.crypto.Data;
import java.util.List;

public class TerminalResponseSyncTidDTO {

    private String clientMessageId;
    private Data data;
    private String errorCode;
    private List<String> errorDesc;

    public TerminalResponseSyncTidDTO() {
    }

    public TerminalResponseSyncTidDTO(String clientMessageId, Data data, String errorCode, List<String> errorDesc) {
        this.clientMessageId = clientMessageId;
        this.data = data;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public String getClientMessageId() {
        return clientMessageId;
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

    public List<String> getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(List<String> errorDesc) {
        this.errorDesc = errorDesc;
    }

    public static class Data {
        private List<Result> result;

        public List<Result> getResult() {
            return result;
        }

        public void setResult(List<Result> result) {
            this.result = result;
        }
    }

    public static class Result {
        private int seq;
        private String terminalName;
        private String terminalId;
        private String syncStatus;
        private String syncError;
        private String message;

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public String getTerminalName() {
            return terminalName;
        }

        public void setTerminalName(String terminalName) {
            this.terminalName = terminalName;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public String getSyncStatus() {
            return syncStatus;
        }

        public void setSyncStatus(String syncStatus) {
            this.syncStatus = syncStatus;
        }

        public String getSyncError() {
            return syncError;
        }

        public void setSyncError(String syncError) {
            this.syncError = syncError;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
