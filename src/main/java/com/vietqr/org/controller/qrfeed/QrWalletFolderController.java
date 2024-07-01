package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.service.qrfeed.QrFolderService;
import com.vietqr.org.service.qrfeed.QrUserService;
import com.vietqr.org.service.qrfeed.QrWalletFolderService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrWalletFolderController {
    private static final Logger logger = Logger.getLogger(QrWalletFolderController.class);

    @Autowired
    QrWalletService qrWalletService;

    @Autowired
    QrFolderService qrFolderService;

    @Autowired
    QrUserService qrUserService;


    @Autowired
    QrWalletFolderService qrWalletFolderService;

    @PostMapping("qr-feed/add-qr-folder")
    public ResponseEntity<Object> addQrWalletsToFolder(
            @RequestParam String qrFolderId,
            @RequestBody List<String> qrWalletIds
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {

            QrFolderEntity entity = qrFolderService.getFolderById(qrFolderId);
            if (Objects.isNull(entity)) {
                result = new ResponseMessageDTO("FAILED", "E149");
                httpStatus = HttpStatus.BAD_REQUEST;
            }else {
                qrWalletFolderService.addQrWalletIds(qrFolderId, qrWalletIds);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("add QR to folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }



}
