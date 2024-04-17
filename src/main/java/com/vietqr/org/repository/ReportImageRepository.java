package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.ReportImageEntity;

@Repository
public interface ReportImageRepository extends JpaRepository<ReportImageEntity, Long> {

    @Query(value = "SELECT img_id FROM report_image WHERE report_id = :reportId", nativeQuery = true)
    List<String> getImgIdsByReportId(@Param(value = "reportId") String reportId);
}
