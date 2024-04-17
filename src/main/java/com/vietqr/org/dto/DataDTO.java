package com.vietqr.org.dto;

import java.util.ArrayList;
import java.util.List;

public class DataDTO {
    private List<?> items;
    private Object extraData;

    public DataDTO() {
        items = new ArrayList<>();
        extraData = new Object();
    }

    public DataDTO(List<?> items, Object extraData) {
        this.items = items;
        this.extraData = extraData;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }
}
