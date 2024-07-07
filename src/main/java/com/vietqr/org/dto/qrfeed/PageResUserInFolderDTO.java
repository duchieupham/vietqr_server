package com.vietqr.org.dto.qrfeed;

import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;

import java.util.List;

public class PageResUserInFolderDTO extends PageResDTO {
    private int countUsers;

    public PageResUserInFolderDTO() {
    }

    public PageResUserInFolderDTO(int countUsers) {
        this.countUsers = countUsers;
    }

    public PageResUserInFolderDTO(PageDTO metadata, List<?> data, int countUsers) {
        super(metadata, data);
        this.countUsers = countUsers;
    }

    public PageResUserInFolderDTO(PageDTO metadata, List<?> data) {
        super(metadata, data);
    }

    public int getCountUsers() {
        return countUsers;
    }

    public void setCountUsers(int countUsers) {
        this.countUsers = countUsers;
    }
}
