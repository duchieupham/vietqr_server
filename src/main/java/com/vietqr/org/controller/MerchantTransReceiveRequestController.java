package com.vietqr.org.controller;

import com.vietqr.org.dto.MapRequestDTO;
import com.vietqr.org.dto.MerchantRequestDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.MerchantEntity;
import com.vietqr.org.entity.TransReceiveRequestMappingEntity;
import com.vietqr.org.service.MerchantService;
import com.vietqr.org.service.MerchantTransReceiveRequestService;
import com.vietqr.org.service.TransReceiveRequestMappingService;
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
    private TransReceiveRequestMappingService transReceiveRequestMappingService;
    @PostMapping("transaction-request")
    public ResponseEntity<ResponseMessageDTO> mapTransactionRequestToTerminal(@RequestBody @Valid MapRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            TransReceiveRequestMappingEntity entity = new TransReceiveRequestMappingEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setMerchantId("");
            entity.setTerminalId("");
            entity.setTransactionReceiveId(dto.getTransactionId());
            entity.setUserId(dto.getUserId());
            entity.setRequestType(dto.getRequestType());
            entity.setRequestValue(dto.getRequestValue());
            entity.setTimeCreated(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            entity.setTimeApproved(0);
            transReceiveRequestMappingService.insert(entity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("mapTransactionRequestToTerminal: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction-request")
    public ResponseEntity<Object> getListTransactionRequest(@RequestParam(value = "page") int page,
                                                            @RequestParam(value = "size") int size,
                                                            @RequestParam(value = "type") int type,
                                                            @RequestParam(value = "value") String value,
                                                            @RequestParam(value = "fromDate") String fromDate,
                                                            @RequestParam(value = "toDate") String toDate,
                                                            @RequestParam(value = "bankId") String bankId,
                                                            @RequestParam(value = "userId") String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {

            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction-request/{id}")
    public ResponseEntity<ResponseMessageDTO> requestMapTransactionDetail(@PathVariable String id) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
