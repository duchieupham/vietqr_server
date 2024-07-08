package com.vietqr.org.service.qrfeed;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrWalletFolderService {
    public void addQrWalletIds(String qrFolderId, List<String> qrWalletIds, String userId);

    public void addQrWalletsToFolder(String folderId, String userId, List<String> qrIds);

    public void addQrWalletsInFolder(String folderId, List<String> qrIds);

    int countQrFolder(String folderId);
}
