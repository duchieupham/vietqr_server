package com.vietqr.org.service;

import com.vietqr.org.dto.IAccountSystemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.AccountSystemEntity;
import com.vietqr.org.repository.AccountSystemRepository;

@Service
public class AccountSystemServiceImpl implements AccountSystemService {

    @Autowired
    AccountSystemRepository repo;

    @Override
    public int insertNewAdmin(AccountSystemEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public AccountSystemEntity loginAdmin(String username, String password) {
        return repo.loginAdmin(username, password);
    }

    @Override
    public String checkExistedAdmin(String id) {
        return repo.checkExistedAdmin(id);
    }

    @Override
    public IAccountSystemDTO findAdminById(String adminId) {
        return repo.findAdminById(adminId);
    }
    @Override
    public boolean resetUserPassword(String phoneNo, String newPassword) {
        int updateRows = repo.updateUserPassword(phoneNo, newPassword);
        return updateRows > 0;
    }
}
