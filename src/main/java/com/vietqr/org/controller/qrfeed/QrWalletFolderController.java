package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.DeleteQrFolder;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.AddQrToFolderRequestDTO;
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

import java.util.ArrayList;
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
            @RequestBody AddQrToFolderRequestDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrFolderEntity entity = qrFolderService.getFolderById(dto.getFolderId());
            if (Objects.isNull(entity)) {
                result = new ResponseMessageDTO("FAILED", "E149");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                // check exist
                List<String> checkQrExists = qrWalletFolderService.checkQrExists(dto.getFolderId(), dto.getUserId());
                List<String> copy = new ArrayList<String>(checkQrExists);
                copy.retainAll(dto.getQrIds());
                dto.getQrIds().removeAll(copy);

                qrWalletFolderService.addQrWalletsToFolder(dto.getFolderId(), dto.getQrIds());

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("add QR to folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("qr-feed/delete-qrs-folder")
    public ResponseEntity<Object> deleteQrsInFolder(
            @RequestBody DeleteQrFolder dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> getQrsFolder = qrWalletFolderService.getListQrsInFolder(dto.getFolderId(), dto.getUserId(), dto.getQrIds());

            qrWalletFolderService.deleteQrsInFolder(getQrsFolder);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("delete QR in folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


}
