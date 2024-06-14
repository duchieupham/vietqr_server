package com.vietqr.org.service;

import com.vietqr.org.dto.IAccountSystemDTO;
import com.vietqr.org.dto.UserRequestDTO;
import com.vietqr.org.dto.UserResponseDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountSystemEntity;

@Service
public interface AccountSystemService {
    public int insertNewAdmin(AccountSystemEntity entity);

    public AccountSystemEntity loginAdmin(String username, String password);

    public String checkExistedAdmin(String id);

    IAccountSystemDTO findAdminById(String adminId);

    boolean resetUserPassword(String phoneNo, String newPassword);

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    boolean updateUserStatus(String userId, boolean status);

}
