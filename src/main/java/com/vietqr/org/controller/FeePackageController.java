package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.FeePackageEntity;
import com.vietqr.org.service.FeePackageService;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FeePackageController {
    private static final Logger logger = Logger.getLogger(FeePackageController.class);

    @Autowired
    private FeePackageService feePackageService;

    @GetMapping("fee-package")
    public ResponseEntity<Object> getListFeePackage(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;
            List<FeePackageDTO> data = new ArrayList<>();
            List<IFeePackageDTO> info = new ArrayList<>();

            switch (type) {
                case 1:
                    info = feePackageService.getListFeePackageByName(value, offset, size);
                    totalElement = feePackageService.countFeePackageByName(value);
                    break;
                case 2:
                    info = feePackageService.getListFeePackageByFee(value, offset, size);
                    totalElement = feePackageService.countFeePackageByFee(value);
                    break;
                default:
                    new ArrayList<>();
                    break;
            }

            data = info.stream().map(item -> {
                FeePackageDTO dto = new FeePackageDTO();
                dto.setId(item.getId());
                dto.setServiceType(StringUtil.getValueNullChecker(item.getServiceType()));
                dto.setTitle(StringUtil.getValueNullChecker(item.getTitle()));
                dto.setShortName(StringUtil.getValueNullChecker(item.getShortName()));
                dto.setDescription(StringUtil.getValueNullChecker(item.getDescription()));
                dto.setAnnualFee(item.getAnnualFee());
                dto.setFixFee(item.getFixFee());
                dto.setPercentFee(item.getPercentFee());
                dto.setVat(item.getVat());
                dto.setRecordType(item.getRecordType());
                dto.setRefId(StringUtil.getValueNullChecker(item.getRefId()));
                return dto;
            }).collect(Collectors.toList());

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            // set data to pageResDTO
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            httpStatus = HttpStatus.OK;
            result = pageResDTO;
        } catch (Exception e) {
            logger.error("AccountController: ERROR: getListFeePackage: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
