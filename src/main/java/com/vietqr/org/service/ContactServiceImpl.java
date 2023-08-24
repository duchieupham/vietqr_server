package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.ContactRechargeDTO;
import com.vietqr.org.entity.ContactEntity;
import com.vietqr.org.repository.ContactRepository;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    ContactRepository repo;

    @Override
    public int insertContact(ContactEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<ContactEntity> getContactApprovedByUserId(String userId) {
        return repo.getContactApprovedByUserId(userId);
    }

    @Override
    public List<ContactEntity> getContactPendingByUserId(String userId) {
        return repo.getContactPendingByUserId(userId);
    }

    @Override
    public String checkExistedRecord(String userId, String value, int type) {
        return repo.checkExistedRecord(userId, value, type);
    }

    @Override
    public void updateContactStatus(int status, String id) {
        repo.updateContactStatus(status, id);
    }

    @Override
    public void deleteContactById(String id) {
        repo.deleteContactById(id);
    }

    @Override
    public ContactEntity getContactById(String id) {
        return repo.getContactById(id);
    }

    @Override
    public void updateContact(String nickname, int type, String additionalData, String id) {
        repo.udpateContact(nickname, type, additionalData, id);
    }

    @Override
    public List<ContactRechargeDTO> getContactRecharge(String userId) {
        return repo.getContactRecharge(userId);
    }

    @Override
    public String getImgIdByWalletId(String walletId) {
        return repo.getImgIdByWalletId(walletId);
    }

    @Override
    public List<ContactEntity> getContactApprovedByUserIdWithPagging(String userId, int offset) {
        return repo.getContactApprovedByUserIdWithPagging(userId, offset);
    }

    @Override
    public List<ContactEntity> getContactApprovedByUserIdAndStatusWithPagging(String userId, int type, int offset) {
        return repo.getContactApprovedByUserIdAndStatusWithPagging(userId, type, offset);
    }

    @Override
    public void updateContactMultipart(String nickname, String additionalData, int colorType, String id) {
        repo.udpateContactMultipart(nickname, additionalData, colorType, id);
    }

    @Override
    public void updateImgIdById(String imgId, String id) {
        repo.updateImgIdById(imgId, id);
    }

}
