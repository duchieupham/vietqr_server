package com.vietqr.org.repository;

import com.vietqr.org.dto.TransRequestDTO;
import com.vietqr.org.entity.TransReceiveRequestMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransReceiveRequestMappingRepository extends JpaRepository<TransReceiveRequestMappingEntity, String> {
    @Query(value = "SELECT * FROM trans_receive_request_mapping " +
            "WHERE t.id = :requestId", nativeQuery = true)
    TransReceiveRequestMappingEntity findByRequestId(String requestId);

    @Query(value = "SELECT a.id AS requestId, a.transaction_receive_id AS transactionId, "
            + "a.user_id AS userId, a.terminal_id AS terminalId, a.merchant_id AS merchantId, "
            + "a.request_value AS requestValue, a.request_type AS requestType, "
            + "CONCAT(b.last_name, ' ', b.middle_name, ' ', b.first_name) AS fullName, '' AS phoneNumber, "
            + "'' AS terminalName, '' AS merchantName "
            + "FROM trans_receive_request_mapping a "
            + "INNER JOIN account_information b ON a.user_id = b.user_id "
            + "WHERE a.transaction_receive_id IN (:listTransId)", nativeQuery = true)
    List<TransRequestDTO> getTransactionReceiveRequest(List<String> listTransId);
}
