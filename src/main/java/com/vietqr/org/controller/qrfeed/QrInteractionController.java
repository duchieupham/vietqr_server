package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.QrInteractionRequestDTO;
import com.vietqr.org.dto.qrfeed.UserLikeDTO;
import com.vietqr.org.service.qrfeed.QrInteractionService;

import com.vietqr.org.util.StringUtil;
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

//    @GetMapping("qr-interaction/users-like/{qrWalletId}")
//    public ResponseEntity<Object> getUsersWhoLiked(@PathVariable String qrWalletId) {
//        Object result = null;
//        HttpStatus httpStatus = null;
//        try {
//            List<String> userNames = qrInteractionService.getUserNamesWhoLiked(qrWalletId);
//            result = userNames;
//            httpStatus = HttpStatus.OK;
//        } catch (Exception e) {
//            result = new ResponseMessageDTO("FAILED", "E05");
//            httpStatus = HttpStatus.BAD_REQUEST;
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }

    @GetMapping("qr-interaction/users-like/{qrWalletId}")
    public ResponseEntity<Object> getLikersByQrWalletId(@PathVariable String qrWalletId,
                                                        @RequestParam int page,
                                                        @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            int totalElements = qrInteractionService.countLikersByQrWalletId(qrWalletId);
            int offset = (page - 1) * size;
            List<UserLikeDTO> likers = qrInteractionService.findLikersByQrWalletId(qrWalletId, offset, size);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));

            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(likers);

            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getLikersByQrWalletId: ERROR: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
