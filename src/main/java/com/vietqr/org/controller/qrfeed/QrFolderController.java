package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.repository.QrWalletFolderRepository;
import com.vietqr.org.service.qrfeed.QrFolderService;
import com.vietqr.org.service.qrfeed.QrFolderUserService;
import com.vietqr.org.service.qrfeed.QrWalletFolderService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrFolderController {
    private static final Logger logger = Logger.getLogger(QrFolderController.class);

    @Autowired
    QrFolderService qrFolderService;
    @Autowired
    QrWalletService qrWalletService;

    @Autowired
    QrFolderUserService qrFolderUserService;

    @Autowired
    QrWalletFolderService qrWalletFolderService;

    @Autowired
    QrWalletFolderRepository qrWalletFolderRepository;

    @PostMapping("qr-feed/generate-folder")
    public ResponseEntity<Object> createFolder(@RequestBody FolderCreateDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrFolderEntity entity = new QrFolderEntity();
            UUID idQrFolder = UUID.randomUUID();
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (dto.getTitle() != null && dto.getTitle() != null && dto.getTitle() != null) {
                entity.setId(idQrFolder.toString());
                entity.setTitle(dto.getTitle());
                entity.setDescription(dto.getDescription());
                entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                entity.setUserId(dto.getUserId());
                // set data user (JSON)
                entity.setUserData("{"
                        + "\"userId\": \"" + dto.getUserId() + "\""
                        + "}");
                // insert folder
                qrFolderService.insertQrFolder(entity);
                //insert user in folder with role ADMIN
                UUID id = UUID.randomUUID();
                qrFolderUserService.addUserAdmin(id.toString(), idQrFolder.toString(), dto.getUserId());

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("create folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("qr-feed/folders")
    public ResponseEntity<Object> getListFolderByUser(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String value,
            @RequestParam String userId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;

            List<ListQrFolderDTO> data = new ArrayList<>();
            List<IListQrFolderDTO> info = new ArrayList<>();
            totalElement = qrFolderService.countQrFolder(value, userId);

            info = qrFolderService.getListFolders(value, offset, size, userId);
            data = info.stream().map(item -> {
                ListQrFolderDTO dto = new ListQrFolderDTO();
                dto.setId(item.getId());
                dto.setTitle(item.getTitle());
                dto.setDescription(item.getDescription());
                dto.setUserId(item.getUserId());
                dto.setTimeCreated(item.getTimeCreate());

                //count qr trong folder
                int countQR = qrWalletFolderService.countQrFolder(item.getId());
                dto.setCountQrs(countQR);

                int countUsers = qrFolderUserService.countUsersFolder(item.getId());
                dto.setCountUsers(countUsers);
                return dto;
            }).collect(Collectors.toList());

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));

            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("get list folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("qr-feed/folder")
    public ResponseEntity<Object> getFolder(@RequestParam String folderId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            FolderDetailDTO folderDetailDTO = new FolderDetailDTO();
            IFolderDetailDTO iFolderDetailDTO = qrFolderService.getFolderDetailById(folderId);

            folderDetailDTO.setId(iFolderDetailDTO.getId());
            folderDetailDTO.setTitle(iFolderDetailDTO.getTitle());
            folderDetailDTO.setDescription(iFolderDetailDTO.getDescription());
            folderDetailDTO.setTimeCreated(iFolderDetailDTO.getTimeCreated());
            // count user trong folder
            int countUser = qrFolderUserService.countUsersFolder(folderId);
            folderDetailDTO.setCountUser(countUser);
            //count qr trong folder
            int countQR = qrWalletFolderService.countQrFolder(folderId);
            folderDetailDTO.setCountQr(countQR);

            result = folderDetailDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("update folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("qr-feed/update-folder")
    public ResponseEntity<Object> updateQrFolder(
            @RequestParam String id,
            @RequestBody QrFolderUpdateDTO dto
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {

            QrFolderEntity entity = qrFolderService.getFolderById(id);

            if (entity != null) {
                if (dto.getTitle() != null && dto.getDescription() != null) {

                    // update
                    qrFolderService.updateQrFolder(id, dto.getDescription(), dto.getTitle());

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            logger.error("update folder: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("/delete-folder")
    public ResponseEntity<Object> deleteFolder(@RequestParam String folderId, @RequestParam boolean deleteItems) {
        HttpStatus httpStatus = null;
        Object result = null;
        try {
            QrFolderEntity folder = qrFolderService.getFolderById(folderId);
            if (deleteItems) {
                List<String> qrItems = qrWalletService.getQrWalletIdsByFolderId(folderId);
                qrWalletService.deleteQrItemsByIds(qrItems);
            }
            qrWalletFolderRepository.deleteByQrFolderId(folderId);
            qrFolderService.deleteFolderById(folderId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("deleteFolder Error at " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/users/search")
    public ResponseEntity<Object> searchUsersByPhoneNo(@RequestParam String phoneNo) {
        Object result;
        HttpStatus httpStatus;
        try {
            List<UserDTO> users = qrFolderService.findUsersByPhoneNo(phoneNo);
            result = users;
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            logger.error("QrFolderController : searchUsersByPhoneNo: Error at " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

}
