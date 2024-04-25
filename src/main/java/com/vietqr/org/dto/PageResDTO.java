package com.vietqr.org.dto;

import java.util.List;

public class PageResDTO {
    private PageDTO metadata;
    private List<?> data;

    public PageResDTO() {
    }

    public PageResDTO(PageDTO metadata, List<?> data) {
        this.metadata = metadata;
        this.data = data;
    }

    public PageDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PageDTO metadata) {
        this.metadata = metadata;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
