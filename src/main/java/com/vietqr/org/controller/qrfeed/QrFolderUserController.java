package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.IUserInFolderDTO;
import com.vietqr.org.dto.qrfeed.UserInFolderDTO;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.service.qrfeed.QrFolderService;
import com.vietqr.org.service.qrfeed.QrFolderUserService;
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
public class QrFolderUserController {
    private static final Logger logger = Logger.getLogger(QrFolderUserController.class);

    @Autowired
    QrFolderService qrFolderService;

    @Autowired
    QrFolderUserService qrFolderUserService;

    @PostMapping("qr-feed/add-user-folder")
    public ResponseEntity<Object> updateUserToFolder(
            @RequestParam String qrFolderId,
            @RequestBody List<String> userIds
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrFolderEntity entity = qrFolderService.getFolderById(qrFolderId);

            if (Objects.isNull(entity)) {
                result = new ResponseMessageDTO("FAILED", "E149");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                qrFolderUserService.addUserIds(qrFolderId, userIds);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("add users to folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    public ResponseEntity<Object> getUserInFolder(
            @RequestParam int type,
            @RequestParam String value, // folder ID
            @RequestParam int page,
            @RequestParam int size
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;

            List<UserInFolderDTO> data = new ArrayList<>();
            List<IUserInFolderDTO> info = new ArrayList<>();

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            logger.error("add users to folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
