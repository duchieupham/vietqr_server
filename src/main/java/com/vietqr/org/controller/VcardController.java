package com.vietqr.org.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.VCardInputDTO;
import com.vietqr.org.dto.VcardGenerateDTO;
import com.vietqr.org.util.VCardUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class VcardController {
    private static final Logger logger = Logger.getLogger(VcardController.class);

    @PostMapping("vcard/generate")
    ResponseEntity<VcardGenerateDTO> generateVCard(
            @RequestBody VCardInputDTO dto) {
        VcardGenerateDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                String qr = VCardUtil.getVcardQR(dto);
                result = new VcardGenerateDTO();
                result.setQr(qr);
                result.setFullname(dto.getFullname());
                result.setPhoneNo(dto.getPhoneNo());
                result.setEmail(dto.getEmail());
                result.setCompanyName(dto.getCompanyName());
                result.setWebsite(dto.getWebsite());
                result.setAddress(dto.getAddress());
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("generateVCard: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
