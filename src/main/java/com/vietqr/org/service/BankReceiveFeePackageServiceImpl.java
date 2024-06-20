package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import com.vietqr.org.repository.BankReceiveFeePackageRepository;
import com.vietqr.org.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankReceiveFeePackageServiceImpl implements BankReceiveFeePackageService {

    @Autowired
    private BankReceiveFeePackageRepository repo;

    @Override
    public List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value, int offset, int size) {
        return repo.getBankInvoiceByBankAccountAndMerchantId(merchantId, value, offset, size);
    }

    @Override
    public int countBankInvoiceByBankAccountAndMerchantId(String merchantId, String value) {
        return repo.countBankInvoiceByBankAccountAndMerchantId(merchantId, value);
    }

    @Override
    public List<IFeePackageDetailDTO> getFeePackageDetail(String bankId) {
        return repo.getFeePackageDetail(bankId);
    }

    @Override
    public IInvoiceItemCreateDTO getFeePackageByBankId(String bankId) {
        return repo.getFeePackageByBankId(bankId);
    }

    @Override
    public IBankDetailAdminDTO getBankReceiveByBankId(String bankId) {
        return repo.getBankReceiveByBankId(bankId);
    }

    @Override
    public IBankAccountInvoiceDTO getBankInvoiceByBankId(String bankId) {
        return repo.getBankInvoiceByBankId(bankId);
    }

    @Override
    public IMerchantBankMapperDTO getMerchantBankMapper(String merchantId, String bankId) {
        return repo.getMerchantBankMapper(merchantId, bankId);
    }

    @Override
    public List<ICustomerDetailDTO> getCustomerDetailByBankId(String bankId) {
        return repo.getCustomerDetailByBankId(bankId);
    }

    @Override
    public IBankReceiveFeePackageDTO getCustomerInfoByBankId(String bankId) {
        return repo.getCustomerInfoById(bankId);
    }

    @Override
    public List<PackageFeeResponseDTO> getFeePackageFeeResponse(String userId) {
        return repo.getFeePackageByUsersId(userId);
    }

    @Override
    public void saveBankReceiveFeePackage(BankReceiveFeePackageEntity bankReceiveFeePackage) {
        repo.save(bankReceiveFeePackage);
    }
    @Override
    public void updateBankReceiveFeePackage(String id, BankReceiveFeePackageUpdateRequestDTO updateRequestDTO) {
        repo.updateBankReceiveFeePackageById(
                id,
                StringUtil.getValueNullChecker(updateRequestDTO.getTitle()),
                updateRequestDTO.getActiveFee(),
                updateRequestDTO.getAnnualFee(),
                updateRequestDTO.getFixFee(),
                updateRequestDTO.getPercentFee(),
                updateRequestDTO.getVat(),
                updateRequestDTO.getRecordType(),
                StringUtil.getValueNullChecker(updateRequestDTO.getFeePackageId())
        );
    }

    @Override
    public List<IListBankReceiveFeePackageDTO> getAllBankReceiveFeePackages(String value, int offset, int size) {
        return repo.getAllBankReceiveFeePackages(value, offset, size);
    }

    @Override
    public int countBankReceiveFeePackagesByName(String value) {
        return repo.countBankReceiveFeePackagesByName(value);
    }

    @Override
    public void deleteBankReceiveFeePackageById(String id) {
        repo.deleteById(id);
    }

    @Override
    public IListBankReceiveFeePackageDTO getBankReceiveFeePackageById(String id) {
        return repo.getBankReceiveFeePackageById(id);
    }

}
