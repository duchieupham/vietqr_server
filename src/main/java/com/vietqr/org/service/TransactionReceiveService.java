package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransByCusSyncDTO;
import com.vietqr.org.dto.TransReceiveAdminDetailDTO;
import com.vietqr.org.dto.TransReceiveResponseDTO;
import com.vietqr.org.dto.TransReceiveStatisticFeeDTO;
import com.vietqr.org.dto.TransStatisticByDateDTO;
import com.vietqr.org.dto.TransStatisticByMonthDTO;
import com.vietqr.org.dto.TransStatisticDTO;
import com.vietqr.org.dto.TransStatisticMerchantDTO;
import com.vietqr.org.dto.TransactionCheckStatusDTO;
import com.vietqr.org.dto.TransactionDetailDTO;
import com.vietqr.org.dto.TransactionFeeDTO;
import com.vietqr.org.dto.TransactionQRDTO;
import com.vietqr.org.dto.TransactionReceiveAdminListDTO;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Service
public interface TransactionReceiveService {

    public int insertTransactionReceive(TransactionReceiveEntity entity);

    public int insertAllTransactionReceive(List<TransactionReceiveEntity> entities);

    public void updateTransactionReceiveStatus(int status, String refId, String referenceNumber, long timePaid,
                                               String id);

    public void updateTransactionReceiveNote(String note, String id);

    public void updateTransactionStatusById(int status, String id);

//    public List<TransactionRelatedDTO> getRelatedTransactionReceives(String businessId);

    public TransactionDetailDTO getTransactionById(String id);

    public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount, String transType);

//        public List<TransactionReceiveEntity> getTransactionByBankId(String bankId);

    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId);

    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId, String fromDate, String toDate);

    public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId, String fromDate, String toDate);

    public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId);
