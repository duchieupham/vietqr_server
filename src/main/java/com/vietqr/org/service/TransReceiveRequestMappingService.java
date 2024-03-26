package com.vietqr.org.service;

import com.vietqr.org.entity.TransReceiveRequestMappingEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransReceiveRequestMappingService {
    int insert(TransReceiveRequestMappingEntity entity);
    int insertAll(List<TransReceiveRequestMappingEntity> entity);
}
