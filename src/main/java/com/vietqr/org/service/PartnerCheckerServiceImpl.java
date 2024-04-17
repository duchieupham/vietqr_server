package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ServicePartnerCheckerEntity;
import com.vietqr.org.repository.ServicePartnerCheckerRepository;

@Service
public class PartnerCheckerServiceImpl implements PartnerCheckerService {

    @Autowired
    ServicePartnerCheckerRepository repo;

    @Override
    public int insert(ServicePartnerCheckerEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
