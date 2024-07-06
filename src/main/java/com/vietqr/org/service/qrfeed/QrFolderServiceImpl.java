package com.vietqr.org.service.qrfeed;

import com.vietqr.org.dto.qrfeed.FolderInformationDTO;
import com.vietqr.org.dto.qrfeed.IFolderInformationDTO;
import com.vietqr.org.dto.qrfeed.IListQrFolderDTO;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.repository.QrFolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QrFolderServiceImpl implements QrFolderService {
    @Autowired
    QrFolderRepository repo;

    @Override
    public int insertQrFolder(QrFolderEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<IListQrFolderDTO> getListFolders(String userId) {
        return repo.getListFolders(userId);
    }

    @Override
    public int countQrFolder(String value,String userId) {
        return repo.countQrFolder(value,userId);
    }

    @Override
    public void updateQrFolder(String id, String description, String title) {
        repo.updateQrFolder(id, description, title);
    }

    @Override
    public QrFolderEntity getFolderById(String id) {
        return repo.getFolderById(id);
    }

    @Override
    public IFolderInformationDTO getFolderInfo(String folderId) {
        return repo.getFolderInfo(folderId);
    }

    @Override
    public IFolderInformationDTO getQrInFolder(String folderId) {
        return repo.getQrInFolder(folderId);
    }

    @Override
    public void deleteFolderById(String folderId) {
        repo.deleteByQrFolderId(folderId);
    }


}
