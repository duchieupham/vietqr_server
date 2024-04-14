package com.vietqr.org.dto;

import java.util.ArrayList;
import java.util.List;

public class PageResponseDTO {
    private PageDTO metadata;
    private List<?> data;
    private Object extraData;

    public PageResponseDTO() {
        metadata = new PageDTO();
        data = new ArrayList<>();
        extraData = new Object();
    }

    public PageResponseDTO(PageDTO metadata, List<?> data, Object extraData) {
        this.metadata = metadata;
        this.data = data;
        this.extraData = extraData;
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

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }
}
