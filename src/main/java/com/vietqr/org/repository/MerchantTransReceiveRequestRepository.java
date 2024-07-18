package com.vietqr.org.repository;

import com.vietqr.org.entity.MerchantTransReceiveRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantTransReceiveRequestRepository extends JpaRepository<MerchantTransReceiveRequestEntity, String> {
}
