package com.vietqr.org.service;

import com.vietqr.org.dto.IRawTransRoleDTO;
import com.vietqr.org.dto.IRoleMemberDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionReceiveRoleService {
    List<IRoleMemberDTO> findRoleByIds(List<String> roleIds);

    List<IRawTransRoleDTO> getAllRole();
}
