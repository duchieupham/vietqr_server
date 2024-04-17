package com.vietqr.org.repository;

import com.vietqr.org.dto.TransRequestDTO;
import com.vietqr.org.entity.TransReceiveRequestMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransReceiveRequestMappingRepository extends JpaRepository<TransReceiveRequestMappingEntity, String> {
    @Query(value = "SELECT t.* FROM trans_receive_request_mapping t " +
            "WHERE t.id = :requestId", nativeQuery = true)
    TransReceiveRequestMappingEntity findByRequestId(String requestId);

    @Query(value = "SELECT a.id AS requestId, a.transaction_receive_id AS transactionId, "
            + "a.user_id AS userId, c.id AS terminalId, c.merchant_id AS merchantId, "
            + "a.request_value AS requestValue, a.request_type AS requestType, "
            + "CONCAT(b.last_name, ' ', b.middle_name, ' ', b.first_name) AS fullName, "
            + "d.phone_no AS phoneNumber, "
            + "c.name AS terminalName, c.merchant_id AS merchantName "
            + "FROM trans_receive_request_mapping a "
            + "INNER JOIN account_information b ON a.user_id = b.user_id "
            + "INNER JOIN terminal c ON a.request_value = c.code "
            + "INNER JOIN account_login d ON b.user_id = d.id "
            + "WHERE a.transaction_receive_id IN (:listTransId) AND a.status = 0 ", nativeQuery = true)
    List<TransRequestDTO> getTransactionReceiveRequest(List<String> listTransId);
}
