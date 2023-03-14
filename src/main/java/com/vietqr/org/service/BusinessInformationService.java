package com.vietqr.org.service;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BusinessInformationEntity;

import com.vietqr.org.dto.BusinessCounterDTO;

@Service
public interface BusinessInformationService {

	public int insertBusinessInformation(BusinessInformationEntity entity);

	public void updateActiveBusinessInformation(boolean value, String id);

	public BusinessInformationEntity getBusinessById(String id);

	public void updateBusinessImage(String imgId, String id);

	public void updateBusinessCover(String coverImgId, String id);

	public BusinessCounterDTO getBusinessCounter(String businessId);
}
