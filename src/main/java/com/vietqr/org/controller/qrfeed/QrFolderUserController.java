package com.vietqr.org.controller.qrfeed;

import com.google.gson.Gson;
import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.service.qrfeed.QrFolderService;
import com.vietqr.org.service.qrfeed.QrFolderUserService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import com.vietqr.org.util.StringUtil;
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

@PostMapping("/qr-feed/add-user-folder")
public ResponseEntity<Object> updateUserToFolder(@RequestBody AddUserToFolderRequestDTO dto) {
    Object result = null;
    HttpStatus httpStatus = null;
    try {
        QrFolderEntity entity = qrFolderService.getFolderById(dto.getFolderId());

        if (Objects.isNull(entity)) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            qrFolderUserService.addUserIds(dto.getFolderId(), dto.getUserRoles(), dto.getUserId());

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        }
    } catch (Exception e) {
        logger.error("add users to folder: ERROR: " + e.toString());
        result = new ResponseMessageDTO("FAILED", "E05");
        httpStatus = HttpStatus.BAD_REQUEST;
    }
    return new ResponseEntity<>(result, httpStatus);
}

    @PutMapping("/qr-feed/update-user-roles")
    public ResponseEntity<Object> updateUserRoles(@RequestBody UpdateUserRoleRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            qrFolderUserService.updateUserRoles(dto.getFolderId(), dto.getUserRoles());

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateUserRoles: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
    @PutMapping("/qr-folder/update-user-role")
    public ResponseEntity<Object> updateUserRole(@RequestBody UpdateSingleUserRoleRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            qrFolderUserService.updateUserRole(dto.getFolderId(), dto.getUserId(), dto.getRole());

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateUserRole: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("qr-folder/user-roles/{folderId}")
    public ResponseEntity<Object> getUserRolesByFolderId(
            @PathVariable String folderId,
            @RequestParam(value = "value", required = false, defaultValue = "") String value,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            int totalElements = qrFolderUserService.countUserRolesByFolderId(folderId, value);
            int offset = (page - 1) * size;
            List<IUserRoleDTO> userRoles = qrFolderUserService.getUserRolesByFolderId(folderId, value, offset, size);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));

            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(userRoles);

            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getUserRolesByFolderId: ERROR: " + e.getMessage() + " at " + System.currentTimeMillis());
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
    @DeleteMapping("/qr-folder/remove-user")
    public ResponseEntity<Object> removeUserFromFolder(@RequestBody RemoveUserFromFolderRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // Log input values
            logger.info("Request to remove user from folder. Folder ID: " + dto.getFolderId() + ", User ID: " + dto.getUserId());

            // Check for null or empty values
            if (dto.getFolderId() == null || dto.getFolderId().isEmpty() || dto.getUserId() == null || dto.getUserId().isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.error("Folder ID or User ID is null or empty.");
            } else {
                qrFolderUserService.deleteUserFromFolder(dto.getFolderId(), dto.getUserId());

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
                logger.info("Successfully removed user from folder. Folder ID: " + dto.getFolderId() + ", User ID: " + dto.getUserId());
            }
        } catch (Exception e) {
            logger.error("removeUserFromFolder: ERROR: " + e.toString(), e);
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
