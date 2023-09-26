package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ServiceFeeEntity;
import com.vietqr.org.repository.ServiceFeeRepository;

@Service
public class ServiceFeeServiceImpl implements ServiceFeeService {

    @Autowired
    ServiceFeeRepository repo;

    @Override
    public int insert(ServiceFeeEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<ServiceFeeEntity> getServiceFees() {
        return repo.getServicesFee();
    }

    @Override
    public List<ServiceFeeEntity> getSubServiceFees(String refId) {
        return repo.getSubServicesFee(refId);
    }

    @Override
    public ServiceFeeEntity getServiceFeeById(String id) {
        return repo.getServiceFeeById(id);
    }

}
