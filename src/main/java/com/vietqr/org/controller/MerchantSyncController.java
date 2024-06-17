package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.MerchantSyncEntity;
import com.vietqr.org.entity.TelegramEntity;
import com.vietqr.org.service.MerchantSyncService;
import com.vietqr.org.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/merchant-sync")
public class MerchantSyncController {
    @Autowired
    public MerchantSyncService merchantSyncService;

    @GetMapping
    public ResponseEntity<Object> getAllMerchants(
            @RequestParam(value = "value", defaultValue = "") String value,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();

        try {
            int totalElement = merchantSyncService.countMerchantsByName(value);
            int offset = (page - 1) * size;
            List<IMerchantSyncDTO> data = merchantSyncService.getAllMerchants(value, offset, size);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            httpStatus = HttpStatus.OK;
            result = pageResDTO;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMerchantById(@PathVariable String id) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            IMerchantSyncDTO data = merchantSyncService.getMerchantById(id);
            if (data != null) {
                httpStatus = HttpStatus.OK;
                result = data;
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
                result = new ResponseMessageDTO("FAILED", "Merchant not found");
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping
    public ResponseEntity<Object> createMerchant(@RequestBody MerchantSyncRequestDTO dto,
                                                 @RequestParam String platform,
                                                 @RequestParam String details) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            if (("Google Chat".equals(platform) || "Lark".equals(platform)) && !isValidUrl(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Webhook không hợp lệ"), HttpStatus.BAD_REQUEST);
            } else if ("Telegram".equals(platform) && !isValidChatId(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Chat ID không hợp lệ"), HttpStatus.BAD_REQUEST);
            }

            MerchantSyncEntity entity = new MerchantSyncEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setName(dto.getName());
            entity.setVso(dto.getVso());
            entity.setBusinessType(dto.getBusinessType());
            entity.setCareer(dto.getCareer());
            entity.setAddress(dto.getAddress());
            entity.setNationalId(dto.getNationalId());
            entity.setUserId(dto.getUserId());
            entity.setEmail(dto.getEmail());
            entity.setIsActive(false);
            entity.setAccountCustomerId("");

            // Save entity
            MerchantSyncEntity createdEntity = merchantSyncService.createMerchant(entity);
            merchantSyncService.savePlatformDetails(platform, createdEntity.getUserId(), details);

            httpStatus = HttpStatus.CREATED;
            result = createdEntity;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    private boolean isValidChatId(String chatId) {
        return chatId != null && !chatId.trim().isEmpty();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMerchant(@PathVariable String id) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            merchantSyncService.deleteMerchant(id);
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "Merchant deleted successfully");
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMerchant(@PathVariable String id,
                                                 @RequestBody MerchantSyncRequestDTO dto,
                                                 @RequestParam String platform,
                                                 @RequestParam String details) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            if (("Google Chat".equals(platform) || "Lark".equals(platform)) && !isValidUrl(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Webhook không hợp lệ"), HttpStatus.BAD_REQUEST);
            } else if ("Telegram".equals(platform) && !isValidChatId(details)) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Thông tin Chat ID không hợp lệ"), HttpStatus.BAD_REQUEST);
            }

            Optional<MerchantSyncEntity> optionalEntity = merchantSyncService.findById(id);
            if (!optionalEntity.isPresent()) {
                return new ResponseEntity<>(new ResponseMessageDTO("FAILED", "Merchant not found"), HttpStatus.NOT_FOUND);
            }

            MerchantSyncEntity entity = optionalEntity.get();
            entity.setName(dto.getName());
            entity.setVso(dto.getVso());
            entity.setBusinessType(dto.getBusinessType());
            entity.setCareer(dto.getCareer());
            entity.setAddress(dto.getAddress());
            entity.setNationalId(dto.getNationalId());
            entity.setUserId(dto.getUserId());
            entity.setEmail(dto.getEmail());

            MerchantSyncEntity updatedEntity = merchantSyncService.updateMerchant(id, entity);
            merchantSyncService.savePlatformDetails(platform, updatedEntity.getUserId(), details);

            httpStatus = HttpStatus.OK;
            result = updatedEntity;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }
    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
