package com.vietqr.org.controller.social;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.GoogleSheetAccountBankEntity;
import com.vietqr.org.entity.GoogleSheetEntity;
import com.vietqr.org.service.social.GoogleSheetAccountBankService;
import com.vietqr.org.service.social.GoogleSheetService;
import com.vietqr.org.util.GoogleSheetUtil;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class GoogleSheetController {
    private static final Logger logger = Logger.getLogger(GoogleSheetController.class);

    @Autowired
    GoogleSheetAccountBankService googleSheetAccountBankService;

    @Autowired
    GoogleSheetService googleSheetService;

    @PostMapping("service/google-sheets/send-message")
    public ResponseEntity<ResponseMessageDTO> sendFirstMessage(@Valid @RequestBody GoogleSheetFirstMessDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("GoogleSheetController: sendFirstMessage: ERROR: " + e.toString());
            //System.out.println("GoogleSheetController: sendFirstMessage: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // insert bank into Google Sheet
    @PostMapping("service/google-sheets/bank")
    public ResponseEntity<ResponseMessageDTO> insertBankIntoGoogleSheet(@RequestBody SocialNetworkBanksDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check empty dto
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetById(dto.getId());
                // check existed bank account into list
                for (String bankId : dto.getBankIds()) {
                    if (bankId != null && !bankId.trim().isEmpty()) {
                        String checkExisted = googleSheetAccountBankService.checkExistedBankId(bankId, dto.getId());
                        if (checkExisted == null || checkExisted.trim().isEmpty()) {
                            // insert Google Sheet account bank entity
                            UUID uuid = UUID.randomUUID();
                            GoogleSheetAccountBankEntity entity = new GoogleSheetAccountBankEntity();
                            entity.setId(uuid.toString());
                            entity.setBankId(bankId);
                            entity.setGoogleSheetId(dto.getId());
                            entity.setWebhook(googleSheetEntity.getWebhook());
                            entity.setUserId(dto.getUserId());
                            googleSheetAccountBankService.insert(entity);
                        }
                    }
                }
                // response
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("GoogleSheetController: insertBankIntoGoogleSheet: ERROR: " + e.toString());
            //System.out.println("GoogleSheetController: insertBankIntoGoogleSheet: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove bank from Google Sheet
    @DeleteMapping("service/google-sheets/bank")
    public ResponseEntity<ResponseMessageDTO> removeBankFromGoogleSheet(@RequestBody SocialNetworkBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                googleSheetAccountBankService.deleteByBankIdAndGoogleSheetId(dto.getBankId(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("GoogleSheetController: removeBankFromGoogleSheet: ERROR: " + e.toString());
            //System.out.println("GoogleSheetController: removeBankFromGoogleSheet: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add Google Sheet connection
    @PostMapping("service/google-sheets")
    public ResponseEntity<ResponseMessageDTO> insertGoogleSheet(@RequestBody LarkInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty() && dto.getNotificationTypes() != null && !dto.getNotificationTypes().isEmpty() && dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                UUID googleSheetUUID = UUID.randomUUID();
                GoogleSheetEntity googleSheetEntity = new GoogleSheetEntity();
                googleSheetEntity.setId(googleSheetUUID.toString());
                if (dto.getName() != null && !dto.getName().isEmpty()) {
                    googleSheetEntity.setName(dto.getName());
                } else {
                    googleSheetEntity.setName("Chia sẻ biến động số dư");
                }
                googleSheetEntity.setUserId(dto.getUserId());
                googleSheetEntity.setWebhook(dto.getWebhook());
                googleSheetEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                googleSheetEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                googleSheetService.insert(googleSheetEntity);
                if (dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                    for (String bankId : dto.getBankIds()) {
                        UUID googleSheetBankUUID = UUID.randomUUID();
                        GoogleSheetAccountBankEntity googleSheetAccountBankEntity = new GoogleSheetAccountBankEntity();
                        googleSheetAccountBankEntity.setId(googleSheetBankUUID.toString());
                        googleSheetAccountBankEntity.setGoogleSheetId(googleSheetUUID.toString());
                        googleSheetAccountBankEntity.setBankId(bankId);
                        googleSheetAccountBankEntity.setUserId(dto.getUserId());
                        googleSheetAccountBankEntity.setWebhook(dto.getWebhook());
                        googleSheetAccountBankService.insert(googleSheetAccountBankEntity);
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertGoogleSheet: INVALID REQUEST BODY");
                //System.out.println("insertGoogleSheet: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("GoogleSheetController: insertGoogleSheet: ERROR: " + e.toString());
            //System.out.println("GoogleSheetController: insertGoogleSheet: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update configure
    @PutMapping("/service/google-sheets/update-configure")
    public ResponseEntity<ResponseMessageDTO> updateGoogleSheetConfigure(@RequestBody GoogleSheetUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getGoogleSheetId() != null && !dto.getGoogleSheetId().isEmpty()) {
                GoogleSheetEntity googleSheetEntity = googleSheetService.getGoogleSheetById(dto.getGoogleSheetId());
                if (googleSheetEntity != null) {
                    // Cập nhật các thông tin cấu hình
                    if (dto.getNotificationTypes() != null && !dto.getNotificationTypes().isEmpty()) {
                        googleSheetEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                    }
                    if (dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                        googleSheetEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                    }
                    googleSheetService.updateGoogleSheet(googleSheetEntity);

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("updateGoogleSheetConfigure: GOOGLE SHEET ID NOT FOUND");
                    //System.out.println("updateGoogleSheetConfigure: GOOGLE SHEET ID NOT FOUND");
                    result = new ResponseMessageDTO("FAILED", "E47");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("updateGoogleSheetConfigure: INVALID REQUEST BODY");
                //System.out.println("updateGoogleSheetConfigure: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at updateGoogleSheetConfigure: " + e.toString());
            //System.out.println("Error at updateGoogleSheetConfigure: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove Google Sheet connection
    @DeleteMapping("service/google-sheets/remove")
    public ResponseEntity<ResponseMessageDTO> removeGoogleSheet(@RequestBody GoogleSheetRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            googleSheetService.removeGoogleSheet(dto.getId());
            googleSheetAccountBankService.deleteByGoogleSheetId(dto.getId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("GoogleSheetController: removeGoogleSheet: ERROR: " + e.toString());
            //System.out.println("GoogleSheetController: removeGoogleSheet: ERROR:  " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // GoogleSheetDetailDTO
    @GetMapping("service/google-sheets/information-detail")
    public ResponseEntity<Object> getGoogleSheetInformationDetail(@RequestParam(value = "id") String id) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            GoogleSheetEntity dto = googleSheetService.getGoogleSheetById(id);
            if (dto != null) {
                GoogleSheetDetailDTO detailDTO = new GoogleSheetDetailDTO();
                detailDTO.setId(dto.getId());
                detailDTO.setWebhook(dto.getWebhook());
                detailDTO.setUserId(dto.getUserId());
                detailDTO.setName(dto.getName());
                List<GoogleSheetBankDTO> bankDTOs = googleSheetAccountBankService.getGoogleSheetAccountBanks(dto.getId());
                detailDTO.setBanks(bankDTOs);
                detailDTO.setNotificationTypes(
                        new ObjectMapper().readValue(dto.getNotificationTypes(), new TypeReference<List<String>>() {}));
                detailDTO.setNotificationContents(
                        new ObjectMapper().readValue(dto.getNotificationContents(), new TypeReference<List<String>>() {}));
                result = detailDTO;
                httpStatus = HttpStatus.OK;
            } else {
                ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO("CHECK", "C13");
                result = responseMessageDTO;
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            logger.error("GoogleSheetController: getGoogleSheetInformationDetail: ERROR: " + e.getMessage() + System.currentTimeMillis());
            //System.out.println("GoogleSheetController: getGoogleSheetInformationDetail: ERROR: " + e.getMessage() + System.currentTimeMillis());
            ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO("FAILED", "E05");
            result = responseMessageDTO;
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("service/google-sheets/update-webhook/{googleSheetId}")
    public ResponseEntity<ResponseMessageDTO> updateGoogleSheetWebhook(@PathVariable String googleSheetId, @RequestBody GoogleSheetUpdateWebhookDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            googleSheetService.updateGoogleSheet(dto.getWebhook(), googleSheetId);
            googleSheetAccountBankService.updateWebHookGoogleSheet(dto.getWebhook(), googleSheetId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("GoogleSheetController: updateGoogleSheetWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            //System.out.println("GoogleSheetController: updateGoogleSheetWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("service/google-sheets/list")
    public ResponseEntity<PageResDTO> getListGoogleSheets(
            @RequestParam("userId") String userId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        HttpStatus httpStatus = null;
        PageResDTO result = null;
        try {
            int totalElements = googleSheetService.countGoogleSheetsByUserId(userId);
            int offset = (page - 1) * size;
            List<GoogleSheetInfoDetailDTO> googleSheets = googleSheetService.getGoogleSheetsByUserIdWithPagination(userId, offset, size);
            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));
            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(googleSheets);
            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getListGoogleSheets Error: " + e.getMessage() + System.currentTimeMillis());
            result = new PageResDTO(new PageDTO(), new ArrayList<>());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}