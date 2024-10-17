package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.dto.ReportDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.ReportEntity;
import com.vietqr.org.entity.ReportImageEntity;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.ReportImageService;
import com.vietqr.org.service.ReportService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ReportController {
    private static final Logger logger = Logger.getLogger(ReportController.class);

    @Autowired
    ReportService reportService;

    @Autowired
    ReportImageService reportImageService;

    @Autowired
    ImageService imageService;

    @PostMapping("report")
    public ResponseEntity<ResponseMessageDTO> insertReport(@ModelAttribute ReportDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuid = UUID.randomUUID();
            LocalDateTime currentDateTime = LocalDateTime.now();
            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            ReportEntity entity = new ReportEntity(uuid.toString(), dto.getType(), dto.getDescription(), false, time);
            reportService.insertReport(entity);
            // insert image
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                for (MultipartFile image : dto.getImages()) {
                    UUID uuidImage = UUID.randomUUID();
                    String fileName = StringUtils.cleanPath(image.getOriginalFilename());
                    ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName,
                            image.getBytes());
                    imageService.insertImage(imageEntity);
                    //
                    UUID uuidReportImage = UUID.randomUUID();
                    ReportImageEntity reportImageEntity = new ReportImageEntity(uuidReportImage.toString(),
                            uuid.toString(), uuidImage.toString());
                    reportImageService.insertReportImage(reportImageEntity);
                }
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error at insertReport: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("reports")
    public ResponseEntity<List<ReportEntity>> getReports() {
        List<ReportEntity> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = reportService.getReports();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error at getReports: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("reports/{id}")
    public ResponseEntity<List<String>> getReportImages(@PathVariable("id") String id) {
        List<String> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = reportImageService.getImgIdsByReportId(id);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error at getReportImages: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
