package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.UserRoleDTO;
import com.vietqr.org.entity.qrfeed.QrUserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrUserService {
    public int insertQrUser(QrUserEntity entity);

    void updateRoleUser(String folderId, String userId, String role);

    void deleteUserFromFolder(String folderId, String userId);

    void updateRoleUser(String folderId, List<UserRoleDTO> userRoles);
}
