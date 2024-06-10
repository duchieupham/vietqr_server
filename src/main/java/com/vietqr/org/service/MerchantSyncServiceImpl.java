package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantEditDetailDTO;
import com.vietqr.org.dto.IMerchantInfoDTO;
import com.vietqr.org.dto.IMerchantInvoiceDTO;
import com.vietqr.org.dto.IMerchantSyncDTO;
import com.vietqr.org.entity.MerchantSyncEntity;
import com.vietqr.org.repository.MerchantSyncRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantSyncServiceImpl implements MerchantSyncService {

    @Autowired
    private MerchantSyncRepository repo;

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
        if (repo.existsById(id)) {
            entity.setId(id);
            return repo.save(entity);
        } else {
            return null;
        }
    }

    @Override
    public void deleteMerchant(String id) {
        repo.deleteMerchantById(id);
    }

    @Override
    public int countMerchantsByName(String value) {
        return repo.countMerchantsByName(value);
    }
}
