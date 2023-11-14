package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ServicePartnerCheckerEntity;

@Service
public interface PartnerCheckerService {

    public int insert(ServicePartnerCheckerEntity entity);
}
