package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.service.qrfeed.QrCommentService;
import com.vietqr.org.service.qrfeed.QrInteractionService;

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
public class QrInteractionController {
    private static final Logger logger = Logger.getLogger(QrInteractionController.class);

    @Autowired
    QrInteractionService qrInteractionService;

    @Autowired
    QrWalletService qrWalletService;

    @Autowired
    QrCommentService qrCommentService;


    @PostMapping("/qr-interaction/interact")
    public ResponseEntity<Object> likeOrUnlikeQrWallet(@RequestBody QrInteractionRequestDTO request,
                                                       @RequestParam int page,
                                                       @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            long currentTime = System.currentTimeMillis() / 1000L; // Lưu thời gian dưới dạng Unix timestamp
            qrInteractionService.likeOrUnlikeQrWallet(request.getQrWalletId(), request.getUserId(), request.getInteractionType(), currentTime);

            // Fetch the updated QR Wallet details
            IQrWalletDTO qrWalletDTO = qrWalletService.getQrWalletDetailsById(request.getUserId(), request.getQrWalletId());
            if (qrWalletDTO == null) {
                result = new ResponseMessageDTO("FAILED", "QrWallet not found");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
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
            }
        } catch (Exception e) {
            logger.error("likeOrUnlikeQrWallet: ERROR: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

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
