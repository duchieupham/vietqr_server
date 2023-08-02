package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ReportImageEntity;
import com.vietqr.org.repository.ReportImageRepository;

@Service
public class ReportImageServiceImpl implements ReportImageService {

    @Autowired
    ReportImageRepository repo;

    @Override
    public int insertReportImage(ReportImageEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<String> getImgIdsByReportId(String reportId) {
        return repo.getImgIdsByReportId(reportId);
    }

}
