package com.vietqr.org.dto;

import java.util.List;

public class AdminListUserDTO {
    private List<AdminListUserAccountResponseDTO> userList;

    public List<AdminListUserAccountResponseDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<AdminListUserAccountResponseDTO> userList) {
        this.userList = userList;
    }


    public AdminListUserDTO() {
    }

    public AdminListUserDTO(List<AdminListUserAccountResponseDTO> userList) {
        this.userList = userList;
    }
}
