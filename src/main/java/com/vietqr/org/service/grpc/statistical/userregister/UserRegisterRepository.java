package com.vietqr.org.service.grpc.statistical.userregister;

import com.vietqr.org.entity.AccountLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegisterRepository extends JpaRepository<AccountLoginEntity, String> {
    @Query(value = "SELECT count(al.id) as userCount," +
            "    SUM(CASE WHEN ai.register_platform = 'MOBILE_ADR' THEN 1 ELSE 0 END) AS androidPlatform, " +
            "    SUM(CASE WHEN ai.register_platform = 'MOBILE' THEN 1 ELSE 0 END) AS iosPlatform," +
            "    SUM(CASE WHEN ai.register_platform = 'WEB' THEN 1 ELSE 0 END) AS webPlatform " +
            "FROM account_login al " +
            "JOIN account_information ai ON al.id = ai.user_id " +
            "WHERE al.time BETWEEN :startDate AND :endDate", nativeQuery = true)
    IUserRegisterDTO getUserRegisterData(@Param("startDate") long startDate, @Param("endDate") long endDate);
}
