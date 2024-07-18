package com.vietqr.org.repository;

import com.vietqr.org.dto.TrMonthDTO;
import com.vietqr.org.entity.TrMonthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrMonthRepository extends JpaRepository<TrMonthEntity, String> {
    @Query(value = "SELECT id AS id, month AS month, "
            + "trs AS data FROM tr_month "
            + "WHERE month = :time LIMIT 1", nativeQuery = true)
    TrMonthDTO getTrMonthByMonth(String time);
}
