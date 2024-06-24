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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class FeePackageController {
    private static final Logger logger = Logger.getLogger(FeePackageController.class);

    @Autowired
    private FeePackageService feePackageService;

    @PutMapping("fee-package")
    public ResponseEntity<Object> updateFeePackage(@RequestBody FeePackageDTO requestDTO){
        Object result = null;
        HttpStatus httpStatus = null;
        try {

        } catch (Exception e) {
        logger.error("AccountController: ERROR: update FeePackage: " + e.getMessage()
                + " at: " + System.currentTimeMillis());
        result = new ResponseMessageDTO("FAILED", "E05");
        httpStatus = HttpStatus.BAD_REQUEST;
    }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("fee-package")
    public ResponseEntity<Object> createFeePackage(@RequestBody FeePackageDTO requestDTO) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (checkFeePackageExist(requestDTO.getId())){

            }
            // insert data to fee_package
            FeePackageEntity feePackageEntity = new FeePackageEntity();
            UUID uuid = UUID.randomUUID();
            feePackageEntity.setId(uuid.toString());
            feePackageEntity.setActiveFee(requestDTO.getActiveFee());
            feePackageEntity.setAnnualFee(requestDTO.getAnnualFee());
            feePackageEntity.setPercentFee(requestDTO.getPercentFee());
            feePackageEntity.setDescription(requestDTO.getDescription());
            feePackageEntity.setFixFee(requestDTO.getFixFee());
            feePackageEntity.setPercentFee(requestDTO.getPercentFee());
            feePackageEntity.setRecordType(requestDTO.getRecordType());
            feePackageEntity.setRefId(requestDTO.getRefId());
            feePackageEntity.setServiceType(requestDTO.getServiceType());
            feePackageEntity.setShortName(requestDTO.getShortName());
            feePackageEntity.setTitle(requestDTO.getTitle());
            feePackageEntity.setVat(requestDTO.getVat());
            feePackageService.insertFeePackage(feePackageEntity);

            // Success
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            logger.error("AccountController: ERROR: create FeePackage: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("fee-packages")
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
                dto.setActiveFee(item.getActiveFee());
                dto.setAnnualFee(item.getAnnualFee());
                dto.setDescription(StringUtil.getValueNullChecker(item.getDescription()));
                dto.setFixFee(item.getFixFee());
                dto.setPercentFee(item.getPercentFee());
                dto.setRecordType(item.getRecordType());
                dto.setRefId(StringUtil.getValueNullChecker(item.getRefId()));
                dto.setServiceType(item.getServiceType());
                dto.setShortName(StringUtil.getValueNullChecker(item.getShortName()));
                dto.setTitle(StringUtil.getValueNullChecker(item.getTitle()));
                dto.setVat(item.getVat());
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
            logger.error("AccountController: ERROR: getListFeePackages: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("fee-package")
    public ResponseEntity<Object> getFeePackage(@Valid @RequestParam String id) {
        Object result = null;
        HttpStatus httpStatus = null;
        IFeePackageDTO data = null;
        try {
            data = feePackageService.getFeePackageById(id);

            result = data;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("AccountController: ERROR: getFeePackage: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private boolean checkFeePackageExist(String id) {
        int data = feePackageService.checkFeePackageExist(id);
        if (data == 0) {
            return false;
        }
        return true;
    }

}
