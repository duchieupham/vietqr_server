package com.vietqr.org.service;

import com.vietqr.org.dto.IBankAccountInvoiceDTO;
import com.vietqr.org.dto.IFeePackageDetailDTO;
import com.vietqr.org.repository.BankReceiveFeePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankReceiveFeePackageServiceImpl implements BankReceiveFeePackageService {

    @Autowired
    private BankReceiveFeePackageRepository repo;

    @Override
    public List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccount(String value, int offset, int size) {
//        return repo.getBankInvoiceByBankAccount(value, offset, size);
        return new ArrayList<>();
    }

    @Override
    public int countBankInvoiceByBankAccount(String value) {
//        return repo.countBankInvoiceByBankAccount(value);
        return 0;
    }

    @Override
    public List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value, int offset, int size) {
//        return repo.getBankInvoiceByBankAccountAndMerchantId(merchantId, value, offset, size);
        return new ArrayList<>();
    }

    @Override
    public int countBankInvoiceByBankAccountAndMerchantId(String merchantId, String value) {
//        return repo.countBankInvoiceByBankAccountAndMerchantId(merchantId, value);
        return 0;
    }

    @Override
    public List<IFeePackageDetailDTO> getFeePackageDetail(String bankId) {
        return repo.getFeePackageDetail(bankId);
    }
}
