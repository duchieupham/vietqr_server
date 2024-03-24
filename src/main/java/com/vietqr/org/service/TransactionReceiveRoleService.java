package com.vietqr.org.service;

import com.vietqr.org.dto.RoleMemberDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionReceiveRoleService {
    List<RoleMemberDTO> findRoleByIds(List<String> roleIds);
}
