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

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.SocialNetworkBankDTO;
import com.vietqr.org.dto.TelBankDTO;
import com.vietqr.org.dto.TelegramDetailDTO;
import com.vietqr.org.dto.TelegramInsertDTO;
import com.vietqr.org.entity.TelegramAccountBankEntity;
import com.vietqr.org.entity.TelegramEntity;
import com.vietqr.org.service.TelegramAccountBankService;
import com.vietqr.org.service.TelegramService;
import com.vietqr.org.util.TelegramUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TelegramController {
    private static final Logger logger = Logger.getLogger(TelegramController.class);

    @Autowired
    TelegramService telegramService;

    @Autowired
    TelegramAccountBankService telegramAccountBankService;

    // send first message
    @GetMapping("service/telegram/send-message")
    public ResponseEntity<ResponseMessageDTO> sendFirstMessage(
            @RequestParam(value = "chatId") String chatId) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String message = "Xin chào quý khách 🎉"
                    + "\nRất vui khi kết nối với quý khách qua kênh liên lạc Telegram."
                    + "\nCảm ơn quý khách đã sử dụng dịch vụ của chúng tôi."
                    + "\n🌐 Truy cập ứng dụng VietQR VN tại: https://vietqr.vn | https://vietqr.com | https://vietqr.org"
                    + "\n📱 hoặc tải ứng dụng thông qua: https://onelink.to/q7zwpe"
                    + "\n📞 Hotline hỗ trợ: 1900 6234 - 092 233 3636";
            TelegramUtil telegramUtil = new TelegramUtil();
            boolean check = telegramUtil.sendMsg(chatId, message);
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

    // add bank to telegram
    @PostMapping("service/telegram/bank")
    public ResponseEntity<ResponseMessageDTO> insertBankIntoTelegram(@RequestBody SocialNetworkBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                TelegramEntity telegramEntity = telegramService.getTelegramById(dto.getId());
                if (telegramEntity != null) {
                    UUID uuid = UUID.randomUUID();
                    TelegramAccountBankEntity telAccBankEntity = new TelegramAccountBankEntity();
                    telAccBankEntity.setId(uuid.toString());
                    telAccBankEntity.setBankId(dto.getBankId());
                    telAccBankEntity.setTelegramId(dto.getId());
                    telAccBankEntity.setUserId(dto.getUserId());
                    telAccBankEntity.setChatId(telegramEntity.getChatId());
                    telegramAccountBankService.insert(telAccBankEntity);
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

    // remove bank from telegram
    @DeleteMapping("service/telegram/bank")
    public ResponseEntity<ResponseMessageDTO> removeBankFromTelegram(@RequestBody SocialNetworkBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                TelegramEntity telegramEntity = telegramService.getTelegramById(dto.getId());
                if (telegramEntity != null) {
                    telegramAccountBankService.removeTelAccBankByTelIdAndBankId(dto.getId(), dto.getBankId());
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

    // add telegram account
    @PostMapping("service/telegram")
    public ResponseEntity<ResponseMessageDTO> insertTelegramChatId(@RequestBody TelegramInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getBankIds() != null && !dto.getBankIds().isEmpty()) {
                UUID uuid = UUID.randomUUID();
                TelegramEntity telegramEntity = new TelegramEntity();
                telegramEntity.setId(uuid.toString());
                telegramEntity.setUserId(dto.getUserId());
                telegramEntity.setChatId(dto.getChatId());
                telegramService.insertTelegram(telegramEntity);
                for (String bankId : dto.getBankIds()) {
                    UUID uuid2 = UUID.randomUUID();
                    TelegramAccountBankEntity telAccBankEntity = new TelegramAccountBankEntity();
                    telAccBankEntity.setId(uuid2.toString());
                    telAccBankEntity.setBankId(bankId);
                    telAccBankEntity.setTelegramId(uuid.toString());
                    telAccBankEntity.setUserId(dto.getUserId());
                    telAccBankEntity.setChatId(dto.getChatId());
                    telegramAccountBankService.insert(telAccBankEntity);
                }
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("insertTelegramChatId: INVALID REQUEST BODY");
                System.out.println("insertTelegramChatId: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at insertTelegramChatId: " + e.toString());
            System.out.println("Error at insertTelegramChatId: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get telegram account information
    @GetMapping("service/telegram/information")
    public ResponseEntity<List<TelegramDetailDTO>> getTelegramInformation(
            @RequestParam(value = "userId") String userId) {
        List<TelegramDetailDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<TelegramEntity> telegramEntities = telegramService.getTelegramsByUserId(userId);
            if (telegramEntities != null && !telegramEntities.isEmpty()) {
                for (TelegramEntity telegramEntity : telegramEntities) {
                    TelegramDetailDTO telegramDetailDTO = new TelegramDetailDTO();
                    telegramDetailDTO.setId(telegramEntity.getId());
                    telegramDetailDTO.setChatId(telegramEntity.getChatId());
                    telegramDetailDTO.setUserId(telegramEntity.getUserId());
                    List<TelBankDTO> telBankDTOs = telegramAccountBankService
                            .getTelAccBanksByTelId(telegramEntity.getId());
                    if (telBankDTOs != null && !telBankDTOs.isEmpty()) {
                        telegramDetailDTO.setBanks(telBankDTOs);
                    }
                    result.add(telegramDetailDTO);
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getTelegramInformation: ERROR: " + e.toString());
            System.out.println("getTelegramInformation: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // remove telegram account
    @DeleteMapping("service/telegram/remove")
    public ResponseEntity<ResponseMessageDTO> removeTelegram(
            @RequestParam(value = "id") String id) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            telegramAccountBankService.removeTelAccBankByTelId(id);
            telegramService.removeTelegramById(id);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Error at removeTelegram: " + e.toString());
            System.out.println("Error at removeTelegram: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
