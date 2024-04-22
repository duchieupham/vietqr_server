package com.vietqr.org.dto;

import java.util.List;

public class PageResDTO {
    private PageDTO metadata;
    private List<?> items;

    public PageResDTO() {
    }

    public PageResDTO(PageDTO metadata, List<?> items) {
        this.metadata = metadata;
        this.items = items;
    }

    public PageDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PageDTO metadata) {
        this.metadata = metadata;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }
}
