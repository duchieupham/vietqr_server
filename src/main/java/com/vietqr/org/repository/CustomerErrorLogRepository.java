package com.vietqr.org.repository;

import com.vietqr.org.entity.CustomerErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerErrorLogRepository extends JpaRepository<CustomerErrorLogEntity, String> {

    @Query(value = "SELECT error_codes FROM customer_error_log WHERE customer_sync_id = :customerId "
            + "AND group_code = 'R' LIMIT 1",
            nativeQuery = true)
    String getRetryErrorsByCustomerId(String customerId);
}
