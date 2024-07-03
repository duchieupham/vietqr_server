package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.QrCommentRequestDTO;
import com.vietqr.org.dto.qrfeed.UserCommentDTO;
import com.vietqr.org.service.qrfeed.QrCommentService;
import com.vietqr.org.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.log4j.Logger;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrCommentController {
    private static final Logger logger = Logger.getLogger(QrCommentController.class);

    @Autowired
    QrCommentService qrCommentService;

    @PostMapping("/qr-comment/add")
    public ResponseEntity<Object> addComment(@RequestBody QrCommentRequestDTO request) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            qrCommentService.addComment(request);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("addComment: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("/qr-comment/delete/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable String commentId){
        Object result = null;
        HttpStatus httpStatus = null;
        try{
            qrCommentService.deleteComment(commentId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        }catch (Exception e){
            logger.error("deleteComment: Error: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/qr-comment/users-comment/{walletId}")
    public ResponseEntity<Object> getCommentersByQrWalletId(@PathVariable String walletId,
                                                            @RequestParam int page,
                                                            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            int totalElements = qrCommentService.countCommentersByQrWalletId(walletId);
            int offset = (page - 1) * size;
            List<UserCommentDTO> commenters = qrCommentService.findCommentersByQrWalletId(walletId, offset, size);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));

            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(commenters);

            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCommentersByQrWalletId: ERROR: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}