package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.BankTypeShortNameDTO;
import com.vietqr.org.dto.IBankTypeQR;
import com.vietqr.org.dto.ICaiBankTypeQR;
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
	public String getBankShortNameByBankCode(String bankCode) {
		return bankTypeRepository.getBankShortNameByBankCode(bankCode);
	}

	@Override
	public Boolean getRpaContainIdByBankCode(String bankCode) {
		return bankTypeRepository.getRpaContainIdByBankCode(bankCode);
	}

	@Override
	public List<BankTypeShortNameDTO> getBankTypeByListId(List<String> ids) {
		return bankTypeRepository.getBankTypeByListId(ids);
	}

	@Override
	public BankTypeEntity getBankTypeByBankCode(String bankCode) {
		return bankTypeRepository.getBankTypeByBankCode(bankCode);
	}

	@Override
	public String getBankCodeByBankShortName(String bankShortName) {
		BankTypeEntity bankTypeEntity = bankTypeRepository.findByBankShortName(bankShortName);
		return bankTypeEntity.getBankCode();
	}

	@Override
	public ICaiBankTypeQR getCaiBankTypeById(String id) {
		return bankTypeRepository.getCaiBankTypeById(id);
	}

	@Override
	public IBankTypeQR getBankTypeQRById(String id) {
		return bankTypeRepository.getBankTypeQRById(id);
	}

	@Override
	public IBankTypeQR getBankTypeQRByCode(String code) {
		return bankTypeRepository.getBankTypeQRByCode(code);
	}
}
