package com.vietqr.org.service.qrfeed;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrFolderUserService {

    public void addUserIds(String qrFolderId, List<String> userIds);

}
