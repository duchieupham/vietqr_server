package com.vietqr.org.service;

import com.vietqr.org.dto.IInvoiceItemDetailDTO;
import com.vietqr.org.dto.IInvoiceItemResponseDTO;
import com.vietqr.org.entity.InvoiceItemEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvoiceItemService {
    void insertAll(List<InvoiceItemEntity> itemEntities);

    void insert(InvoiceItemEntity entity);

    List<IInvoiceItemResponseDTO> getInvoiceByInvoiceId(String invoiceId);

    List<IInvoiceItemDetailDTO> getInvoiceItemsByInvoiceId(String invoiceId);
}
