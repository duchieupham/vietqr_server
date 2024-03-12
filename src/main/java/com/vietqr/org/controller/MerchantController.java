package com.vietqr.org.controller;


import com.vietqr.org.dto.MerchantRequestDTO;
import com.vietqr.org.dto.MerchantResponseDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.MerchantEntity;
import com.vietqr.org.service.MerchantService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantController {
    private static final Logger logger = Logger.getLogger(MerchantController.class);

    @Autowired
    private MerchantService merchantService;

    @PostMapping("merchant")
    public ResponseEntity<ResponseMessageDTO> createMerchant(@RequestBody @Valid MerchantRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            MerchantEntity entity = new MerchantEntity();
            String merchantId = java.util.UUID.randomUUID().toString();
            entity.setId(merchantId);
            entity.setName(dto.getName());
            entity.setAddress(dto.getAddress());
            LocalDateTime now = LocalDateTime.now();
            long time = now.toEpochSecond(ZoneOffset.UTC);
            entity.setTimeCreated(time);
            entity.setVsoCode("");
//            entity.setType("");
//            entity.setUserId(dto.getUserId());
            merchantService.insertMerchant(entity);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant/{userId}")
    public ResponseEntity<List<MerchantResponseDTO>> getMerchantsByUserId(@PathVariable String userId) {
        List<MerchantResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = merchantService.getMerchantsByUserId(userId);
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
