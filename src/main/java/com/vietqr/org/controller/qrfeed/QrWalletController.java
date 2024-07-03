package com.vietqr.org.controller.qrfeed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.qrfeed.QrUserEntity;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.service.AmazonS3Service;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.qrfeed.QrCommentService;
import com.vietqr.org.service.qrfeed.QrUserService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
@Validated
public class QrWalletController {
    private static final Logger logger = Logger.getLogger(QrWalletController.class);

    @Autowired
    ImageService imageService;

    @Autowired
    AmazonS3Service amazonS3Service;

    @Autowired
    QrWalletService qrWalletService;

    @Autowired
    QrUserService qrUserService;

    @Autowired
    CaiBankService caiBankService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    private QrCommentService qrCommentService;

    //    @PostMapping("qr-wallet/generate-qr-vietqr")
    public ResponseEntity<Object> generateQRUnauthenticated(
            int isPublic,
            int type,
            VietQRCreateUnauthenticatedExtendDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        VietQRDTO vietQRDTO = new VietQRDTO();
        try {
            if (dto.isNull()) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                if (bankTypeId != null) {
                    String caiValue = caiBankService.getCaiValue(bankTypeId);
                    BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(caiValue);
                    vietQRGenerateDTO.setBankAccount(dto.getBankAccount());
                    String amount = "";
                    String content = "";
                    if (dto.getAmount() != null && !dto.getAmount().trim().isEmpty()) {
                        amount = dto.getAmount();
                    } else if (dto.getAmount() != null && dto.getAmount().trim().isEmpty()) {
                        amount = "0";
                    }
                    if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                        content = dto.getContent();
                    }
                    vietQRGenerateDTO.setAmount(amount);
                    vietQRGenerateDTO.setContent(content);
                    String qr = "";
                    if (dto.getAmount().trim().isEmpty() && dto.getContent().trim().isEmpty()) {
                        qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                    } else {
                        qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    }
                    vietQRDTO.setBankCode(dto.getBankCode());
                    vietQRDTO.setBankName(bankTypeEntity.getBankName());
                    vietQRDTO.setBankAccount(dto.getBankAccount());
                    vietQRDTO.setUserBankName(dto.getUserBankName().toUpperCase());
                    vietQRDTO.setAmount(amount);
                    vietQRDTO.setContent(content);
                    vietQRDTO.setQrCode(qr);
                    vietQRDTO.setImgId("");
                    vietQRDTO.setExisting(0);
                    vietQRDTO.setTransactionId("");
                    vietQRDTO.setTransactionRefId("");
                    vietQRDTO.setTerminalCode("");
                    vietQRDTO.setQrLink("");

                    // add data qr vào qr_wallet
                    QrWalletEntity entity = new QrWalletEntity();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    UUID idQrWallet = UUID.randomUUID();
                    entity.setId(idQrWallet.toString());
                    entity.setTitle("QR VietQR");
                    entity.setDescription("Mã VietQR của: " + dto.getUserBankName());
                    entity.setValue(qr);
                    entity.setQrType(3);

                    TempVietQRDTO tempVietQRDTO = new TempVietQRDTO();
                    tempVietQRDTO.setBankAccount(dto.getBankAccount());
                    tempVietQRDTO.setUserBankName(dto.getUserBankName());
                    tempVietQRDTO.setBankCode(dto.getBankCode());
                    tempVietQRDTO.setAmount(dto.getAmount());
                    tempVietQRDTO.setContent(dto.getContent());
                    tempVietQRDTO.setValue(qr);
                    entity.setQrData(tempVietQRDTO.toString());
                    entity.setUserData("{"
                            + "\"userId\": \"" + dto.getUserId() + "\", "
                            + "\"bankAccount\": \"" + dto.getBankAccount() + "\", "
                            + "\"userBankName\": \"" + dto.getUserBankName() + "\", "
                            + "\"qrType\": \"" + type + "\", "
                            + "\"bankCode\": \"" + dto.getBankCode() + "\""
                            + "}");
                    if (isPublic == 1) {
                        entity.setIsPublic(1);
                    } else if (isPublic == 0) {
                        entity.setIsPublic(0);
                    }
                    entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                    entity.setUserId(dto.getUserId());
                    entity.setPin("");
                    entity.setPublicId("");
                    entity.setTheme(dto.getTheme());
                    entity.setStyle(dto.getStyle());
                    qrWalletService.insertQrWallet(entity);

                    //add qr vào qr_user (ch implements)
                    QrUserEntity qrUserEntity = new QrUserEntity();
                    UUID idQrUser = UUID.randomUUID();
                    qrUserEntity.setId(idQrUser.toString());
                    qrUserEntity.setQrWalletId(idQrWallet.toString());
                    qrUserEntity.setUserId(dto.getUserId());
                    qrUserEntity.setRole("ADMIN");
                    qrUserService.insertQrUser(qrUserEntity);

                    result = vietQRDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("generateVCard: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //    @PostMapping("qr-wallet/generate-qr-vcard")
    ResponseEntity<Object> createQrVcard(
            int isPublic,
            int type,
            VCardInputExtendDTO dto) {
        Object result = null;
        QrVcardRequestDTO qrVcardRequestDTO = null;
        HttpStatus httpStatus = null;
        try {
            if (dto.isNull()) {
                result = new ResponseMessageDTO("FAILED", "E100");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                String qr = VCardUtil.getVcardQR(dto);
                qrVcardRequestDTO = new QrVcardRequestDTO();
                qrVcardRequestDTO.setQr(qr);
                qrVcardRequestDTO.setFullname(dto.getFullname());
                qrVcardRequestDTO.setPhoneNo(dto.getPhoneNo());
                qrVcardRequestDTO.setEmail(dto.getEmail());
                qrVcardRequestDTO.setCompanyName(dto.getCompanyName());
                qrVcardRequestDTO.setWebsite(dto.getWebsite());
                qrVcardRequestDTO.setAddress(dto.getAddress());
                if (isPublic == 1) {
                    qrVcardRequestDTO.setIsPublic(1);
                } else if (isPublic == 0) {
                    qrVcardRequestDTO.setIsPublic(0);
                }
                qrVcardRequestDTO.setStyle(dto.getStyle());
                qrVcardRequestDTO.setTheme(dto.getTheme());

                // add data qr vào qr_wallet
                QrWalletEntity entity = new QrWalletEntity();
                LocalDateTime currentDateTime = LocalDateTime.now();
                UUID idQrWallet = UUID.randomUUID();
                entity.setId(idQrWallet.toString());
                entity.setTitle("Qr Vcard");
                entity.setDescription("Qr Vcard: " + dto.getFullname());
                entity.setValue(qr);
                entity.setQrType(2);

                TempVCardDTO temp = new TempVCardDTO();
                temp.setFullName(dto.getFullname());
                temp.setPhoneNo(dto.getPhoneNo());
                temp.setEmail(dto.getEmail());
                temp.setCompanyName(dto.getCompanyName());
                temp.setWebsite(dto.getWebsite());
                temp.setValue(qr);
                temp.setAddress(dto.getAddress());

                entity.setQrData(temp.toString());
                entity.setUserData("{"
                        + "\"userId\": \"" + dto.getUserId() + "\", "
                        + "\"fullName\": \"" + dto.getFullname() + "\", "
                        + "\"phoneNo\": \"" + dto.getPhoneNo() + "\", "
                        + "\"email\": \"" + dto.getEmail() + "\", "
                        + "\"companyName\": \"" + dto.getCompanyName() + "\", "
                        + "\"website\": \"" + dto.getWebsite() + "\", "
                        + "\"website\": \"" + dto.getWebsite() + "\", "
                        + "\"qrType\": \"" + type + "\", "
                        + "\"additionalData\": \"" + dto.getAddress() + "\""
                        + "}");
                if (isPublic == 1) {
                    entity.setIsPublic(1);
                } else if (isPublic == 0) {
                    entity.setIsPublic(0);
                }
                entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                entity.setUserId(dto.getUserId());
                entity.setPin("");
                entity.setPublicId("");
                entity.setStyle(dto.getStyle());
                entity.setTheme(dto.getTheme());
                qrWalletService.insertQrWallet(entity);

                //add qr vào qr_user (ch implements)
                QrUserEntity qrUserEntity = new QrUserEntity();
                UUID idQrUser = UUID.randomUUID();
                qrUserEntity.setId(idQrUser.toString());
                qrUserEntity.setQrWalletId(idQrWallet.toString());
                qrUserEntity.setUserId(dto.getUserId());
                qrUserEntity.setRole("ADMIN");
                qrUserService.insertQrUser(qrUserEntity);

                result = qrVcardRequestDTO;
                httpStatus = HttpStatus.OK;
            }


        } catch (Exception e) {
            logger.error("generateVCard: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // update information QrLink or QrText
    @PutMapping("qr-wallet/update-qr-link")
    public ResponseEntity<Object> updateQrLink(
            @RequestParam int type,
            @RequestBody QrLinkOrTextUpdateRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            ListQrWalletDTO request = null;
            QrWalletEntity qrWalletEntity = qrWalletService.getQrLinkOrQrTextById(dto.getQrId());

            if (qrWalletEntity == null) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                if (type == 1) {
                    //update qr link (Description, title, link)
                    qrWalletService.updateQrWallet(dto.getQrId(), dto.getQrDescription(), dto.getIsPublic(), 1,
                            dto.getTitle(), dto.getValue(), dto.getStyle(), dto.getTheme());
                } else if (type == 0) {
                    //update qr text (Description, title, text)
                    qrWalletService.updateQrWallet(dto.getQrId(), dto.getQrDescription(), dto.getIsPublic(), 0,
                            dto.getTitle(), dto.getValue(), dto.getStyle(), dto.getTheme());
                }
                // update thêm data_qr và data_user (JSON)

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    //    @PostMapping("qr-wallet/generate-qr-link")
    public ResponseEntity<Object> createQrLink(int type, QrCreateRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto.isNull()) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                if (type == 1 || type == 0) {
                    // generate QR Code
                    QrWalletResponseDTO qrWalletResponseDTO = new QrWalletResponseDTO();
                    qrWalletResponseDTO.setQrName(dto.getQrName());
                    qrWalletResponseDTO.setValue(dto.getValue()); // cho hiển thị ra chuỗi giá trị
                    qrWalletResponseDTO.setQrContent(dto.getQrDescription());
                    UUID genImgId = UUID.randomUUID();
                    qrWalletResponseDTO.setImgId(genImgId.toString());
                    UUID genRefId = UUID.randomUUID();
                    String refId = TransactionRefIdUtil.encryptTransactionId(genRefId.toString());
                    String qrLink = EnvironmentUtil.getQRLinkNewFeed() + refId;
                    qrWalletResponseDTO.setPublicRefId(refId);
                    qrWalletResponseDTO.setQrLink(qrLink);
                    qrWalletResponseDTO.setExisting(1);

                    // add data qr vào qr_wallet
                    QrWalletEntity entity = new QrWalletEntity();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    UUID idQrWallet = UUID.randomUUID();
                    QrUserEntity qrUserEntity = new QrUserEntity();
                    UUID idQrUser = UUID.randomUUID();
                    switch (type) {
                        case 1: // link
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(dto.getQrName());
                            entity.setDescription(dto.getQrDescription());
                            entity.setValue(dto.getValue());
                            entity.setQrType(1);
                            entity.setQrData("{"
                                    + "\"title\": \"" + dto.getQrName() + "\", "
                                    + "\"description\": \"" + dto.getQrDescription() + "\", "
                                    + "\"isPublic\": \"" + dto.getQrDescription() + "\", "
                                    + "\"style\": \"" + dto.getQrDescription() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"theme\": \"" + dto.getQrDescription() + "\", "
                                    + "\"value\": \"" + dto.getValue() + "\""
                                    + "}");
                            entity.setUserData("{"
                                    + "\"userId\": \"" + dto.getUserId() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"content\": \"" + dto.getValue() + "\""
                                    + "}");
                            if (dto.getIsPublic() == 1) {
                                entity.setIsPublic(1);
                            } else if (dto.getIsPublic() == 0) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(dto.getUserId());
                            entity.setPin(dto.getPin());
                            entity.setPublicId(qrLink);
                            entity.setStyle(dto.getStyle());
                            entity.setTheme(dto.getTheme());
                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(dto.getUserId());
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            break;
                        case 0: // text
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(dto.getQrName());
                            entity.setDescription(dto.getQrDescription());
                            entity.setValue(dto.getValue());
                            entity.setQrType(0);
                            entity.setQrData("{"
                                    + "\"title\": \"" + dto.getQrName() + "\", "
                                    + "\"description\": \"" + dto.getQrDescription() + "\", "
                                    + "\"isPublic\": \"" + dto.getQrDescription() + "\", "
                                    + "\"style\": \"" + dto.getQrDescription() + "\", "
                                    + "\"theme\": \"" + dto.getQrDescription() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"value\": \"" + dto.getValue() + "\""
                                    + "}");
                            entity.setUserData("{"
                                    + "\"userId\": \"" + dto.getUserId() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"content\": \"" + dto.getValue() + "\""
                                    + "}");
                            if (dto.getIsPublic() == 1) {
                                entity.setIsPublic(1);
                            } else if (dto.getIsPublic() == 0) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(dto.getUserId());
                            entity.setPin(dto.getPin());
                            entity.setPublicId(qrLink);
                            entity.setStyle(dto.getStyle());
                            entity.setTheme(dto.getTheme());
                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(dto.getUserId());
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            break;
                    }
                    result = qrWalletResponseDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E148");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("qr-wallet")
    public ResponseEntity<Object> getQrWallet(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElement = 0;
            int offset = (page - 1) * size;

            List<ListQrWalletDTO> data = new ArrayList<>();
            List<IListQrWalletDTO> infos = new ArrayList<>();
            // từ folder ID sẽ có list userID sẽ lấy được user_data -> xử lý JSON là show user_data

            totalElement = qrWalletService.countQrWallet(value);
            infos = qrWalletService.getQrWallets(value, offset, size);
            data = infos.stream().map(item -> {
                ListQrWalletDTO listQrWalletDTO = new ListQrWalletDTO();
                listQrWalletDTO.setId(item.getId());
                listQrWalletDTO.setDescription(item.getDescription());
                listQrWalletDTO.setTitle(item.getTitle());
                listQrWalletDTO.setIsPublic(item.getIsPublic());
                listQrWalletDTO.setQrType(item.getQrType());
                listQrWalletDTO.setTimeCreate(item.getTimeCreate());
                listQrWalletDTO.setContent(item.getContent());
                return listQrWalletDTO;
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
            logger.error("QrWalletController: ERROR: get QrWallet: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("qr-wallet/delete-qr")
    public ResponseEntity<Object> deleteQrWallets(@RequestBody List<String> ids) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            qrWalletService.deleteQrWalletsByIds(ids);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: get QrWallet: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("qr-wallet/logo-save")
    public ResponseEntity<ResponseMessageDTO> insertImage(
            @RequestParam MultipartFile image) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuid = UUID.randomUUID();
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            ImageEntity entity = new ImageEntity(uuid.toString(), fileName, image.getBytes());
            // Amazon S3
            Thread thread = new Thread(() -> {

            });
            thread.start();
            amazonS3Service.uploadFile(uuid.toString(), image);
            // update field image trong qr_wallet

            result = new ResponseMessageDTO("SUCCESS", uuid.toString());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Error at insertImage: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("qr-wallet/generate-qr")
    public ResponseEntity<Object> createQRFinal(
            @Valid
            @RequestParam int isPublic,
            @RequestParam int type,
            @RequestBody String json
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            switch (type) {
                case 0://link
                case 1: //text
                    QrCreateRequestDTO qrCreateRequestDTO = objectMapper.readValue(json, QrCreateRequestDTO.class);
                    return createQrLink(type, qrCreateRequestDTO);
                case 2://vcard
                    VCardInputExtendDTO vCardInputDTO = objectMapper.readValue(json, VCardInputExtendDTO.class);
                    return createQrVcard(isPublic, type, vCardInputDTO);
                case 3://viet qr
                    VietQRCreateUnauthenticatedExtendDTO value = objectMapper.readValue(json, VietQRCreateUnauthenticatedExtendDTO.class);
                    return generateQRUnauthenticated(isPublic, type, value);
            }

        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: get QrWallet: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("Lỗi: " + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

//        @GetMapping("/qr-wallets/publics")
//    public ResponseEntity<Object> getAllPublicQrWallets() {
//        Object result = null;
//        HttpStatus httpStatus = null;
//        try {
//            List<IQrWalletDTO> qrWallets = qrWalletService.getAllPublicQrWallets();
//            result = qrWallets;
//            httpStatus = HttpStatus.OK;
//        } catch (Exception e) {
//            logger.error("getAllPublicQrWallet Error at " + e.getMessage() + System.currentTimeMillis());
//            result = new ResponseMessageDTO("FAILED", "E05");
//            httpStatus = HttpStatus.BAD_REQUEST;
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }

    //    @GetMapping("/qr-wallets/public")
//    public ResponseEntity<Object> getAllPublicQrWallets(@RequestParam String userId) {
//        Object result = null;
//        HttpStatus httpStatus = null;
//        try {
//            List<IQrWalletDTO> qrWallets = qrWalletService. getAllPublicQrWallets(userId);
//            result = qrWallets;
//            httpStatus = HttpStatus.OK;
//        } catch (Exception e) {
//            logger.error("getAllPublicQrWallet Error at " + e.getMessage() + System.currentTimeMillis());
//            result = new ResponseMessageDTO("FAILED", "E05");
//            httpStatus = HttpStatus.BAD_REQUEST;
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }
//    @GetMapping("/qr-wallets/public")
//    public ResponseEntity<Object> getAllPublicQrWallets(
//            @RequestParam String userId,
//            @RequestParam int page,
//            @RequestParam int size) {
//        Object result = null;
//        HttpStatus httpStatus = null;
//        try {
//            int totalElements = qrWalletService.countPublicQrWallets();
//            int offset = (page - 1) * size;
//            List<IQrWalletDTO> qrWallets = qrWalletService.getAllPublicQrWallets(userId, offset, size);
//
//            PageDTO pageDTO = new PageDTO();
//            pageDTO.setSize(size);
//            pageDTO.setPage(page);
//            pageDTO.setTotalElement(totalElements);
//            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));
//
//            PageResDTO pageResDTO = new PageResDTO();
//            pageResDTO.setMetadata(pageDTO);
//            pageResDTO.setData(qrWallets);
//
//            result = pageResDTO;
//            httpStatus = HttpStatus.OK;
//        } catch (Exception e) {
//            logger.error("getAllPublicQrWallet Error at " + e.getMessage() + System.currentTimeMillis());
//            result = new ResponseMessageDTO("FAILED", "E05");
//            httpStatus = HttpStatus.BAD_REQUEST;
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }


    @GetMapping("/qr-wallets/public")
    public ResponseEntity<Object> getAllPublicQrWallets(
            @RequestParam String userId,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("Fetching QR Wallets for userId: " + userId + ", page: " + page + ", size: " + size);
            int totalElements = qrWalletService.countPublicQrWallets();
            int offset = (page - 1) * size;
            List<IQrWalletDTO> qrWallets = qrWalletService.getAllPublicQrWallets(userId, offset, size);
            logger.info("QR Wallets Retrieved: " + qrWallets);

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));

            PageResDTO pageResDTO = new PageResDTO();
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(qrWallets);

            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getAllPublicQrWallet Error at " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


//    @GetMapping("/qr-wallets/public/details/{id}")
//    public ResponseEntity<Object> getQrWalletDetails(@PathVariable String id) {
//        Object result = null;
//        HttpStatus httpStatus = null;
//        try {
//            IQrWalletDTO qrWalletDTO = qrWalletService.getQrWalletDetailsById(id);
//            if (qrWalletDTO == null) {
//                result = new ResponseMessageDTO("FAILED", "QrWallet not found");
//                httpStatus = HttpStatus.BAD_REQUEST;
//            } else {
//                List<QrCommentDTO> comments = qrCommentService.findCommentsByQrWalletId(id);
//
//                QrWalletDetailDTO detailDTO = new QrWalletDetailDTO();
//                detailDTO.setId(qrWalletDTO.getId());
//                detailDTO.setTitle(qrWalletDTO.getTitle());
//                detailDTO.setDescription(qrWalletDTO.getDescription());
//                detailDTO.setValue(qrWalletDTO.getValue());
//                detailDTO.setQrType(qrWalletDTO.getQrType());
//                detailDTO.setTimeCreated(qrWalletDTO.getTimeCreated());
//                detailDTO.setUserId(qrWalletDTO.getUserId());
//                detailDTO.setLikeCount(qrWalletDTO.getLikeCount());
//                detailDTO.setCommentCount(qrWalletDTO.getCommentCount());
//                detailDTO.setComments(comments);
//
//                result = detailDTO;
//                httpStatus = HttpStatus.OK;
//            }
//        } catch (Exception e) {
//
//            logger.error("getQrWalletDetails: ERROR: " + e.getMessage() + System.currentTimeMillis());
//            result = new ResponseMessageDTO("FAILED", "E05");
//            httpStatus = HttpStatus.BAD_REQUEST;
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }


    @GetMapping("/qr-wallets/public/details/{id}")
    public ResponseEntity<Object> getQrWalletDetails(@PathVariable String id,
                                                     @RequestParam int page,
                                                     @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            IQrWalletDTO qrWalletDTO = qrWalletService.getQrWalletDetailsById(id);
            if (qrWalletDTO == null) {
                result = new ResponseMessageDTO("FAILED", "QrWallet not found");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                int totalCommentElements = qrWalletService.countCommentsByQrWalletId(id);
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<QrCommentDTO> commentsPage = qrWalletService.findCommentsByQrWalletId(id, pageable);

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
            logger.error("getQrWalletDetails: ERROR: " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


}
