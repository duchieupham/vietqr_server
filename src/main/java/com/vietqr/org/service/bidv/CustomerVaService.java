package com.vietqr.org.service.bidv;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.bidv.CustomerVaEntity;

@Service
public interface CustomerVaService {

    public int insert(CustomerVaEntity entity);
}