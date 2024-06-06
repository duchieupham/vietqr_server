package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BankReceiveFeePackageService {

    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value,
                                                                          int offset, int size);

    int countBankInvoiceByBankAccountAndMerchantId(String merchantId, String value);

    List<IFeePackageDetailDTO> getFeePackageDetail(String bankId);

    IInvoiceItemCreateDTO getFeePackageByBankId(String bankId);

    IBankDetailAdminDTO getBankReceiveByBankId(String bankId);

    IBankAccountInvoiceDTO getBankInvoiceByBankId(String bankId);

    IMerchantBankMapperDTO getMerchantBankMapper(String merchantId, String bankId);

    List<ICustomerDetailDTO> getCustomerDetailByBankId(String bankId);

    List<PackageFeeResponseDTO> getFeePackageFeeResponse(String userId);
}
