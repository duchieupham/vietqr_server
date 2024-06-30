package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.PageDTO;
import com.vietqr.org.dto.PageResDTO;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.qrfeed.FolderCreateDTO;
import com.vietqr.org.dto.qrfeed.IListQrFolderDTO;
import com.vietqr.org.dto.qrfeed.ListQrFolderDTO;
import com.vietqr.org.dto.qrfeed.QrFolderUpdateDTO;
import com.vietqr.org.entity.qrfeed.QrFolderEntity;
import com.vietqr.org.repository.QrWalletFolderRepository;
import com.vietqr.org.service.qrfeed.QrFolderService;
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
                // insert
                qrFolderService.insertQrFolder(entity);

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
            @RequestParam int type,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String value
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;

            List<ListQrFolderDTO> data = new ArrayList<>();
            List<IListQrFolderDTO> info = new ArrayList<>();

            totalElement = qrFolderService.countQrFolder(value);
            info = qrFolderService.getListFolders(value, offset, size);
            data = info.stream().map(item -> {
                ListQrFolderDTO dto = new ListQrFolderDTO();
                dto.setId(item.getId());
                dto.setTitle(item.getTitle());
                dto.setDescription(item.getDescription());
                dto.setUserId(item.getUserId());
                dto.setTimeCreated(item.getTimeCreate());
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
            httpStatus = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            logger.error("get list folder: ERROR: " + e.toString());
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

    @DeleteMapping("/delete-folder-and-items")
    public ResponseEntity<Object> deleteFolderAndItems(@RequestParam String folderId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrFolderEntity folder = qrFolderService.getFolderById(folderId);
            if (folder == null) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                List<String> qrItemIds = qrWalletService.getQrWalletIdsByFolderId(folderId);
                qrWalletService.deleteQrItemsByIds(qrItemIds);
                qrWalletFolderRepository.deleteByQrFolderId(folderId);
                qrFolderService.deleteFolderById(folderId);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("deleteFolderAndItems: ERROR: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("/delete-folder-keep-items")
    public ResponseEntity<Object> deleteFolderKeepItems(@RequestParam String folderId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrFolderEntity folder = qrFolderService.getFolderById(folderId);
            if (folder == null) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                qrWalletFolderRepository.deleteByQrFolderId(folderId);
                qrFolderService.deleteFolderById(folderId);
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("deleteFolderKeepItems: ERROR: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
