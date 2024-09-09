package com.vietqr.org.controller.social;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.SlackAccountBankEntity;
import com.vietqr.org.entity.SlackEntity;
import com.vietqr.org.service.social.SlackAccountBankService;
import com.vietqr.org.service.social.SlackService;
import com.vietqr.org.util.SlackUtil;
import com.vietqr.org.util.StringUtil;
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
public class SlackController {
    private static final Logger logger = Logger.getLogger(SlackController.class);

    @Autowired
    SlackAccountBankService slackAccountBankService;

    @Autowired
    SlackService slackService;

    // send first message
    @PostMapping("service/slacks/send-message")
    public ResponseEntity<ResponseMessageDTO> sendFirstMessage(@Valid @RequestBody SlackFirstMessDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String message = "Xin chào quý khách 🎉"
                    + "\nRất vui khi kết nối với quý khách qua kênh liên lạc Slack."
                    + "\nCảm ơn quý khách đã sử dụng dịch vụ của chúng tôi."
                    + "\n🌐 Truy cập ứng dụng VietQR VN tại: https://vietqr.vn | https://vietqr.com"
                    + "\n📱 Hoặc tải ứng dụng thông qua: https://onelink.to/q7zwpe"
                    + "\n📞 Hotline hỗ trợ: 1900 6234 - 092 233 3636";
            SlackUtil slackUtil = new SlackUtil();
            boolean check = slackUtil.sendMessageToSlack(message, dto.getWebhook());
            if (check) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E71");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("SlackController: sendFirstMessage: ERROR: " + e.toString());
            System.out.println("SlackController: sendFirstMessage: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // insert bank into Slack
    @PostMapping("service/slacks/bank")
    public ResponseEntity<ResponseMessageDTO> insertBankIntoSlack(@RequestBody SocialNetworkBanksDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check empty dto
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                SlackEntity slackEntity = slackService.getSlackById(dto.getId());
                // check existed bank account into list
                for (String bankId : dto.getBankIds()) {
                    if (bankId != null && !bankId.trim().isEmpty()) {
                        String checkExisted = slackAccountBankService.checkExistedBankId(bankId, dto.getId());
                        if (checkExisted == null || checkExisted.trim().isEmpty()) {
                            // insert Slack account bank entity
                            UUID uuid = UUID.randomUUID();
                            SlackAccountBankEntity entity = new SlackAccountBankEntity();
                            entity.setId(uuid.toString());
                            entity.setBankId(bankId);
                            entity.setSlackId(dto.getId());
                            entity.setWebhook(slackEntity.getWebhook());
                            entity.setUserId(dto.getUserId());
                            slackAccountBankService.insert(entity);
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
            logger.error("SlackController: insertBankIntoSlack: ERROR: " + e.toString());
            System.out.println("SlackController: insertBankIntoSlack: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove bank from Slack
    @DeleteMapping("service/slacks/bank")
    public ResponseEntity<ResponseMessageDTO> removeBankFromSlack(@RequestBody SocialNetworkBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                slackAccountBankService.deleteByBankIdAndSlackId(dto.getBankId(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("SlackController: removeBankFromSlack: ERROR: " + e.toString());
            System.out.println("SlackController: removeBankFromSlack: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add Slack connection
    @PostMapping("service/slacks")
    public ResponseEntity<ResponseMessageDTO> insertSlack(@RequestBody LarkInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty() && dto.getNotificationTypes() != null && !dto.getNotificationTypes().isEmpty() && dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                UUID slackUUID = UUID.randomUUID();
                SlackEntity slackEntity = new SlackEntity();
                slackEntity.setId(slackUUID.toString());
                if (dto.getName() != null && !dto.getName().isEmpty()) {
                    slackEntity.setName(dto.getName());
                } else {
                    slackEntity.setName("Chia sẻ biến động số dư");
                }
                slackEntity.setUserId(dto.getUserId());
                slackEntity.setWebhook(dto.getWebhook());
                slackEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                slackEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                slackService.insert(slackEntity);
                if (dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                    for (String bankId : dto.getBankIds()) {
                        UUID slackBankUUID = UUID.randomUUID();
                        SlackAccountBankEntity slackAccountBankEntity = new SlackAccountBankEntity();
                        slackAccountBankEntity.setId(slackBankUUID.toString());
                        slackAccountBankEntity.setSlackId(slackUUID.toString());
                        slackAccountBankEntity.setBankId(bankId);
                        slackAccountBankEntity.setUserId(dto.getUserId());
                        slackAccountBankEntity.setWebhook(dto.getWebhook());
                        slackAccountBankService.insert(slackAccountBankEntity);
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertSlack: INVALID REQUEST BODY");
                System.out.println("insertSlack: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("SlackController: insertSlack: ERROR: " + e.toString());
            System.out.println("SlackController: insertSlack: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    // update configure
    @PutMapping("/service/slacks/update-configure")
    public ResponseEntity<ResponseMessageDTO> updateSlackConfigure(@RequestBody SlackUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getSlackId() != null && !dto.getSlackId().isEmpty()) {
                SlackEntity slackEntity = slackService.getSlackById(dto.getSlackId());
                if (slackEntity != null) {
                    // Cập nhật các thông tin cấu hình
                    if (dto.getNotificationTypes() != null && !dto.getNotificationTypes().isEmpty()) {
                        slackEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                    }
                    if (dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                        slackEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                    }
                    slackService.updateSlack(slackEntity);

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("updateSlackConfigure: SLACK ID NOT FOUND");
                    System.out.println("updateSlackConfigure: SLACK ID NOT FOUND");
                    result = new ResponseMessageDTO("FAILED", "E47");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("updateSlackConfigure: INVALID REQUEST BODY");
                System.out.println("updateSlackConfigure: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at updateSlackConfigure: " + e.toString());
            System.out.println("Error at updateSlackConfigure: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove Slack connection
    @DeleteMapping("service/slacks/remove")
    public ResponseEntity<ResponseMessageDTO> removeSlack(@RequestBody SlackRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            slackService.removeSlack(dto.getId());
            slackAccountBankService.deleteBySlackId(dto.getId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("SlackController: removeSlack: ERROR: " + e.toString());
            System.out.println("SlackController: removeSlack: ERROR:  " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // SlackDetailDTO
    @GetMapping("service/slacks/information-detail")
    public ResponseEntity<Object> getSlackInformationDetail(@RequestParam(value = "id") String id) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            SlackEntity dto = slackService.getSlackById(id);
            if (dto != null) {
                SlackDetailDTO detailDTO = new SlackDetailDTO();
                detailDTO.setId(dto.getId());
                detailDTO.setWebhook(dto.getWebhook());
                detailDTO.setUserId(dto.getUserId());
                detailDTO.setName(dto.getName());
                List<SlackBankDTO> bankDTOs = slackAccountBankService.getSlackAccountBanks(dto.getId());
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
            logger.error("SlackController: getSlackInformationDetail: ERROR: " + e.getMessage() + System.currentTimeMillis());
            System.out.println("SlackController: getSlackInformationDetail: ERROR: " + e.getMessage() + System.currentTimeMillis());
            ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO("FAILED", "E05");
            result = responseMessageDTO;
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("service/slacks/update-webhook/{slackId}")
    public ResponseEntity<ResponseMessageDTO> updateSlackWebhook(@PathVariable String slackId, @RequestBody SlackUpdateWebhookDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            slackService.updateSlack(dto.getWebhook(), slackId);
            slackAccountBankService.updateWebHookSlack(dto.getWebhook(), slackId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("SlackController: updateSlackWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            System.out.println("SlackController: updateSlackWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("service/slacks/list")
    public ResponseEntity<PageResDTO> getListSlacks(
            @RequestParam("userId") String userId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        HttpStatus httpStatus = null;
        PageResDTO result = null;
        try {
            int totalElements = slackService.countSlacksByUserId(userId);
            int offset = (page - 1) * size;
            List<SlackInfoDetailDTO> slacks = slackService.getSlacksByUserIdWithPagination(userId, offset, size);
            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));
            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(slacks);
            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getListSlacks Error: " + e.getMessage() + System.currentTimeMillis());
            result = new PageResDTO(new PageDTO(), new ArrayList<>());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}