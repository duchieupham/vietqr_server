package com.vietqr.org.dto;

import java.util.List;

public class PageResListUserDTO {
    private PageDTO metadata;
    private long totalUsers;
    private long totalUsersToday;
    private List<?> data;

    public PageResListUserDTO() {
    }

    public PageResListUserDTO(PageDTO metadata, long totalUsers, long totalUsersToday, List<?> data) {
        this.metadata = metadata;
        this.totalUsers = totalUsers;
        this.totalUsersToday = totalUsersToday;
        this.data = data;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalUsersToday() {
        return totalUsersToday;
    }

    public void setTotalUsersToday(long totalUsersToday) {
        this.totalUsersToday = totalUsersToday;
    }

    public PageDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PageDTO metadata) {
        this.metadata = metadata;
    }

}
