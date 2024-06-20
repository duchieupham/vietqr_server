package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
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

    IBankReceiveFeePackageDTO getCustomerInfoByBankId(String bankId);

    List<PackageFeeResponseDTO> getFeePackageFeeResponse(String userId);
    void saveBankReceiveFeePackage(BankReceiveFeePackageEntity bankReceiveFeePackage);
    void updateBankReceiveFeePackage(String id, BankReceiveFeePackageUpdateRequestDTO updateRequestDTO);

    List<IListBankReceiveFeePackageDTO> getAllBankReceiveFeePackages(String value, int offset, int size);
    int countBankReceiveFeePackagesByName(String value);
    void deleteBankReceiveFeePackageById(String id);
    IListBankReceiveFeePackageDTO getBankReceiveFeePackageById(String id);
}
