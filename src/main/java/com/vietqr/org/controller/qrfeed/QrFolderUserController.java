package com.vietqr.org.controller.qrfeed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.service.qrfeed.QrFolderService;
import com.vietqr.org.service.qrfeed.QrFolderUserService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrFolderUserController {
    private static final Logger logger = Logger.getLogger(QrFolderUserController.class);

    @Autowired
    QrFolderService qrFolderService;

    @Autowired
    QrFolderUserService qrFolderUserService;

    @Autowired
    QrWalletService qrWalletService;

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

    // lấy ra thông tin những user trong folder
    @GetMapping("qr-feed/folder-users")
    public ResponseEntity<Object> getUserInFolder(
            @RequestParam(name = "type", defaultValue = "-1") int type,
            @RequestParam String folderId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            UserInFolderResponseDTO data = new UserInFolderResponseDTO();

            IFolderInformationDTO folderInfo = null;
            folderInfo = qrFolderService.getFolderInfo(folderId);

            if (Objects.nonNull(folderInfo)) {
                List<DataUserDTO> userInfoLinkOrText = new ArrayList<>();

                Gson gson = new Gson();
                data.setUserId(folderInfo.getUserId());

                List<String> userDataJson = qrWalletService.getUserLinkOrTextData(folderId, type);
                userInfoLinkOrText = userDataJson.stream().map(userInfo -> {
                    DataUserDTO dto = gson.fromJson(userInfo, DataUserDTO.class);
                    return dto;
                }).collect(Collectors.toList());
                data.setUserData(userInfoLinkOrText);
                data.setFolderId(folderInfo.getFolderId());
                data.setTitleFolder(folderInfo.getTitleFolder());
                data.setDescriptionFolder(folderInfo.getDescriptionFolder());

                result = data;
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E151");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("add users to folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    public ResponseEntity<Object> getQrInFolder(
            @RequestParam int type,
            @RequestParam String folderId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        Gson gson = new Gson();
        try {
            QrInFolderResponseDTO data = new QrInFolderResponseDTO();

            // get information about folder
            IFolderInformationDTO folderInfo = null;
            folderInfo = qrFolderService.getFolderInfo(folderId);

            // chứa qr_data và qr_info
            QRInfo qrInfo = new QRInfo();

            // xử lý chuỗi JSON thành object
            QrData qrData = new QrData();


            // tạo object chứa qr_data
            List<QrInfoLinkOrTextDTO> qrInfoLinks = new ArrayList<>();
            List<QrInfoVCardDTO> qrInfoVCard = new ArrayList<>();
            List<QrData> qrInfoVietQr = new ArrayList<>();

            // (type = 0 : QR Link & QR Other )
            // (type = 1: QR VCard)
            // (type = 2: VietQR)


            result = "";
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("add users to folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
