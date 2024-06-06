package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceItemEntity;
import com.vietqr.org.repository.InvoiceItemRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceItemServiceImpl implements InvoiceItemService {

    @Autowired
    private InvoiceItemRepository repo;

    @Override
    public void insertAll(List<InvoiceItemEntity> itemEntities) {
        repo.saveAll(itemEntities);
    }

    @Override
    public void insert(InvoiceItemEntity entity) {
        repo.save(entity);
    }

    @Override
    public List<IInvoiceItemResponseDTO> getInvoiceByInvoiceId(String invoiceId) {
        return repo.getInvoiceByInvoiceId(invoiceId);
    }

    @Override
    public List<IInvoiceItemDetailDTO> getInvoiceItemsByInvoiceId(String invoiceId) {
        return repo.getInvoiceItemsByInvoiceId(invoiceId);
    }

    @Override
    public InvoiceItemEntity getInvoiceItemById(String itemId) {
        return repo.getInvoiceItemById(itemId);
    }

    @Override
    public void removeByInvoiceIdInorge(String invoiceId, List<String> itemIds) {
        repo.removeByInvoiceId(invoiceId, itemIds);
    }

    @Override
    public void removeByInvoiceId(String invoiceId) {
        repo.removeByInvoiceId(invoiceId);
    }

    @Override
    public List<IInvoiceItemDetailDTO> getInvoiceItemsByIds(List<String> itemItemIds) {
        return repo.getInvoiceItemsByIds(itemItemIds);
    }

    @Override
    public IAdminExtraInvoiceDTO getExtraInvoice(String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getExtraInvoice(fromDate, toDate);
    }
    @Override
    public List<InvoiceItemEntity> findByInvoiceId(String invoiceId) {
        return repo.findInvoiceItemEntityByInvoiceId(invoiceId);
    }

    @Override
    public int updateAllItemIds(List<String> itemIds, long timePaid) {
        return repo.updateAllItemIds(itemIds, timePaid);
    }

    @Override
    public int checkCountUnPaid(String invoiceId) {
        return repo.checkCountUnPaid(invoiceId);
    }

    @Override
    public void updateStatusInvoiceItem(String invoiceId) {
        repo.updateStatusInvoiceItem(invoiceId, DateTimeUtil.getCurrentDateTimeUTC());
    }

    @Override
    public int checkInvoiceItemExist(String bankId, String merchantId, int type, String processDate) {
        return repo.checkInvoiceItemExist(bankId, merchantId,type, processDate);
    }

    @Override
    public List<BankIdProcessDateResponseDTO> getProcessDatesByType(int type, List<String> bankId) {
        return repo.getProcessDateByType(type, bankId);
    }

}
