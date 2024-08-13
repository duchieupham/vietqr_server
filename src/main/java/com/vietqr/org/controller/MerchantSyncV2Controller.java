package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.entity.MerchantSyncEntity;
import com.vietqr.org.service.AccountLoginService;
import com.vietqr.org.service.MerchantSyncV2Service;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.StringUtil;
import org.apache.poi.hpsf.GUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/merchant-sync-v2")
public class MerchantSyncV2Controller {

    @Autowired
    public MerchantSyncV2Service merchantSyncV2Service;

    @Autowired
    public AccountLoginService accountLoginService;

    @PostMapping("create-merchant-sync")
    public ResponseEntity<Object> createMerchantSync(@RequestBody MerchantSyncCreateV2DTO request) {

        Object result = null;
        HttpStatus httpStatus = null;

        try {

            //Tìm kiếm user bằng user id
            IUserInfoDTO existedAccount = accountLoginService.getUserInfoByUserId(request.getUserId());

            if (existedAccount == null) {
                //Mẫu return, sửa sau
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E02");
                return new ResponseEntity<>(result, httpStatus);
            }

            //Kiểm tra merchant sync trùng lặp
            int count = merchantSyncV2Service.countMerchantSyncByName(request.getName());

            if (count != 0) {
                //Mẫu return, sửa sau
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E105");
                return new ResponseEntity<>(result, httpStatus);
            }

            //Kiểm tra publish id trùng lặp
            String publishId = "MER" + RandomCodeUtil.generateOTP(6);

            while (merchantSyncV2Service.countMerchantSyncByPublishId(publishId) != 0) {
                publishId = "MER" + RandomCodeUtil.generateOTP(6);
            }

            //Tạp entity với request
            MerchantSyncEntity entity = new MerchantSyncEntity();
            entity.setId(UUID.randomUUID().toString());
            entity.setBusinessType(request.getBusinessType());
            entity.setAddress(request.getAddress());
            entity.setCareer(request.getCareer());
            entity.setName(request.getName());
            entity.setNationalId(request.getNationalId());
            entity.setUserId(request.getUserId());
            entity.setVso(request.getVso());
            entity.setEmail(request.getEmail());
            entity.setFullName(request.getFullName());
            entity.setPhoneNo(request.getPhoneNo());
            entity.setAccountCustomerId("");
            entity.setIsActive(true);
            entity.setRefId("");
            entity.setIsMaster(false);
            entity.setCertificate("");
            entity.setWebhook("");
            entity.setClientId("");
            entity.setPublishId(publishId);

            //Lưu trữ entity
            MerchantSyncEntity newMerchantSync = merchantSyncV2Service.createMerchantSync(entity);

            if (newMerchantSync == null) {
                //Mẫu return, sửa sau
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E179");
                return new ResponseEntity<>(result, httpStatus);
            }

            httpStatus = HttpStatus.CREATED;
            result = newMerchantSync;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("update-merchant-sync")
    public ResponseEntity<Object> updateMerchantSync(@RequestBody MerchantSyncUpdateV2DTO request) {

        Object result = null;
        HttpStatus httpStatus = null;

        try {

            //Kiểm tra merchant sync trùng lặp
            int count = merchantSyncV2Service.countMerchantSyncByName(request.getName());

            if (count != 0) {
                //Mẫu return, sửa sau
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E105");
                return new ResponseEntity<>(result, httpStatus);
            }

            //Update entity với request
            Optional<MerchantSyncEntity> optional = merchantSyncV2Service.getMerchantSyncById(request.getId());

            MerchantSyncEntity entity = new MerchantSyncEntity();

            if (!optional.isPresent()) {
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E76");
                return new ResponseEntity<>(result, httpStatus);
            }

            entity = optional.get();
            entity.setBusinessType(request.getBusinessType() != null && !request.getBusinessType().isEmpty() ? request.getBusinessType() : entity.getBusinessType());
            entity.setAddress(request.getAddress() != null && !request.getAddress().isEmpty() ? request.getAddress() : entity.getAddress());
            entity.setCareer(request.getCareer() != null && !request.getCareer().isEmpty() ? request.getCareer() : entity.getCareer());
            entity.setName(request.getName() != null && !request.getName().isEmpty() ? request.getName() : entity.getName());
            entity.setNationalId(request.getNationalId() != null && !request.getNationalId().isEmpty() ? request.getNationalId() : entity.getNationalId());
            entity.setVso(request.getVso() != null && !request.getVso().isEmpty() ? request.getVso() : entity.getVso());
            entity.setEmail(request.getEmail() != null && !request.getEmail().isEmpty() ? request.getEmail() : entity.getEmail());
            entity.setFullName(request.getFullName() != null && !request.getFullName().isEmpty() ? request.getFullName() : entity.getFullName());
            entity.setPhoneNo(request.getPhoneNo() != null && !request.getPhoneNo().isEmpty() ? request.getPhoneNo() : entity.getPhoneNo());

            //Lưu trữ entity
            MerchantSyncEntity updatedMerchantSync = merchantSyncV2Service.createMerchantSync(entity);

            if (updatedMerchantSync == null) {
                //Mẫu return, sửa sau
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E180");
                return new ResponseEntity<>(result, httpStatus);
            }

            httpStatus = HttpStatus.OK;
            result = updatedMerchantSync;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("delete-merchant-sync")
    public ResponseEntity<Object> deleteMerchantSync(@RequestParam String id) {

        Object result = null;
        HttpStatus httpStatus = null;

        try {

            //Xóa entity với id
            Optional<MerchantSyncEntity> optional = merchantSyncV2Service.getMerchantSyncById(id);

            if (!optional.isPresent()) {
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E76");
                return new ResponseEntity<>(result, httpStatus);
            }

            merchantSyncV2Service.deleteMerchantSync(id);

            httpStatus = HttpStatus.OK;
            result = "";
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("get-merchant-syncs")
    public ResponseEntity<Object> getMerchantSyncs(
            @RequestParam @Min(1) int page,
            @RequestParam @Min(1) int size
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();

        try {

            int index = (page - 1) * size + 1;

            List<IMerchantSyncV2DTO> list = merchantSyncV2Service.getMerchantSyncs(index, size);

            int count = merchantSyncV2Service.countMerchantSync();

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(count);
            pageDTO.setTotalPage(StringUtil.getTotalPage(count, size));
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(list);

            httpStatus = HttpStatus.OK;
            result = pageResDTO;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("get-merchant-sync-by-id")
    public ResponseEntity<Object> getMerchantSyncById(@RequestParam String id) {

        Object result = null;
        HttpStatus httpStatus = null;

        try {

            //Tim entity với id
            Optional<MerchantSyncEntity> optional = merchantSyncV2Service.getMerchantSyncById(id);

            if (!optional.isPresent()) {
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "E76");
                return new ResponseEntity<>(result, httpStatus);
            }

            httpStatus = HttpStatus.OK;
            result = optional.get();
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }
}
