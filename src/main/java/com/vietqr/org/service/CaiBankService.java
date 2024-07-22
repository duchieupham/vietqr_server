package com.vietqr.org.service;

// import org.springframework.scheduling.annotation.Async;
import com.vietqr.org.dto.CaiValueDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CaiBankEntity;

import java.util.List;

@Service
public interface CaiBankService {

	public int insertCaiBank(CaiBankEntity entity);

	// @Async
	public String getCaiValue(String bankTypeId);

	public CaiBankEntity getCaiBankByCaiValue(String caiValue);

	List<CaiValueDTO> getCaiValues(List<String> bankTypeIds);

	String getCaiValueByBankId(String bankId);
}
