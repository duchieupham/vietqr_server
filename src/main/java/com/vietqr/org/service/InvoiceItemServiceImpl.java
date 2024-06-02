package com.vietqr.org.service;

import com.vietqr.org.dto.IInvoiceItemDetailDTO;
import com.vietqr.org.dto.IInvoiceItemResponseDTO;
import com.vietqr.org.entity.InvoiceItemEntity;
import com.vietqr.org.repository.InvoiceItemRepository;
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
}
