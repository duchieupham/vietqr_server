package com.vietqr.org.controller.social;

import com.vietqr.org.dto.ResponseMessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MessengerController {

    @Value("${messenger.page.access.token}")
    private String pageAccessToken;

    @Value("${messenger.verify.token}")
    private String verifyToken;
    private static final String FB_MSG_URL = "https://graph.facebook.com/v11.0/me/messages?access_token=";
    @PostMapping("messenger/send")
    public ResponseEntity<Object> sendMessage(@RequestParam String recipientId, @RequestParam String messageText) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = FB_MSG_URL + pageAccessToken;

            String message = "Xin chào quý khách 🎉"
                    + "\nRất vui khi kết nối với quý khách qua kênh liên lạc Discord."
                    + "\nCảm ơn quý khách đã sử dụng dịch vụ của chúng tôi."
                    + "\n🌐 Truy cập ứng dụng VietQR VN tại: https://vietqr.vn | https://vietqr.com"
                    + "\n📱 Hoặc tải ứng dụng thông qua: https://onelink.to/q7zwpe"
                    + "\n📞 Hotline hỗ trợ: 1900 6234 - 092 233 3636";

            // Cấu trúc tin nhắn
            String jsonBody = "{"
                    + "\"recipient\":{\"id\":\"" + recipientId + "\"},"
                    + "\"message\":{\"text\":\"" + messageText + "\"}"
                    + "}";

            // Gửi tin nhắn
            restTemplate.postForObject(url, jsonBody, String.class);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED: " + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
