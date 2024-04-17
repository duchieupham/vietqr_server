package com.vietqr.org.service;

import com.vietqr.org.dto.IRoleMemberDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransReceiveRoleService {
    List<IRoleMemberDTO> getRoleByIds(List<String> receiveRoles);
}
