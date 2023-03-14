package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BusinessInformationEntity;
import com.vietqr.org.repository.BusinessInformationRepository;
import com.vietqr.org.dto.BusinessCounterDTO;

@Service
public class BusinessInformationServiceImpl implements BusinessInformationService {

	@Autowired
	BusinessInformationRepository repo;

	@Override
	public int insertBusinessInformation(BusinessInformationEntity entity) {
		return repo.save(entity) == null ? 0 : 1;
	}

	@Override
	public void updateActiveBusinessInformation(boolean value, String id) {
		repo.updateActiveBussiness(value, id);
	}

	@Override
	public BusinessInformationEntity getBusinessById(String id) {
		return repo.getBusinessInformationById(id);
	}

	@Override
	public void updateBusinessImage(String imgId, String id) {
		repo.updateBusinessImage(imgId, id);
	}

	@Override
	public void updateBusinessCover(String coverImgId, String id) {
		repo.updateBusinessCover(coverImgId, id);
	}

	@Override
	public BusinessCounterDTO getBusinessCounter(String businessId) {
		return repo.getBusinessCounter(businessId);
	}
}
