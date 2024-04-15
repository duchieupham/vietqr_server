package com.vietqr.org.dto;

public class PageDTO {
    private int page;
    private int size;
    private int totalPage;
    private int totalElement;


    public PageDTO() {
        page = 1;
        size = 20;
        totalPage = 1;
        totalElement = 0;
    }

    public PageDTO(int page, int size, int totalPage, int totalElement) {
        this.page = page;
        this.size = size;
        this.totalPage = totalPage;
        this.totalElement = totalElement;
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

    public void setTotalElement(int totalElement) {
        this.totalElement = totalElement;
    }
}
