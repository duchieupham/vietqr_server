package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.entity.ReportImageEntity;

@Service
public interface ReportImageService {

    public int insertReportImage(ReportImageEntity entity);

    public List<String> getImgIdsByReportId(String reportId);
}
