package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.QrInteractionRequestDTO;
import com.vietqr.org.service.qrfeed.QrInteractionService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrInteractionController {
    private static final Logger logger = Logger.getLogger(QrInteractionController.class);

    @Autowired
    QrInteractionService qrInteractionService;


    @PostMapping("qr-interaction/interact")
    public ResponseEntity<Object> likeOrUnlikeQrWallet(@RequestBody QrInteractionRequestDTO request) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            long currentTime = System.currentTimeMillis() / 1000L; // Lưu thời gian dưới dạng Unix timestamp
            qrInteractionService.likeOrUnlikeQrWallet(request.getQrWalletId(), request.getUserId(), request.getInteractionType(), currentTime);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("likeOrUnlikeQrWallet: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("qr-interaction/users-like/{qrWalletId}")
    public ResponseEntity<Object> getUsersWhoLiked(@PathVariable String qrWalletId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> userNames = qrInteractionService.getUserNamesWhoLiked(qrWalletId);
            result = userNames;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
