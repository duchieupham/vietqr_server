package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CarrierTypeEntity;
import com.vietqr.org.repository.CarrierTypeRepository;

@Service
public class CarrierTypeServiceImpl implements CarrierTypeService {

    @Autowired
    CarrierTypeRepository repo;

    @Override
    public int insertCarrierType(CarrierTypeEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<CarrierTypeEntity> getCarrierTypes() {
        return repo.getCarrierTypes();
    }

    @Override
    public CarrierTypeEntity getCarrierTypeById(String id) {
        return repo.getCarrierTypeById(id);
    }

}
