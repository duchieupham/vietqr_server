package com.vietqr.org.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountCustomerEntity;

@Repository
public interface AccountCustomerRepository extends JpaRepository<AccountCustomerEntity, Long> {

    @Query(value = "SELECT * FROM account_customer WHERE username = :username AND available = true", nativeQuery = true)
    AccountCustomerEntity getUserByUsername(@Param(value = "username") String username);
}