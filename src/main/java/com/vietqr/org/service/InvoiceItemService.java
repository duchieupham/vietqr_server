package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceItemEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvoiceItemService {
    void insertAll(List<InvoiceItemEntity> itemEntities);

    void insert(InvoiceItemEntity entity);

    List<IInvoiceItemResponseDTO> getInvoiceByInvoiceId(String invoiceId);

    List<IInvoiceItemDetailDTO> getInvoiceItemsByInvoiceId(String invoiceId);

    InvoiceItemEntity getInvoiceItemById(String itemId);

    void removeByInvoiceIdInorge(String invoiceId, List<String> itemIds);

    void removeByInvoiceId(String invoiceId);

    List<IInvoiceItemDetailDTO> getInvoiceItemsByIds(List<String> itemItemIds);

    IAdminExtraInvoiceDTO getExtraInvoice(String time);

    List<InvoiceItemEntity> findByInvoiceId(String invoiceId);

    int updateAllItemIds(List<String> itemIds, long timePaid);

    int checkCountUnPaid(String id);

    void updateStatusInvoiceItem(String invoiceId);

    int checkInvoiceItemExist(String bankId, String merchantId, int type, String processDate);

    List<BankIdProcessDateResponseDTO> getProcessDatesByType(int type, List<String> bankId);

    List<InvoiceItemProcessDateDTO> getInvoiceItemByInvoiceId(String invoiceId);
}
