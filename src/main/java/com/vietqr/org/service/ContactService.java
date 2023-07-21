package com.vietqr.org.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.vietqr.org.entity.ContactEntity;

@Service
public interface ContactService {

    public int insertContact(ContactEntity entity);

    public List<ContactEntity> getContactApprovedByUserId(String userId);

    public List<ContactEntity> getContactPendingByUserId(String userId);

    public String checkExistedRecord(String userId, String value, int type);

    public void updateContactStatus(int status, String id);

    public void deleteContactById(String id);

    public ContactEntity getContactById(String id);

    public void updateContact(String nickname, int type, String additionalData, String id);
}
