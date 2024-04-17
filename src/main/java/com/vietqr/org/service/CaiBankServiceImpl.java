package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CaiBankEntity;
import com.vietqr.org.repository.CaiBankRepository;

@Service
public class CaiBankServiceImpl implements CaiBankService {

	@Autowired
	CaiBankRepository caiBankRepository;

	@Override
	public int insertCaiBank(CaiBankEntity entity) {
		return caiBankRepository.save(entity) == null ? 0 : 1;
	}

	@Override
	public String getCaiValue(String bankTypeId) {
		return caiBankRepository.getCaiValue(bankTypeId);
	}

	@Override
	public CaiBankEntity getCaiBankByCaiValue(String caiValue) {
		return caiBankRepository.getCaiBankByCaiValue(caiValue);
	}

}
