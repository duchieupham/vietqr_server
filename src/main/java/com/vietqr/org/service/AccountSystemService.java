package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountSystemEntity;

@Service
public interface AccountSystemService {
    public int insertNewAdmin(AccountSystemEntity entity);

    public AccountSystemEntity loginAdmin(String username, String password);

    public String checkExistedAdmin(String id);

    IAccountSystemDTO findAdminById(String adminId);

    boolean resetUserPassword(String phoneNo, String newPassword);

    boolean updateUserStatus(String id, boolean status);

    void updateUser(String userId, UserUpdateRequestDTO userRequestDTO);

    void updateAccountEmail(String userId, String email, int type);
}
