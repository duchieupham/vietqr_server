package com.vietqr.org.dto;

public class PageResponseDTO {
    private PageDTO metadata;

    private DataDTO data;

    public PageResponseDTO() {
        metadata = new PageDTO();
        data = new DataDTO();
    }

    public PageResponseDTO(DataDTO data) {
        metadata = new PageDTO();
        this.data = data;
    }

    public PageResponseDTO(PageDTO metadata, DataDTO data) {
        this.metadata = metadata;
        this.data = data;
    }

    public PageDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PageDTO metadata) {
        this.metadata = metadata;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }
}
