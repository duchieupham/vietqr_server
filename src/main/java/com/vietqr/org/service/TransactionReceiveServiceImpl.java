package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveRepository;
import java.util.List;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransByCusSyncDTO;
import com.vietqr.org.dto.TransReceiveAdminDetailDTO;
import com.vietqr.org.dto.TransReceiveResponseDTO;
import com.vietqr.org.dto.TransStatisticByDateDTO;
import com.vietqr.org.dto.TransStatisticByMonthDTO;
import com.vietqr.org.dto.TransStatisticDTO;
import com.vietqr.org.dto.TransactionCheckStatusDTO;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionFeeDTO;
import com.vietqr.org.dto.TransactionQRDTO;
import com.vietqr.org.dto.TransactionReceiveAdminListDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Service
public class TransactionReceiveServiceImpl implements TransactionReceiveService {

    @Autowired
    TransactionReceiveRepository repo;

    @Override
    public int insertTransactionReceive(TransactionReceiveEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void updateTransactionReceiveStatus(int status, String refId, String referenceNumber, long timePaid,
            String id) {
        repo.updateTransactionReceiveStatus(status, refId, referenceNumber, timePaid, id);
    }

    @Override
    public List<TransactionRelatedDTO> getRelatedTransactionReceives(String businessId) {
        return repo.getRelatedTransactionReceives(businessId);
    }

    @Override
    public TransactionDetailDTO getTransactionById(String id) {
        return repo.getTransactionById(id);
    }

    @Override
    public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount, String transType) {
        Long amountParsed = Long.parseLong(amount);
        return repo.getTransactionByTraceId(id, amountParsed, transType);
    }

