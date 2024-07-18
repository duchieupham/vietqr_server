package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.LarkWebhookPartnerInsertDTO;
import com.vietqr.org.dto.LarkWebhookPartnerStatusDTO;
import com.vietqr.org.dto.LarkWebhookPartnerUpdateDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.LarkWebhookPartnerEntity;
import com.vietqr.org.service.LarkWebhookPartnerService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class LarkWebhookPartnerController {
    private static final Logger logger = Logger.getLogger(ContactController.class);

    @Autowired
    LarkWebhookPartnerService larkWebhookPartnerService;

    // insert
    @PostMapping("lark-partner")
    public ResponseEntity<ResponseMessageDTO> insertLarkWebhookPartner(
            @RequestBody LarkWebhookPartnerInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                UUID uuid = UUID.randomUUID();
                LarkWebhookPartnerEntity entity = new LarkWebhookPartnerEntity();
                entity.setId(uuid.toString());
                entity.setWebhook(dto.getWebhook());
                entity.setPartnerName(dto.getPartnerName());
                entity.setDescription(dto.getDescription());
                entity.setActive(true);
                larkWebhookPartnerService.insert(entity);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertLarkWebhookPartner: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertLarkWebhookPartner: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list
    @GetMapping("lark-partner")
    public ResponseEntity<List<LarkWebhookPartnerEntity>> getLarkPartners() {
        List<LarkWebhookPartnerEntity> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = larkWebhookPartnerService.getLarkWebhookPartners();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getLarkPartners: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update data
    @PostMapping("lark-partner/update")
    public ResponseEntity<ResponseMessageDTO> updateLarkWebhookPartner(
            @RequestBody LarkWebhookPartnerUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                larkWebhookPartnerService.updateLarkWebhookPartner(dto.getPartnerName(), dto.getWebhook(),
                        dto.getDescription(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateLarkWebhookPartner: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateLarkWebhookPartner: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update status
    @PostMapping("lark-partner/status/update")
    public ResponseEntity<ResponseMessageDTO> updateLarkWebhookPartnerStatus(
            @RequestBody LarkWebhookPartnerStatusDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                larkWebhookPartnerService.updateLarkWebhookPartnerStatus(dto.getActive(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateLarkWebhookPartnerStatus: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateLarkWebhookPartnerStatus: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove
    @DeleteMapping("lark-partner/remove")
    public ResponseEntity<ResponseMessageDTO> removePartner(
            @RequestParam String id) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (id != null && !id.trim().isEmpty()) {
                larkWebhookPartnerService.removeLarkWebhookPartnerById(id);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("removePartner: INVALID ID");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateLarkWebhookPartner: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
