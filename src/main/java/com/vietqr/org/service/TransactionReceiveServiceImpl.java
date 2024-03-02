package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveRepository;
import java.util.List;
import com.vietqr.org.entity.TransactionReceiveEntity;

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

    // @Override
    // public List<TransactionRelatedDTO> getRelatedTransactionReceives(String
    // businessId) {
    // return repo.getRelatedTransactionReceives(businessId);
    // }

    @Override
    public TransactionDetailDTO getTransactionById(String id) {
        return repo.getTransactionById(id);
    }

    @Override
    public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount, String transType) {
        Long amountParsed = Long.parseLong(amount);
        return repo.getTransactionByTraceId(id, amountParsed, transType, DateTimeUtil.get2LastPartition());
    }

    // // not use
    // @Override
    // public List<TransactionReceiveEntity> getTransactionByBankId(String bankId) {
    // return repo.getRelatedTransactionByBankId(bankId);
    // }

    @Override
    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransactions(offset, bankId, time);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId, String fromDate, String toDate) {
        return repo.getTransactions(offset, bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    // @Override
    // public TransactionReceiveEntity getTransactionReceiveById(String id) {
    // return repo.getTransactionReceiveById(id);
    // }

    @Override
    public TransactionReceiveEntity getTransactionByOrderId(String orderId, String amount) {
        return repo.getTransactionByOrderId(orderId, amount, DateTimeUtil.get2LastPartition());
    }

    @Override
    public TransactionReceiveEntity findTransactionReceiveByFtCode(String ftCode) {
        return repo.findTransactionReceiveByFtCode(ftCode, DateTimeUtil.get2LastPartition());
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveByRefNumberAndOrderId(String referenceNumber, String orderId) {
        return repo.getTransactionReceiveByRefNumberAndOrderId(referenceNumber, orderId,
                DateTimeUtil.get2LastPartition());
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveByRefNumber(String referenceNumber) {
        return repo.getTransactionReceiveByRefNumber(referenceNumber, DateTimeUtil.get2LastPartition());
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveByOrderId(String orderId) {
        return repo.getTransactionReceiveByOrderId(orderId, DateTimeUtil.get2LastPartition());
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
    public TransStatisticDTO getTransactionOverview(String bankId, String month, String userId) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndMonth(month);
        long fromDate = dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = dto.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getTransactionOverview(bankId, userId, fromDate, toDate);
    }

    @Override
    public List<TransStatisticByDateDTO> getTransStatisticByTerminalId(String bankId, String month, String userId) {
        return repo.getTransStatisticByTerminalId(bankId, userId,
                DateTimeUtil.getStartEndMonth(month).getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getStartEndMonth(month).getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByDateDTO> getTransStatisticByTerminalId(String bankId, String terminalCode,
                                                                       String month, String userId) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndMonth(month);
        long fromDate = dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = dto.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getTransStatisticByTerminalId(bankId, terminalCode, userId,
                fromDate,
                toDate);
    }

    @Override
    public List<TransStatisticByMonthDTO> getTransStatisticByMonth(String bankId) {
        return repo.getTransStatisticByMonth(bankId);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId, String fromDate,
            String toDate) {
        return repo.getTransactionsByStatus(status, offset, bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransactionsByStatus(status, offset, bankId, time);
    }

    @Override
    public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransactionsByCustomerSync(bankId, customerSyncId, offset, time);
    }

    @Override
    public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset,
            String fromDate, String toDate) {
        return repo.getTransactionsByCustomerSync(bankId, customerSyncId,
                offset, DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    //
    //
    //
    /// ADMIN
    @Override
    public List<TransactionReceiveAdminListDTO> getTransByBankAccountAllDate(String value, int offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByBankAccountAllDate(value, offset, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDate(String value, String fromDate,
            String toDate, long offset) {
        return repo.getTransByBankAccountFromDate(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransAllDate(long offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getAllTransAllDate(offset, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransFromDate(String fromDate, String toDate, long offset) {
        return repo.getAllTransFromDate(DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCode(String value, long offset, String fromDate,
            String toDate) {
        return repo.getTransByFtCode(value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCode(String value, long offset) {
        return repo.getTransByFtCode(value, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderId(String value, long offset, String fromDate,
            String toDate) {
        return repo.getTransByOrderId(value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderId(String value, long offset) {
        return repo.getTransByOrderId(value, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContent(String value, long offset, String fromDate,
            String toDate) {
        return repo.getTransByContent(value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContent(String value, long offset) {
        return repo.getTransByContent(value, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
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
            long offset, String fromDate, String toDate) {
        return repo.getTransByFtCodeAndMerchantId(value, merchantId, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCodeAndMerchantId(String value, String merchantId,
            long offset) {
        return repo.getTransByFtCodeAndMerchantId(value, merchantId, offset,
                DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderIdAndMerchantId(String value, String merchantId,
            long offset, String fromDate, String toDate) {
        return repo.getTransByOrderIdAndMerchantId(value, merchantId, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderIdAndMerchantId(String value, String merchantId,
            long offset) {
        return repo.getTransByOrderIdAndMerchantId(value, merchantId, offset,
                DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContentAndMerchantId(String value, String merchantId,
            long offset, String fromDate, String toDate) {
        return repo.getTransByContentAndMerchantId(value, merchantId, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContentAndMerchantId(String value, String merchantId,
            long offset) {
        return repo.getTransByContentAndMerchantId(value, merchantId, offset,
                DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransAllDateByMerchantId(String merchantId, long offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getAllTransAllDateByMerchantId(merchantId, offset, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransFromDateByMerchantId(String fromDate, String toDate,
            String merchantId, long offset) {
        return repo.getAllTransFromDateByMerchantId(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, merchantId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCodeAndUserId(String value, String userId, long offset,
            String fromDate, String toDate) {
        return repo.getTransByFtCodeAndUserId(value, userId, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCodeAndUserId(String value, String userId, long offset) {
        return repo.getTransByFtCodeAndUserId(value, userId, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderIdAndUserId(String value, String userId, long offset,
            String fromDate, String toDate) {
        return repo.getTransByOrderIdAndUserId(value, userId, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderIdAndUserId(String value, String userId, long offset) {
        return repo.getTransByOrderIdAndUserId(value, userId, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContentAndUserId(String value, String userId, long offset,
            String fromDate, String toDate) {
        return repo.getTransByContentAndUserId(value, userId, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContentAndUserId(String value, String userId, long offset) {
        return repo.getTransByContentAndUserId(value, userId, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransAllDateByUserId(String userId, long offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getAllTransAllDateByUserId(userId, offset, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransFromDateByUserId(String fromDate, String toDate,
            String userId, long offset) {
        return repo.getAllTransFromDateByUserId(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByBankAccountFromDate(String value, String fromDate,
            String toDate) {
        return repo.exportTransByBankAccountFromDate(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByFtCodeAndMerchantId(String value, String merchantId) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.exportTransByFtCodeAndMerchantId(value, merchantId, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByFtCodeAndMerchantId(String value, String merchantId,
            String fromDate, String toDate) {
        return repo.exportTransByFtCodeAndMerchantId(value, merchantId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByOrderIdAndMerchantId(String value, String merchantId) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.exportTransByOrderIdAndMerchantId(value, merchantId, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByOrderIdAndMerchantId(String value, String merchantId,
            String fromDate, String toDate) {
        return repo.exportTransByOrderIdAndMerchantId(value, merchantId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByContentAndMerchantId(String value, String merchantId) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.exportTransByContentAndMerchantId(value, merchantId, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByContentAndMerchantId(String value, String merchantId,
            String fromDate, String toDate) {
        return repo.exportTransByContentAndMerchantId(value, merchantId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportAllTransAllDateByMerchantId(String merchantId) {
        return repo.exportAllTransAllDateByMerchantId(merchantId, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportAllTransFromDateByMerchantId(String fromDate, String toDate,
            String merchantId) {
        return repo.exportAllTransFromDateByMerchantId(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, merchantId);
    }

    @Override
    public TransactionQRDTO getTransactionQRById(String id) {
        return repo.getTransactionQRById(id);
    }

    @Override
    public List<TransReceiveResponseDTO> getTransByOrderId(String value, String bankAccount) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByOrderId(value, bankAccount, time);
    }

    @Override
    public List<TransReceiveResponseDTO> getTransByReferenceNumber(String value, String bankAccount) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByReferenceNumber(value, bankAccount, time);
    }

    @Override
    public List<TransStatisticMerchantDTO> getStatisticYearByMerchantId(String id, String value) {
        return repo.getStatisticYearByMerchantId(id, value);
    }

    @Override
    public List<TransStatisticMerchantDTO> getStatisticMonthByMerchantId(String id, String value) {
        return repo.getStatisticMonthByMerchantId(id, value);
    }

    @Override
    public List<TransStatisticMerchantDTO> getStatisticYearByBankId(String id, String value) {
        return repo.getStatisticYearByBankId(id, value);
    }

    @Override
    public List<TransStatisticMerchantDTO> getStatisticMonthByBankId(String id, String value) {
        return repo.getStatisticMonthByBankId(id, value);
    }

    @Override
    public void updateTransactionStatusById(int status, String id) {
        repo.updateTransactionStatusById(status, id);
    }

    @Override
    public void updateTransactionReceiveNote(String note, String id) {
        repo.updateTransactionReceiveNote(note, id);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAllDate(String value, long offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByTerminalCodeAllDate(value, offset, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeFromDate(String value, String fromDate,
            String toDate, long offset) {
        return repo.getTransByTerminalCodeFromDate(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndMerchantIdAllDate(String value,
            String merchantId, long offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByTerminalCodeAndMerchantIdAllDate(value, merchantId, offset, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndMerchantIdFromDate(String fromDate,
            String toDate, String value, String merchantId, long offset) {
        return repo.getTransByTerminalCodeAndMerchantIdFromDate(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value, merchantId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndUserIdAllDate(String value, String userId,
            long offset) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByTerminalCodeAndUserIdAllDate(value, userId, offset, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndUserIdFromDate(String fromDate, String toDate,
            String value, String userId, long offset) {
        return repo.getTransByTerminalCodeAndUserIdFromDate(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value, userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransFromDateByTerminalCodeAndMerchantId(String fromDate,
            String toDate, String value, String merchantId) {
        return repo.exportTransFromDateByTerminalCodeAndMerchantId(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value, merchantId);
    }

    @Override
    public TransReceiveStatisticFeeDTO getTransStatisticForServiceFee(String bankId, String month) {
        return repo.getTransStatisticForServiceFee(bankId, month);
    }

    @Override
    public TransReceiveStatisticFeeDTO getTransStatisticForServiceFeeWithSystemType(String bankId, String month) {
        return repo.getTransStatisticForServiceFeeWithSystemType(bankId, month);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByFtCode(String value, int offset, String bankId, String fromDate,
            String toDate) {
        return repo.getTransactionsByFtCode(value, offset, bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByFtCode(String value, int offset, String bankId) {
        return repo.getTransactionsByFtCode(value, offset, bankId, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByOrderId(String value, int offset, String bankId,
            String fromDate,
            String toDate) {
        return repo.getTransactionsByOrderId(value, offset, bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByOrderId(String value, int offset, String bankId) {
        return repo.getTransactionsByOrderId(value, offset, bankId,
                DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByContent(String value, int offset, String bankId,
            String fromDate,
            String toDate) {
        return repo.getTransactionsByContent(value, offset, bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByContent(String value, int offset, String bankId) {
        return repo.getTransactionsByContent(value, offset, bankId,
                DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByTerminalCodeAndDate(String value, int offset, String fromDate,
            String toDate, String bankId) {
        return repo.getTransactionsByTerminalCode(value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, bankId);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByBankAccountAllDate(String value) {
        return repo.exportTransByBankAccountAllDate(value, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionReceiveAdminListDTO> exportTransByTerminalCodeAndMerchantIdAllDate(String value,
            String merchantId) {
        return repo.exportTransFromDateByTerminalCodeAndMerchantId(DateTimeUtil.get3MonthsPreviousAsLongInt(), value,
                merchantId);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByTerminalCodeAllDate(String value, int offset, String bankId) {
        return repo.getTransactionsByTerminalCode(value, offset, bankId, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, int offset) {
        return repo.getTransTerminalByStatus(bankId, userId, value, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, int offset) {
        return repo.getTransTerminalByFtCode(bankId, userId, value, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, int offset) {
        return repo.getTransTerminalByOrderId(bankId, userId, value, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, int offset) {
        return repo.getTransTerminalByContent(bankId, userId, value, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, int offset) {
        return repo.getAllTransTerminal(bankId, userId, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByStatus(bankId, userId, value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByFtCode(bankId, userId, value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByOrderId(bankId, userId, value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByContent(bankId, userId, value, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String fromDate, String toDate, int offset) {
        return repo.getAllTransTerminal(bankId, userId, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String terminalCode, int offset) {
        return repo.getTransTerminalByStatus(bankId, userId, value, terminalCode, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String terminalCode, int offset) {
        return repo.getTransTerminalByFtCode(bankId, userId, value, terminalCode, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String terminalCode, int offset) {
        return repo.getTransTerminalByOrderId(bankId, userId, value, terminalCode, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String terminalCode, int offset) {
        return repo.getTransTerminalByContent(bankId, userId, value, terminalCode, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String terminalCode, int offset) {
        return repo.getAllTransTerminal(bankId, userId, terminalCode, offset, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String terminalCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByStatus(bankId, userId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String terminalCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByFtCode(bankId, userId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String terminalCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByOrderId(bankId, userId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String terminalCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByContent(bankId, userId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String terminalCode, String fromDate, String toDate, int offset) {
        return repo.getAllTransTerminal(bankId, userId, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverview(String bankId, String terminalCode, String month, String userId) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndMonth(month);
        return repo.getTransactionOverview(bankId, terminalCode, userId, dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                dto.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverview(String bankId, String month) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndMonth(month);
        return repo.getTransactionOverview(bankId, dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                dto.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByDateDTO> getTransStatisticByBankId(String bankId, String month) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndMonth(month);
        return repo.getTransStatisticByBankId(bankId, dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                dto.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByDateDTO> getTransStatisticByTerminalIdNotSync(String bankId, String terminalCode, String month) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndMonth(month);
        return repo.getTransStatisticByTerminalIdNotSync(bankId, terminalCode,
                dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                dto.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverviewNotSync(String bankId, String terminalCode, String month) {
        StartEndTimeDTO dto = DateTimeUtil.getStartEndMonth(month);
        return repo.getTransactionOverviewNotSync(bankId, terminalCode,
                dto.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET,
                dto.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverviewNotSync(String bankId, String terminalCode, String fromDate, String toDate) {
        long startTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long endTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransactionOverviewNotSync(bankId, terminalCode,
                startTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                endTime - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdNotSync(String bankId, String terminalCode, String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransStatisticByTerminalIdNotSyncByDate(bankId, terminalCode,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdAndDate(String bankId, String terminalCode, String fromDate, String toDate, String userId) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransStatisticByTerminalIdAndDate(bankId, terminalCode, userId,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdAndDate(String bankId, String fromDate, String toDate, String userId) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransStatisticByTerminalIdAndDate(bankId, userId,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticByBankIdAndDate(String bankId, String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransStatisticByBankIdAndDate(bankId,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverviewByDay(String bankId, String fromDate, String toDate) {
        long startDate = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long endDate = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransactionOverview(bankId, startDate - DateTimeUtil.GMT_PLUS_7_OFFSET,
                endDate - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverviewByDay(String bankId, String fromDate, String toDate, String userId) {
        long startDate = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long endDate = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransactionOverview(bankId, userId,
                startDate - DateTimeUtil.GMT_PLUS_7_OFFSET,
                endDate - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverviewByDay(String bankId, String terminalCode, String fromDate, String toDate, String userId) {
        long startDate = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long endDate = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransactionOverview(bankId, terminalCode, userId,
                startDate - DateTimeUtil.GMT_PLUS_7_OFFSET,
                endDate - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

}
