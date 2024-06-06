package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import org.springframework.stereotype.Service;
import com.vietqr.org.entity.TransactionReceiveEntity;

@Service
public interface TransactionReceiveService {

        public int insertTransactionReceive(TransactionReceiveEntity entity);

        public int insertTransactionReceiveWithCheckDuplicated(TransactionReceiveEntity entity);

        public int insertAllTransactionReceive(List<TransactionReceiveEntity> entities);

        public void updateTransactionReceiveStatus(int status, String refId, String referenceNumber, long timePaid,
                        String id);

        public void updateTransactionReceiveNote(String note, String id);

        public void updateTransactionStatusById(int status, String id);

        // public List<TransactionRelatedDTO> getRelatedTransactionReceives(String
        // businessId);

        public TransactionDetailDTO getTransactionById(String id);

        public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount, String transType);

        public TransactionReceiveEntity getTransactionsById(String id);

        // public List<TransactionReceiveEntity> getTransactionByBankId(String bankId);

        public List<TransactionRelatedDTO> getTransactions(int offset, String bankId);

        public List<TransactionRelatedDTO> getTransactions(int offset, String bankId, String fromDate, String toDate);

        public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId,
                        String fromDate, String toDate);

        public List<TransactionRelatedDTO> getTransactionsByStatus(int status, int offset, String bankId);
        // public TransactionReceiveEntity getTransactionReceiveById(String id);

        public TransactionReceiveEntity getTransactionByOrderId(String orderId, String amount);

        public TransactionReceiveEntity findTransactionReceiveByFtCode(String ftCode);

        public TransactionReceiveEntity getTransactionReceiveByRefNumberAndOrderId(String referenceNumber,
                        String orderId);

        public TransactionReceiveEntity getTransactionReceiveByRefNumber(String referenceNumber);

        public TransactionReceiveEntity getTransactionReceiveByOrderId(String orderId);

        public TransactionCheckStatusDTO getTransactionCheckStatus(String transactionId);

        public TransStatisticDTO getTransactionOverview(String bankId, String month, String userId);

        //bankId, month, userId
        public List<TransStatisticByDateDTO> getTransStatisticByTerminalId(String bankId,
                                                                           String month, String userId);

        public List<TransStatisticByDateDTO> getTransStatisticByTerminalId(String bankId,
                                                                           String terminalCode,
                                                                           String month, String userId);

        public List<TransStatisticByMonthDTO> getTransStatisticByMonth(String bankId);

        public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset);

        public List<TransByCusSyncDTO> getTransactionsByCustomerSync(String bankId, String customerSyncId, int offset,
                        String fromDate, String toDate);

        public List<FeePackageResponseDTO> getFeePackageResponse(long startTime, long endTime, List<String> bankId);

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

        List<TransactionReceiveAdminListDTO> exportTransByTerminalCodeAndMerchantIdAllDate(String value,
                        String merchantId);

        List<TransactionRelatedDTO> getTransactionsByTerminalCodeAllDate(String value, int offset, String bankId);

        List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, int offset);

        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, int offset);

        List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, int offset);

        List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, int offset);

        List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, int offset);

        List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, String terminalCode, int offset);

        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, String terminalCode, int offset);

        List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, String terminalCode, int offset);

        List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, String terminalCode, int offset);

        List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, String terminalCode, int offset);
//
        List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, List<String> terminalCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, List<String> terminalCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value, List<String> terminalCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, List<String> terminalCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, List<String> terminalCode, String fromDate, String toDate, int offset);
