package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CarrierTypeEntity;

@Service
public interface CarrierTypeService {

    public int insertCarrierType(CarrierTypeEntity entity);

    public List<CarrierTypeEntity> getCarrierTypes();

}
