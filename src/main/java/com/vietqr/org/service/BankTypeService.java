package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BankTypeEntity;

@Service
public interface BankTypeService {

	public int insertBankType(BankTypeEntity dto);

	public List<BankTypeEntity> getBankTypes();

	public BankTypeEntity getBankTypeById(String id);

	public String getBankTypeIdByBankCode(String bankCode);

}
