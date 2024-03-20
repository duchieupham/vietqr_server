package com.vietqr.org.controller;

import com.vietqr.org.dto.MerchantRequestDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.MerchantEntity;
import com.vietqr.org.service.MerchantService;
import com.vietqr.org.service.MerchantTransReceiveRequestService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantTransReceiveRequestController {
    private static final Logger logger = Logger.getLogger(MerchantTransReceiveRequestController.class);

    @Autowired
    private MerchantTransReceiveRequestService merchantTransReceiveRequestService;

    @Autowired
    private MerchantService merchantService;

    @PostMapping("map-request")
    public ResponseEntity<ResponseMessageDTO> mapTransactionRequestToTerminal(@RequestBody @Valid MerchantRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            MerchantEntity entity = new MerchantEntity();
            String merchantId = UUID.randomUUID().toString();
            entity.setId(merchantId);
            entity.setName(dto.getName());
            entity.setAddress("");
            LocalDateTime now = LocalDateTime.now();
            long time = now.toEpochSecond(ZoneOffset.UTC);
            entity.setTimeCreated(time);
            entity.setTimePublish(time);
            entity.setActive(true);
            entity.setMaster(false);
            entity.setVso("");
            entity.setUserId(dto.getUserId());
            entity.setType(0);
            entity.setAccountCustomerMerchantId("");
            entity.setRefId("");
            entity.setPublicId(UUID.randomUUID().toString());
            merchantService.insertMerchant(entity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
