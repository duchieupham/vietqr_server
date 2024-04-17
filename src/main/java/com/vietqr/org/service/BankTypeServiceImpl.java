package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.repository.BankTypeRepository;

@Service
public class BankTypeServiceImpl implements BankTypeService {

	@Autowired
	BankTypeRepository bankTypeRepository;

	@Override
	public int insertBankType(BankTypeEntity entity) {
		return bankTypeRepository.save(entity) == null ? 0 : 1;
	}

	@Override
	public List<BankTypeEntity> getBankTypes() {
		return bankTypeRepository.getBankTypes();
	}

	@Override
	public BankTypeEntity getBankTypeById(String id) {
		return bankTypeRepository.getBankTypeById(id);
	}

	@Override
	public String getBankTypeIdByBankCode(String bankCode) {
		return bankTypeRepository.getBankTypeIdByBankCode(bankCode);
	}

	@Override
	public Boolean getRpaContainIdByBankCode(String bankCode) {
		return bankTypeRepository.getRpaContainIdByBankCode(bankCode);
	}

}
