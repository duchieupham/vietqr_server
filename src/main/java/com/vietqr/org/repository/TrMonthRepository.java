package com.vietqr.org.repository;

import com.vietqr.org.dto.TrMonthDTO;
import com.vietqr.org.entity.TrMonthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrMonthRepository extends JpaRepository<TrMonthEntity, String> {
    @Query(value = "SELECT id AS id, month AS month, "
            + "trs AS data FROM tr_month "
            + "WHERE month = :time LIMIT 1", nativeQuery = true)
    List<TrMonthDTO> getTrMonthByMonth(String time);


//    @Query(value = "SELECT id AS id, month AS month, "
//            + "trs AS data FROM tr_month "
//            + "WHERE month = :time", nativeQuery = true)
//    List<TrMonthDTO> getTrMonthByMonths(@Param(value = "month")String time);
}
