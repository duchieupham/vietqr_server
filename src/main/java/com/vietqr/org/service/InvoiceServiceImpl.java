package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceEntity;
import com.vietqr.org.repository.InvoiceRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository repo;

    @Override
    public void insert(InvoiceEntity entity) {
        repo.save(entity);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserId(String userId, int status, int offset, int size) {
        return repo.getInvoiceByUserId(userId, status, offset, size);
    }

    @Override
    public String checkDuplicatedInvoiceId(String invoiceId) {
        return repo.checkDuplicatedInvoiceId(invoiceId);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserIdAndMonth(String userId, int status, String month, int offset, int size) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByUserIdAndMonth(userId, status, fromDate, toDate, offset, size);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankId(String userId, int status, String bankId, int offset, int size) {
        return repo.getInvoiceByUserIdAndBankId(userId, status, bankId, offset, size);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankIdAndMonth(String userId, int status, String bankId, String month, int offset, int size) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo
                .getInvoiceByUserIdAndBankIdAndMonth(userId, status, bankId, fromDate, toDate, offset, size);
    }

    @Override
    public int countInvoiceByUserId(String userId, int status) {
        return repo.countInvoiceByUserId(userId, status);
    }

    @Override
    public int countInvoiceByUserIdAndMonth(String userId, int status, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByUserIdAndMonth(userId, status, fromDate, toDate);
    }

    @Override
    public int countInvoiceByUserIdAndBankId(String userId, int status, String bankId) {
        return repo.countInvoiceByUserIdAndBankId(userId, status, bankId);
    }

    @Override
    public int countInvoiceByUserIdAndBankIdAndMonth(String userId, int status, String bankId, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByUserIdAndBankIdAndMonth(userId, status, bankId, fromDate, toDate);
    }

    @Override
    public IInvoiceDetailDTO getInvoiceDetailById(String invoiceId) {
        return repo.getInvoiceDetailById(invoiceId);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByMerchantId(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByMerchantId(value, offset, size, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceUnpaid(String value, int offset, int size, String month, String userId) {
        StartEndTimeDTO startDate = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startDate.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startDate.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;

        return repo.getInvoiceUnpaid(value, offset, size, fromDate, toDate, userId);
    }

    @Override
    public int countInvoiceUnpaid(String value, String month, String userId) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceUnpaid(value, fromDate, toDate, userId);
    }


    @Override
    public int countInvoiceByMerchantId(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByMerchantId(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByInvoiceNumber(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByInvoiceNumber(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByInvoiceNumber(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByInvoiceNumber(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByBankAccount(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByBankAccount(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByBankAccount(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByBankAccount(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByPhoneNo(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByPhoneNo(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByPhoneNo(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByPhoneNo(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByStatus(int status, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByStatus(status, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByStatus(int status, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByStatus(status, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoices(int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoices(offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoice(String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoice(fromDate, toDate);
    }

    @Override
    public IAdminExtraInvoiceDTO getExtraInvoice(String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getExtraInvoice(fromDate, toDate);
    }

    @Override
    public IInvoiceQrDetailDTO getInvoiceQrById(String invoiceId) {
        return repo.getInvoiceQrById(invoiceId);
    }

    @Override
    public IInvoiceDTO getInvoiceByInvoiceDetail(String invoiceId) {
        return repo.getInvoiceByInvoiceDetail(invoiceId);
    }

    @Override
    public InvoiceUpdateItemDTO getInvoiceById(String invoiceId) {
        return repo.getInvoiceById(invoiceId);
    }

    public InvoiceEntity findInvoiceById(String invoiceId) {
        return repo.findById(invoiceId).orElse(null);
    }

    @Override
    public void updateInvoiceById(long vatAmount, long totalAmount,
                                  long totalAmountAfterVat, String invoiceId) {
        repo.updateInvoiceById(vatAmount, totalAmount, totalAmountAfterVat, invoiceId);
    }

    @Override
    public void removeByInvoiceId(String invoiceId) {
        repo.removeByInvoiceId(invoiceId);
    }

    @Override
    public InvoiceEntity getInvoiceEntityById(String invoiceId) {
        return repo.getInvoiceEntityById(invoiceId);
    }

    @Override
    public InvoiceEntity getInvoiceEntityByRefId(String id, long amount) {
        return repo.getInvoiceEntityByRefId(id, amount);
    }

    @Override
    public int updateStatusInvoice(String id, int status) {
        return repo.updateStatusInvoice(id, status);
    }

    @Override
    public String checkExistedInvoice(String invoiceId) {
        return repo.checkExistedInvoice(invoiceId);
    }

    @Override
    public IInvoiceDTO getInvoiceRequestPayment(String invoiceId) {
        return repo.getInvoiceRequestPayment(invoiceId);
    }

    @Override
    public InvoiceUnpaidStatisticDTO getTotalInvoiceUnpaidByUserId(String userId) {
        return repo.getTotalInvoiceUnpaidByUserId(userId);
    }
}
