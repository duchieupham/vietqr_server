package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.IListQrFolderDTO;
import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QrFolderService {

    public int insertQrFolder(QrFolderEntity entity);

    public List<IListQrFolderDTO> getListFolders(String value, int offset, int size);

    public int countQrFolder(String value);

    public void updateQrFolder(String id, String description, String title);

    public QrFolderEntity getFolderById(String id);



    void deleteFolderById(String folderId);

}