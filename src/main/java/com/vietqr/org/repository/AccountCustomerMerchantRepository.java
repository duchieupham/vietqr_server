package com.vietqr.org.repository;

import com.vietqr.org.entity.AccountCustomerMerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountCustomerMerchantRepository extends JpaRepository<AccountCustomerMerchantEntity, String> {
}
