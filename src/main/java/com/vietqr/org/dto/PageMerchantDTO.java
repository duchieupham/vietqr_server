package com.vietqr.org.dto;

import java.util.List;

public class PageMerchantDTO {
    private PageDTO metadata;
    private Object masterData;
    private Object data;

    public PageMerchantDTO() {
    }

    public PageMerchantDTO(PageDTO metadata, Object masterData, Object data) {
        this.metadata = metadata;
        this.masterData = masterData;
        this.data = data;
    }

    public Object getMasterData() {
        return masterData;
    }

    public void setMasterData(Object masterData) {
        this.masterData = masterData;
    }

    public PageDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PageDTO metadata) {
        this.metadata = metadata;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
