package com.vietqr.org.service;

import java.util.ArrayList;
import java.util.List;

// import org.springframework.scheduling.annotation.Async;
import com.vietqr.org.dto.BankCaiTypeDTO;
import com.vietqr.org.dto.BankTypeShortNameDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.BankTypeEntity;

@Service
public interface BankTypeService {

    public int insertBankType(BankTypeEntity dto);

    public List<BankTypeEntity> getBankTypes();

    // @Async
    public BankTypeEntity getBankTypeById(String id);

    public String getBankTypeIdByBankCode(String bankCode);

    public String getBankShortNameByBankCode(String bankCode);

    public Boolean getRpaContainIdByBankCode(String bankCode);

    List<BankTypeShortNameDTO> getBankTypeByListId(List<String> ids);

    BankTypeEntity getBankTypeByBankCode(String bankCode);

    String getBankCodeByBankShortName(String bankShortName);

    BankCaiTypeDTO getBankCaiByBankCode(String bankCode);
}
