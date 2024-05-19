package com.vietqr.org.service;

import com.vietqr.org.dto.IInvoiceItemDetailDTO;
import com.vietqr.org.dto.IInvoiceItemRemoveDTO;
import com.vietqr.org.dto.IInvoiceItemResponseDTO;
import com.vietqr.org.dto.InvoiceUpdateItemDTO;
import com.vietqr.org.entity.InvoiceItemEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvoiceItemService {
    void insertAll(List<InvoiceItemEntity> itemEntities);

    void insert(InvoiceItemEntity entity);

    List<IInvoiceItemResponseDTO> getInvoiceByInvoiceId(String invoiceId);

    List<IInvoiceItemDetailDTO> getInvoiceItemsByInvoiceId(String invoiceId);

    IInvoiceItemRemoveDTO getInvoiceRemoveByInvoiceId(String itemId);

    void removeById(String invoiceId);

    InvoiceItemEntity getInvoiceItemById(String itemId);

    void removeByInvoiceId(String invoiceId);
}
