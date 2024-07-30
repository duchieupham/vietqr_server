package com.vietqr.org.dto;

import java.util.List;

public class PageMerchantDTO {
    private PageDTO metadata;
    private Object data;

    public PageMerchantDTO() {
    }

    public PageMerchantDTO(PageDTO metadata, Object data) {
        this.metadata = metadata;
        this.data = data;
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
