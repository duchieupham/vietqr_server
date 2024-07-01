package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IUserInFolderDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrFolderUserService {

    public void addUserIds(String qrFolderId, List<String> userIds, String userId);
    public List<IUserInFolderDTO> getUserInFolder(String qrFolderId);


}
