package com.vietqr.org.service;

import com.vietqr.org.dto.RoleMemberDTO;
import com.vietqr.org.repository.TransactionReceiveRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransReceiveRoleServiceImpl implements TransReceiveRoleService {
    @Autowired
    private TransactionReceiveRoleRepository repo;

    @Override
    public List<RoleMemberDTO> getRoleByIds(List<String> receiveRoles) {
        return repo.getRoleByIds(receiveRoles);
    }
}
