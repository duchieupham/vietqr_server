package com.vietqr.org.repository.impl;

import com.vietqr.org.dto.TransReceiveInvoiceDTO;
import com.vietqr.org.repository.CustomQueryRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CustomQueryRepositoryImpl implements CustomQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TransReceiveInvoiceDTO> getTransReceiveInvoice(String tableName, String bankId, long fromDate, long toDate) {
        String queryString =
                "SELECT a.id AS id, a.amount AS amount, a.content AS content "
                + "FROM " + tableName + " a "
                + "WHERE a.order_id = :billNumber "
                + "AND a.bank_id = :bankId "
                + "AND a.time >= :fromTime AND a.time <= :toTime ";
        Query query = entityManager.createNativeQuery(queryString, "TransReceiveInvoiceDTO");
        query.setParameter("bankId", bankId);
        query.setParameter("fromTime", fromDate + "");
        query.setParameter("toTime", toDate + "");
        return (List<TransReceiveInvoiceDTO>) query.getResultList();
    }
}
