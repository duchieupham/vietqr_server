package com.vietqr.org.service.qrfeed;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrWalletFolderService {
    public void addQrWalletIds(String qrFolderId, List<String> qrWalletIds);

}