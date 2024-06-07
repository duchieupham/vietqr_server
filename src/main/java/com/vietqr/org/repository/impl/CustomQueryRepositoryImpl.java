package com.vietqr.org.repository.impl;

import com.vietqr.org.dto.DataTransactionDTO;
import com.vietqr.org.dto.FeeTransactionInfoDTO;
import com.vietqr.org.dto.TransReceiveInvoicesDTO;
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
    public List<TransReceiveInvoicesDTO> getTransReceiveInvoice(String tableName, String bankId, long fromDate, long toDate) {
        String queryString =
                "SELECT a.id AS id, a.amount, a.content, a.trans_type, "
                        + "a.type, a.time, a.time_paid, a.status "
                        + "FROM " + tableName + " a "
                        + "WHERE a.bank_id = :bankId AND a.status = 1 AND a.content != 'NODATA' "
                        + "AND a.time >= :fromTime AND a.time <= :toTime ";
        Query query = entityManager.createNativeQuery(queryString, "TransReceiveInvoicesDTO");
        query.setParameter("bankId", bankId);
        query.setParameter("fromTime", fromDate + "");
        query.setParameter("toTime", toDate + "");
        return (List<TransReceiveInvoicesDTO>) query.getResultList();
    }

    @Override
    public List<FeeTransactionInfoDTO> getTransactionInfoDataByBankId(String tableName, String bankId, long fromDate, long toDate) {
        String queryString = "SELECT "
                + "count(id) as totalCount, "
                + "sum(amount) as totalAmount, "
                + "count(case when trans_type ='C' then id else null end) as creditCount, "
                + "sum(case when trans_type='C' then amount else 0 end) as creditAmount, "
                + "count(case when trans_type='D' then id else null end) as debitCount, "
                + "sum(case when trans_type='D' then amount else 0 end) as debitAmount, "
                + "count(case when trans_type='C' and (type = 0 or type = 1) then id else null end) as controlCount, "
                + "sum(case when trans_type ='C' and (type = 0 or type = 1) then amount else 0 end) as controlAmount "
                + "FROM " + tableName + " "
                + "WHERE bank_id = :bank_id "
                + "AND status = 1 "
                + "AND time BETWEEN :fromDate AND :toDate "
                + "AND content != 'NODATA'";

        Query query = entityManager.createNativeQuery(queryString, "FeeTransactionInfoDTO");
        query.setParameter("bank_id", bankId);
        query.setParameter("fromDate", fromDate + "");
        query.setParameter("toDate", toDate + "");
        return (List<FeeTransactionInfoDTO>) query.getResultList();
    }

    @Override
    public List<DataTransactionDTO> findTransactionsByBankIdAndTimeRange(String tableName, String bankId, long fromDate, long toDate, int recordType) {
        String sql;
        if (recordType == 0) {
            sql = "SELECT bank_account, content, amount, time, time_paid, type, status, trans_type, reference_number, " +
                    "order_id, terminal_code, note FROM "+ tableName +"  WHERE bank_id = :bank_id AND time " +
                    "BETWEEN :fromDate AND :toDate AND status = 1 AND content != 'NODATA' AND trans_type = 'C' " +
                    "AND (type = 0 OR type = 1)";
        } else {
            sql = "SELECT bank_account, content, amount, time, time_paid, type, status, trans_type, reference_number, " +
                    "order_id, terminal_code, note FROM "+ tableName +"  WHERE bank_id = :bank_id AND time BETWEEN" +
                    " :fromDate AND :toDate AND status = 1 AND content != 'NODATA'";
        }
        Query query = entityManager.createNativeQuery(sql, "DataTransactionDTO");
        query.setParameter("bank_id", bankId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        return query.getResultList();
    }
}