//    public TransactionReceiveEntity getTransactionReceiveById(String id);

    public TransactionReceiveEntity getTransactionByOrderId(String orderId, String amount);

    public TransactionReceiveEntity findTransactionReceiveByFtCode(String ftCode);

    public TransactionReceiveEntity getTransactionReceiveByRefNumberAndOrderId(String referenceNumber,
                                                                               String orderId);

    public TransactionReceiveEntity getTransactionReceiveByRefNumber(String referenceNumber);

    public TransactionReceiveEntity getTransactionReceiveByOrderId(String orderId);

    public TransactionCheckStatusDTO getTransactionCheckStatus(String transactionId);

    public TransStatisticDTO getTransactionOverview(String bankId);

    public List<TransStatisticByDateDTO> getTransStatisticByDate(String bankId);

    public List<TransStatisticByMonthDTO> getTransStatisticByMonth(String bankId);

    public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset);

    public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset,
                                                                 String fromDate, String toDate);

    // admin
    List<TransactionReceiveAdminListDTO> getTransByBankAccountAllDate(String value, int offset);

    List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDate(
            String value,
            String fromDate,
            String toDate,
            long offset);

    List<TransactionReceiveAdminListDTO> getAllTransAllDate(
            long offset);

    List<TransactionReceiveAdminListDTO> getAllTransFromDate(
            String fromDate,
            String toDate,
            long offset);

    List<TransactionReceiveAdminListDTO> getTransByFtCode(
            String value,
            long offset, String fromDate, String toDate);

    List<TransactionReceiveAdminListDTO> getTransByFtCode(
            String value, long offset);

    List<TransactionReceiveAdminListDTO> getTransByOrderId(
            String value,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByOrderId(
            String value,
            long offset);

    List<TransactionReceiveAdminListDTO> getTransByContent(
            String value,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByContent(
            String value,
            long offset);

    TransReceiveAdminDetailDTO getDetailTransReceiveAdmin(String id);

    TransStatisticDTO getTransStatisticCustomerSync(String customerSyncId);

    TransStatisticDTO getTransStatisticCustomerSyncByMonth(String customerSyncId, String month);

    TransactionFeeDTO getTransactionFeeCountingTypeAll(String bankId, String month);

    TransactionFeeDTO getTransactionFeeCountingTypeSystem(String bankId, String month);

    //
    List<TransactionReceiveAdminListDTO> getTransByFtCodeAndMerchantId(
            String value,
            String merchantId,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByFtCodeAndMerchantId(
            String value,
            String merchantId,
            long offset);

    List<TransactionReceiveAdminListDTO> getTransByOrderIdAndMerchantId(
            String value,
            String merchantId,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByOrderIdAndMerchantId(
            String value,
            String merchantId,
            long offset);

    List<TransactionReceiveAdminListDTO> getTransByContentAndMerchantId(
            String value,
            String merchantId,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByContentAndMerchantId(
            String value,
            String merchantId,
            long offset);

    List<TransactionReceiveAdminListDTO> getAllTransAllDateByMerchantId(
            String merchantId,
            long offset);

    List<TransactionReceiveAdminListDTO> getAllTransFromDateByMerchantId(
            String fromDate,
            String toDate,
            String merchantId,
            long offset);

    List<TransactionReceiveAdminListDTO> getTransByFtCodeAndUserId(
            String value,
            String userId,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByFtCodeAndUserId(
            String value,
            String userId,
            long offset);

    List<TransactionReceiveAdminListDTO> getTransByOrderIdAndUserId(
            String value,
            String userId,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByOrderIdAndUserId(
            String value,
            String userId,
            long offset);

    List<TransactionReceiveAdminListDTO> getTransByContentAndUserId(
            String value,
            String userId,
            long offset,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> getTransByContentAndUserId(
            String value,
            String userId,
            long offset);

    List<TransactionReceiveAdminListDTO> getAllTransAllDateByUserId(
            String userId,
            long offset);

    List<TransactionReceiveAdminListDTO> getAllTransFromDateByUserId(
            String fromDate,
            String toDate,
            String userId,
            long offset);

    List<TransactionReceiveAdminListDTO> exportTransByBankAccountFromDate(
            String value,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> exportTransByFtCodeAndMerchantId(
            String value,
            String merchantId);

    List<TransactionReceiveAdminListDTO> exportTransByFtCodeAndMerchantId(
            String value,
            String merchantId,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> exportTransByOrderIdAndMerchantId(
            String value,
            String merchantId);

    List<TransactionReceiveAdminListDTO> exportTransByOrderIdAndMerchantId(
            String value,
            String merchantId,
            String fromDate,
            String toDate);

    List<TransactionReceiveAdminListDTO> exportTransByContentAndMerchantId(
            String value,
            String merchantId);

    List<TransactionReceiveAdminListDTO> exportTransByContentAndMerchantId(
            String value,
            String merchantId,
            String fromDate,
            String toDate);

        List<TransactionReceiveAdminListDTO> exportAllTransAllDateByMerchantId(
                        String merchantId);

    List<TransactionReceiveAdminListDTO> exportAllTransFromDateByMerchantId(
            String fromDate,
            String toDate,
            String merchantId);

    TransactionQRDTO getTransactionQRById(String id);

    List<TransReceiveResponseDTO> getTransByOrderId(String value, String bankAccount);

    List<TransReceiveResponseDTO> getTransByReferenceNumber(String value, String bankAccount);

    List<TransStatisticMerchantDTO> getStatisticYearByMerchantId(String id, String value);

    List<TransStatisticMerchantDTO> getStatisticMonthByMerchantId(String id, String value);

    List<TransStatisticMerchantDTO> getStatisticYearByBankId(String id, String value);

    List<TransStatisticMerchantDTO> getStatisticMonthByBankId(String id, String value);

    /// TERMINAL CODE
    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAllDate(String value, long offset);

    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeFromDate(String value, String fromDate,
                                                                        String toDate, long offset);

    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndMerchantIdAllDate(String value, String merchantId,
                                                                                    long offset);

    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndMerchantIdFromDate(String fromDate, String toDate,
                                                                                     String value, String merchantId, long offset);

    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndUserIdAllDate(String value, String userId,
                                                                                long offset);

    List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAndUserIdFromDate(String fromDate, String toDate,
                                                                                 String value, String userId, long offset);

    List<TransactionReceiveAdminListDTO> exportTransFromDateByTerminalCodeAndMerchantId(String fromDate,
                                                                                        String toDate, String value, String merchantId);

    // for service fee
    TransReceiveStatisticFeeDTO getTransStatisticForServiceFee(String bankId, String month);

    TransReceiveStatisticFeeDTO getTransStatisticForServiceFeeWithSystemType(String bankId, String month);

    //
    List<TransactionRelatedDTO> getTransactionsByFtCode(String value, int offset, String bankId, String fromDate,
                                                        String toDate);

    List<TransactionRelatedDTO> getTransactionsByFtCode(String value, int offset, String bankId);

    List<TransactionRelatedDTO> getTransactionsByOrderId(String value, int offset, String bankId, String fromDate,
                                                         String toDate);

    List<TransactionRelatedDTO> getTransactionsByOrderId(String value, int offset, String bankId);

    List<TransactionRelatedDTO> getTransactionsByContent(String value, int offset, String bankId, String fromDate,
                                                         String toDate);

    List<TransactionRelatedDTO> getTransactionsByContent(String value, int offset, String bankId);

    List<TransactionRelatedDTO> getTransactionsByTerminalCodeAndDate(String value, int offset, String fromDate,
                                                                     String toDate, String bankId);

    List<TransactionReceiveAdminListDTO> exportTransByBankAccountAllDate(String value);

    List<TransactionReceiveAdminListDTO> exportTransByTerminalCodeAndMerchantIdAllDate(String value, String merchantId);

    List<TransactionRelatedDTO> getTransactionsByTerminalCodeAllDate(String value, int offset, String bankId);
}
