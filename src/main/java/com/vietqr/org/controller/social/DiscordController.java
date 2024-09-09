package com.vietqr.org.controller.social;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.DiscordAccountBankEntity;
import com.vietqr.org.entity.DiscordEntity;
import com.vietqr.org.service.social.DiscordAccountBankService;
import com.vietqr.org.service.social.DiscordService;
import com.vietqr.org.util.DiscordUtil;
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
public class DiscordController {
    private static final Logger logger = Logger.getLogger(DiscordController.class);

    @Autowired
    DiscordAccountBankService discordAccountBankService;

    @Autowired
    DiscordService discordService;

    // send first message
    @PostMapping("service/discords/send-message")
    public ResponseEntity<ResponseMessageDTO> sendFirstMessage(@Valid @RequestBody DiscordFirstMessDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String message = "Xin chào quý khách 🎉"
                    + "\nRất vui khi kết nối với quý khách qua kênh liên lạc Discord."
                    + "\nCảm ơn quý khách đã sử dụng dịch vụ của chúng tôi."
                    + "\n🌐 Truy cập ứng dụng VietQR VN tại: https://vietqr.vn | https://vietqr.com"
                    + "\n📱 Hoặc tải ứng dụng thông qua: https://onelink.to/q7zwpe"
                    + "\n📞 Hotline hỗ trợ: 1900 6234 - 092 233 3636";
            DiscordUtil discordUtil = new DiscordUtil();
            boolean check = discordUtil.sendMessageToDiscord(message, dto.getWebhook());
            if (check) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E71");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("DiscordController: sendFirstMessage: ERROR: " + e.toString());
            System.out.println("DiscordController: sendFirstMessage: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // insert bank into Discord
    @PostMapping("service/discords/bank")
    public ResponseEntity<ResponseMessageDTO> insertBankIntoDiscord(@RequestBody SocialNetworkBanksDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check empty dto
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                DiscordEntity discordEntity = discordService.getDiscordById(dto.getId());
                // check existed bank account into list
                for (String bankId : dto.getBankIds()) {
                    if (bankId != null && !bankId.trim().isEmpty()) {
                        String checkExisted = discordAccountBankService.checkExistedBankId(bankId, dto.getId());
                        if (checkExisted == null || checkExisted.trim().isEmpty()) {
                            // insert Discord account bank entity
                            UUID uuid = UUID.randomUUID();
                            DiscordAccountBankEntity entity = new DiscordAccountBankEntity();
                            entity.setId(uuid.toString());
                            entity.setBankId(bankId);
                            entity.setDiscordId(dto.getId());
                            entity.setWebhook(discordEntity.getWebhook());
                            entity.setUserId(dto.getUserId());
                            discordAccountBankService.insert(entity);
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
            logger.error("DiscordController: insertBankIntoDiscord: ERROR: " + e.toString());
            System.out.println("DiscordController: insertBankIntoDiscord: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove bank from Discord
    @DeleteMapping("service/discords/bank")
    public ResponseEntity<ResponseMessageDTO> removeBankFromDiscord(@RequestBody SocialNetworkBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                discordAccountBankService.deleteByBankIdAndDiscordId(dto.getBankId(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("DiscordController: removeBankFromDiscord: ERROR: " + e.toString());
            System.out.println("DiscordController: removeBankFromDiscord: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add Discord connection
    @PostMapping("service/discords")
    public ResponseEntity<ResponseMessageDTO> insertDiscord(@RequestBody LarkInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty() && dto.getNotificationTypes() != null && !dto.getNotificationTypes().isEmpty() && dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                UUID discordUUID = UUID.randomUUID();
                DiscordEntity discordEntity = new DiscordEntity();
                discordEntity.setId(discordUUID.toString());
                if (dto.getName() != null && !dto.getName().isEmpty()) {
                    discordEntity.setName(dto.getName());
                } else {
                    discordEntity.setName("Chia sẻ biến động số dư");
                }
                discordEntity.setUserId(dto.getUserId());
                discordEntity.setWebhook(dto.getWebhook());
                discordEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                discordEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                discordService.insert(discordEntity);
                if (dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                    for (String bankId : dto.getBankIds()) {
                        UUID discordBankUUID = UUID.randomUUID();
                        DiscordAccountBankEntity discordAccountBankEntity = new DiscordAccountBankEntity();
                        discordAccountBankEntity.setId(discordBankUUID.toString());
                        discordAccountBankEntity.setDiscordId(discordUUID.toString());
                        discordAccountBankEntity.setBankId(bankId);
                        discordAccountBankEntity.setUserId(dto.getUserId());
                        discordAccountBankEntity.setWebhook(dto.getWebhook());
                        discordAccountBankService.insert(discordAccountBankEntity);
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertDiscord: INVALID REQUEST BODY");
                System.out.println("insertDiscord: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("DiscordController: insertDiscord: ERROR: " + e.toString());
            System.out.println("DiscordController: insertDiscord: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    // update configure
    @PutMapping("/service/discords/update-configure")
    public ResponseEntity<ResponseMessageDTO> updateDiscordConfigure(@RequestBody DiscordUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getDiscordId() != null && !dto.getDiscordId().isEmpty()) {
                DiscordEntity discordEntity = discordService.getDiscordById(dto.getDiscordId());
                if (discordEntity != null) {
                    // Cập nhật các thông tin cấu hình
                    if (dto.getNotificationTypes() != null && !dto.getNotificationTypes().isEmpty()) {
                        discordEntity.setNotificationTypes(new ObjectMapper().writeValueAsString(dto.getNotificationTypes()));
                    }
                    if (dto.getNotificationContents() != null && !dto.getNotificationContents().isEmpty()) {
                        discordEntity.setNotificationContents(new ObjectMapper().writeValueAsString(dto.getNotificationContents()));
                    }
                    discordService.updateDiscord(discordEntity);

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("updateDiscordConfigure: DISCORD ID NOT FOUND");
                    System.out.println("updateDiscordConfigure: DISCORD ID NOT FOUND");
                    result = new ResponseMessageDTO("FAILED", "E47");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("updateDiscordConfigure: INVALID REQUEST BODY");
                System.out.println("updateDiscordConfigure: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at updateDiscordConfigure: " + e.toString());
            System.out.println("Error at updateDiscordConfigure: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    // remove Discord connection
    @DeleteMapping("service/discords/remove")
    public ResponseEntity<ResponseMessageDTO> removeDiscord(@RequestBody DiscordRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            discordService.removeDiscord(dto.getId());
            discordAccountBankService.deleteByDiscordId(dto.getId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("DiscordController: removeDiscord: ERROR: " + e.toString());
            System.out.println("DiscordController: removeDiscord: ERROR:  " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // DiscordDetailDTO
    @GetMapping("service/discords/information-detail")
    public ResponseEntity<Object> getDiscordInformationDetail(@RequestParam(value = "id") String id) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            DiscordEntity dto = discordService.getDiscordById(id);
            if (dto != null) {
                DiscordDetailDTO detailDTO = new DiscordDetailDTO();
                detailDTO.setId(dto.getId());
                detailDTO.setWebhook(dto.getWebhook());
                detailDTO.setUserId(dto.getUserId());
                detailDTO.setName(dto.getName());
                List<DiscordBankDTO> bankDTOs = discordAccountBankService.getDiscordAccountBanks(dto.getId());
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
            logger.error("DiscordController: getDiscordInformationDetail: ERROR: " + e.getMessage() + System.currentTimeMillis());
            System.out.println("DiscordController: getDiscordInformationDetail: ERROR: " + e.getMessage() + System.currentTimeMillis());
            ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO("FAILED", "E05");
            result = responseMessageDTO;
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("service/discords/update-webhook/{discordId}")
    public ResponseEntity<ResponseMessageDTO> updateDiscordWebhook(@PathVariable String discordId, @RequestBody DiscordUpdateWebhookDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            discordService.updateDiscord(dto.getWebhook(), discordId);
            discordAccountBankService.updateWebHookDiscord(dto.getWebhook(), discordId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("DiscordController: updateDiscordWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            System.out.println("DiscordController: updateDiscordWebhook: ERROR: "  + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("service/discords/list")
    public ResponseEntity<PageResDTO> getListDiscords(
            @RequestParam("userId") String userId,
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        HttpStatus httpStatus = null;
        PageResDTO result = null;
        try {
            int totalElements = discordService.countDiscordsByUserId(userId);
            int offset = (page - 1) * size;
            List<DiscordInfoDetailDTO> discords = discordService.getDiscordsByUserIdWithPagination(userId, offset, size);
            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));
            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(discords);
            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getListDiscords Error: " + e.getMessage() + System.currentTimeMillis());
            result = new PageResDTO(new PageDTO(), new ArrayList<>());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
