package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ReportEntity;
import com.vietqr.org.repository.ReportRepository;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportRepository repo;

    @Override
    public int insertReport(ReportEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<ReportEntity> getReports() {
        return repo.getReports();
    }

}
