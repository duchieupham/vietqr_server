package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.PartnerConnectEntity;
import com.vietqr.org.repository.PartnerConnectRepository;

@Service
public class PartnerConnectServiceImpl implements PartnerConnectService {

    @Autowired
    PartnerConnectRepository repo;

    @Override
    public PartnerConnectEntity getPartnerConnectByServiceName(String service) {
        return repo.getPartnerConnectByServiceName(service);
    }

}
