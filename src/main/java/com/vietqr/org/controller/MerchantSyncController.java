package com.vietqr.org.controller;

import com.vietqr.org.dto.IMerchantSyncDTO;
import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.MerchantSyncEntity;
import com.vietqr.org.service.MerchantSyncService;
import com.vietqr.org.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<Object> createMerchant(@RequestBody MerchantSyncEntity entity) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            entity.setId(UUID.randomUUID().toString());
            MerchantSyncEntity createdEntity = merchantSyncService.createMerchant(entity);
            httpStatus = HttpStatus.CREATED;
            result = createdEntity;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
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
    public ResponseEntity<Object> updateMerchant(@PathVariable String id, @RequestBody MerchantSyncEntity entity) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            MerchantSyncEntity updatedEntity = merchantSyncService.updateMerchant(id, entity);
            if (updatedEntity != null) {
                httpStatus = HttpStatus.OK;
                result = updatedEntity;
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


}
