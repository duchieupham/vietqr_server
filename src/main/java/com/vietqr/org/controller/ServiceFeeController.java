package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.ServiceFeeInsertDTO;
import com.vietqr.org.dto.ServiceFeeItemDTO;
import com.vietqr.org.entity.ServiceFeeEntity;
import com.vietqr.org.service.ServiceFeeService;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class ServiceFeeController {
    private static final Logger logger = Logger.getLogger(ServiceFeeController.class);

    @Autowired
    ServiceFeeService serviceFeeService;

    @PostMapping("service-fee")
    public ResponseEntity<ResponseMessageDTO> insertNewServiceFee(
            @RequestBody ServiceFeeInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                ServiceFeeEntity entity = new ServiceFeeEntity();
                UUID uuid = UUID.randomUUID();
                entity.setId(uuid.toString());
                entity.setShortName(dto.getShortName());
                entity.setName(dto.getName());
                entity.setDescription(dto.getDescription());
                entity.setActiveFee(dto.getActiveFee());
                entity.setAnnualFee(dto.getAnnualFee());
                entity.setMonthlyCycle(dto.getMonthlyCycle());
                entity.setTransFee(dto.getTransFee());
                entity.setPercentFee(dto.getPercentFee());
                entity.setActive(true);
                entity.setSub(dto.isSub());
                entity.setRefId(dto.getRefId());
                entity.setCountingTransType(dto.getCountingTransType());
                entity.setVat(dto.getVat());
                serviceFeeService.insert(entity);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertNewServiceFee: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("service-fee")
    public ResponseEntity<List<ServiceFeeItemDTO>> getServiceFees() {
        List<ServiceFeeItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<ServiceFeeEntity> entities = serviceFeeService.getServiceFees();
            if (entities != null && !entities.isEmpty()) {
                for (ServiceFeeEntity entity : entities) {
                    ServiceFeeItemDTO itemDTO = new ServiceFeeItemDTO();
                    itemDTO.setItem(entity);
                    List<ServiceFeeEntity> subItems = new ArrayList<>();
                    List<ServiceFeeEntity> subEntities = serviceFeeService.getSubServiceFees(entity.getId());
                    if (subEntities != null && !subEntities.isEmpty()) {
                        for (ServiceFeeEntity subEntity : subEntities) {
                            subItems.add(subEntity);
                        }
                    }
                    itemDTO.setSubItems(subItems);
                    result.add(itemDTO);
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("insertNewServiceFee: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
