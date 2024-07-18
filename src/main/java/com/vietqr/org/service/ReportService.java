package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ReportEntity;

@Service
public interface ReportService {

    public int insertReport(ReportEntity entity);

    public List<ReportEntity> getReports();
}