    @Override
    public List<TransactionReceiveEntity> getTransactionByBankId(String bankId) {
        return repo.getRelatedTransactionByBankId(bankId);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId) {
        return repo.getTransactions(offset, bankId);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveById(String id) {
        return repo.getTransactionReceiveById(id);
    }

    @Override
    public TransactionReceiveEntity getTransactionByOrderId(String orderId, String amount) {
        return repo.getTransactionByOrderId(orderId, amount);
    }

    @Override
    public TransactionReceiveEntity findTransactionReceiveByFtCode(String ftCode) {
        return repo.findTransactionReceiveByFtCode(ftCode);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveByRefNumberAndOrderId(String referenceNumber, String orderId) {
        return repo.getTransactionReceiveByRefNumberAndOrderId(referenceNumber, orderId);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveByRefNumber(String referenceNumber) {
        return repo.getTransactionReceiveByRefNumber(referenceNumber);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveByOrderId(String orderId) {
        return repo.getTransactionReceiveByOrderId(orderId);
    }

    @Override
    public TransactionCheckStatusDTO getTransactionCheckStatus(String transactionId) {
        return repo.getTransactionCheckStatus(transactionId);
    }

    @Override
    public int insertAllTransactionReceive(List<TransactionReceiveEntity> entities) {
        return repo.saveAll(entities) == null ? 0 : 1;
    }

    @Override
    public TransStatisticDTO getTransactionOverview(String bankId) {
        return repo.getTransactionOverview(bankId);
    }

    @Override
    public List<TransStatisticByDateDTO> getTransStatisticByDate(String bankId) {
        return repo.getTransStatisticByDate(bankId);
    }

    @Override
    public List<TransStatisticByMonthDTO> getTransStatisticByMonth(String bankId) {
        return repo.getTransStatisticByMonth(bankId);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId) {
        return repo.getTransactionsByStatus(status, offset, bankId);
    }

    @Override
    public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset) {
        return repo.getTransactionsByCustomerSync(bankId, customerSyncId, offset);
    }

    //
    //
    //
    /// ADMIN
    @Override
    public List<TransactionReceiveAdminListDTO> getTransByBankAccountAllDate(String value, int offset) {
        return repo.getTransByBankAccountAllDate(value, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDate(String value, String fromDate,
            String toDate, long offset) {
        fromDate = fromDate + " 00:00:00";
        toDate = toDate + " 23:59:59";
        return repo.getTransByBankAccountFromDate(value, fromDate, toDate, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransAllDate(long offset) {
        return repo.getAllTransAllDate(offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransFromDate(String fromDate, String toDate, long offset) {
        fromDate = fromDate + " 00:00:00";
        toDate = toDate + " 23:59:59";
        return repo.getAllTransFromDate(fromDate, toDate, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCode(String value, long offset) {
        return repo.getTransByFtCode(value, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderId(String value, long offset) {
        return repo.getTransByOrderId(value, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContent(String value, long offset) {
        return repo.getTransByContent(value, offset);
    }

    @Override
    public TransReceiveAdminDetailDTO getDetailTransReceiveAdmin(String id) {
        return repo.getDetailTransReceiveAdmin(id);
    }

    @Override
    public TransStatisticDTO getTransStatisticCustomerSync(String customerSyncId) {
        return repo.getTransStatisticCustomerSync(customerSyncId);
    }

    @Override
    public TransStatisticDTO getTransStatisticCustomerSyncByMonth(String customerSyncId, String month) {
        return repo.getTransStatisticCustomerSyncByMonth(customerSyncId, month);
    }

    @Override
    public TransactionFeeDTO getTransactionFeeCountingTypeAll(String bankId, String month) {
        return repo.getTransactionFeeCountingTypeAll(bankId, month);
    }

    @Override
    public TransactionFeeDTO getTransactionFeeCountingTypeSystem(String bankId, String month) {
        return repo.getTransactionFeeCountingTypeSystem(bankId, month);
    }

    //
    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCodeAndMerchantId(String value, String merchantId,
            long offset) {
        return repo.getTransByFtCodeAndMerchantId(value, merchantId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderIdAndMerchantId(String value, String merchantId,
            long offset) {
        return repo.getTransByOrderIdAndMerchantId(value, merchantId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContentAndMerchantId(String value, String merchantId,
            long offset) {
        return repo.getTransByContentAndMerchantId(value, merchantId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransAllDateByMerchantId(String merchantId, long offset) {
        return repo.getAllTransAllDateByMerchantId(merchantId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransFromDateByMerchantId(String fromDate, String toDate,
            String merchantId, long offset) {
        return repo.getAllTransFromDateByMerchantId(fromDate, toDate, merchantId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCodeAndUserId(String value, String userId, long offset) {
        return repo.getTransByFtCodeAndUserId(value, userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderIdAndUserId(String value, String userId, long offset) {
        return repo.getTransByOrderIdAndUserId(value, userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContentAndUserId(String value, String userId, long offset) {
        return repo.getTransByContentAndUserId(value, userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransAllDateByUserId(String userId, long offset) {
        return repo.getAllTransAllDateByUserId(userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransFromDateByUserId(String fromDate, String toDate,
            String userId, long offset) {
        return repo.getAllTransFromDateByUserId(fromDate, toDate, userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByBankAccountFromDate(String value, String fromDate,
            String toDate) {
        return repo.exportTransByBankAccountFromDate(value, fromDate, toDate);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByFtCodeAndMerchantId(String value, String merchantId) {
        return repo.exportTransByFtCodeAndMerchantId(value, merchantId);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByOrderIdAndMerchantId(String value, String merchantId) {
        return repo.exportTransByOrderIdAndMerchantId(value, merchantId);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByContentAndMerchantId(String value, String merchantId) {
        return repo.exportTransByContentAndMerchantId(value, merchantId);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportAllTransAllDateByMerchantId(String merchantId) {
        return repo.exportAllTransAllDateByMerchantId(merchantId);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportAllTransFromDateByMerchantId(String fromDate, String toDate,
            String merchantId) {
        return repo.exportAllTransFromDateByMerchantId(fromDate, toDate, merchantId);
    }

    @Override
    public TransactionQRDTO getTransactionQRById(String id) {
        return repo.getTransactionQRById(id);
    }

    @Override
    public List<TransReceiveResponseDTO> getTransByOrderId(String value, String bankAccount) {
        return repo.getTransByOrderId(value, bankAccount);
    }

    @Override
    public List<TransReceiveResponseDTO> getTransByReferenceNumber(String value, String bankAccount) {
        return repo.getTransByReferenceNumber(value, bankAccount);
    }

}
