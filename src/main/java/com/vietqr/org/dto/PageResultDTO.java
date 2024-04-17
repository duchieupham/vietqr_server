package com.vietqr.org.dto;

import java.util.ArrayList;
import java.util.List;

public class PageResultDTO {
    private int page;
    private int size;
    private int totalPage;
    private long totalElement;
    private List<?> items;

    public PageResultDTO(int page, int size, int totalPage, long totalElement, List<?> items) {
        this.page = page;
        this.size = size;
        this.totalPage = totalPage;
        this.totalElement = totalElement;
        this.items = items;
    }

    public PageResultDTO() {
        page = 1;
        size = 20;
        totalPage = 0;
        totalElement = 0;
        items = new ArrayList<>();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalElement() {
        return totalElement;
    }

    public void setTotalElement(long totalElement) {
        this.totalElement = totalElement;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }
}
