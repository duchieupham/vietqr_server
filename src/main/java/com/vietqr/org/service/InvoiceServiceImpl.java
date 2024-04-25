package com.vietqr.org.service;

import com.vietqr.org.dto.IInvoiceDetailDTO;
import com.vietqr.org.dto.IInvoiceResponseDTO;
import com.vietqr.org.dto.StartEndTimeDTO;
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
}
