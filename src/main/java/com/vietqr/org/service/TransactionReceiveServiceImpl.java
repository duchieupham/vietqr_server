package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.CustomerInvoiceInfoDataDTO;
import com.vietqr.org.repository.CustomQueryRepository;
import com.vietqr.org.util.DateTimeUtil;
import com.vietqr.org.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vietqr.org.entity.TransactionReceiveEntity;

@Service
public class TransactionReceiveServiceImpl implements TransactionReceiveService {

    private static final String TRANSACTION_RECEIVE_NAME = "transaction_receive";

    @Autowired
    TransactionReceiveRepository repo;

    @Autowired
    CustomQueryRepository customQueryRepository;

    public int insertTransactionReceive(TransactionReceiveEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public void updateTransactionReceiveStatus(int status, String refId, String referenceNumber, long timePaid,
                                               String id) {
        repo.updateTransactionReceiveStatus(status, refId, referenceNumber, timePaid, id);
    }

    @Override
    public TransactionDetailDTO getTransactionById(String id) {
        return repo.getTransactionById(id);
    }

    @Override
    public TransactionReceiveEntity getTransactionByTraceIdAndAmount(String id, String amount, String transType) {
        Long amountParsed = Long.parseLong(amount);
        return repo.getTransactionByTraceId(id, amountParsed, transType, DateTimeUtil.get2LastPartition());
    }

    @Override
    public TransactionReceiveEntity getTransactionsById(String id) {
        return repo.getTransactionReceiveById(id);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransactions(offset, bankId, time);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactions(int offset, String bankId, String fromDate, String toDate) {
        return repo.getTransactions(offset, bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

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
    public TransactionReceiveEntity getTransactionReceiveByRefNumber(String referenceNumber, String transType) {
        return repo.getTransactionReceiveByRefNumber(referenceNumber, transType, DateTimeUtil.get2LastPartition());
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
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
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

    @Override
    public List<FeeTransactionInfoDTO> getFeePackageResponse(String time, List<String> bankIds) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long currentTime = DateTimeUtil.getCurrentDateTimeUTC();
        String year = DateTimeUtil.getYearAsString(toDate);
        int differenceMonthFromTime = DateTimeUtil.getDifferenceMonthFromTime(toDate, currentTime);
        List<String> suffix = new ArrayList<>();
        if (differenceMonthFromTime < 3) {
            suffix.add("");
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
        } else {
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
            if (DateTimeUtil.getMonth(toDate) == 1 && "24".equals(year)) {
                suffix.add("_2312");
            }
        }

        List<FeeTransactionInfoDTO> data = new ArrayList<>();
        for (String item : suffix) {
            List<FeeTransactionInfoDTO> dtos = new ArrayList<>();
            try {
                dtos = customQueryRepository.getTransactionInfoDataByBankIds(TRANSACTION_RECEIVE_NAME + item,
                        bankIds,
                        fromDate,
                        toDate);
            } catch (Exception e) {
                dtos = new ArrayList<>();
            }
            data.addAll(dtos);

        }
        return new ArrayList<>(data.stream()
                .collect(Collectors.toMap(
                        FeeTransactionInfoDTO::getBankId,
                        dto -> dto,
                        (dto1, dto2) -> {
                            dto1.setTotalCount(dto1.getTotalCount() + dto2.getTotalCount());
                            dto1.setTotalAmount(dto1.getTotalAmount() + dto2.getTotalAmount());
                            dto1.setCreditCount(dto1.getCreditCount() + dto2.getCreditCount());
                            dto1.setCreditAmount(dto1.getCreditAmount() + dto2.getCreditAmount());
                            dto1.setDebitCount(dto1.getDebitCount() + dto2.getDebitCount());
                            dto1.setDebitAmount(dto1.getDebitAmount() + dto2.getDebitAmount());
                            dto1.setControlCount(dto1.getControlCount() + dto2.getControlCount());
                            dto1.setControlAmount(dto1.getControlAmount() + dto2.getControlAmount());
                            return dto1;
                        }
                ))
                .values());
    }

    @Override
    public List<FeeTransactionInfoDTO> getFeePackageResponseRecordType(String time, List<String> bankIds) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long currentTime = DateTimeUtil.getCurrentDateTimeUTC();
        String year = DateTimeUtil.getYearAsString(toDate);
        int differenceMonthFromTime = DateTimeUtil.getDifferenceMonthFromTime(toDate, currentTime);
        List<String> suffix = new ArrayList<>();
        if (differenceMonthFromTime < 3) {
            suffix.add("");
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
        } else {
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
            if (DateTimeUtil.getMonth(toDate) == 1 && "24".equals(year)) {
                suffix.add("_2312");
            }
        }

        List<FeeTransactionInfoDTO> data = new ArrayList<>();
        for (String item : suffix) {
            List<FeeTransactionInfoDTO> dtos = new ArrayList<>();
            try {
                dtos = customQueryRepository.getTransactionInfoDataByBankIdRecords(TRANSACTION_RECEIVE_NAME + item,
                        bankIds,
                        fromDate,
                        toDate);
            } catch (Exception e) {
                dtos = new ArrayList<>();
            }
            data.addAll(dtos);

        }
        return new ArrayList<>(data.stream()
                .collect(Collectors.toMap(
                        FeeTransactionInfoDTO::getBankId,
                        dto -> dto,
                        (dto1, dto2) -> {
                            dto1.setTotalCount(dto1.getTotalCount() + dto2.getTotalCount());
                            dto1.setTotalAmount(dto1.getTotalAmount() + dto2.getTotalAmount());
                            dto1.setCreditCount(dto1.getCreditCount() + dto2.getCreditCount());
                            dto1.setCreditAmount(dto1.getCreditAmount() + dto2.getCreditAmount());
                            dto1.setDebitCount(dto1.getDebitCount() + dto2.getDebitCount());
                            dto1.setDebitAmount(dto1.getDebitAmount() + dto2.getDebitAmount());
                            dto1.setControlCount(dto1.getControlCount() + dto2.getControlCount());
                            dto1.setControlAmount(dto1.getControlAmount() + dto2.getControlAmount());
                            return dto1;
                        }
                ))
                .values());
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
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getTransByBankAccountFromDate(value,
                fromTime,
                toTime, offset);
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
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
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
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
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
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
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
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
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
    public List<TransactionRelatedDTO> getTransTerminalByStatus(String bankId, String userId, int value, List<String> terminalCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByStatus(bankId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByFtCode(String bankId, String userId, String value, List<String> terminalCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByFtCode(bankId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByOrderId(String bankId, String userId, String value,
                                                                 List<String> terminalCode, String fromDate,
                                                                 String toDate, int offset) {
        return repo.getTransTerminalByOrderId(bankId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalByContent(String bankId, String userId, String value, List<String> terminalCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalByContent(bankId, value, terminalCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getAllTransTerminal(String bankId, String userId, List<String> terminalCode, String fromDate, String toDate, int offset) {
        return repo.getAllTransTerminal(bankId, terminalCode, offset,
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
    public List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByFtCode(String terminalId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransTerminalByIdAndByFtCode(terminalId, value, fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByOrderId(String terminalId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransTerminalByIdAndByOrderId(terminalId, value,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByContent(String terminalId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransTerminalByIdAndByContent(terminalId, value,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<ITransactionRelatedDetailDTO> getTransTerminalByIdAndByStatus(String terminalId, int status, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransTerminalByIdAndByStatus(terminalId, status,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<ITransactionRelatedDetailDTO> getAllTransTerminalById(String terminalId, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getAllTransTerminalById(terminalId,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeFromDateTerminal(String fromDate, String toDate, String value, String userId, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransByTerminalCodeAndUserIdFromDateTerminal(
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, value, userId, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDateTerminal(String userId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getTransByBankAccountFromDateTerminal(userId, value,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getUnsettledTransactions(String bankId, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getUnsettledTransactions(bankId,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByFtCode(String bankId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getUnsettledTransactionsByFtCode(bankId, value,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByOrderId(String bankId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getUnsettledTransactionsByOrderId(bankId, value,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByContent(String bankId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getUnsettledTransactionsByContent(bankId, value,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByTerminalCode(String bankId, String value, String fromDate, String toDate, int offset) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate);
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate);
        return repo.getUnsettledTransactionsByTerminalCode(bankId, value,
                fromTime - DateTimeUtil.GMT_PLUS_7_OFFSET,
                toTime - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public void updateTransactionReceiveTerminal(String transactionId, String terminalCode, int type) {
        repo.updateTransactionReceiveTerminalCode(transactionId, terminalCode, type);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveById(String transactionId, String userId) {
        return repo.getTransactionReceiveByIdAndUserId(transactionId, userId);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveById(String transactionId) {
        return repo.getTransactionReceiveById(transactionId);
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByTerminalCodeAllDateListCode(List<String> allTerminalCode, int offset, String bankId) {
        return repo.getTransactionsByTerminalCodeAllDateListCode(allTerminalCode, offset, bankId, DateTimeUtil.get3MonthsPreviousAsLongInt());
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsByTerminalCodeAndDateListCode(List<String> allTerminalCode, int offset, String bankId, String from, String to) {
        return repo.getTransactionsByTerminalCodeAndDateListCode(allTerminalCode, offset, bankId,
                DateTimeUtil.getDateTimeAsLongInt(from) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(to) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(from) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(to) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getSubTerminalTransactions(List<String> codes, String fromDate, String toDate, int offset, int size) {
        return repo.getSubTerminalTransactions(codes,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public List<TransactionRelatedDTO> getSubTerminalTransactionsByFtCode(List<String> codes, String value, String fromDate, String toDate, int offset, int size) {
        return repo.getSubTerminalTransactionsByFtCode(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public List<TransactionRelatedDTO> getSubTerminalTransactionsByOrderId(List<String> codes, String value, String fromDate, String toDate, int offset, int size) {
        return repo.getSubTerminalTransactionsByOrderId(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public List<TransactionRelatedDTO> getSubTerminalTransactionsByContent(List<String> codes, String value, String fromDate, String toDate, int offset, int size) {
        return repo.getSubTerminalTransactionsByContent(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public List<TransactionRelatedDTO> getSubTerminalTransactionsByAmount(List<String> codes, int value, String fromDate, String toDate, int offset, int size) {
        return repo.getSubTerminalTransactionsByAmount(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public int countSubTerminalTransactions(List<String> codes, String fromDate, String toDate) {
        return repo.getTotalSubTerminalTransactions(codes,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countSubTerminalTransactionsByFtCode(List<String> codes, String value, String fromDate, String toDate) {
        return repo.getTotalSubTerminalTransactionsByFtCode(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countSubTerminalTransactionsByOrderId(List<String> codes, String value, String fromDate, String toDate) {
        return repo.getTotalSubTerminalTransactionsByOrderId(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countSubTerminalTransactionsByContent(List<String> codes, String value, String fromDate, String toDate) {
        return repo.getTotalSubTerminalTransactionsByContent(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countSubTerminalTransactionsByAmount(List<String> codes, int value, String fromDate, String toDate) {
        return repo.getTotalSubTerminalTransactionsByAmount(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransStatisticDTO getTransactionOverviewBySubTerminalCode(List<String> subTerminalCodes, String fromDate, String toDate) {
        return repo.getTransactionOverviewBySubTerminalCode(subTerminalCodes,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeDate(List<String> codes, String fromDate, String toDate) {
        return repo.getTransStatisticSubTerminalByTerminalCodeDate(codes,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeDate(String subTerminalCode, String fromDate, String toDate) {
        return repo.getTransStatisticSubTerminalByTerminalCodeDate(subTerminalCode,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeMonth(List<String> codes, String fromDate, String toDate) {
        return repo.getTransStatisticSubTerminalByTerminalCodeMonth(codes,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransStatisticByTimeDTO> getTransStatisticSubTerminalByTerminalCodeMonth(String subTerminalCode, String fromDate, String toDate) {
        return repo.getTransStatisticSubTerminalByTerminalCodeMonth(subTerminalCode,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getSubTerminalTransactionsByStatus(List<String> codes, int value, String fromDate, String toDate, int offset, int size) {
        return repo.getSubTerminalTransactionsByStatus(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public int countSubTerminalTransactionsByStatus(List<String> codes, int value, String fromDate, String toDate) {
        return repo.getTotalSubTerminalTransactionsByStatus(codes, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<ITransactionRelatedDetailDTO> getTransByBankId(String bankId, String fromDate, String toDate) {
        return repo.getTransByBankId(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<ITransactionRelatedDetailDTO> getTransByTerminalCode(String terminalCode,
                                                                    String fromDate, String toDate) {
        return repo.getTransByTerminalCode(terminalCode,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequest(String bankId, String fromDate,
                                                                                 String toDate, int offset, int size) {
        return repo.getTransactionReceiveWithRequest(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public int countTransactionReceiveWithRequest(String bankId, String fromDate, String toDate) {
        return repo.countTransactionReceiveWithRequest(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestById(String bankId, String fromDate, String toDate, int offset, int size, String value) {
        return repo.getTransactionReceiveWithRequestById(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size, value);
    }

    @Override
    public ITransStatisticResponseWebDTO getTransactionWebOverview(String bankId, String fromDate, String toDate) {
        return repo.getTransactionWebOverview(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByFtCode(String bankId, String fromDate, String toDate, int offset, int size, String value) {
        return repo.getTransactionReceiveWithRequestByFtCode(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size, value);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByOrderId(String bankId, String fromDate, String toDate, int offset, int size, String value) {
        return repo.getTransactionReceiveWithRequestByOrderId(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size, value);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByTerminalCode(String bankId, String fromDate, String toDate, int offset, int size, String value) {
        return repo.getTransactionReceiveWithRequestByTerminalCode(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size, value);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByStatus(String bankId, String fromDate, String toDate, int offset, int size, int value) {
        return repo.getTransactionReceiveWithRequestByStatus(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size, value);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransactionReceiveWithRequestByContent(String bankId, String fromDate, String toDate, int offset, int size, String value) {
        return repo.getTransactionReceiveWithRequestByContent(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size, value);
    }

    @Override
    public int countTransactionReceiveWithRequestByStatus(String bankId, String fromDate, String toDate, int value) {
        return repo.countTransactionReceiveWithRequestByStatus(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value);
    }

    @Override
    public int countTransactionReceiveWithRequestByTerminalCode(String bankId, String fromDate, String toDate, String value) {
        return repo.countTransactionReceiveWithRequestByTerminalCode(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value);
    }

    @Override
    public int countTransactionReceiveWithRequestByContent(String bankId, String fromDate, String toDate, String value) {
        return repo.countTransactionReceiveWithRequestByContent(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value);
    }

    @Override
    public int countTransactionReceiveWithRequestByOrderId(String bankId, String fromDate, String toDate, String value) {
        return repo.countTransactionReceiveWithRequestByOrderId(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value);
    }

    @Override
    public int countTransactionReceiveWithRequestByFtCode(String bankId, String fromDate, String toDate, String value) {
        return repo.countTransactionReceiveWithRequestByFtCode(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, value);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getUnsettledTransactionsByStatus(String bankId, int status, String fromDate, String toDate, int offset) {
        return repo.getUnsettledTransactionsByStatus(bankId, status,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalWithType2ByFtCode(String bankId, String userId, String value, List<String> listCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalWithType2ByFtCode(bankId, value, listCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalWithType2ByOrderId(String bankId, String userId, String value, List<String> listCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalWithType2ByOrderId(bankId, value, listCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalWithType2ByContent(String bankId, String userId, String value, List<String> listCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalWithType2ByContent(bankId, value, listCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getTransTerminalWithType2ByStatus(String bankId, String userId, int value, List<String> listCode, String fromDate, String toDate, int offset) {
        return repo.getTransTerminalWithType2ByStatus(bankId, value, listCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionRelatedDTO> getAllTransTerminalWithType2(String bankId, String userId, List<String> listCode, String fromDate, String toDate, int offset) {
        return repo.getAllTransTerminalWithType2(bankId, listCode, offset,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransactionReceiveUpdateDTO getTransactionUpdateByBillNumber(String billNumberCre, String bankId, long time) {
        return repo.getTransactionUpdateByBillNumber(billNumberCre, bankId,
                time - DateTimeUtil.GMT_PLUS_7_OFFSET,
                time + DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int updateTransactionReceiveForInvoice(long totalAmountAfterVat, String qr, String id) {
        return repo.updateTransactionReceiveForInvoice(totalAmountAfterVat, qr, id);
    }

    @Override
    public List<TransReceiveInvoicesDTO> getTransactionReceiveByBankId(String bankId, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long currentTime = DateTimeUtil.getCurrentDateTimeUTC();
        String year = DateTimeUtil.getYearAsString(toDate);
        int differenceMonthFromTime = DateTimeUtil.getDifferenceMonthFromTime(toDate, currentTime);
        List<String> suffix = new ArrayList<>();
        if (differenceMonthFromTime < 3) {
            suffix.add("");
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
        } else {
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
            if (DateTimeUtil.getMonth(toDate) == 1 && "24".equals(year)) {
                suffix.add("_2312");
            }
        }

        List<TransReceiveInvoicesDTO> data = new ArrayList<>();
        for (String item : suffix) {
            List<TransReceiveInvoicesDTO> dtos = new ArrayList<>();
            try {
                dtos = customQueryRepository.getTransReceiveInvoice(TRANSACTION_RECEIVE_NAME + item,
                        bankId,
                        fromDate,
                        toDate);
            } catch (Exception e) {
                dtos = new ArrayList<>();
            }
            data.addAll(dtos);
        }
        return data;
    }

    @Override
    public String checkExistedBillId(String billId) {
        return repo.checkExistedBillId(billId);
    }

    @Override
    public CustomerInvoiceInfoDataDTO getTransactionReceiveCusInfo(String customerId) {
        return repo.getTransactionReceiveCusInfo(customerId);
    }

    @Override
    public TransactionReceiveEntity getTransactionReceiveByBillId(String billId) {
        return repo.getTransactionReceiveByBillId(billId, DateTimeUtil.get2LastPartition());
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsV2(String bankId, List<String> transType,
                                                           String fromDate, String toDate, int offset) {
        return repo.getTransactionsV2(bankId, transType,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public ITransStatisticListExtra getExtraTransactionsV2(String bankId, String fromDate, String toDate) {
        return repo.getExtraTransactionsV2(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET
        );
    }

    @Override
    public TransactionDetailV2DTO getTransactionV2ById(String id) {
        return repo.getTransactionV2ById(id);
    }

    @Override
    public void updateHashTagTransaction(String hashTag, String transactionId) {
        repo.updateHashTagTransaction(hashTag, transactionId);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsListCodeV2(String bankId, List<String> terminalCodes,
                                                                   List<String> transType, String fromDate, String toDate, int offset) {
        return repo.getTransactionsListCodeV2(bankId, terminalCodes, transType,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsV2ByAmount(String bankId, List<String> transType, String value, String fromDate, String toDate, int offset) {
        return repo.getTransactionsV2ByAmount(bankId, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsV2ByStatus(String bankId, List<String> transType, String value, String fromDate, String toDate, int offset) {
        return repo.getTransactionsV2ByStatus(bankId, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsV2ByTerminalCode(String bankId, List<String> transType, List<String> terminalCodes,
                                                                         String fromDate, String toDate, int offset) {
        return repo.getTransactionsV2ByTerminalCode(bankId, transType, terminalCodes,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsV2ByContent(String bankId, List<String> transType, String value,
                                                                    String fromDate, String toDate, int offset) {
        return repo.getTransactionsV2ByContent(bankId, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsV2ByFtCode(String bankId, List<String> transType, String value,
                                                                   String fromDate, String toDate, int offset) {
        return repo.getTransactionsV2ByFtCode(bankId, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsV2ByOrderId(String bankId, List<String> transType, String value, String fromDate, String toDate, int offset) {
        return repo.getTransactionsV2ByOrderId(bankId, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByAmount(String bankId, List<String> terminalCodes,
                                                                           List<String> transType, String value,
                                                                           String fromDate, String toDate, int offset) {
        return repo.getTransactionsListCodeV2ByAmount(bankId, terminalCodes, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByStatus(String bankId, List<String> terminalCodes,
                                                                           List<String> transType, String value,
                                                                           String fromDate, String toDate, int offset) {
        return repo.getTransactionsListCodeV2ByStatus(bankId, terminalCodes, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByTerminalCode(String bankId, List<String> terminalCodes,
                                                                                 List<String> transType, List<String> value,
                                                                                 String fromDate, String toDate, int offset) {
        return repo.getTransactionsListCodeV2ByTerminalCode(bankId, terminalCodes, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByContent(String bankId, List<String> terminalCodes,
                                                                            List<String> transType, String value,
                                                                            String fromDate, String toDate, int offset) {
        return repo.getTransactionsListCodeV2ByContent(bankId, terminalCodes, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByOrderId(String bankId, List<String> terminalCodes,
                                                                            List<String> transType, String value,
                                                                            String fromDate, String toDate, int offset) {
        return repo.getTransactionsListCodeV2ByOrderId(bankId, terminalCodes, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public List<TransactionRelatedV2DTO> getTransactionsListCodeV2ByReferenceNumber(String bankId, List<String> terminalCodes,
                                                                                    List<String> transType, String value,
                                                                                    String fromDate, String toDate, int offset) {
        return repo.getTransactionsListCodeV2ByReferenceNumber(bankId, terminalCodes, transType, value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset);
    }

    @Override
    public ITransStatisticListExtra getExtraTransactionsByListCodeV2(String bankId, List<String> listCode, String fromDate, String toDate) {
        return repo.getExtraTransactionsByListCodeV2(bankId, listCode,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET
        );
    }

    @Override
    public String checkExistedOrderId(String bankId, String orderId) {
        return repo.checkExistedOrderId(bankId, orderId);
    }

    @Override
    public TransStatisticV2DTO getTransactionOverviewV2(String bankId, String fromDate, String toDate) {
        return repo.getTransactionOverviewV2(bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<ITransactionLatestDTO> getTransactionLastest(String bankId, int limit) {
        return repo.getTransactionLastest(bankId, limit, DateTimeUtil.get2LastPartition());
    }

    @Override
    public List<TransactionRelatedDTO> getTransactionsBySubCode(String value, int offset, String bankId, String from, String to) {
        return repo.getTransactionsBySubCode(value, offset,
            DateTimeUtil.getDateTimeAsLongInt(from) - DateTimeUtil.GMT_PLUS_7_OFFSET - 600,
            DateTimeUtil.getDateTimeAsLongInt(to) - DateTimeUtil.GMT_PLUS_7_OFFSET,
            DateTimeUtil.getDateTimeAsLongInt(from) - DateTimeUtil.GMT_PLUS_7_OFFSET,
            DateTimeUtil.getDateTimeAsLongInt(to) - DateTimeUtil.GMT_PLUS_7_OFFSET, bankId);
    }

    @Override
    public void updateTransactionRefundStatus(String ftCode, String subCode, String terminalCode, String orderId, int type) {
        repo.updateTransactionRefundStatus(ftCode, subCode, terminalCode, orderId, type);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByBankAccountAllDate(String value, int offset, int size) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByBankAccountAllDate(value, offset, size, time);
    }

    @Override
    public int countTransByBankAccountAllDate(String value) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.countTransByBankAccountAllDate(value, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByBankAccountFromDate(String value, String fromDate, String toDate, int offset, int size) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getTransByBankAccountFromDate(value, fromTime, toTime, offset, size);
    }

    @Override
    public int countTransByBankAccountFromDate(String value, String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countTransByBankAccountFromDate(value, fromTime, toTime);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCode(String value, int offset, int size) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByFtCode(value, offset, size, time);
    }

    @Override
    public int countTransByFtCode(String value) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.countTransByFtCode(value, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByFtCode(String value, int offset, String fromDate, String toDate, int size) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getTransByFtCode(value, offset, fromTime, toTime, size);
    }

    @Override
    public int countTransByFtCode(String value, String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countTransByFtCode(value, fromTime, toTime);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderId(String value, int offset, int size) {
        return null;
    }

    @Override
    public int countTransByOrderId(String value) {
        return 0;
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByOrderId(String value, String fromDate, String toDate, int offset, int size) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByOrderId(value, offset, size, time);
    }

    @Override
    public int countTransByOrderId(String value, String fromDate, String toDate) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.countTransByOrderId(value, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContent(String value, int offset, int size) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByContent(value, offset, size, time);
    }

    @Override
    public int countTransByContent(String value) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.countTransByContent(value, time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByContent(String value, String fromDate, String toDate, int offset, int size) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getTransByContent(value, fromTime, toTime, offset, size);
    }

    @Override
    public int countTransByContent(String value, String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countTransByContent(value, fromTime, toTime);
    }


    @Override
    public int countTransByTerminalCode(String value) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.countTransByTerminalCode(value, time);
    }

    @Override
    public int countTransByTerminalCode(String value, String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countTransByTerminalCode(value, fromTime, toTime);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransAllDate(int offset, int size) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getAllTransAllDate(offset, size, time);
    }

    @Override
    public int countAllTransAllDate() {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.countAllTransAllDate(time);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getAllTransFromDate(String fromDate, String toDate, int offset, int size) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getAllTransFromDate(fromTime, toTime, offset, size);
    }

    @Override
    public int countAllTransFromDate(String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countAllTransFromDate(fromTime, toTime);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeFromDate(String value, String fromDate, String toDate, int offset, int size) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getTransByTerminalCodeFromDate(value, fromTime, toTime, offset, size);
    }

    @Override
    public int countTransByTerminalCodeFromDate(String value, String fromDate, String toDate) {
        long fromTime = DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toTime = DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countTransByTerminalCodeFromDate(value, fromTime, toTime);
    }

    @Override
    public List<TransactionReceiveAdminListDTO> getTransByTerminalCodeAllDate(String value, int offset, int size) {
        long time = DateTimeUtil.get3MonthsPreviousAsLongInt();
        return repo.getTransByTerminalCodeAllDate(value, offset, size, time);
    }

    @Override
    public TransStatisticV2DTO getTransactionOverviewV2ByTerminalCode(String bankId, String terminalCode, String fromDate, String toDate) {
        return repo.getTransactionOverviewV2ByTerminalCode(bankId, terminalCode,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<ITransactionReceiveAdminInfoDTO> getTransactionReceiveToMapInvoice(String bankId, String fromDate, String toDate) {
        return repo.getTransactionReceiveToMapInvoice(
                bankId,
                DateTimeUtil.getDateTimeAsLongInt(fromDate),
                DateTimeUtil.getDateTimeAsLongInt(toDate)
        );
    }

    @Override
    public void updateTransactionReceiveType(String id) {
        repo.updateTransactionReceiveType(id);
    }

    @Override
    public TransStatisticDTO getTransactionOverviewBySubTerminalCode(String subTerminalCode, String fromDate, String toDate) {
        return repo.getTransactionOverviewBySubTerminalCode(subTerminalCode,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
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

    @Override
    public List<FeeTransactionInfoDTO> getTransactionInfoDataByBankId(String bankId, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long currentTime = DateTimeUtil.getCurrentDateTimeUTC();
        String year = DateTimeUtil.getYearAsString(toDate);
        int differenceMonthFromTime = DateTimeUtil.getDifferenceMonthFromTime(toDate, currentTime);
        List<String> suffix = new ArrayList<>();
        if (differenceMonthFromTime < 3) {
            suffix.add("");
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
        } else {
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
            if (DateTimeUtil.getMonth(toDate) == 1 && "24".equals(year)) {
                suffix.add("_2312");
            }
        }

        List<FeeTransactionInfoDTO> data = new ArrayList<>();
        for (String item : suffix) {
            List<FeeTransactionInfoDTO> dtos = new ArrayList<>();
            try {
                dtos = customQueryRepository.getTransactionInfoDataByBankId(TRANSACTION_RECEIVE_NAME + item,
                        bankId,
                        fromDate,
                        toDate);
            } catch (Exception e) {
                dtos = new ArrayList<>();
            }
            data.addAll(dtos);
        }
        return new ArrayList<>(data.stream()
                .collect(Collectors.toMap(
                        FeeTransactionInfoDTO::getBankId,
                        dto -> dto,
                        (dto1, dto2) -> {
                            dto1.setTotalCount(dto1.getTotalCount() + dto2.getTotalCount());
                            dto1.setTotalAmount(dto1.getTotalAmount() + dto2.getTotalAmount());
                            dto1.setCreditCount(dto1.getCreditCount() + dto2.getCreditCount());
                            dto1.setCreditAmount(dto1.getCreditAmount() + dto2.getCreditAmount());
                            dto1.setDebitCount(dto1.getDebitCount() + dto2.getDebitCount());
                            dto1.setDebitAmount(dto1.getDebitAmount() + dto2.getDebitAmount());
                            dto1.setControlCount(dto1.getControlCount() + dto2.getControlCount());
                            dto1.setControlAmount(dto1.getControlAmount() + dto2.getControlAmount());
                            return dto1;
                        }
                ))
                .values());
    }

    @Override
    public List<DataTransactionDTO> getTransactionInfo(String bankId, String time, int recordType) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long currentTime = DateTimeUtil.getCurrentDateTimeUTC();
        String year = DateTimeUtil.getYearAsString(toDate);
        int differenceMonthFromTime = DateTimeUtil.getDifferenceMonthFromTime(toDate, currentTime);
        List<String> suffix = new ArrayList<>();
        if (differenceMonthFromTime < 3) {
            suffix.add("");
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
        } else {
            List<String> strings = StringUtil.getStartQuarter(DateTimeUtil.getMonth(toDate), year);
            for (String item : strings) {
                String dto = "";
                dto = "_" + year + item;
                suffix.add(dto);
            }
            if (DateTimeUtil.getMonth(toDate) == 1 && "24".equals(year)) {
                suffix.add("_2312");
            }
        }

        List<DataTransactionDTO> data = new ArrayList<>();
        for (String item : suffix) {
            List<DataTransactionDTO> dtos = new ArrayList<>();
            try {
                dtos = customQueryRepository.findTransactionsByBankIdAndTimeRange(TRANSACTION_RECEIVE_NAME + item,
                        bankId,
                        fromDate,
                        toDate,
                        recordType);
            } catch (Exception e) {
                dtos = new ArrayList<>();
            }
            data.addAll(dtos);
        }
        return data;
    }
}
