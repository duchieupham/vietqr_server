package com.vietqr.org.controller.qrfeed;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.qrfeed.*;
import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.qrfeed.QrUserEntity;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.service.BankTypeService;
import com.vietqr.org.service.CaiBankService;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.qrfeed.QrUserService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import com.vietqr.org.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrWalletController {
    private static final Logger logger = Logger.getLogger(QrWalletController.class);

    @Autowired
    QrWalletService qrWalletService;

    @Autowired
    QrUserService qrUserService;

    @Autowired
    CaiBankService caiBankService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    private ImageService imageService;

    @PostMapping("qr-wallet/generate-qr-vietqr")
    public ResponseEntity<Object> generateQRUnauthenticated(
            @Valid
            @RequestParam int isPublic,
            @RequestParam String userId,
            @RequestBody VietQRCreateUnauthenticatedDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        VietQRDTO vietQRDTO = new VietQRDTO();
        try {
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
                entity.setQrType("QR VietQR");
                entity.setQrData("{"
                        + "\"bankAccount\": \"" + dto.getBankAccount() + "\", "
                        + "\"userBankName\": \"" + dto.getUserBankName() + "\", "
                        + "\"bankCode\": \"" + dto.getBankCode() + "\", "
                        + "\"amount\": \"" + dto.getAmount() + "\", "
                        + "\"content\": \"" + dto.getContent() + "\""
                        + "}");
                entity.setUserData("{"
                        + "\"bankAccount\": \"" + dto.getBankAccount() + "\", "
                        + "\"userBankName\": \"" + dto.getUserBankName() + "\", "
                        + "\"bankCode\": \"" + dto.getBankCode() + "\""
                        + "}");
                if (isPublic == 1) {
                    entity.setIsPublic(1);
                } else if (isPublic == 0) {
                    entity.setIsPublic(0);
                }
                entity.setIsPublic(0);
                entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                entity.setUserId(userId);
                entity.setPin("");
                entity.setPublicId("");
                qrWalletService.insertQrWallet(entity);

                //add qr vào qr_user (ch implements)
                QrUserEntity qrUserEntity = new QrUserEntity();
                UUID idQrUser = UUID.randomUUID();
                qrUserEntity.setId(idQrUser.toString());
                qrUserEntity.setQrWalletId(idQrWallet.toString());
                qrUserEntity.setUserId(userId);
                qrUserEntity.setRole("ADMIN");
                qrUserService.insertQrUser(qrUserEntity);

                result = vietQRDTO;
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("generateVCard: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("qr-wallet/generate-qr-vcard")
    ResponseEntity<Object> createQrVcard(
            @RequestParam int isPublic,
            @RequestBody VCardInputDTO dto) {
        Object result = null;
        QrVcardRequestDTO qrVcardRequestDTO = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
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

                // add data qr vào qr_wallet
                QrWalletEntity entity = new QrWalletEntity();
                LocalDateTime currentDateTime = LocalDateTime.now();
                UUID idQrWallet = UUID.randomUUID();
                entity.setId(idQrWallet.toString());
                entity.setTitle("Qr Vcard");
                entity.setDescription("Qr Vcard: " + dto.getFullname());
                entity.setValue(qr);
                entity.setQrType("QR Vcard");
                entity.setQrData("{"
                        + "\"fullName\": \"" + dto.getFullname() + "\", "
                        + "\"phoneNo\": \"" + dto.getPhoneNo() + "\", "
                        + "\"email\": \"" + dto.getEmail() + "\", "
                        + "\"companyName\": \"" + dto.getCompanyName() + "\", "
                        + "\"website\": \"" + dto.getWebsite() + "\", "
                        + "\"address\": \"" + dto.getAddress() + "\""
                        + "}");
                entity.setUserData("{"
                        + "\"fullName\": \"" + dto.getFullname() + "\", "
                        + "\"phoneNo\": \"" + dto.getPhoneNo() + "\", "
                        + "\"email\": \"" + dto.getEmail() + "\", "
                        + "\"companyName\": \"" + dto.getCompanyName() + "\", "
                        + "\"website\": \"" + dto.getWebsite() + "\", "
                        + "\"address\": \"" + dto.getAddress() + "\""
                        + "}");
                if (isPublic == 1) {
                    entity.setIsPublic(1);
                } else if (isPublic == 0) {
                    entity.setIsPublic(0);
                }
                entity.setIsPublic(0);
                entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                entity.setUserId(dto.getUserId());
                entity.setPin("");
                entity.setPublicId("");
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

            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("generateVCard: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }


    @PostMapping("qr-wallet/generate-qr-link")
    public ResponseEntity<Object> createQrLink(
            @RequestParam String type,
            @RequestBody QrCreateRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        UUID generateUUID = UUID.randomUUID();
        try {
            if (type.equals("QR Link") || type.equals("QR Text")) {
                // generate QR Code
                QrWalletResponseDTO qrWalletResponseDTO = new QrWalletResponseDTO();
                qrWalletResponseDTO.setQrName(dto.getQrName());
                qrWalletResponseDTO.setValue(dto.getText()); // cho hiển thị ra chuỗi giá trị
                qrWalletResponseDTO.setQrContent(dto.getQrDescription());
                qrWalletResponseDTO.setImgId(generateUUID.toString());
                String refId = TransactionRefIdUtil.encryptTransactionId(generateUUID.toString());
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
                    case "QR Link":
                        entity.setId(idQrWallet.toString());
                        entity.setTitle(dto.getQrName());
                        entity.setDescription(dto.getQrDescription());
                        entity.setValue(dto.getText());
                        entity.setQrType("QR Link");
                        entity.setQrData("{\"link\": \"" + dto.getText() + "\"}");
                        entity.setUserData("{}");
                        if (dto.getIsPublic() == 1) {
                            entity.setIsPublic(1);
                        } else if (dto.getIsPublic() == 0) {
                            entity.setIsPublic(0);
                        }
                        entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                        entity.setUserId(dto.getUserId());
                        entity.setPin(dto.getPin());
                        entity.setPublicId(qrLink);
                        qrWalletService.insertQrWallet(entity);

                        //add qr vào qr_user (ch implements)
                        qrUserEntity.setId(idQrUser.toString());
                        qrUserEntity.setQrWalletId(idQrWallet.toString());
                        qrUserEntity.setUserId(dto.getUserId());
                        qrUserEntity.setRole("ADMIN");
                        qrUserService.insertQrUser(qrUserEntity);

                        break;
                    case "QR Text":
                        entity.setId(idQrWallet.toString());
                        entity.setTitle(dto.getQrName());
                        entity.setDescription(dto.getQrDescription());
                        entity.setValue(dto.getText());
                        entity.setQrType("QR Text");
                        entity.setQrData("{\"text\": \"" + dto.getText() + "\"}");
                        entity.setUserData("{}");
                        if (dto.getIsPublic() == 1) {
                            entity.setIsPublic(1);
                        } else if (dto.getIsPublic() == 0) {
                            entity.setIsPublic(0);
                        }
                        entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                        entity.setUserId(dto.getUserId());
                        entity.setPin(dto.getPin());
                        entity.setPublicId(qrLink);
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
}
