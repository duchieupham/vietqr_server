package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.PartnerConnectEntity;

@Service
public interface PartnerConnectService {

    public PartnerConnectEntity getPartnerConnectByServiceName(String service);
}
