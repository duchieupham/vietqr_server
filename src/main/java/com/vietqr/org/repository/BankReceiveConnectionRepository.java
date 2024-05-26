package com.vietqr.org.repository;

import com.vietqr.org.dto.IMerchantBankMapperDTO;
import com.vietqr.org.entity.BankReceiveConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BankReceiveConnectionRepository extends JpaRepository<BankReceiveConnectionEntity, String> {
}
