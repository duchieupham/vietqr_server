package com.vietqr.org.service;

import java.util.List;

// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BankTypeEntity;

@Service
public interface BankTypeService {

	public int insertBankType(BankTypeEntity dto);

	public List<BankTypeEntity> getBankTypes();

	// @Async
	public BankTypeEntity getBankTypeById(String id);

	public String getBankTypeIdByBankCode(String bankCode);

	public Boolean getRpaContainIdByBankCode(String bankCode);
}
