package com.vietqr.org.service.qrfeed;

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
    public List<IListQrFolderDTO> getListFolders(String value, int offset, int size) {
        return repo.getListFolders(value, offset, size);
    }

    @Override
    public int countQrFolder(String value) {
        return repo.countQrFolder(value);
    }

    @Override
    public void updateQrFolder(String id, String description, String title) {
        repo.updateQrFolder(id, description, title);
    }

    @Override
    public QrFolderEntity getFolderById(String id) {
        return repo.getFolderById(id);
    }
}
