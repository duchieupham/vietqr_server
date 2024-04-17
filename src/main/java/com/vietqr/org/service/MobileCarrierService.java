package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.MobileCarrierEntity;

@Service
public interface MobileCarrierService {

    public int insertMobileCarrier(MobileCarrierEntity entity);

    public MobileCarrierEntity getMobileCarrierById(String id);

    public String getTypeIdByPrefix(String prefix);

}
