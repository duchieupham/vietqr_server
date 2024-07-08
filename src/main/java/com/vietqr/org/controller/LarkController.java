package com.vietqr.org.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.LarkAccountBankEntity;
import com.vietqr.org.entity.LarkEntity;
import com.vietqr.org.service.LarkAccountBankService;
import com.vietqr.org.service.LarkService;
import com.vietqr.org.util.LarkUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class LarkController {
    private static final Logger logger = Logger.getLogger(LarkController.class);

    @Autowired
    LarkService larkService;

    @Autowired
    LarkAccountBankService larkAccountBankService;

    // send first message
    @GetMapping("service/lark/send-message")
    public ResponseEntity<ResponseMessageDTO> sendFirstMessage(
            @RequestParam(value = "webhook") String webhook) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String message = "Xin chào quý khách 🎉"
                    + "\\nRất vui khi kết nối với quý khách qua kênh liên lạc Lark."
                    + "\\nCảm ơn quý khách đã sử dụng dịch vụ của chúng tôi."
                    + "\\n🌐 Truy cập ứng dụng VietQR VN tại: https://vietqr.vn | https://vietqr.com | https://vietqr.org"
                    + "\\n📱 hoặc tải ứng dụng thông qua: https://onelink.to/q7zwpe"
                    + "\\n📞 Hotline hỗ trợ: 1900 6234 - 092 233 3636";
            LarkUtil larkUtil = new LarkUtil();
            boolean check = larkUtil.sendMessageToLark(message, webhook);
            if (check == true) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E71");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at sendFirstMessage: " + e.toString());
            System.out.println("Error at sendFirstMessage: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add bank to lark
    @PostMapping("service/lark/bank")
    public ResponseEntity<ResponseMessageDTO> insertBankIntoLark(@RequestBody SocialNetworkBanksDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                LarkEntity larkEntity = larkService.getLarkById(dto.getId());
                if (larkEntity != null) {
                    for (String bankId : dto.getBankIds()) {
                        if (bankId != null && !bankId.trim().isEmpty()) {
                            String checkExisted = larkAccountBankService.checkExistedBankId(bankId, dto.getId());
                            if (checkExisted == null || checkExisted.trim().isEmpty()) {
                                // insert google chat account bank entity
                                UUID uuid = UUID.randomUUID();
                                LarkAccountBankEntity entity = new LarkAccountBankEntity();
                                entity.setId(uuid.toString());
                                entity.setBankId(bankId);
                                entity.setWebhook(larkEntity.getWebhook());
                                entity.setLarkId(larkEntity.getId());
                                entity.setUserId(dto.getUserId());
                                larkAccountBankService.insert(entity);
                            }
                        }
                    }
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("NOT FOUND LARK INFORMATION");
                    System.out.println("NOT FOUND LARK INFORMATION");
                    result = new ResponseMessageDTO("FAILED", "E05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("INVALID REQUEST BODY");
                System.out.println("INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at insertBankIntoLark: " + e.toString());
            System.out.println("Error at insertBankIntoLark: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove bank from lark
    @DeleteMapping("service/lark/bank")
    public ResponseEntity<ResponseMessageDTO> removeBankFromLark(@RequestBody SocialNetworkBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                LarkEntity larkEntity = larkService.getLarkById(dto.getId());
                if (larkEntity != null) {
                    larkAccountBankService.removeLarkAccBankByLarkIdAndBankId(dto.getId(), dto.getBankId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("NOT FOUND LARK INFORMATION");
                    System.out.println("NOT FOUND LARK INFORMATION");
                    result = new ResponseMessageDTO("FAILED", "E05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("INVALID REQUEST BODY");
                System.out.println("INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at removeBankFromLark: " + e.toString());
            System.out.println("Error at removeBankFromLark: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add lark account
    @PostMapping("service/lark")
    public ResponseEntity<ResponseMessageDTO> insertLarkChatId(@RequestBody LarkInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                UUID uuid = UUID.randomUUID();
                LarkEntity larkEntity = new LarkEntity();
                larkEntity.setId(uuid.toString());
                larkEntity.setUserId(dto.getUserId());
                larkEntity.setWebhook(dto.getWebhook());
                larkService.insertLark(larkEntity);
                for (String bankId : dto.getBankIds()) {
                    UUID uuid2 = UUID.randomUUID();
                    LarkAccountBankEntity telAccBankEntity = new LarkAccountBankEntity();
                    telAccBankEntity.setId(uuid2.toString());
                    telAccBankEntity.setBankId(bankId);
                    telAccBankEntity.setLarkId(uuid.toString());
                    telAccBankEntity.setUserId(dto.getUserId());
                    telAccBankEntity.setWebhook(dto.getWebhook());
                    larkAccountBankService.insert(telAccBankEntity);
                }
                result = new ResponseMessageDTO("SUCCESS", uuid.toString());
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertLarkChatId: INVALID REQUEST BODY");
                System.out.println("insertLarkChatId: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at insertLarkChatId: " + e.toString());
            System.out.println("Error at insertLarkChatId: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    @PostMapping("service/larks")
    public ResponseEntity<ResponseMessageDTO> insertLarkChatIds(@RequestBody LarkInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty() && dto.getNotificationTypes() != null
                    && !dto.getNotificationTypes().isEmpty() && dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                UUID uuid = UUID.randomUUID();
                LarkEntity larkEntity = new LarkEntity();
                larkEntity.setId(uuid.toString());
                larkEntity.setUserId(dto.getUserId());
                larkEntity.setWebhook(dto.getWebhook());
                larkEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                larkEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                larkService.insertLark(larkEntity);
                for (String bankId : dto.getBankIds()) {
                    UUID uuid2 = UUID.randomUUID();
                    LarkAccountBankEntity larkAccBankEntity = new LarkAccountBankEntity();
                    larkAccBankEntity.setId(uuid2.toString());
                    larkAccBankEntity.setBankId(bankId);
                    larkAccBankEntity.setLarkId(uuid.toString());
                    larkAccBankEntity.setUserId(dto.getUserId());
                    larkAccBankEntity.setWebhook(dto.getWebhook());
                    larkAccountBankService.insert(larkAccBankEntity);
                }
                result = new ResponseMessageDTO("SUCCESS", uuid.toString());
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertLarkChatId: INVALID REQUEST BODY");
                System.out.println("insertLarkChatId: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at insertLarkChatId: " + e.toString());
            System.out.println("Error at insertLarkChatId: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    // update configure
    @PutMapping("service/larks/update-configure")
    public ResponseEntity<ResponseMessageDTO> updateLarkConfigure(@RequestBody LarkUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getLarkId() != null && !dto.getLarkId().isEmpty()) {
                LarkEntity larkEntity = larkService.getLarkById(dto.getLarkId());
                if (larkEntity != null) {
                    // Cập nhật các thông tin cấu hình
                    if (dto.getNotificationTypes() != null && !dto.getNotificationTypes().isEmpty()) {
                        larkEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                    }
                    if (dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                        larkEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                    }
                    larkService.updateLark(larkEntity);

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("updateLarkChatIds: LARK ID NOT FOUND");
                    System.out.println("updateLarkChatIds: LARK ID NOT FOUND");
                    result = new ResponseMessageDTO("FAILED", "E47");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("updateLarkConfigure: INVALID REQUEST BODY");
                System.out.println("updateLarkConfigure: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at updateLarkConfigure: " + e.toString());
            System.out.println("Error at updateLarkConfigure: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get lark account information
    @GetMapping("service/lark/information")
    public ResponseEntity<List<LarkDetailDTO>> getLarkInformation(
            @RequestParam(value = "userId") String userId) {
        List<LarkDetailDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<LarkEntity> larkEntities = larkService.getLarksByUserId(userId);
            if (larkEntities != null && !larkEntities.isEmpty()) {
                for (LarkEntity larkEntity : larkEntities) {
                    LarkDetailDTO larkDetailDTO = new LarkDetailDTO();
                    larkDetailDTO.setId(larkEntity.getId());
                    larkDetailDTO.setWebhook(larkEntity.getWebhook());
                    larkDetailDTO.setUserId(larkEntity.getUserId());
                    List<LarkBankDTO> telBankDTOs = larkAccountBankService
                            .getLarkAccBanksByLarkId(larkEntity.getId());
                    if (telBankDTOs != null && !telBankDTOs.isEmpty()) {
                        larkDetailDTO.setBanks(telBankDTOs);
                    } else {
                        larkDetailDTO.setBanks(new ArrayList<>());
                    }
                    result.add(larkDetailDTO);
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getLarkInformation: ERROR: " + e.toString());
            System.out.println("getLarkInformation: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove lark account
    @DeleteMapping("service/lark/remove")
    public ResponseEntity<ResponseMessageDTO> removeLark(
            @RequestParam(value = "id") String id) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            larkAccountBankService.removeLarkAccBankByLarkId(id);
            larkService.removeLarkById(id);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error at removeLark: " + e.toString());
            System.out.println("Error at removeLark: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    @GetMapping("service/larks/information-detail")
    public ResponseEntity<LarkDetailDTO> getLarkInformationDetail(
            @RequestParam(value = "id") String id) {
        LarkDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            LarkEntity larkEntity = larkService.getLarkById(id);
            if (larkEntity != null) {
                LarkDetailDTO larkDetailDTO = new LarkDetailDTO();
                larkDetailDTO.setId(larkEntity.getId());
                larkDetailDTO.setWebhook(larkEntity.getWebhook());
                larkDetailDTO.setUserId(larkEntity.getUserId());
                List<LarkBankDTO> telBankDTOs = larkAccountBankService
                        .getLarkAccBanksByLarkId(larkEntity.getId());
                if (telBankDTOs != null && !telBankDTOs.isEmpty()) {
                    larkDetailDTO.setBanks(telBankDTOs);
                } else {
                    larkDetailDTO.setBanks(new ArrayList<>());
                }
                larkDetailDTO.setNotificationTypes(
                        new ObjectMapper().readValue(larkEntity.getNotificationTypes(), new TypeReference<List<String>>() {
                        }));
                larkDetailDTO.setNotificationContents(
                        new ObjectMapper().readValue(larkEntity.getNotificationContents(), new TypeReference<List<String>>() {
                        }));
                result = larkDetailDTO;
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            logger.error("getLarkInformationDetail: ERROR: " + e.getMessage() + System.currentTimeMillis());
            System.out.println("getLarkInformationDetail: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    @PutMapping("service/larks/update-webhook/{larkId}")
    public ResponseEntity<ResponseMessageDTO> updateLarkWebhook(@PathVariable String larkId, @Valid @RequestBody LarkUpdateWebhookDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            larkService.updateLarkWebhook(larkId, dto.getWebhook());
            larkAccountBankService.updateWebhook(dto.getWebhook(), larkId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("LarkController: updateLarkWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            System.out.println("LarkController: updateLarkWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("service/larks/list")
    public ResponseEntity<PageResDTO> getListLarks(
            @RequestParam("userId") String userId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        HttpStatus httpStatus = null;
        PageResDTO result = null;
        try {
            int totalElements = larkService.countLarksByUserId(userId);
            int offset = (page - 1) * size;
            List<LarkInfoDetailDTO> larks = larkService.getLarksByUserIdWithPagination(userId, offset, size);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage((int) Math.ceil((double) totalElements / size));

            result = new PageResDTO();
            result.setMetadata(pageDTO);
            result.setData(larks);

            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getListLarks Error: " + e.getMessage() + System.currentTimeMillis());
            result = new PageResDTO(new PageDTO(), new ArrayList<>());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
