package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.GoogleChatEntity;
import com.vietqr.org.entity.LarkEntity;
import com.vietqr.org.entity.MerchantSyncEntity;
import com.vietqr.org.entity.TelegramEntity;
import com.vietqr.org.repository.GoogleChatRepository;
import com.vietqr.org.repository.LarkRepository;
import com.vietqr.org.repository.MerchantSyncRepository;
import com.vietqr.org.repository.TelegramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MerchantSyncServiceImpl implements MerchantSyncService {

    @Autowired
    private MerchantSyncRepository repo;

    @Autowired
    private LarkRepository larkRepository;
    @Autowired
    private TelegramRepository telegramRepository;
    @Autowired
    private GoogleChatRepository googleChatRepository;

    @Override
    public List<IMerchantInvoiceDTO> getMerchantSyncs(int offset, int size) {
        return repo.getMerchantSyncs(offset, size);
    }

    @Override
    public List<IMerchantInvoiceDTO> getMerchantSyncsByName(String value, int offset, int size) {
        return repo.getMerchantSyncsByName(value, offset, size);
    }

    @Override
    public int countMerchantSyncsByName(String value) {
        return repo.countMerchantSyncsByName(value);
    }

    @Override
    public IMerchantEditDetailDTO getMerchantEditDetail(String merchantId) {
        return repo.getMerchantEditDetail(merchantId);
    }

    @Override
    public IMerchantInfoDTO getMerchantSyncInfo(String merchantId) {
        return repo.getMerchantSyncInfo(merchantId);
    }

    @Override
    public List<IMerchantSyncDTO> getAllMerchants(String value, int offset, int size) {
        return repo.getAllMerchants(value, offset, size);
    }

    @Override
    public IMerchantSyncDTO getMerchantById(String id) {
        return repo.getMerchantById(id);
    }

    @Override
    public MerchantSyncEntity createMerchant(MerchantSyncEntity entity) {
        return repo.save(entity);
    }

    @Override
    public MerchantSyncEntity updateMerchant(String id, MerchantSyncEntity entity) {
//        if (repo.existsById(id)) {
//            entity.setId(id);
//            return repo.save(entity);
//        } else {
//            return null;
//        }
        // Assuming a findById method exists in your repository or service
        Optional<MerchantSyncEntity> existingEntityOptional = repo.findById(id);
        if (!existingEntityOptional.isPresent()) {
            return null;
        }

        MerchantSyncEntity existingEntity = existingEntityOptional.get();
        existingEntity.setName(entity.getName());
        existingEntity.setVso(entity.getVso());
        existingEntity.setBusinessType(entity.getBusinessType());
        existingEntity.setCareer(entity.getCareer());
        existingEntity.setAddress(entity.getAddress());
        existingEntity.setNationalId(entity.getNationalId());
        existingEntity.setUserId(entity.getUserId());
        existingEntity.setEmail(entity.getEmail());
        existingEntity.setIsActive(entity.getIsActive());
        existingEntity.setAccountCustomerId(entity.getAccountCustomerId());

        return repo.save(existingEntity);
    }

    @Override
    public void updateMerchantV2(String publishId, String accountCustomerId, String address, String career, String businessType,
                                 String name, String userId, String vso, String email, String refId, String fullName, String phoneNo
    ) {
        repo.updateMerchantV2(publishId, accountCustomerId, address, career, businessType, name, userId, vso, email, refId, fullName, phoneNo);
    }

    @Override
    public Optional<MerchantSyncEntity> findById(String id) {
        return Optional.empty();
    }

    @Override
    public void deleteMerchant(String id) {
        repo.deleteMerchantById(id);
    }

    @Override
    public int countMerchantsByName(String value) {
        return repo.countMerchantsByName(value);
    }

    @Override
    public void savePlatformDetails(String platform, String userId, String details) {
        if (platform == null || details == null) {
            return;
        }
        switch (platform) {
            case "Telegram":
                TelegramEntity telegramEntity = new TelegramEntity();
                telegramEntity.setId(UUID.randomUUID().toString());
                telegramEntity.setUserId(userId);
                telegramEntity.setChatId(details);
                telegramRepository.save(telegramEntity);
                break;
            case "Google Chat":
                GoogleChatEntity googleChatEntity = new GoogleChatEntity();
                googleChatEntity.setId(UUID.randomUUID().toString());
                googleChatEntity.setUserId(userId);
                googleChatEntity.setWebhook(details);
                googleChatRepository.save(googleChatEntity);
                break;
            case "Lark":
                LarkEntity larkEntity = new LarkEntity();
                larkEntity.setId(UUID.randomUUID().toString());
                larkEntity.setUserId(userId);
                larkEntity.setWebhook(details);
                larkRepository.save(larkEntity);
                break;

        }
    }

    @Override
    public void updateMerchantName(String midName, String mid) {
        repo.updateMerchantName(midName, mid);
    }

    @Override
    public MerchantSyncEntity getMerchantSyncByName(String merchantName) {
        return repo.getMerchantSyncsByMerchantName(merchantName);
    }

    @Override
    public MerchantSyncEntity getMerchantSyncByPublishId(String mid) {
        return repo.getMerchantSyncByPublicId(mid);
    }

    @Override
    public String getPublishIdSyncByCertificate(String certificate) {
        return repo.getPublishIdSyncByCertificate(certificate);
    }

    @Override
    public MerchantSyncEntity getMerchantSyncById(String mid) {
        return repo.getMerchantSyncById(mid);
    }

    @Override
    public int countMerchantByMidSync(String mid) {
        return repo.countMerchantByMidSync(mid);
    }

    @Override
    public List<IMerchantSyncPublicDTO> getMerchantByMidSync(String refId, int offset, int size) {
        return repo.getMerchantByMidSync(refId, offset, size);
    }

    @Override
    public List<IMerchantSyncPublicDTO> getMerchantByMidSyncV2(String refId) {
        return repo.getMerchantByMidSyncV2(refId);
    }

    @Override
    public String getIdByPublicId(String publicId) {
        return repo.getIdByPublicId(publicId);
    }

    @Override
    public String getMerchantIdSyncByName(String merchantName) {
        return repo.getMerchantIdSyncByName(merchantName);
    }

    @Override
    public String checkExistedPublishId(String code) {
        return repo.checkExistedPublishId(code);
    }

    @Override
    public void insertAll(List<MerchantSyncEntity> entities) {
        repo.saveAll(entities);
    }

    @Override
    public int insert(MerchantSyncEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }
}
