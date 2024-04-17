package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BankTextFormEntity;

@Service
public interface BankTextFormService {

	public int insertBankTextForm(BankTextFormEntity entity);

	public List<BankTextFormEntity> getBankTextFormsByBankId(String bankId);

	public void removeBankTextForm(String id);

}
