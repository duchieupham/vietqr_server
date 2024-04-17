package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ServiceFeeEntity;

@Service
public interface ServiceFeeService {

    public int insert(ServiceFeeEntity entity);

    public ServiceFeeEntity getServiceFeeById(String id);

    public List<ServiceFeeEntity> getServiceFees();

    public List<ServiceFeeEntity> getSubServiceFees(String refId);
}
