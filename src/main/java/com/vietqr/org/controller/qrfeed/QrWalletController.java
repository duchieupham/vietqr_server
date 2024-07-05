package com.vietqr.org.controller.qrfeed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.ImageEntity;
import com.vietqr.org.entity.qrfeed.QrUserEntity;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.qrfeed.QRCodeService;
import com.vietqr.org.service.qrfeed.QrCommentService;
import com.vietqr.org.service.qrfeed.QrUserService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import com.vietqr.org.service.vnpt.services.Interfaces;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
@Validated
public class QrWalletController {
    private static final Logger logger = Logger.getLogger(QrWalletController.class);

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    FileAttachService imageInvoiceService;

    @Autowired
    private QRCodeService qrCodeService;

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
            String isPublic,
            String type,
            VietQRCreateUnauthenticatedExtendDTO dto,
            MultipartFile file) {
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
                    vietQRDTO.setImgId(""); // get image logo
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
                    entity.setTitle(dto.getQrName());
                    entity.setDescription(dto.getQrDescription());
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
                    if (Integer.parseInt(isPublic) == 1) {
                        entity.setIsPublic(1);
                    } else if (Integer.parseInt(isPublic) == 0) {
                        entity.setIsPublic(0);
                    }
                    entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                    entity.setUserId(dto.getUserId());
                    entity.setPin("");
                    entity.setPublicId("");
                    entity.setTheme(Integer.parseInt(dto.getTheme()));
                    entity.setStyle(Integer.parseInt(dto.getStyle()));

                    UUID uuid = UUID.randomUUID();
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
                    imageService.insertImage(IE);
                    entity.setFileAttachmentId(uuid.toString());

                    qrWalletService.insertQrWallet(entity);

                    //add qr vào qr_user (ch implements)
                    QrUserEntity qrUserEntity = new QrUserEntity();
                    UUID idQrUser = UUID.randomUUID();
                    qrUserEntity.setId(idQrUser.toString());
                    qrUserEntity.setQrWalletId(idQrWallet.toString());
                    qrUserEntity.setUserId(dto.getUserId());
                    qrUserEntity.setRole("ADMIN");
                    qrUserService.insertQrUser(qrUserEntity);

//                    result = vietQRDTO;
                    result = new ResponseMessageDTO("SUCCESS", "");
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

    public ResponseEntity<Object> generateQRUnauthenticatedWithoutLogo(
            String isPublic,
            String type,
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
                    vietQRDTO.setImgId(""); // get image logo
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
                    entity.setTitle(dto.getQrName());
                    entity.setDescription(dto.getQrDescription());
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
                    IUserInfoDTO userInfoDTO = accountLoginService.getUserInfoDetailsByUserId(dto.getUserId());
                    entity.setUserData("{"
                            + "\"userId\": \"" + dto.getUserId() + "\", "
                            + "\"fullName\": \"" + userInfoDTO.getFullName() + "\", "
                            + "\"phoneNo\": \"" + userInfoDTO.getPhoneNo() + "\", "
                            + "\"email\": \"" + userInfoDTO.getEmail() + "\", "
                            + "\"bankAccount\": \"" + dto.getBankAccount() + "\", "
                            + "\"userBankName\": \"" + dto.getUserBankName() + "\", "
                            + "\"qrType\": \"" + type + "\", "
                            + "\"bankCode\": \"" + dto.getBankCode() + "\""
                            + "}");
                    if (Integer.parseInt(isPublic) == 1) {
                        entity.setIsPublic(1);
                    } else if (Integer.parseInt(isPublic) == 0) {
                        entity.setIsPublic(0);
                    }
                    entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                    entity.setUserId(dto.getUserId());
                    entity.setPin("");
                    entity.setPublicId("");
                    entity.setTheme(Integer.parseInt(dto.getTheme()));
                    entity.setStyle(Integer.parseInt(dto.getStyle()));

                    entity.setFileAttachmentId("");
                    qrWalletService.insertQrWallet(entity);

                    //add qr vào qr_user (ch implements)
                    QrUserEntity qrUserEntity = new QrUserEntity();
                    UUID idQrUser = UUID.randomUUID();
                    qrUserEntity.setId(idQrUser.toString());
                    qrUserEntity.setQrWalletId(idQrWallet.toString());
                    qrUserEntity.setUserId(dto.getUserId());
                    qrUserEntity.setRole("ADMIN");
                    qrUserService.insertQrUser(qrUserEntity);

//                    result = vietQRDTO;
                    result = new ResponseMessageDTO("SUCCESS", "");
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
    ResponseEntity<Object> createQrVcard(String isPublic, String type, VCardInputExtendDTO dto, MultipartFile file) {
        Object result = null;
        QrVcardRequestDTO qrVcardRequestDTO = null;
        HttpStatus httpStatus = null;
        try {
            if (dto.isNull()) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                String qr = VCardUtil.getVcardQR(dto);
                qrVcardRequestDTO = new QrVcardRequestDTO();
                qrVcardRequestDTO.setQr(qr);
                qrVcardRequestDTO.setQrName(dto.getQrName());
                qrVcardRequestDTO.setQrDescription(dto.getQrDescription());
                qrVcardRequestDTO.setFullname(dto.getFullname());
                qrVcardRequestDTO.setPhoneNo(dto.getPhoneNo());
                qrVcardRequestDTO.setEmail(dto.getEmail());
                qrVcardRequestDTO.setCompanyName(dto.getCompanyName());
                qrVcardRequestDTO.setWebsite(dto.getWebsite());
                qrVcardRequestDTO.setAddress(dto.getAddress());
                if (Integer.parseInt(isPublic) == 1) {
                    qrVcardRequestDTO.setIsPublic(1);
                } else if (Integer.parseInt(isPublic) == 0) {
                    qrVcardRequestDTO.setIsPublic(0);
                }
                qrVcardRequestDTO.setStyle(Integer.parseInt(dto.getStyle()));
                qrVcardRequestDTO.setTheme(Integer.parseInt(dto.getTheme()));

                // add data qr vào qr_wallet
                QrWalletEntity entity = new QrWalletEntity();
                LocalDateTime currentDateTime = LocalDateTime.now();
                UUID idQrWallet = UUID.randomUUID();
                entity.setId(idQrWallet.toString());
                entity.setTitle(dto.getQrName());
                entity.setDescription(dto.getQrDescription());
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
                if (Integer.parseInt(isPublic) == 1) {
                    qrVcardRequestDTO.setIsPublic(1);
                } else if (Integer.parseInt(isPublic) == 0) {
                    qrVcardRequestDTO.setIsPublic(0);
                }
                entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                entity.setUserId(dto.getUserId());
                entity.setPin("");
                entity.setPublicId("");
                entity.setStyle(Integer.parseInt(dto.getStyle()));
                entity.setTheme(Integer.parseInt(dto.getTheme()));
                entity.setIsPublic(Integer.parseInt(dto.getIsPublic()));

                // save image
                UUID uuid = UUID.randomUUID();
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
                imageService.insertImage(IE);

                entity.setFileAttachmentId(uuid.toString());
                qrWalletService.insertQrWallet(entity);

                //add qr vào qr_user (ch implements)
                QrUserEntity qrUserEntity = new QrUserEntity();
                UUID idQrUser = UUID.randomUUID();
                qrUserEntity.setId(idQrUser.toString());
                qrUserEntity.setQrWalletId(idQrWallet.toString());
                qrUserEntity.setUserId(dto.getUserId());
                qrUserEntity.setRole("ADMIN");
                qrUserService.insertQrUser(qrUserEntity);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            logger.error("generateVCard: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    ResponseEntity<Object> createQrVcardWithoutLogo(String isPublic, String type, VCardInputExtendDTO dto) {
        Object result = null;
        QrVcardRequestDTO qrVcardRequestDTO = null;
        HttpStatus httpStatus = null;
        try {
            if (dto.isNull()) {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                String qr = VCardUtil.getVcardQR(dto);
                qrVcardRequestDTO = new QrVcardRequestDTO();
                qrVcardRequestDTO.setQr(qr);
                qrVcardRequestDTO.setQrName(dto.getQrName());
                qrVcardRequestDTO.setQrDescription(dto.getQrDescription());
                qrVcardRequestDTO.setFullname(dto.getFullname());
                qrVcardRequestDTO.setPhoneNo(dto.getPhoneNo());
                qrVcardRequestDTO.setEmail(dto.getEmail());
                qrVcardRequestDTO.setCompanyName(dto.getCompanyName());
                qrVcardRequestDTO.setWebsite(dto.getWebsite());
                qrVcardRequestDTO.setAddress(dto.getAddress());
                if (Integer.parseInt(isPublic) == 1) {
                    qrVcardRequestDTO.setIsPublic(1);
                } else if (Integer.parseInt(isPublic) == 0) {
                    qrVcardRequestDTO.setIsPublic(0);
                }
                qrVcardRequestDTO.setStyle(Integer.parseInt(dto.getStyle()));
                qrVcardRequestDTO.setTheme(Integer.parseInt(dto.getTheme()));

                // add data qr vào qr_wallet
                QrWalletEntity entity = new QrWalletEntity();
                LocalDateTime currentDateTime = LocalDateTime.now();
                UUID idQrWallet = UUID.randomUUID();
                entity.setId(idQrWallet.toString());
                entity.setTitle(dto.getQrName());
                entity.setDescription(dto.getQrDescription());
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
                        + "\"qrType\": \"" + type + "\", "
                        + "\"additionalData\": \"" + dto.getAdditionalData() + "\""
                        + "}");
                if (Integer.parseInt(isPublic) == 1) {
                    qrVcardRequestDTO.setIsPublic(1);
                } else if (Integer.parseInt(isPublic) == 0) {
                    qrVcardRequestDTO.setIsPublic(0);
                }
                entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                entity.setUserId(dto.getUserId());
                entity.setPin("");
                entity.setPublicId("");
                entity.setStyle(Integer.parseInt(dto.getStyle()));
                entity.setTheme(Integer.parseInt(dto.getTheme()));
                entity.setIsPublic(Integer.parseInt(dto.getIsPublic()));

                // save image
//                UUID uuid = UUID.randomUUID();
//                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//                ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
//                imageService.insertImage(IE);
//                entity.setFileAttachmentId(uuid.toString());

                entity.setFileAttachmentId("");
                qrWalletService.insertQrWallet(entity);

                //add qr vào qr_user (ch implements)
                QrUserEntity qrUserEntity = new QrUserEntity();
                UUID idQrUser = UUID.randomUUID();
                qrUserEntity.setId(idQrUser.toString());
                qrUserEntity.setQrWalletId(idQrWallet.toString());
                qrUserEntity.setUserId(dto.getUserId());
                qrUserEntity.setRole("ADMIN");
                qrUserService.insertQrUser(qrUserEntity);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }


        } catch (Exception e) {
            logger.error("generateVCard: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //update QR VietQR
    @PutMapping("qr-wallet/update-qr-vietqr")
    public ResponseEntity<Object> updateVietQR(@RequestBody VietQRCreateUnauthenticatedExtendDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrWalletEntity qrWalletEntity = qrWalletService.getQrVietQR(dto.getId());

            if (dto.isNull()) {
                result = new ResponseMessageDTO("FAILED", "E156");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                VietQRCreateUnauthenticatedExtendDTO vietQrInput = new VietQRCreateUnauthenticatedExtendDTO();
                VietQRDTO vietQRDTO = new VietQRDTO();
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
                String qrGenerate = "";
                if (dto.getAmount().trim().isEmpty() && dto.getAmount().trim().isEmpty()) {
                    qrGenerate = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                } else {
                    qrGenerate = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                }
                vietQRDTO.setBankCode(dto.getBankCode());
                vietQRDTO.setBankName(bankTypeEntity.getBankName());
                vietQRDTO.setBankAccount(dto.getAmount());
                vietQRDTO.setUserBankName(dto.getUserBankName().toUpperCase());
                vietQRDTO.setAmount(amount);
                vietQRDTO.setContent(content);
                vietQRDTO.setQrCode(qrGenerate);
                vietQRDTO.setImgId(""); // get image logo
                vietQRDTO.setExisting(0);
                vietQRDTO.setTransactionId("");
                vietQRDTO.setTransactionRefId("");
                vietQRDTO.setTerminalCode("");
                vietQRDTO.setQrLink("");

                // add data qr vào qr_wallet
//                QrWalletEntity entity = new QrWalletEntity();
                LocalDateTime currentDateTime = LocalDateTime.now();
//                UUID idQrWallet = UUID.randomUUID();
//                qrWalletEntity.setId(qrWalletEntity.getId());
//                qrWalletEntity.setTitle(dto.getQrTitle());
//                qrWalletEntity.setDescription(dto.getQrDescription());
//                qrWalletEntity.setValue(qr);
//                qrWalletEntity.setQrType(2);

                TempVietQRDTO temp = new TempVietQRDTO();
                temp.setBankAccount(dto.getBankAccount());
                temp.setContent(dto.getContent());
                temp.setAmount(dto.getAmount());
                temp.setBankCode(dto.getBankCode());
                temp.setUserBankName(dto.getUserBankName());
                temp.setValue(qrGenerate);

                qrWalletEntity.setQrData(temp.toString());
                qrWalletEntity.setUserData("{"
                        + "\"userId\": \"" + dto.getUserId() + "\", "
                        + "\"bankAccount\": \"" + dto.getBankAccount() + "\", "
                        + "\"userBankName\": \"" + dto.getUserBankName() + "\", "
                        + "\"qrType\": \"" + 3 + "\", "
                        + "\"bankCode\": \"" + dto.getBankCode() + "\""
                        + "}");

//                if (dto.getIsPublic() == 1) {
//                    qrWalletEntity.setIsPublic(1);
//                } else if (dto.getIsPublic() == 0) {
//                    qrWalletEntity.setIsPublic(0);
//                }
//                qrWalletEntity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
//                qrWalletEntity.setUserId(dto.getUserId());
//                qrWalletEntity.setPin("");
//                qrWalletEntity.setPublicId("");
                qrWalletEntity.setStyle(Integer.parseInt(dto.getStyle()));
                qrWalletEntity.setTheme(Integer.parseInt(dto.getTheme()));

                qrWalletService.updateQrVietQR(dto.getId(), dto.getQrDescription(), Integer.parseInt(dto.getIsPublic()),
                        3, dto.getQrName(), qrGenerate, Integer.parseInt(dto.getStyle()), Integer.parseInt(dto.getTheme()));

//                qrWalletService.insertQrWallet(entity);

                //add qr vào qr_user (ch implements)
//                QrUserEntity qrUserEntity = new QrUserEntity();
//                UUID idQrUser = UUID.randomUUID();
//                qrUserEntity.setId(idQrUser.toString());
//                qrUserEntity.setQrWalletId(idQrWallet.toString());
//                qrUserEntity.setUserId(dto.getUserId());
//                qrUserEntity.setRole("ADMIN");
//                qrUserService.insertQrUser(qrUserEntity);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED: " + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PutMapping("qr-wallet/update-qr-vcard")
    public ResponseEntity<Object> updateQrVietQr(@RequestBody QrVCardUpdateDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            QrWalletEntity qrWalletEntity = qrWalletService.getQrVCardUpdate(dto.getId());

            if (dto.isNull()) {
                result = new ResponseMessageDTO("FAILED", "E156");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                String qr = VCardUtil.getVcardQR(dto);
                QrVcardRequestDTO qrVcardRequestDTO = new QrVcardRequestDTO();
                qrVcardRequestDTO.setQr(qr);
                qrVcardRequestDTO.setFullname(dto.getFullname());
                qrVcardRequestDTO.setPhoneNo(dto.getPhoneNo());
                qrVcardRequestDTO.setEmail(dto.getEmail());
                qrVcardRequestDTO.setCompanyName(dto.getCompanyName());
                qrVcardRequestDTO.setWebsite(dto.getWebsite());
                qrVcardRequestDTO.setAddress(dto.getAddress());
                if (dto.getIsPublic() == 1) {
                    qrVcardRequestDTO.setIsPublic(1);
                } else if (dto.getIsPublic() == 0) {
                    qrVcardRequestDTO.setIsPublic(0);
                }
                qrVcardRequestDTO.setStyle(dto.getStyle());
                qrVcardRequestDTO.setTheme(dto.getTheme());

                // add data qr vào qr_wallet
//                QrWalletEntity entity = new QrWalletEntity();
                LocalDateTime currentDateTime = LocalDateTime.now();
//                UUID idQrWallet = UUID.randomUUID();
//                qrWalletEntity.setId(qrWalletEntity.getId());
//                qrWalletEntity.setTitle(dto.getQrTitle());
//                qrWalletEntity.setDescription(dto.getQrDescription());
//                qrWalletEntity.setValue(qr);
//                qrWalletEntity.setQrType(2);

                TempVCardDTO temp = new TempVCardDTO();
                temp.setFullName(dto.getFullname());
                temp.setPhoneNo(dto.getPhoneNo());
                temp.setEmail(dto.getEmail());
                temp.setCompanyName(dto.getCompanyName());
                temp.setWebsite(dto.getWebsite());
                temp.setValue(qr);
                temp.setAddress(dto.getAddress());

                qrWalletEntity.setQrData(temp.toString());
                qrWalletEntity.setUserData("{"
                        + "\"userId\": \"" + dto.getUserId() + "\", "
                        + "\"fullName\": \"" + dto.getFullname() + "\", "
                        + "\"phoneNo\": \"" + dto.getPhoneNo() + "\", "
                        + "\"email\": \"" + dto.getEmail() + "\", "
                        + "\"companyName\": \"" + dto.getCompanyName() + "\", "
                        + "\"website\": \"" + dto.getWebsite() + "\", "
                        + "\"website\": \"" + dto.getWebsite() + "\", "
                        + "\"qrType\": \"" + 2 + "\", "
                        + "\"additionalData\": \"" + dto.getAddress() + "\""
                        + "}");
//                if (dto.getIsPublic() == 1) {
//                    qrWalletEntity.setIsPublic(1);
//                } else if (dto.getIsPublic() == 0) {
//                    qrWalletEntity.setIsPublic(0);
//                }
//                qrWalletEntity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                qrWalletEntity.setUserId(dto.getUserId());
//                qrWalletEntity.setPin("");
//                qrWalletEntity.setPublicId("");
                qrWalletEntity.setStyle(dto.getStyle());
                qrWalletEntity.setTheme(dto.getTheme());

                qrWalletService.updateQrVCard(dto.getId(), dto.getQrDescription(), dto.getIsPublic(), 2,
                        dto.getQrTitle(), qr, dto.getStyle(), dto.getTheme());

//                qrWalletService.insertQrWallet(entity);

                //add qr vào qr_user (ch implements)
//                QrUserEntity qrUserEntity = new QrUserEntity();
//                UUID idQrUser = UUID.randomUUID();
//                qrUserEntity.setId(idQrUser.toString());
//                qrUserEntity.setQrWalletId(idQrWallet.toString());
//                qrUserEntity.setUserId(dto.getUserId());
//                qrUserEntity.setRole("ADMIN");
//                qrUserService.insertQrUser(qrUserEntity);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED: " + e.getMessage(), "E05");
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
    public ResponseEntity<Object> createQrLink(int type, QrCreateRequestDTO dto, MultipartFile file) throws IOException {
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
                        case 0: // link
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(dto.getQrName());
                            entity.setDescription(dto.getQrDescription());
                            entity.setValue(dto.getValue());
                            entity.setQrType(type);
                            entity.setQrData("{"
                                    + "\"title\": \"" + dto.getQrName() + "\", "
                                    + "\"description\": \"" + dto.getQrDescription() + "\", "
                                    + "\"isPublic\": \"" + dto.getQrDescription() + "\", "
                                    + "\"style\": \"" + dto.getQrDescription() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"theme\": \"" + dto.getQrDescription() + "\", "
                                    + "\"value\": \"" + dto.getValue() + "\""
                                    + "}");
                            IUserInfoDTO userDataInfo = accountLoginService.getUserInfoDetailsByUserId(dto.getUserId());
                            entity.setUserData("{"
                                    + "\"userId\": \"" + dto.getUserId() + "\", "
                                    + "\"fullName\": \"" + userDataInfo.getFullName() + "\", "
                                    + "\"email\": \"" + userDataInfo.getEmail() + "\", "
                                    + "\"phoneNo\": \"" + userDataInfo.getPhoneNo() + "\", "
                                    + "\"address\": \"" + userDataInfo.getAddress() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"content\": \"" + dto.getValue() + "\""
                                    + "}");
                            if (dto.getIsPublic().equals("1")) {
                                entity.setIsPublic(1);
                            } else if (dto.getIsPublic().equals("0")) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(dto.getUserId());
                            entity.setPin(dto.getPin());
                            entity.setPublicId(qrLink);
                            entity.setStyle(Integer.parseInt(dto.getStyle()));
                            entity.setTheme(Integer.parseInt(dto.getTheme()));

                            // save image
                            UUID uuid = UUID.randomUUID();
                            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                            ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
                            imageService.insertImage(IE);
                            entity.setFileAttachmentId(uuid.toString());
                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(dto.getUserId());
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            // update file log to qr
                            qrWalletService.updateFileQrById(uuid.toString(), idQrWallet.toString());

                            break;
                        case 1: // text
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(dto.getQrName());
                            entity.setDescription(dto.getQrDescription());
                            entity.setValue(dto.getValue());
                            entity.setQrType(type);
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
                            if (dto.getIsPublic().equals("1")) {
                                entity.setIsPublic(1);
                            } else if (dto.getIsPublic().equals("0")) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(dto.getUserId());
                            entity.setPin(dto.getPin());
                            entity.setPublicId(qrLink);
                            entity.setStyle(Integer.parseInt(dto.getStyle()));
                            entity.setTheme(Integer.parseInt(dto.getTheme()));

                            // save image
                            UUID ids = UUID.randomUUID();
                            imageInvoiceService.saveFile(file, ids.toString());
                            entity.setFileAttachmentId(ids.toString());
                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(dto.getUserId());
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            // update file log to qr
                            qrWalletService.updateFileQrById(ids.toString(), idQrWallet.toString());
                            break;
                    }
//                    result = qrWalletResponseDTO;
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E148");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    public ResponseEntity<Object> createQrLinkWithoutLogo(int type, QrCreateRequestDTO dto) throws IOException {
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
                        case 0: // link
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(dto.getQrName());
                            entity.setDescription(dto.getQrDescription());
                            entity.setValue(dto.getValue());
                            entity.setQrType(type);
                            entity.setQrData("{"
                                    + "\"title\": \"" + dto.getQrName() + "\", "
                                    + "\"description\": \"" + dto.getQrDescription() + "\", "
                                    + "\"isPublic\": \"" + dto.getQrDescription() + "\", "
                                    + "\"style\": \"" + dto.getQrDescription() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"theme\": \"" + dto.getQrDescription() + "\", "
                                    + "\"value\": \"" + dto.getValue() + "\""
                                    + "}");
                            IUserInfoDTO userDataInfo = accountLoginService.getUserInfoDetailsByUserId(dto.getUserId());
                            entity.setUserData("{"
                                    + "\"userId\": \"" + dto.getUserId() + "\", "
                                    + "\"fullName\": \"" + userDataInfo.getFullName() + "\", "
                                    + "\"email\": \"" + userDataInfo.getEmail() + "\", "
                                    + "\"phoneNo\": \"" + userDataInfo.getPhoneNo() + "\", "
                                    + "\"address\": \"" + userDataInfo.getAddress() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"content\": \"" + dto.getValue() + "\""
                                    + "}");
                            if (dto.getIsPublic().equals("1")) {
                                entity.setIsPublic(1);
                            } else if (dto.getIsPublic().equals("0")) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(dto.getUserId());
                            entity.setPin(dto.getPin());
                            entity.setPublicId(qrLink);
                            entity.setStyle(Integer.parseInt(dto.getStyle()));
                            entity.setTheme(Integer.parseInt(dto.getTheme()));

                            // save image
//                            UUID uuid = UUID.randomUUID();
//                            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//                            ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
//                            imageService.insertImage(IE);
//                            entity.setFileAttachmentId(uuid.toString());
                            entity.setFileAttachmentId("");
                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(dto.getUserId());
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            // update file log to qr
                            //qrWalletService.updateFileQrById(uuid.toString(), idQrWallet.toString());

                            break;
                        case 1: // text
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(dto.getQrName());
                            entity.setDescription(dto.getQrDescription());
                            entity.setValue(dto.getValue());
                            entity.setQrType(type);
                            entity.setQrData("{"
                                    + "\"title\": \"" + dto.getQrName() + "\", "
                                    + "\"description\": \"" + dto.getQrDescription() + "\", "
                                    + "\"isPublic\": \"" + dto.getQrDescription() + "\", "
                                    + "\"style\": \"" + dto.getQrDescription() + "\", "
                                    + "\"theme\": \"" + dto.getQrDescription() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"value\": \"" + dto.getValue() + "\""
                                    + "}");

                            IUserInfoDTO userDataInfos = accountLoginService.getUserInfoByUserId(dto.getUserId());

                            entity.setUserData("{"
                                    + "\"userId\": \"" + dto.getUserId() + "\", "
                                    + "\"fullName\": \"" + userDataInfos.getFullName() + "\", "
                                    + "\"email\": \"" + userDataInfos.getEmail() + "\", "
                                    + "\"phoneNo\": \"" + userDataInfos.getPhoneNo() + "\", "
                                    + "\"address\": \"" + userDataInfos.getAddress() + "\", "
                                    + "\"qrType\": \"" + type + "\", "
                                    + "\"content\": \"" + dto.getValue() + "\""
                                    + "}");
                            if (dto.getIsPublic().equals("1")) {
                                entity.setIsPublic(1);
                            } else if (dto.getIsPublic().equals("0")) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(dto.getUserId());
                            entity.setPin(dto.getPin());
                            entity.setPublicId(qrLink);
                            entity.setStyle(Integer.parseInt(dto.getStyle()));
                            entity.setTheme(Integer.parseInt(dto.getTheme()));

                            // save image
//                            UUID ids = UUID.randomUUID();
//                            imageInvoiceService.saveFile(file, ids.toString());
//                            entity.setFileAttachmentId(ids.toString());
                            entity.setFileAttachmentId("");
                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(dto.getUserId());
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            // update file log to qr
//                            qrWalletService.updateFileQrById(ids.toString(), idQrWallet.toString());
                            break;
                    }
//                    result = qrWalletResponseDTO;
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E148");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
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

            switch (type) {
                case 0: // ds qr cá nhân
                    totalElement = qrWalletService.countQrWallet(value);
                    infos = qrWalletService.getQrWallets(value, offset, size);
                    break;
                case 1: // ds qr cộng đồng
                    totalElement = qrWalletService.countQrWalletPublic(value);
                    infos = qrWalletService.getQrWalletPublic(value, offset, size);
                    break;

            }
//            totalElement = qrWalletService.countQrWallet(value);
//            infos = qrWalletService.getQrWallets(value, offset, size);

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

    @PostMapping("qr-wallet/image-qr-logo")
    public ResponseEntity<Object> uploadLogoQr(
            @RequestPart String qrId,
            @RequestPart("file") MultipartFile file) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            UUID id = UUID.randomUUID();
            imageInvoiceService.saveFile(file, id.toString());

            // update file log to qr
            qrWalletService.updateFileQrById(id.toString(), qrId);

            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("qr-wallet/logo-without")
    public ResponseEntity<Object> pushLogoWithout(@RequestPart(required = false) MultipartFile file) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (file != null && !file.isEmpty()) {
                // save image
                UUID uuid = UUID.randomUUID();
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
                imageService.insertImage(IE);

                // update image into QR
                //qrWalletService.updateLogoQrWallet(uuid.toString());

                result = new ResponseMessageDTO("SUCCESS", uuid.toString());
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("SUCCESS", "Not find image");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: get QrWallet: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping(value = "qr-wallet/generate-qr-without-data")
    public ResponseEntity<Object> createQRWithoutLogo(
            @RequestParam("type") String type,
            @RequestBody String json
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1);
            }
            switch (Integer.parseInt(type)) {
                case 0: //link
                case 1: //text
                    QrCreateRequestDTO qrCreateRequestDTO = objectMapper.readValue(json, QrCreateRequestDTO.class);
                    return createQrLinkWithoutLogo(Integer.parseInt(type), qrCreateRequestDTO);
                case 2: //vcard
                    VCardInputExtendDTO vCardInputDTO = objectMapper.readValue(json, VCardInputExtendDTO.class);
                    return createQrVcardWithoutLogo(vCardInputDTO.getIsPublic(), type, vCardInputDTO);
                case 3: //viet qr
                    VietQRCreateUnauthenticatedExtendDTO value = objectMapper.readValue(json, VietQRCreateUnauthenticatedExtendDTO.class);
                    return generateQRUnauthenticatedWithoutLogo(value.getIsPublic(), type, value);
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: get QrWallet: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("Lỗi: " + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping(value = "qr-wallet/generate-qr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> createQRFinal(
            @RequestParam(required = false) String typeDto, // 0: Link, 1: Text, 2: VCard, 3: VietQR
            @RequestPart(required = false) MultipartFile file,
            @RequestParam(required = false) String userIdDTO,
            @RequestParam(required = false) String qrNameDTO,
            @RequestParam(required = false) String qrDescriptionDTO,
            @RequestParam(required = false) String valueDTO, // Link or Text
            @RequestParam(required = false) String pinDTO,
            @RequestParam(required = false) String fullNameDTO, // VCard
            @RequestParam(required = false) String phoneNoDTO,
            @RequestParam(required = false) String emailDTO,
            @RequestParam(required = false) String companyNameDTO,
            @RequestParam(required = false) String websiteDTO,
            @RequestParam(required = false) String addressDTO,
            @RequestParam(required = false) String additionalDataDTO,
            @RequestParam(required = false) String bankAccountDTO, // VietQR
            @RequestParam(required = false) String bankCodeDTO,
            @RequestParam(required = false) String userBankNameDTO,
            @RequestParam(required = false) String amountDTO,
            @RequestParam(required = false) String contentDTO,
            @RequestParam(required = false) String isPublicDTO, // support
            @RequestParam(required = false) String styleDTO,
            @RequestParam(required = false) String themeDTO
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        CreateQrRequestDTO dto = new CreateQrRequestDTO();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (typeDto == null || typeDto.isEmpty()) {
                throw new IllegalArgumentException("Type is required");
            }

            int type = Integer.parseInt(typeDto);

            switch (type) {
                case 0: //link
                case 1: //text
//                    QrCreateRequestDTO qrCreateRequestDTO = objectMapper.readValue(json, QrCreateRequestDTO.class);
                    QrCreateRequestDTO dto01 = new QrCreateRequestDTO();
                    try {
                        // generate QR Code
                        QrWalletResponseDTO qrWalletResponseDTO = new QrWalletResponseDTO();
                        qrWalletResponseDTO.setQrName(qrNameDTO);
                        qrWalletResponseDTO.setValue(valueDTO); // cho hiển thị ra chuỗi giá trị
                        qrWalletResponseDTO.setQrContent(qrDescriptionDTO);
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
                        if (Integer.parseInt(typeDto) == 0) {
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(qrNameDTO);
                            entity.setDescription(qrDescriptionDTO);
                            entity.setValue(valueDTO);
                            entity.setQrType(Integer.parseInt(typeDto));
                            entity.setQrData("{"
                                    + "\"title\": \"" + qrNameDTO + "\", "
                                    + "\"description\": \"" + qrDescriptionDTO + "\", "
                                    + "\"isPublic\": \"" + isPublicDTO + "\", "
                                    + "\"style\": \"" + styleDTO + "\", "
                                    + "\"qrType\": \"" + typeDto + "\", "
                                    + "\"theme\": \"" + themeDTO + "\", "
                                    + "\"value\": \"" + valueDTO + "\""
                                    + "}");

                            IUserInfoDTO userDataInfo = accountLoginService.getUserInfoDetailsByUserId(userIdDTO);
                            entity.setUserData("{"
                                    + "\"userId\": \"" + userIdDTO + "\", "
                                    + "\"fullName\": \"" + userDataInfo.getFullName() + "\", "
                                    + "\"email\": \"" + userDataInfo.getEmail() + "\", "
                                    + "\"phoneNo\": \"" + userDataInfo.getPhoneNo() + "\", "
                                    + "\"address\": \"" + userDataInfo.getAddress() + "\", "
                                    + "\"qrType\": \"" + typeDto + "\", "
                                    + "\"content\": \"" + valueDTO + "\""
                                    + "}");
                            if (isPublicDTO.equals("1")) {
                                entity.setIsPublic(1);
                            } else if (isPublicDTO.equals("0")) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(userIdDTO);
                            entity.setPin(pinDTO);
                            entity.setPublicId(qrLink);
                            entity.setStyle(Integer.parseInt(styleDTO));
                            entity.setTheme(Integer.parseInt(themeDTO));

                            // save image
                            UUID uuid = UUID.randomUUID();
                            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                            if (!file.getOriginalFilename().isEmpty()) {
                                ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
                                imageService.insertImage(IE);
                                entity.setFileAttachmentId(uuid.toString());
                            } else {
                                entity.setFileAttachmentId("");
                            }
                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(userIdDTO);
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            // update file log to qr
                            qrWalletService.updateFileQrById(uuid.toString(), idQrWallet.toString());
                        } else if (Integer.parseInt(typeDto) == 1) {
                            entity.setId(idQrWallet.toString());
                            entity.setTitle(qrNameDTO);
                            entity.setDescription(qrDescriptionDTO);
                            entity.setValue(valueDTO);
                            entity.setQrType(Integer.parseInt(typeDto));
                            entity.setQrData("{"
                                    + "\"title\": \"" + qrNameDTO + "\", "
                                    + "\"description\": \"" + qrDescriptionDTO + "\", "
                                    + "\"isPublic\": \"" + isPublicDTO + "\", "
                                    + "\"style\": \"" + styleDTO + "\", "
                                    + "\"qrType\": \"" + typeDto + "\", "
                                    + "\"theme\": \"" + themeDTO + "\", "
                                    + "\"value\": \"" + valueDTO + "\""
                                    + "}");

                            IUserInfoDTO userDataInfos = accountLoginService.getUserInfoDetailsByUserId(userIdDTO);
                            entity.setUserData("{"
                                    + "\"userId\": \"" + userIdDTO + "\", "
                                    + "\"fullName\": \"" + userDataInfos.getFullName() + "\", "
                                    + "\"email\": \"" + userDataInfos.getEmail() + "\", "
                                    + "\"phoneNo\": \"" + userDataInfos.getPhoneNo() + "\", "
                                    + "\"address\": \"" + userDataInfos.getAddress() + "\", "
                                    + "\"qrType\": \"" + typeDto + "\", "
                                    + "\"content\": \"" + valueDTO + "\""
                                    + "}");
                            if (isPublicDTO.equals("1")) {
                                entity.setIsPublic(1);
                            } else if (isPublicDTO.equals("0")) {
                                entity.setIsPublic(0);
                            }
                            entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                            entity.setUserId(userIdDTO);
                            entity.setPin(pinDTO);
                            entity.setPublicId(qrLink);
                            entity.setStyle(Integer.parseInt(styleDTO));
                            entity.setTheme(Integer.parseInt(themeDTO));

                            // save image
                            UUID ids = UUID.randomUUID();
                            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                            if (!file.getOriginalFilename().isEmpty()) {
                                ImageEntity IE = new ImageEntity(ids.toString(), fileName, file.getBytes());
                                imageService.insertImage(IE);
                                entity.setFileAttachmentId(ids.toString());
                            } else {
                                entity.setFileAttachmentId("");
                            }

                            qrWalletService.insertQrWallet(entity);

                            //add qr vào qr_user (ch implements)
                            qrUserEntity.setId(idQrUser.toString());
                            qrUserEntity.setQrWalletId(idQrWallet.toString());
                            qrUserEntity.setUserId(userIdDTO);
                            qrUserEntity.setRole("ADMIN");
                            qrUserService.insertQrUser(qrUserEntity);

                            // update file log to qr
                            qrWalletService.updateFileQrById(ids.toString(), idQrWallet.toString());
                        }
                        // result = qrWalletResponseDTO;
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } catch (Exception e) {
                        logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                                + " at: " + System.currentTimeMillis());
                        result = new ResponseMessageDTO("FAILED" + e.getMessage(), "E05");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                    break;
                case 2: //vcard
//                    VCardInputExtendDTO vCardInputDTO = objectMapper.readValue(json, VCardInputExtendDTO.class);
                    VCardInputExtendDTO input = new VCardInputExtendDTO();
                    QrVcardRequestDTO qrVcardRequestDTO = new QrVcardRequestDTO();
                    String qr = VCardUtil.getVcardQR(input);
                    qrVcardRequestDTO = new QrVcardRequestDTO();
                    qrVcardRequestDTO.setQr(qr);
                    qrVcardRequestDTO.setQrName(qrNameDTO);
                    qrVcardRequestDTO.setQrDescription(qrDescriptionDTO);
                    qrVcardRequestDTO.setFullname(fullNameDTO);
                    qrVcardRequestDTO.setPhoneNo(phoneNoDTO);
                    qrVcardRequestDTO.setEmail(emailDTO);
                    qrVcardRequestDTO.setCompanyName(companyNameDTO);
                    qrVcardRequestDTO.setWebsite(websiteDTO);
                    qrVcardRequestDTO.setAddress(addressDTO);
                    if (Integer.parseInt(isPublicDTO) == 1) {
                        qrVcardRequestDTO.setIsPublic(1);
                    } else if (Integer.parseInt(isPublicDTO) == 0) {
                        qrVcardRequestDTO.setIsPublic(0);
                    }
                    qrVcardRequestDTO.setStyle(Integer.parseInt(styleDTO));
                    qrVcardRequestDTO.setTheme(Integer.parseInt(themeDTO));

                    // add data qr vào qr_wallet
                    QrWalletEntity entity = new QrWalletEntity();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    UUID idQrWallet = UUID.randomUUID();
                    entity.setId(idQrWallet.toString());
                    entity.setTitle(qrNameDTO);
                    entity.setDescription(qrDescriptionDTO);
                    entity.setValue(qr);
                    entity.setQrType(2);

                    TempVCardDTO temp = new TempVCardDTO();
                    temp.setFullName(fullNameDTO);
                    temp.setPhoneNo(phoneNoDTO);
                    temp.setEmail(emailDTO);
                    temp.setCompanyName(companyNameDTO);
                    temp.setWebsite(websiteDTO);
                    temp.setValue(qr);
                    temp.setAddress(addressDTO);

                    entity.setQrData(temp.toString());
                    entity.setUserData("{"
                            + "\"userId\": \"" + userIdDTO + "\", "
                            + "\"fullName\": \"" + fullNameDTO + "\", "
                            + "\"phoneNo\": \"" + phoneNoDTO + "\", "
                            + "\"email\": \"" + emailDTO + "\", "
                            + "\"companyName\": \"" + companyNameDTO + "\", "
                            + "\"website\": \"" + websiteDTO + "\", "
                            + "\"qrType\": \"" + typeDto + "\", "
                            + "\"additionalData\": \"" + addressDTO + "\""
                            + "}");
                    if (Integer.parseInt(isPublicDTO) == 1) {
                        qrVcardRequestDTO.setIsPublic(1);
                    } else if (Integer.parseInt(isPublicDTO) == 0) {
                        qrVcardRequestDTO.setIsPublic(0);
                    }
                    entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                    entity.setUserId(userIdDTO);
                    entity.setPin("");
                    entity.setPublicId("");
                    entity.setStyle(Integer.parseInt(styleDTO));
                    entity.setTheme(Integer.parseInt(themeDTO));
                    entity.setIsPublic(Integer.parseInt(isPublicDTO));

                    // save image
                    UUID uuid = UUID.randomUUID();
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    if (!file.getOriginalFilename().isEmpty()) {
                        ImageEntity IE = new ImageEntity(uuid.toString(), fileName, file.getBytes());
                        imageService.insertImage(IE);
                        entity.setFileAttachmentId(uuid.toString());
                    } else {
                        entity.setFileAttachmentId("");
                    }

                    qrWalletService.insertQrWallet(entity);

                    //add qr vào qr_user (ch implements)
                    QrUserEntity qrUserEntity = new QrUserEntity();
                    UUID idQrUser = UUID.randomUUID();
                    qrUserEntity.setId(idQrUser.toString());
                    qrUserEntity.setQrWalletId(idQrWallet.toString());
                    qrUserEntity.setUserId(userIdDTO);
                    qrUserEntity.setRole("ADMIN");
                    qrUserService.insertQrUser(qrUserEntity);

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                    break;
                case 3: //viet qr
                    String bankTypeId = bankTypeService.getBankTypeIdByBankCode(bankCodeDTO);
                    VietQRCreateUnauthenticatedExtendDTO vietQrInput = new VietQRCreateUnauthenticatedExtendDTO();
                    VietQRDTO vietQRDTO = new VietQRDTO();
                    String caiValue = caiBankService.getCaiValue(bankTypeId);
                    BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(bankTypeId);
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(caiValue);
                    vietQRGenerateDTO.setBankAccount(bankAccountDTO);
                    String amount = "";
                    String content = "";
                    if (amountDTO != null && !amountDTO.trim().isEmpty()) {
                        amount = amountDTO;
                    } else if (amountDTO != null && amountDTO.trim().isEmpty()) {
                        amount = "0";
                    }
                    if (contentDTO != null && !contentDTO.trim().isEmpty()) {
                        content = contentDTO;
                    }
                    vietQRGenerateDTO.setAmount(amount);
                    vietQRGenerateDTO.setContent(content);
                    String qrGenerate = "";
                    if (amountDTO.trim().isEmpty() && contentDTO.trim().isEmpty()) {
                        qrGenerate = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                    } else {
                        qrGenerate = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    }
                    vietQRDTO.setBankCode(bankCodeDTO);
                    vietQRDTO.setBankName(bankTypeEntity.getBankName());
                    vietQRDTO.setBankAccount(amountDTO);
                    vietQRDTO.setUserBankName(userBankNameDTO.toUpperCase());
                    vietQRDTO.setAmount(amount);
                    vietQRDTO.setContent(content);
                    vietQRDTO.setQrCode(qrGenerate);
                    vietQRDTO.setImgId(""); // get image logo
                    vietQRDTO.setExisting(0);
                    vietQRDTO.setTransactionId("");
                    vietQRDTO.setTransactionRefId("");
                    vietQRDTO.setTerminalCode("");
                    vietQRDTO.setQrLink("");

                    // add data qr vào qr_wallet
                    QrWalletEntity entity1 = new QrWalletEntity();
                    LocalDateTime currentDateTimes = LocalDateTime.now();
                    UUID idQr3 = UUID.randomUUID();
                    entity1.setId(idQr3.toString());
                    entity1.setTitle(qrNameDTO);
                    entity1.setDescription(qrDescriptionDTO);
                    entity1.setValue(qrGenerate);
                    entity1.setQrType(3);

                    TempVietQRDTO tempVietQRDTO = new TempVietQRDTO();
                    tempVietQRDTO.setBankAccount(bankAccountDTO);
                    tempVietQRDTO.setUserBankName(userBankNameDTO);
                    tempVietQRDTO.setBankCode(bankCodeDTO);
                    tempVietQRDTO.setAmount(amountDTO);
                    tempVietQRDTO.setContent(contentDTO);
                    tempVietQRDTO.setValue(qrGenerate);
                    entity1.setQrData(tempVietQRDTO.toString());

                    entity1.setUserData("{"
                            + "\"userId\": \"" + userIdDTO + "\", "
                            + "\"bankAccount\": \"" + bankAccountDTO + "\", "
                            + "\"userBankName\": \"" + userBankNameDTO + "\", "
                            + "\"qrType\": \"" + typeDto + "\", "
                            + "\"bankCode\": \"" + bankCodeDTO + "\""
                            + "}");
                    if (Integer.parseInt(isPublicDTO) == 1) {
                        entity1.setIsPublic(1);
                    } else if (Integer.parseInt(isPublicDTO) == 0) {
                        entity1.setIsPublic(0);
                    }
                    entity1.setTimeCreated(currentDateTimes.toEpochSecond(ZoneOffset.UTC));
                    entity1.setUserId(userIdDTO);
                    entity1.setPin("");
                    entity1.setPublicId("");
                    entity1.setTheme(Integer.parseInt(themeDTO));
                    entity1.setStyle(Integer.parseInt(styleDTO));

                    if (!file.getOriginalFilename().isEmpty()) {
                        UUID ids = UUID.randomUUID();
                        String files = StringUtils.cleanPath(file.getOriginalFilename());
                        ImageEntity ies = new ImageEntity(ids.toString(), files, file.getBytes());
                        imageService.insertImage(ies);
                        entity1.setFileAttachmentId(ids.toString());
                    } else {
                        entity1.setFileAttachmentId("");
                    }

                    qrWalletService.insertQrWallet(entity1);

                    //add qr vào qr_user (ch implements)
                    QrUserEntity qrUserEntity1 = new QrUserEntity();
                    UUID idgen = UUID.randomUUID();
                    qrUserEntity1.setId(idgen.toString());
                    qrUserEntity1.setQrWalletId(idQr3.toString());
                    qrUserEntity1.setUserId(userIdDTO);
                    qrUserEntity1.setRole("ADMIN");
                    qrUserService.insertQrUser(qrUserEntity1);

                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                    break;
            }
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: get QrWallet: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("Lỗi: " + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

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

    @GetMapping("/qr-wallets/details/{qrWalletId}")
    public ResponseEntity<Object> getQrWalletDetails(@PathVariable String qrWalletId,
                                                     @RequestParam String userId,
                                                     @RequestParam int page,
                                                     @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            IQrWalletDTO qrWalletDTO = qrWalletService.getQrWalletDetailsById(userId, qrWalletId);
            if (qrWalletDTO == null) {
                result = new ResponseMessageDTO("FAILED", "QrWallet not found");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                int totalCommentElements = qrWalletService.countCommentsByQrWalletId(qrWalletId);
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<QrCommentDTO> commentsPage = qrWalletService.findCommentsByQrWalletId(qrWalletId, pageable);

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
                detailDTO.setFileAttachmentId(qrWalletDTO.getFileAttachmentId());

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

    @PostMapping("qr-wallets/generate/test")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody QrCreateRequestDTO qr) {
        try {
            byte[] qrImage = qrCodeService.generateQRCodeImage(qr.getValue(), 250, 250);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");
            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("qr-wallets/generate/test/parsecode")
    public ResponseEntity<String> generateQRCodeParseCode(@RequestBody QrCreateRequestDTO qrRequest) {
        try {
            String qrImageBase64 = qrCodeService.generateQRCodeImageBase64(qrRequest.getValue(), 250, 250);
            return new ResponseEntity<>(qrImageBase64, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "qr-wallet/logo-load/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        try {
//            id = "logo-vietqr-official-transparent.png";
            ResponseInputStream<?> responseInputStream = amazonS3Service.downloadFile(id);

            BufferedImage originalImage = ImageIO.read(responseInputStream);
            BufferedImage resizedImage = resizeImage(originalImage, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();
            return ResponseEntity.ok().body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }
    @GetMapping("/qr-wallets/private")
    public ResponseEntity<Object> getAllPrivateQrWallets(
            @RequestParam String userId,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("Fetching Private QR Wallets for userId: " + userId + ", page: " + page + ", size: " + size);
            int totalElements = qrWalletService.countPrivateQrWallets();
            int offset = (page - 1) * size;
            List<IQrWalletPrivateDTO> qrWallets = qrWalletService.getAllPrivateQrWallets(userId, offset, size);
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
            logger.error("getAllPrivateQrWallets Error at " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/qr-wallets")
    public ResponseEntity<Object> getQrWallets(
            @RequestParam String userId,
            @RequestParam int isPublic,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.info("Fetching QR Wallets for userId: " + userId + ", isPublic: " + isPublic + ", page: " + page + ", size: " + size);
            int totalElements = qrWalletService.countQrWalletsByPublicStatus(isPublic);
            int offset = (page - 1) * size;
            List<IQrWalletDTO> qrWallets = qrWalletService.getQrWalletsByPublicStatus(userId, isPublic, offset, size);
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
            logger.error("getQrWallets Error at " + e.getMessage() + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
