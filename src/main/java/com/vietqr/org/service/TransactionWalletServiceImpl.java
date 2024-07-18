package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.*;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.TransactionWalletEntity;

import org.springframework.beans.factory.annotation.Autowired;
import com.vietqr.org.repository.TransactionWalletRepository;

@Service
public class TransactionWalletServiceImpl implements TransactionWalletService {

    @Autowired
    TransactionWalletRepository repo;

    @Override
    public int insertTransactionWallet(TransactionWalletEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public int insertAll(List<TransactionWalletEntity> entities) {
        return repo.saveAll(entities) == null ? 0 : 1;
    }

    @Override
    public TransactionWalletEntity getTransactionWalletById(String id) {
        return repo.getTransactionWalletById(id);
    }

    @Override
    public List<TransactionWalletEntity> getTransactionWalletsByUserId(String userId) {
        return repo.getTransactionWalletsByUserId(userId);
    }

    @Override
    public List<TransactionWalletEntity> getTransactionWallets() {
        return getTransactionWallets();
    }

    @Override
    public void updateTransactionWalletStatus(int status, long timePaid, String id) {
        repo.updateTransactionWalletStatus(status, timePaid, id);
    }

    @Override
    public TransactionWalletEntity getTransactionWalletByBillNumber(String billNumber) {
        return repo.getTransactionWalletByBillNumber(billNumber);
    }

    @Override
    public void updateTransactionWallet(int status, long timePaid, String amount, String phoneNoRC, String userId,
            String otp,
            int paymentType) {
        repo.updateTransactionWallet(status, timePaid, amount, phoneNoRC, userId, otp, paymentType);
    }

    @Override
    public String checkExistedTransactionnWallet(String otp, String userId, int paymentType) {
        return repo.checkExistedTransactionnWallet(otp, userId, paymentType);
    }

    @Override
    public void updateTransactionWalletConfirm(long timeCreated, String amount, String userId, String otp,
            int paymentType) {
        repo.updateTransactionWalletConfirm(timeCreated, amount, userId, otp, paymentType);
    }

    @Override
    public List<TransWalletListDTO> getTransactionWalletList(String userId, int offset) {
        return repo.getTransactionWalletList(userId, offset);
    }

    @Override
    public List<TransWalletListDTO> getTransactionWalletListByStatus(String userId, int status, int offset) {
        return repo.getTransactionWalletListByStatus(userId, status, offset);
    }

    @Override
    public List<TransactionVNPTItemDTO> getTransactionsVNPT(int offset) {
        return repo.getTransactionsVNPT(offset);
    }

    @Override
    public List<TransactionVNPTItemDTO> getTransactionsVNPTFilter(int status, int offset) {
        return repo.getTransactionsVNPTFilter(status, offset);
    }

    @Override
    public VNPTEpayTransCounterDTO getVNPTEpayCounter() {
        return repo.getVNPTEpayCounter();
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletByBillNumberAndVNPTEpay(String value,
                                                                                       String fromDate, String toDate,
                                                                                       int offset, int size) {
        return repo.getTransactionWalletByBillNumberAndVNPTEpay(
                value, DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset, size
        );
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletByBillNumber(String value, String fromDate, String toDate,
                                                                            int offset, int size) {
        return repo.getTransactionWalletByBillNumber(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset, size
        );
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletByBillNumberAndAnnualFee(String value,
                                                                                        String fromDate, String toDate,
                                                                                        int offset, int size) {
        return repo.getTransactionWalletByBillNumberAndAnnualFee(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset, size
        );
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletByPhoneNoAndVNPTEpay(String value,
                                                                                    String fromDate, String toDate,
                                                                                    int offset, int size) {
        return repo.getTransactionWalletByPhoneNoAndVNPTEpay(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset, size
        );
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletByPhoneNoAndAnnualFee(String value,
                                                                                     String fromDate, String toDate,
                                                                                     int offset, int size) {
        return repo.getTransactionWalletByPhoneNoAndAnnualFee(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset, size
        );
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletByPhoneNo(String value, String fromDate, String toDate,
                                                                         int offset, int size) {
        return repo.getTransactionWalletByPhoneNo(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset, size
        );
    }

    @Override
    public int countTransactionWalletByBillNumberAndVNPTEpay(String value, String fromDate, String toDate) {
        return repo.countTransactionWalletByBillNumberAndVNPTEpay(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countTransactionWalletByBillNumberAndAnnualFee(String value, String fromDate, String toDate) {
        return repo.countTransactionWalletByBillNumberAndAnnualFee(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countTransactionWalletByBillNumber(String value, String fromDate, String toDate) {
        return repo.countTransactionWalletByBillNumber(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countTransactionWalletByPhoneNoAndVNPTEpay(String value, String fromDate, String toDate) {
        return repo.countTransactionWalletByPhoneNoAndVNPTEpay(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate)- DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countTransactionWalletByPhoneNoAndAnnualFee(String value, String fromDate, String toDate) {
        return repo.countTransactionWalletByPhoneNoAndAnnualFee(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public int countTransactionWalletByPhoneNo(String value, String fromDate, String toDate) {
        return repo.countTransactionWalletByPhoneNo(value,
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletVNPTEpay(String fromDate, String toDate, int offset, int size) {
        return repo.getTransactionWalletVNPTEpay(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                offset, size);
    }

    @Override
    public int countTransactionWalletVNPTEpay(String fromDate, String toDate) {
        return repo.countTransactionWalletVNPTEpay(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public List<TransactionWalletAdminDTO> getTransactionWalletAnnualFee(String fromDate, String toDate, int offset, int size) {
        return repo.getTransactionWalletAnnualFee(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET, offset, size);
    }

    @Override
    public int countTransactionWalletAnnualFee(String fromDate, String toDate) {
        return repo.countTransactionWalletAnnualFee(
                DateTimeUtil.getDateTimeAsLongInt(fromDate) - DateTimeUtil.GMT_PLUS_7_OFFSET,
                DateTimeUtil.getDateTimeAsLongInt(toDate) - DateTimeUtil.GMT_PLUS_7_OFFSET);
    }

    @Override
    public TransactionWalletEntity getTransactionWalletByRefId(String id) {
        return repo.getTransactionWalletByRefId(id);
    }

    @Override
    public String getRefIdDebitByInvoiceRefId(String refId) {
        return repo.getRefIdDebitByInvoiceRefId(refId);
    }

    @Override
    public TransWalletUpdateDTO getBillNumberByRefIdTransWallet(String refIdDebit) {
        return repo.getBillNumberByRefIdTransWallet(refIdDebit);
    }

    @Override
    public void updateAmountTransWallet(String id, String amount) {
        repo.updateAmountTransWallet(id, amount);
    }

}
