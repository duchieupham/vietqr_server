package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.service.qrfeed.QrCommentService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrCommentController {
    private static final Logger logger = Logger.getLogger(QrCommentController.class);

    @Autowired
    QrCommentService qrCommentService;
    @Autowired
    QrWalletService qrWalletService;

    @PostMapping("/qr-comment/add")
    public ResponseEntity<Object> addComment(
            @RequestBody QrCommentRequestDTO request,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            qrCommentService.addComment(request);
            IQrWalletDTO qrWalletDTO = qrWalletService.getQrWalletDetailsById(request.getUserId(), request.getQrWalletId());
            int totalCommentElements = qrWalletService.countCommentsByQrWalletId(request.getQrWalletId());
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<QrCommentDTO> commentsPage = qrWalletService.findCommentsByQrWalletId(request.getQrWalletId(), pageable);

            QrWalletDetailDTO detailDTO = new QrWalletDetailDTO();
            detailDTO.setId(qrWalletDTO.getId());
            detailDTO.setTitle(qrWalletDTO.getTitle());
            detailDTO.setDescription(qrWalletDTO.getDescription());
            detailDTO.setValue(qrWalletDTO.getValue());
            detailDTO.setQrType(qrWalletDTO.getQrType());
            detailDTO.setTimeCreated(qrWalletDTO.getTimeCreated());
            detailDTO.setUserId(qrWalletDTO.getUserId());
            detailDTO.setLikeCount(qrWalletDTO.getLikeCount());
            detailDTO.setCommentCount(qrWalletDTO.getCommentCount());
            detailDTO.setHasLiked(qrWalletDTO.getHasLiked());
            detailDTO.setData(qrWalletDTO.getData());
            detailDTO.setFullName(qrWalletDTO.getFullName());
            detailDTO.setImageId(qrWalletDTO.getImageId());
            detailDTO.setStyle(qrWalletDTO.getStyle());
            detailDTO.setTheme(qrWalletDTO.getTheme());
            detailDTO.setFileAttachmentId(qrWalletDTO.getFileAttachmentId());

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalCommentElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalCommentElements, size));

            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(commentsPage.getContent());

            detailDTO.setComments(pageResDTO);

            result = detailDTO;
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            logger.error("addComment: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("/qr-comment/delete/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable String commentId, @RequestParam String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            boolean isDeleted = qrCommentService.deleteComment(commentId, userId);
            if (isDeleted) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "You do not have permission to delete this comment");
                httpStatus = HttpStatus.FORBIDDEN;
            }
        } catch (Exception e) {
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