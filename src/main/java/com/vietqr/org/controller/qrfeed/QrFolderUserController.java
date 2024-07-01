package com.vietqr.org.controller.qrfeed;

import com.google.gson.Gson;
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
            @RequestBody AddUserToFolderRequestDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrFolderEntity entity = qrFolderService.getFolderById(dto.getFolderId());

            if (Objects.isNull(entity)) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                qrFolderUserService.addUserIds(dto.getFolderId(), dto.getUserIds(), dto.getUserId());


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
            @RequestParam(name = "type", required = true) Integer type,
            @RequestParam String folderId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (type == null) {
                type = -1;
            }
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

    @GetMapping("qr-feed/folder-qrs")
    public ResponseEntity<Object> getQrInFolder(
            @RequestParam Integer type,
            @RequestParam String folderId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        Gson gson = new Gson();
        try {
            if (type == null) {
                type = -1;
            }
`
            QrInFolderResponseDTO data = new QrInFolderResponseDTO();

            // get information about folder
            IFolderInformationDTO qrInFolderDTO = null;
            qrInFolderDTO = qrFolderService.getQrInFolder(folderId);

            if (Objects.nonNull(qrInFolderDTO)) {
                // xử lý chuỗi JSON thành object
                List<DataQrDTO> listQrDataDTOs = new ArrayList<>();
                data.setUserId(qrInFolderDTO.getUserId());

                List<String> userDataJson = qrWalletService.getQrData(folderId, type);
                listQrDataDTOs = userDataJson.stream().map(userInfo -> {
                    DataQrDTO qrData = gson.fromJson(userInfo, DataQrDTO.class);
                    return qrData;
                }).collect(Collectors.toList());
                // chứa qr_data và qr_info
//                QRInfo qrInfo = new QRInfo();
//                qrInfo.setData(listQrDataDTOs);
//                qrInfo.setValue("Lấy value ở bảng QR bỏ vào");

                data.setQrData(listQrDataDTOs);
                data.setFolderId(qrInFolderDTO.getFolderId());
                data.setTitleFolder(qrInFolderDTO.getTitleFolder());
                data.setDescriptionFolder(qrInFolderDTO.getDescriptionFolder());

                result = data;
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("add users to folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }



}
