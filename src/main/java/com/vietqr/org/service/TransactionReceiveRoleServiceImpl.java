package com.vietqr.org.service;

import com.vietqr.org.dto.RoleMemberDTO;
import com.vietqr.org.repository.TransactionReceiveRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionReceiveRoleServiceImpl implements TransactionReceiveRoleService {
    @Autowired
    private TransactionReceiveRoleRepository repo;
    @Override
    public List<RoleMemberDTO> findRoleByIds(List<String> roleIds) {
        return repo.getRoleByIds(roleIds);
    }
}
