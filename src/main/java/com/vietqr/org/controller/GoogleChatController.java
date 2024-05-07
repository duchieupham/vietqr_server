package com.vietqr.org.controller;

import java.util.List;
import java.util.UUID;

import com.vietqr.org.dto.*;
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

import com.vietqr.org.entity.GoogleChatAccountBankEntity;
import com.vietqr.org.entity.GoogleChatEntity;
import com.vietqr.org.service.GoogleChatAccountBankService;
import com.vietqr.org.service.GoogleChatService;
import com.vietqr.org.util.GoogleChatUtil;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class GoogleChatController {
    private static final Logger logger = Logger.getLogger(GoogleChatController.class);

    @Autowired
    GoogleChatAccountBankService googleChatAccountBankService;

    @Autowired
    GoogleChatService googleChatService;

    // send first message
    @PostMapping("service/google-chat/send-message")
    public ResponseEntity<ResponseMessageDTO> sendFirstMessage(
            @Valid @RequestBody GoogleChatFirstMessDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String message = "Xin chào quý khách 🎉"
                    + "\nRất vui khi kết nối với quý khách qua kênh liên lạc Google Chat."
                    + "\nCảm ơn quý khách đã sử dụng dịch vụ của chúng tôi."
                    + "\n🌐 Truy cập ứng dụng VietQR VN tại: https://vietqr.vn | https://vietqr.com"
                    + "\n📱 Hoặc tải ứng dụng thông qua: https://onelink.to/q7zwpe"
                    + "\n📞 Hotline hỗ trợ: 1900 6234 - 092 233 3636";
            GoogleChatUtil googleChatUtil = new GoogleChatUtil();
            boolean check = googleChatUtil.sendMessageToGoogleChat(message, dto.getWebhook());
            if (check == true) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E71");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("GoogleChatController: sendFirstMessage: ERROR: " + e.toString());
            System.out.println("GoogleChatController: sendFirstMessage: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // insert bank into google chat
    @PostMapping("service/google-chat/bank")
    public ResponseEntity<ResponseMessageDTO> insertBankIntoGoogleChat(@RequestBody SocialNetworkBanksDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check empty dto
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                GoogleChatEntity googleChatEntity = googleChatService.getGoogleChatById(dto.getId());
                // check existed bank account into list
                for (String bankId : dto.getBankIds()) {
                    if (bankId != null && !bankId.trim().isEmpty()) {
                        String checkExisted = googleChatAccountBankService.checkExistedBankId(bankId, dto.getId());
                        if (checkExisted == null || checkExisted.trim().isEmpty()) {
                            // insert google chat account bank entity
                            UUID uuid = UUID.randomUUID();
                            GoogleChatAccountBankEntity entity = new GoogleChatAccountBankEntity();
                            entity.setId(uuid.toString());
                            entity.setBankId(bankId);
                            entity.setGoogleChatId(dto.getId());
                            entity.setWebhook(googleChatEntity.getWebhook());
                            entity.setUserId(dto.getUserId());
                            googleChatAccountBankService.insert(entity);
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
            logger.error("GoogleChatController: insertBankIntoGoogleChat: ERROR: " + e.toString());
            System.out.println("GoogleChatController: insertBankIntoGoogleChat: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove bank from google chat
    @DeleteMapping("service/google-chat/bank")
    public ResponseEntity<ResponseMessageDTO> removeBankFromGoogleChat(@RequestBody SocialNetworkBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                googleChatAccountBankService.deleteByBankIdAndGoogleChatId(dto.getBankId(), dto.getId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("GoogleChatController: removeBankFromGoogleChat: ERROR: " + e.toString());
            System.out.println("GoogleChatController: removeBankFromGoogleChat: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add google chat connection
    @PostMapping("service/google-chat")
    public ResponseEntity<ResponseMessageDTO> insertGoogleChat(@RequestBody LarkInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                UUID ggChatUUID = UUID.randomUUID();
                GoogleChatEntity googleChatEntity = new GoogleChatEntity();
                googleChatEntity.setId(ggChatUUID.toString());
                googleChatEntity.setUserId(dto.getUserId());
                googleChatEntity.setWebhook(dto.getWebhook());
                googleChatService.insert(googleChatEntity);
                if (dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                    for (String bankId : dto.getBankIds()) {
                        UUID ggChatBankUUID = UUID.randomUUID();
                        GoogleChatAccountBankEntity googleChatAccountBankEntity = new GoogleChatAccountBankEntity();
                        googleChatAccountBankEntity.setId(ggChatBankUUID.toString());
                        googleChatAccountBankEntity.setGoogleChatId(ggChatUUID.toString());
                        googleChatAccountBankEntity.setBankId(bankId);
                        googleChatAccountBankEntity.setUserId(dto.getUserId());
                        googleChatAccountBankEntity.setWebhook(dto.getWebhook());
                        googleChatAccountBankService.insert(googleChatAccountBankEntity);
                    }
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("GoogleChatController: insertGoogleChat: ERROR: " + e.toString());
            System.out.println("GoogleChatController: insertGoogleChat: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get google chat connection information
    // GoogleChatDetailDTO
    @GetMapping("service/google-chat/information")
    public ResponseEntity<Object> getLarkInformation(
            @RequestParam(value = "userId") String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            GoogleChatEntity dto = googleChatService.getGoogleChatsByUserId(userId);
            if (dto != null) {
                GoogleChatDetailDTO detailDTO = new GoogleChatDetailDTO();
                detailDTO.setId(dto.getId());
                detailDTO.setWebhook(dto.getWebhook());
                detailDTO.setUserId(dto.getUserId());
                List<GoogleChatBankDTO> bankDTOs = googleChatAccountBankService.getGoogleAccountBanks(dto.getId());
                detailDTO.setBanks(bankDTOs);
                result = detailDTO;
                httpStatus = HttpStatus.OK;
            } else {
                ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO("CHECK", "C13");
                result = responseMessageDTO;
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            logger.error("GoogleChatController: getLarkInformation: ERROR: " + e.toString());
            System.out.println("GoogleChatController: getLarkInformation: ERROR: " + e.toString());
            ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO("FAILED", "E05");
            result = responseMessageDTO;
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove google chat connection
    @DeleteMapping("service/google-chat/remove")
    public ResponseEntity<ResponseMessageDTO> removeGoogleChat(
            @RequestBody GoogleChatRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            googleChatService.removeGoogleChat(dto.getId());
            googleChatAccountBankService.deleteByGoogleChatId(dto.getId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("GoogleChatController: removeGoogleChat: ERROR: " + e.toString());
            System.out.println("GoogleChatController: removeGoogleChat: ERROR:  " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}