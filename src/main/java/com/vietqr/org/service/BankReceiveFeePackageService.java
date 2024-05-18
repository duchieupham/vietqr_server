package com.vietqr.org.service;

import com.vietqr.org.dto.IBankAccountInvoiceDTO;
import com.vietqr.org.dto.IBankDetailAdminDTO;
import com.vietqr.org.dto.IFeePackageDetailDTO;
import com.vietqr.org.dto.IInvoiceItemCreateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BankReceiveFeePackageService {
    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccount(String value, int offset, int size);

    int countBankInvoiceByBankAccount(String value);

    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value,
                                                                          int offset, int size);

    int countBankInvoiceByBankAccountAndMerchantId(String merchantId, String value);

    List<IFeePackageDetailDTO> getFeePackageDetail(String bankId);

    IInvoiceItemCreateDTO getFeePackageByBankId(String bankId);

    IBankDetailAdminDTO getBankReceiveByBankId(String bankId);
}
