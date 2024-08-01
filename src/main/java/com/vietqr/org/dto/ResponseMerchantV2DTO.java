package com.vietqr.org.dto;

public class ResponseMerchantV2DTO {
    private String status;
    private Object masterData;
    private Object data;

    public ResponseMerchantV2DTO() {
    }

    public ResponseMerchantV2DTO(String status, Object masterData, Object data) {
        this.status = status;
        this.masterData = masterData;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getMasterData() {
        return masterData;
    }

    public void setMasterData(Object masterData) {
        this.masterData = masterData;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
