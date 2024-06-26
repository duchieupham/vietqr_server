package com.vietqr.org.controller.qrfeed;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.VietQRGenerateDTO;
import com.vietqr.org.dto.qrfeed.QrCreateRequestDTO;
import com.vietqr.org.dto.qrfeed.QrLinkDTO;
import com.vietqr.org.dto.qrfeed.QrWalletResponseDTO;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import com.vietqr.org.service.ImageService;
import com.vietqr.org.service.qrfeed.QrWalletService;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.TransactionRefIdUtil;
import com.vietqr.org.util.VietQRUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class QrWalletController {
    private static final Logger logger = Logger.getLogger(QrWalletController.class);

    @Autowired
    QrWalletService qrWalletService;

    @Autowired
    private ImageService imageService;

    @PostMapping("qr-wallet/generate-qr-link")
    public ResponseEntity<Object> createQrLink(
            @RequestParam String type,
            @RequestBody QrCreateRequestDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        UUID generateUUID = UUID.randomUUID();
        try {

//            String text = dto.getUrlLink();
//            int width = 300;
//            int height = 300;
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            Map<EncodeHintType, Object> hints = new HashMap<>();
//            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//
//            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
//
//            // Convert BitMatrix to Base64 string
//            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
//            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
//            byte[] pngData = pngOutputStream.toByteArray();
//            String base64EncodedQRCode = Base64.getEncoder().encodeToString(pngData);


            // generate QR Code
            QrWalletResponseDTO qrWalletResponseDTO = new QrWalletResponseDTO();
            qrWalletResponseDTO.setQrName(dto.getQrName());
            qrWalletResponseDTO.setQrCode(dto.getText()); // cho hiển thị ra chuỗi giá trị
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
            UUID id = UUID.randomUUID();
            switch (type) {
                case "QR Link":
                    entity.setId(id.toString());
                    entity.setTitle(dto.getQrName());
                    entity.setDescription(dto.getQrDescription());
                    entity.setValue(dto.getText());
                    entity.setQrType("QR Link");
                    entity.setQrData("{\"URL\": \"" + dto.getText() + "\"}");
                    entity.setUserData("{}");
                    entity.setIsPublic(0);
                    entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                    entity.setUserId(dto.getUserId());
                    entity.setPin(dto.getPin());
                    entity.setPublicId(qrLink);
                    qrWalletService.insertQrWallet(entity);
                    break;
                case "QR Text":
                    entity.setId(id.toString());
                    entity.setTitle(dto.getQrName());
                    entity.setDescription(dto.getQrDescription());
                    entity.setValue(dto.getText());
                    entity.setQrType("QR Text");
                    entity.setQrData("{\"URL\": \"" + dto.getText() + "\"}");
                    entity.setUserData("{}");
                    entity.setIsPublic(0);
                    entity.setTimeCreated(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                    entity.setUserId(dto.getUserId());
                    entity.setPin(dto.getPin());
                    entity.setPublicId(qrLink);
                    qrWalletService.insertQrWallet(entity);
                    break;
            }

            result = qrWalletResponseDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("QrWalletController: ERROR: create QR: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("qr-wallet")
    public ResponseEntity<Object> getQrWallet() {
        Object result = null;
        HttpStatus httpStatus = null;
        try {


            result = null;
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
