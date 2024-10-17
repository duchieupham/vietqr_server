package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vietqr.org.dto.CarrierTypeUpdateDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.entity.CarrierTypeEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.MobileCarrierEntity;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.CarrierTypeService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.MobileCarrierService;
import com.vietqr.org.service.AccountInformationService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MobileCarrierController {
    private static final Logger logger = Logger.getLogger(MobileCarrierController.class);

    @Autowired
    MobileCarrierService mobileCarrierService;

    @Autowired
    CarrierTypeService carrierTypeService;

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    ImageService imageService;

    @PostMapping(value = "mobile-carrier/type", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ResponseMessageDTO> insertCarrierType(
            @Valid @RequestParam String id,
            @Valid @RequestParam String name,
            @Valid @RequestParam String code,
            @Valid @RequestParam MultipartFile image) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuidImage = UUID.randomUUID();
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            // insert image
            ImageEntity imageEntity = new ImageEntity(uuidImage.toString(), fileName, image.getBytes());
            imageService.insertImage(imageEntity);
            //
            CarrierTypeEntity entity = new CarrierTypeEntity(id, name, code, uuidImage.toString());
            carrierTypeService.insertCarrierType(entity);
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "");
        } catch (Exception e) {
            logger.error("ERROR at insertCarrierType: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("mobile-carrier/type")
    public ResponseEntity<List<CarrierTypeEntity>> getCarrierTypes() {
        List<CarrierTypeEntity> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = carrierTypeService.getCarrierTypes();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("ERROR at getCarrierTypes: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("mobile-carrier")
    public ResponseEntity<ResponseMessageDTO> insertMobileCarriers() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> viettelCarriers = new ArrayList<>();
            viettelCarriers.add("032");
            viettelCarriers.add("033");
            viettelCarriers.add("034");
            viettelCarriers.add("035");
            viettelCarriers.add("036");
            viettelCarriers.add("037");
            viettelCarriers.add("038");
            viettelCarriers.add("039");
            viettelCarriers.add("096");
            viettelCarriers.add("097");
            viettelCarriers.add("098");
            viettelCarriers.add("086");
            //
            List<String> vinaphoneCarriers = new ArrayList<>();
            vinaphoneCarriers.add("083");
            vinaphoneCarriers.add("084");
            vinaphoneCarriers.add("085");
            vinaphoneCarriers.add("081");
            vinaphoneCarriers.add("082");
            vinaphoneCarriers.add("088");
            vinaphoneCarriers.add("091");
            vinaphoneCarriers.add("094");
            //
            List<String> mobiphoneCarriers = new ArrayList<>();
            mobiphoneCarriers.add("070");
            mobiphoneCarriers.add("079");
            mobiphoneCarriers.add("077");
            mobiphoneCarriers.add("076");
            mobiphoneCarriers.add("078");
            mobiphoneCarriers.add("090");
            mobiphoneCarriers.add("093");
            mobiphoneCarriers.add("089");
            //
            List<String> vietnamobileCarriers = new ArrayList<>();
            vietnamobileCarriers.add("056");
            vietnamobileCarriers.add("058");
            vietnamobileCarriers.add("092");
            //
            List<String> gmobileCarriers = new ArrayList<>();
            gmobileCarriers.add("059");
            gmobileCarriers.add("099");
            //
            for (String viettel : viettelCarriers) {
                UUID uuid = UUID.randomUUID();
                MobileCarrierEntity entity = new MobileCarrierEntity();
                entity.setId(uuid.toString());
                entity.setPrefix(viettel);
                entity.setType("1");
                mobileCarrierService.insertMobileCarrier(entity);
            }
            //
            for (String vina : vinaphoneCarriers) {
                UUID uuid = UUID.randomUUID();
                MobileCarrierEntity entity = new MobileCarrierEntity();
                entity.setId(uuid.toString());
                entity.setPrefix(vina);
                entity.setType("3");
                mobileCarrierService.insertMobileCarrier(entity);
            }
            //
            for (String mobi : mobiphoneCarriers) {
                UUID uuid = UUID.randomUUID();
                MobileCarrierEntity entity = new MobileCarrierEntity();
                entity.setId(uuid.toString());
                entity.setPrefix(mobi);
                entity.setType("2");
                mobileCarrierService.insertMobileCarrier(entity);
            }
            //
            for (String vnmobile : vietnamobileCarriers) {
                UUID uuid = UUID.randomUUID();
                MobileCarrierEntity entity = new MobileCarrierEntity();
                entity.setId(uuid.toString());
                entity.setPrefix(vnmobile);
                entity.setType("4");
                mobileCarrierService.insertMobileCarrier(entity);
            }
            //
            for (String gmobile : gmobileCarriers) {
                UUID uuid = UUID.randomUUID();
                MobileCarrierEntity entity = new MobileCarrierEntity();
                entity.setId(uuid.toString());
                entity.setPrefix(gmobile);
                entity.setType("5");
                mobileCarrierService.insertMobileCarrier(entity);
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("ERROR at insertMobileCarriers: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update carrier type for all user
    // 1. find sdt by userId - lấy 3 số sdt đầu
    // 2. check 3 số điện thoại lấy carrier type id
    // 2.1. Nếu lấy được, set giá trị
    // 2.2. Nếu không lấy được, set giá trị = ""
    @PostMapping("mobile-carrier/type/update-all")
    public ResponseEntity<ResponseMessageDTO> updateCarrierTypeForUsers() {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // 1.
            List<AccountLoginEntity> accounts = accountLoginService.getAllAccountLogin();
            if (accounts != null && !accounts.isEmpty()) {
                int index = 0;
                for (AccountLoginEntity account : accounts) {
                    if (account != null && account.getId() != null && !account.getId().trim().isEmpty()) {
                        index++;
                        String phoneNo = account.getPhoneNo();
                        if (phoneNo != null && !phoneNo.trim().isEmpty()) {
                            // 2
                            String prefix = phoneNo.substring(0, 3);
                            String carrierTypeId = mobileCarrierService.getTypeIdByPrefix(prefix);
                            if (carrierTypeId != null) {
                                accountInformationService.updateCarrierTypeIdByUserId(carrierTypeId, account.getId());
                            } else {
                                accountInformationService.updateCarrierTypeIdByUserId("", account.getId());
                            }
                        } else {
                            accountInformationService.updateCarrierTypeIdByUserId("", account.getId());
                        }
                    } else {
                        logger.error("updateCarrierTypeForUsers: account null or userId null");
                    }
                }
            }
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "");
        } catch (Exception e) {
            logger.error("ERROR at updateCarrierTypeForUsers: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update carrier type nếu user chọn nhà mạng khác
    @PostMapping("mobile-carrier/type/update")
    public ResponseEntity<ResponseMessageDTO> updateCarrierTypeIdByUserId(@RequestBody CarrierTypeUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountInformationService.updateCarrierTypeIdByUserId(dto.getCarrierTypeId(), dto.getUserId());
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "");
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
