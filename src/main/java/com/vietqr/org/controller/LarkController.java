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
import com.vietqr.org.dto.LarkBankDTO;
import com.vietqr.org.dto.LarkDetailDTO;
import com.vietqr.org.dto.LarkInsertDTO;
import com.vietqr.org.entity.LarkAccountBankEntity;
import com.vietqr.org.entity.LarkEntity;
import com.vietqr.org.service.LarkAccountBankService;
import com.vietqr.org.service.LarkService;
import com.vietqr.org.util.LarkUtil;

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
                result = new ResponseMessageDTO("SUCCESS", "");
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
}
