package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.MobileCarrierEntity;
import com.vietqr.org.repository.MobileCarrierRepository;

@Service
public class MobileCarrierServiceImpl implements MobileCarrierService {

    @Autowired
    MobileCarrierRepository repo;

    @Override
    public int insertMobileCarrier(MobileCarrierEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public MobileCarrierEntity getMobileCarrierById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMobileCarrierById'");
    }

    @Override
    public String getTypeIdByPrefix(String prefix) {
        return repo.getTypeIdByPrefix(prefix);
    }

}
