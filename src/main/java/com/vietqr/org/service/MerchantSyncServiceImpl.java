package com.vietqr.org.service;

import com.vietqr.org.dto.IMerchantInvoiceDTO;
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
}
