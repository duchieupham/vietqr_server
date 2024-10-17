package com.vietqr.org.repository;

import com.vietqr.org.dto.CustomerErrorLogDTO;
import com.vietqr.org.entity.CustomerErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerErrorLogRepository extends JpaRepository<CustomerErrorLogEntity, String> {

    @Query(value = "SELECT error_codes AS errorCodes, group_code AS groupCode "
            + "FROM customer_error_log WHERE customer_sync_id = :customerId ",
            nativeQuery = true)
    List<CustomerErrorLogDTO> getRetryErrorsByCustomerId(String customerId);
}
