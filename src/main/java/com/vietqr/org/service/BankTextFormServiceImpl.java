package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BankTextFormEntity;
import com.vietqr.org.repository.BankTextFormRepository;

@Service
public class BankTextFormServiceImpl implements BankTextFormService{

	@Autowired
	BankTextFormRepository bankTextFormRepository;

	@Override
	public int insertBankTextForm(BankTextFormEntity entity) {
		return bankTextFormRepository.save(entity) == null ? 0 : 1;
	}

	@Override
	public List<BankTextFormEntity> getBankTextFormsByBankId(String bankId) {
		return bankTextFormRepository.getBankTextFormsByBankId(bankId);
	}

	@Override
	public void removeBankTextForm(String id) {
		bankTextFormRepository.deleteBankTextForm(id);
	}

}
