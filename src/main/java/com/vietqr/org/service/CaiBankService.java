package com.vietqr.org.service;

// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.CaiBankEntity;

@Service
public interface CaiBankService {

	public int insertCaiBank(CaiBankEntity entity);

	// @Async
	public String getCaiValue(String bankTypeId);

	public CaiBankEntity getCaiBankByCaiValue(String caiValue);
}
