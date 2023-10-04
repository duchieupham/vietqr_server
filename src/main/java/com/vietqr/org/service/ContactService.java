package com.vietqr.org.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.vietqr.org.dto.ContactRechargeDTO;
import com.vietqr.org.entity.ContactEntity;

@Service
public interface ContactService {

    public int insertContact(ContactEntity entity);

    public int insertAllContact(List<ContactEntity> entities);

    public List<ContactEntity> getContactApprovedByUserId(String userId);

    public List<ContactEntity> getContactPendingByUserId(String userId);

    public String checkExistedRecord(String userId, String value, int type);

    public void updateContactStatus(int status, String id);

    public void deleteContactById(String id);

    public ContactEntity getContactById(String id);

    public void updateContact(String nickname, int type, String additionalData, String id);

    public void updateContactMultipart(String nickname, String additionalData, int colorType, String id);

    public List<ContactRechargeDTO> getContactRecharge(String userId);

    public String getImgIdByWalletId(String walletId);

    public List<ContactEntity> getContactApprovedByUserIdWithPagging(String userId, int offset);

    public List<ContactEntity> getContactApprovedByUserIdAndStatusWithPagging(String userId, int type, int offset);

    public void updateImgIdById(String imgId, String id);

    public void updateContactRelation(int relation, String id);

    public List<ContactEntity> getContactPublicByUserIdWithPagging(int offset);

    public void updateContactVcard(
            String nickname, String note, int colorType, String address,
            String company, String email, String phoneNo, String website,
            String value, String id);

    public List<ContactEntity> searchContactByNickname(String userId, String nickname);

    public List<ContactEntity> searchContactByNicknameGlobal(String nickname);

    public List<ContactEntity> searchContactByNicknameAndType(String userId, int type, String nickname);

    public List<String> checkExistedVcard(String userId, String phoneNo);
}