//
        TransStatisticDTO getTransactionOverview(String bankId, String terminalCode, String month, String userId);

        TransStatisticDTO getTransactionOverview(String bankId, String month);

        List<TransStatisticByDateDTO> getTransStatisticByBankId(String bankId, String month);

        List<TransStatisticByDateDTO> getTransStatisticByTerminalIdNotSync(String bankId, String terminalCode, String month);

        TransStatisticDTO getTransactionOverviewNotSync(String bankId, String terminalCode, String month);

        TransStatisticDTO getTransactionOverviewByDay(String bankId, String fromDate, String toDate);

        TransStatisticDTO getTransactionOverviewByDay(String bankId, String fromDate, String toDate, String userId);

        TransStatisticDTO getTransactionOverviewByDay(String bankId, String terminalCode, String fromDate, String toDate, String userId);

        TransStatisticDTO getTransactionOverviewNotSync(String bankId, String terminalCode, String fromDate, String toDate);

        List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdNotSync(String bankId, String terminalCode, String fromDate, String toDate);

        List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdAndDate(String bankId, String terminalCode, String fromDate, String toDate, String userId);

        List<TransStatisticByTimeDTO> getTransStatisticByTerminalIdAndDate(String bankId, String fromDate, String toDate, String userId);

        List<TransStatisticByTimeDTO> getTransStatisticByBankIdAndDate(String bankId, String fromDate, String toDate);

        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByFtCode(String terminalId, String value, String fromDate, String toDate, int offset);

        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByOrderId(String terminalId, String value, String fromDate, String toDate, int offset);

        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByContent(String terminalId, String value, String fromDate, String toDate, int offset);

        List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByStatus(String terminalId, int status, String fromDate, String toDate, int offset);

        List<ITransactionRelatedDetailDTO> getAllTransTerminalById(String terminalId, String fromDate, String toDate, int offset);

        List<TransactionReceiveAdminListDTO> getTransByTerminalCodeFromDateTerminal(String fromDate, String toDate, String value, String userId, int offset);

        List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDateTerminal(String userId, String value, String fromDate, String toDate, int offset);

        List<TransactionReceiveAdminListDTO> getUnsettledTransactions(String bankId, String fromDate, String toDate, int offset);

        List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByFtCode(String bankId, String value, String fromDate, String toDate, int offset);

        List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByOrderId(String bankId, String value, String fromDate, String toDate, int offset);

        List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByContent(String bankId, String value, String fromDate, String toDate, int offset);

        List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByTerminalCode(String bankId, String value, String fromDate, String toDate, int offset);

        void updateTransactionReceiveTerminal(String transactionId, String terminalCode, int type);

        TransactionReceiveEntity getTransactionReceiveById(String transactionId, String userId);

        TransactionReceiveEntity getTransactionReceiveById(String transactionId);

        List<TransactionRelatedDTO> getTransactionsByTerminalCodeAllDateListCode(List<String> allTerminalCode, int offset, String bankId);

        List<TransactionRelatedDTO> getTransactionsByTerminalCodeAndDateListCode(List<String> allTerminalCode, int offset, String bankId, String from, String to);

        List<TransactionRelatedDTO> getSubTerminalTransactions(List<String> codes,
                                                                       String fromDate, String toDate, int offset, int size);

        List<TransactionRelatedDTO> getSubTerminalTransactionsByFtCode(List<String> codes, String value,
                                                                               String fromDate, String toDate, int offset, int size);

        List<TransactionRelatedDTO> getSubTerminalTransactionsByOrderId(List<String> codes, String value,
                                                                                String fromDate, String toDate, int offset, int size);

        List<TransactionRelatedDTO> getSubTerminalTransactionsByContent(List<String> codes, String value,
                                                                                String fromDate, String toDate, int offset, int size);

        List<TransactionRelatedDTO> getSubTerminalTransactionsByAmount(List<String> codes, int value, String fromDate, String toDate, int offset, int size);

        int countSubTerminalTransactions(List<String> codes, String fromDate, String toDate);

        int countSubTerminalTransactionsByFtCode(List<String> codes, String value, String fromDate, String toDate);

        int countSubTerminalTransactionsByOrderId(List<String> codes, String value, String fromDate, String toDate);

        int countSubTerminalTransactionsByContent(List<String> codes, String value, String fromDate, String toDate);

        int countSubTerminalTransactionsByAmount(List<String> codes, int value, String fromDate, String toDate);

        TransStatisticDTO getTransactionOverviewBySubTerminalCode(String terminalCode, String fromDate, String toDate);

        TransStatisticDTO getTransactionOverviewBySubTerminalCode(List<String> subTerminalCodes,
                                                                  String fromDate, String toDate);

        List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeDate(List<String> codes,
                                                                                     String fromDate, String toDate);

        List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeDate(String subTerminalCode,
                                                                                     String fromDate, String toDate);

        List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeMonth(List<String> codes,
                                                                                      String fromDate, String toDate);

        List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeMonth(String subTerminalCode,
                                                                                      String fromDate, String toDate);

        List<TransactionRelatedDTO> getSubTerminalTransactionsByStatus(List<String> codes, int value, String fromDate, String toDate, int offset, int size);

        int countSubTerminalTransactionsByStatus(List<String> codes, int value, String fromDate, String toDate);

        List<ITransactionRelatedDetailDTO> getTransByBankId(String bankId, String fromDate, String toDate);

        List<ITransactionRelatedDetailDTO> getTransByTerminalCode(String terminalCode,
                                                                 String fromDate, String toDate);

        List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequest(String bankId,
                                                                              String fromDate, String toDate, int offset, int size);

        int countTransactionReceiveWithRequest(String bankId, String fromDate, String toDate);

        List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestById(String bankId, String fromDate, String toDate, int offset, int size, String value);

        ITransStatisticResponseWebDTO getTransactionWebOverview(String bankId, String fromDate, String toDate);

        List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByFtCode(String bankId, String fromDate, String toDate, int offset, int size, String value);

        List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByOrderId(String bankId, String fromDate, String toDate, int offset, int size, String value);

        List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByTerminalCode(String bankId, String fromDate, String toDate, int offset, int size, String value);

        List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByStatus(String bankId, String fromDate, String toDate, int offset, int size, int value);

        List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByContent(String bankId, String fromDate, String toDate, int offset, int size, String value);

        int countTransactionReceiveWithRequestByStatus(String bankId, String fromDate, String toDate, int value);

        int countTransactionReceiveWithRequestByTerminalCode(String bankId, String fromDate, String toDate, String value);

        int countTransactionReceiveWithRequestByContent(String bankId, String fromDate, String toDate, String value);

        int countTransactionReceiveWithRequestByOrderId(String bankId, String fromDate, String toDate, String value);

        int countTransactionReceiveWithRequestByFtCode(String bankId, String fromDate, String toDate, String value);

        List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByStatus(String bankId, int status, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalWithType2ByFtCode(String bankId, String userId, String value, List<String> listCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalWithType2ByOrderId(String bankId, String userId, String value, List<String> listCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalWithType2ByContent(String bankId, String userId, String value, List<String> listCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getTransTerminalWithType2ByStatus(String bankId, String userId, int value, List<String> listCode, String fromDate, String toDate, int offset);

        List<TransactionRelatedDTO> getAllTransTerminalWithType2(String bankId, String userId, List<String> listCode, String fromDate, String toDate, int offset);

        TransactionReceiveUpdateDTO getTransactionUpdateByBillNumber(String billNumberCre, String bankId, long time);

        int updateTransactionReceiveForInvoice(long totalAmountAfterVat, String qr, String id);

        List<TransReceiveInvoicesDTO> getTransactionReceiveByBankId(String bankId, String time);
}
