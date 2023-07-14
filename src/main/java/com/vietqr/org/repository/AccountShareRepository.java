package com.vietqr.org.repository;

import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AccountShareRepository extends JpaRepository<AccountShareEntity, Long> {

}
