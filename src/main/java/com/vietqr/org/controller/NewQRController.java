package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.entity.AccountBankReceiveEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.service.mqtt.MqttMessagingService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class NewQRController {
    private static final Logger logger = Logger.getLogger(NewQRController.class);
    @Autowired
    CaiBankService caiBankService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    MqttMessagingService mqttMessagingService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    CustomerVaService customerVaService;

    @Autowired
    TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    TerminalItemService terminalItemService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionReceiveBranchService transactionReceiveBranchService;

    @Autowired
    BranchMemberService branchMemberService;

    @Autowired
    BranchInformationService branchInformationService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    private SocketHandler socketHandler;

    @Autowired
    AccountBankReceivePersonalService accountBankReceivePersonalService;

    @Autowired
    BankReceiveBranchService bankReceiveBranchService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    CustomerInvoiceService customerInvoiceService;


    @PostMapping("/qr-image/generate-customer")
    public ResponseEntity<Object> generateQRCustomer(@RequestBody VietQRCreateCustomerDTO dto,
                                                     @RequestHeader("Authorization") String token) {
        Object result = null;
        HttpStatus httpStatus = null;
        UUID transactionUUID = UUID.randomUUID();
        int qrType = 0;
        ResponseEntity<Object> response = null;
        if (Objects.nonNull(dto.getQrType())) {
            qrType = dto.getQrType();
        }
        try {
            if (StringUtil.containsOnlyDigits(dto.getBankCode())) {
                String bankCode = bankTypeService.getBankCodeByCaiValue(dto.getBankCode());
                if (!StringUtil.isNullOrEmpty(bankCode)) {
                    dto.setBankCode(bankCode);
                }
            }
            switch (qrType) {
                case 0:
                    response = generateDynamicQrCustomer(dto, token);
                    result = response.getBody();
                    httpStatus = response.getStatusCode();
                    break;
//                case 1:
//                    response = generateStaticQrCustomer(dto, token);
//                    result = response.getBody();
//                    httpStatus = response.getStatusCode();
//                    break;
//                case 3:
//                    response = generateSemiDynamicQrCustomer(dto, token);
//                    result = response.getBody();
//                    httpStatus = response.getStatusCode();
//                    break;
                default:
                    // Invalid QR type
                    result = new ResponseMessageDTO("FAILED", "E46");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
        } catch (Exception e) {
            logger.error("VietQRController: generateQRCustomer: ERROR: " + e.getMessage() + " at: "
                    + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private ResponseEntity<Object> generateDynamicQrCustomer(VietQRCreateCustomerDTO dto, String token) {
        Object result = null;
        HttpStatus httpStatus = null;
        UUID transactionUUID = UUID.randomUUID();
        String serviceCode = !StringUtil.isNullOrEmpty(dto.getServiceCode()) ? dto.getServiceCode() : "";
        String subRawCode = StringUtil.getValueNullChecker(dto.getSubTerminalCode());
        ITerminalBankReceiveQR terminalBankReceiveEntity = null;
        if (!StringUtil.isNullOrEmpty(subRawCode) && !"3991031291095".equals(dto.getBankAccount())) {
            terminalBankReceiveEntity =
                    terminalBankReceiveService.getTerminalBankReceiveQR(subRawCode);
            if (terminalBankReceiveEntity != null) {
                dto.setTerminalCode(terminalBankReceiveEntity.getTerminalCode());
            }
        }
        VietQRDTO vietQRDTO = null;
        if (dto.getReconciliation() == null || dto.getReconciliation() == true) {
            switch (dto.getBankCode().toUpperCase()) {
                case "MB":
                    // for saving qr mms flow 2
                    String qrMMS = "";
                    // find bankAccount đã liên kết và mms = true và check transType = "C -> gọi
                    // luồng 2
                    String checkExistedMMSBank = accountBankReceiveService.checkMMSBankAccount(dto.getBankAccount());
                    boolean checkMMS = false;
                    String transType = "C";
                    if (dto.getTransType() != null) {
                        transType = dto.getTransType().trim();
                    }
                    if (checkExistedMMSBank != null && !checkExistedMMSBank.trim().isEmpty() && transType.equals("C")) {
                        checkMMS = true;
                    }
                    if (!checkMMS) {
                        // Luồng 1
                        String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                        String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
//					if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
//						bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
//					} else {
//						bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getCustomerBankCode());
//					}
                        vietQRDTO = new VietQRDTO();
                        try {
                            if (dto.getContent().length() <= 50) {
                                // check if generate qr with transtype = D or C
                                // if D => generate with customer information
                                // if C => do normal
                                // find bankTypeId by bankcode
                                if (bankTypeId != null && !bankTypeId.isEmpty()) {
                                    // get cai value
                                    ICaiBankTypeQR caiBankTypeQR = bankTypeService.getCaiBankTypeById(bankTypeId);
                                    // find bank by bankAccount and banktypeId
                                    IAccountBankInfoQR accountBankEntity = null;
                                    if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                        accountBankEntity = accountBankReceiveService
                                                .getAccountBankQRByAccountAndId(dto.getBankAccount(), bankTypeId);
                                    } else {
                                        accountBankEntity = accountBankReceiveService
                                                .getAccountBankQRByAccountAndId(dto.getCustomerBankAccount(), bankTypeId);
                                    }
                                    if (accountBankEntity != null) {
                                        String content = dto.getContent();
                                        if (dto.getReconciliation() == null || dto.getReconciliation()) {
                                            content = traceId + " " + dto.getContent();
                                        }
                                        // generate VietQRGenerateDTO
                                        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                        vietQRGenerateDTO.setCaiValue(caiBankTypeQR.getCaiValue());
                                        vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                        vietQRGenerateDTO.setContent(content);
                                        vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                                        // generate VietQRDTO
                                        String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                        vietQRDTO.setBankCode(caiBankTypeQR.getBankCode());
                                        vietQRDTO.setBankName(caiBankTypeQR.getBankName());
                                        vietQRDTO.setBankAccount(accountBankEntity.getBankAccount());
                                        vietQRDTO.setUserBankName(accountBankEntity.getBankAccountName().toUpperCase());
                                        vietQRDTO.setAmount(dto.getAmount() + "");
                                        vietQRDTO.setContent(content);
                                        vietQRDTO.setQrCode(VietQRUtil.generateTransactionQR(vietQRGenerateDTO));

                                        byte[] logoBytes = null;

                                        String base64String = "data:image/jpg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCAKTAnUDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKRmCKWYhVHJJPArjtc+MXgzw+zpd+IrLzUOGjgk85gfTCZrKpVp0VepJJebsRKcaavN2OyorwjXv2uvDdjvTS9MvtTkHR5NsMZ/Ekt/wCO1514p/bQ1iysZp4dP0vSbdOGnumaQrnp3UZ/A15NTOsFB8qnzPsk2edUzPC0953Pryms6xqWYhVHJJOAK/Lbxx+3f421CWWHTNYuHXtLsWBPwCAMR9SK8J8V/GHxr42kc614m1K+Rj/q3uW2Y9MA9PrXZTxFSqrqm4rz0f3K58/W4ow0NKUHL8P8z9jvEXxo8BeE5Gj1fxjoenyr96Ka/iDjHqu7P6VwWsftrfBfRVcy+OLW4K9Fs7aecn6bEI/Wvx9JyTQK6udnj1OKsS/gpxXrd/5H6ha5/wAFKPhXppK2Nl4h1duzQWccafnJIp/SuR1H/gqH4fX/AI8PA+pTe9zeRx/yDV+dw68CnKKXOzzp8SZhLaSXyX63PuvUP+Co2oNxY+ALVD/euNSZv0EY/nXO3n/BTbx/MCLPwv4dtvQzJPL/AClWvjlRUq/dFRzs4J59mMv+Xr+5f5H1Hef8FGPizcsxjXQbX2hsGOP++pDWRcft9fGW4Py69Z249I9Ng/qpr51HbPWnhanmZxyzfHy3rS+895k/bg+M0uc+Ldp/2bC3H/tOo2/bU+MbcnxjL+Fpbj/2nXhlSAUuZnNLMsb/AM/5f+BP/M9u/wCG0vjH/wBDlN/4Cwf/ABum/wDDZfxiZif+E0uf/AaD/wCIrxTFPVfwqeaXczeZ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5Hr5akH6up7V8k+GfDt94u8RabommQm41DULiO1t4h/E7sFA+mTX7VfBn4X2Hwc+G+i+FNPO9LGEedPjBnmbmSQ/VifoMDtVxWp9Xw9gPrWJ9tNe7DX59P8AM7aiiitT9YCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooArajp9rq9hcWN7BHdWdxG0U0Eq7kdCMFSO4Ir8uP2rv2cLv4G+KvtWnRyT+EdRcmxuGJYwt1MLn1HOD3HuDX6oVg+OvBGkfEbwrqHh7XLYXWm30ZjkX+JfRlPZgcEHsRWVSmqiPAzjKaea0OTaa+F/p6M/FI/rURr1D9oD4Gaz8CfGsukX4a502fdLp2oBcLcQ5I5xwHHRl7cHoQT5dn3rjUXHRn4VWoVMPUlRqq0luGc0xjTuneo6ZkgppOaVqSgsSg0NTaCgoopDVIoQmkooo3KEzSZpWptMBDSUUfjQUIPWlopGbFAxGbio6D+lNansaJWAmiik5oSuUgLYplK1N6032RaF70M2KDxUdUMKKK6r4XfDvVPit480fwto8Re81CcR7sfLEg5eRvQKoJP0plxjKpJQirtn2D/wAE2/gW15qV78TdUhH2e23WWkqwyWkIxNL7YB2A/wC0/pX6EVgeA/BemfDrwdpHhrR4Fg07TbdbeJQOWwOWPqzHLE9ySa363Ssj9sy3BLAYaNFb7v16hRRRTPUCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAOH+MPwh0L41eC7rw9rkIww3212qgy2swBCyJn0zyO4JHevya+Lfwn134N+Mrzw9rtuUliO6C4VT5dzEfuyIe4P6EEHkV+zteY/Hz4DaH8evB8mlaiq2upwBn0/U1TMltIQPzQ4AZe+B3AIznDmR8ln2SRzOn7WlpVjt5+T/Q/HhutITXUfEr4c658K/F174e8QWbWl9bNweqSpn5ZEP8AEpHIP8iCK5XrXJsfjMqcqcnCas10EpaKaaRIlFFFNFCGkoNJT8igzikNLtpppjEp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QAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAH//Z";

                                        if (base64String != null && !base64String.isEmpty()) {
                                            // Loại bỏ phần tiền tố "data:image/jpeg;base64," nếu có
                                            String base64Image = base64String.split(",")[1];

                                            // Chuyển đổi base64 thành byte[]
                                            logoBytes = java.util.Base64.getDecoder().decode(base64Image);
                                        }

                                        // Tạo QR code với logo và chuyển thành Base64
                                        String qrCodeBase64 = VietQRUtil.generateTransactionQRWithLogoBase64(vietQRGenerateDTO, logoBytes);

                                        vietQRDTO.setQrCodeBase64(qrCodeBase64);

                                        vietQRDTO.setImgId(caiBankTypeQR.getImgId());
                                        vietQRDTO.setExisting(1);
                                        vietQRDTO.setTransactionId("");
                                        vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                        vietQRDTO.setTransactionRefId(refId);
                                        vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + refId);
                                        vietQRDTO.setOrderId(dto.getOrderId());
                                        vietQRDTO.setAdditionalData(new ArrayList<>());
                                        if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
                                            vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                        }
                                        vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                        vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));

                                        result = vietQRDTO;
                                        httpStatus = HttpStatus.OK;
                                    } else {
                                        String bankAccount = dto.getCustomerBankAccount();
                                        String userBankName = dto.getCustomerName().trim().toUpperCase();
                                        String content = dto.getContent();
                                        if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                            bankAccount = dto.getBankAccount();
                                            userBankName = dto.getUserBankName().trim().toUpperCase();
                                        }
                                        // generate VietQRGenerateDTO
                                        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                        vietQRGenerateDTO.setCaiValue(caiBankTypeQR.getCaiValue());
                                        vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                        if (dto.getReconciliation() == null || dto.getReconciliation()) {
                                            content = traceId + " " + dto.getContent();
                                        }
                                        vietQRGenerateDTO.setContent(content);
                                        vietQRGenerateDTO.setBankAccount(bankAccount);
                                        // generate VietQRDTO
                                        vietQRDTO.setBankCode(caiBankTypeQR.getBankCode());
                                        vietQRDTO.setBankName(caiBankTypeQR.getBankName());
                                        vietQRDTO.setBankAccount(bankAccount);
                                        vietQRDTO.setUserBankName(userBankName);
                                        vietQRDTO.setAmount(dto.getAmount() + "");
                                        vietQRDTO.setContent(content);
                                        vietQRDTO.setQrCode(VietQRUtil.generateTransactionQR(vietQRGenerateDTO));
                                        byte[] logoBytes = null;
                                        String base64String = "data:image/jpg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCAKTAnUDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKRmCKWYhVHJJPArjtc+MXgzw+zpd+IrLzUOGjgk85gfTCZrKpVp0VepJJebsRKcaavN2OyorwjXv2uvDdjvTS9MvtTkHR5NsMZ/Ekt/wCO1514p/bQ1iysZp4dP0vSbdOGnumaQrnp3UZ/A15NTOsFB8qnzPsk2edUzPC0953Pryms6xqWYhVHJJOAK/Lbxx+3f421CWWHTNYuHXtLsWBPwCAMR9SK8J8V/GHxr42kc614m1K+Rj/q3uW2Y9MA9PrXZTxFSqrqm4rz0f3K58/W4ow0NKUHL8P8z9jvEXxo8BeE5Gj1fxjoenyr96Ka/iDjHqu7P6VwWsftrfBfRVcy+OLW4K9Fs7aecn6bEI/Wvx9JyTQK6udnj1OKsS/gpxXrd/5H6ha5/wAFKPhXppK2Nl4h1duzQWccafnJIp/SuR1H/gqH4fX/AI8PA+pTe9zeRx/yDV+dw68CnKKXOzzp8SZhLaSXyX63PuvUP+Co2oNxY+ALVD/euNSZv0EY/nXO3n/BTbx/MCLPwv4dtvQzJPL/AClWvjlRUq/dFRzs4J59mMv+Xr+5f5H1Hef8FGPizcsxjXQbX2hsGOP++pDWRcft9fGW4Py69Z249I9Ng/qpr51HbPWnhanmZxyzfHy3rS+895k/bg+M0uc+Ldp/2bC3H/tOo2/bU+MbcnxjL+Fpbj/2nXhlSAUuZnNLMsb/AM/5f+BP/M9u/wCG0vjH/wBDlN/4Cwf/ABum/wDDZfxiZif+E0uf/AaD/wCIrxTFPVfwqeaXczeZ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5Hr5akH6up7V8k+GfDt94u8RabommQm41DULiO1t4h/E7sFA+mTX7VfBn4X2Hwc+G+i+FNPO9LGEedPjBnmbmSQ/VifoMDtVxWp9Xw9gPrWJ9tNe7DX59P8AM7aiiitT9YCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooArajp9rq9hcWN7BHdWdxG0U0Eq7kdCMFSO4Ir8uP2rv2cLv4G+KvtWnRyT+EdRcmxuGJYwt1MLn1HOD3HuDX6oVg+OvBGkfEbwrqHh7XLYXWm30ZjkX+JfRlPZgcEHsRWVSmqiPAzjKaea0OTaa+F/p6M/FI/rURr1D9oD4Gaz8CfGsukX4a502fdLp2oBcLcQ5I5xwHHRl7cHoQT5dn3rjUXHRn4VWoVMPUlRqq0luGc0xjTuneo6ZkgppOaVqSgsSg0NTaCgoopDVIoQmkooo3KEzSZpWptMBDSUUfjQUIPWlopGbFAxGbio6D+lNansaJWAmiik5oSuUgLYplK1N6032RaF70M2KDxUdUMKKK6r4XfDvVPit480fwto8Re81CcR7sfLEg5eRvQKoJP0plxjKpJQirtn2D/wAE2/gW15qV78TdUhH2e23WWkqwyWkIxNL7YB2A/wC0/pX6EVgeA/BemfDrwdpHhrR4Fg07TbdbeJQOWwOWPqzHLE9ySa363Ssj9sy3BLAYaNFb7v16hRRRTPUCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAOH+MPwh0L41eC7rw9rkIww3212qgy2swBCyJn0zyO4JHevya+Lfwn134N+Mrzw9rtuUliO6C4VT5dzEfuyIe4P6EEHkV+zteY/Hz4DaH8evB8mlaiq2upwBn0/U1TMltIQPzQ4AZe+B3AIznDmR8ln2SRzOn7WlpVjt5+T/Q/HhutITXUfEr4c658K/F174e8QWbWl9bNweqSpn5ZEP8AEpHIP8iCK5XrXJsfjMqcqcnCas10EpaKaaRIlFFFNFCGkoNJT8igzikNLtpppjEp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QAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAH//Z";

                                        if (base64String != null && !base64String.isEmpty()) {
                                            // Loại bỏ phần tiền tố "data:image/jpeg;base64," nếu có
                                            String base64Image = base64String.split(",")[1];

                                            // Chuyển đổi base64 thành byte[]
                                            logoBytes = java.util.Base64.getDecoder().decode(base64Image);
                                        }

                                        // Tạo QR code với logo và chuyển thành Base64
                                        String qrCodeBase64 = VietQRUtil.generateTransactionQRWithLogoBase64(vietQRGenerateDTO, logoBytes);

                                        vietQRDTO.setQrCodeBase64("data:image/jpg;base64,"+qrCodeBase64);

                                        vietQRDTO.setImgId(caiBankTypeQR.getImgId());
                                        vietQRDTO.setExisting(1);
                                        vietQRDTO.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                                        vietQRDTO.setAdditionalData(new ArrayList<>());
                                        if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
                                            vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                        }
                                        vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                        vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                                        result = vietQRDTO;
                                        httpStatus = HttpStatus.OK;
                                    }
                                } else {
                                    result = new ResponseMessageDTO("FAILED", "E24");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                }
                            } else {
                                result = new ResponseMessageDTO("FAILED", "E26");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                            return new ResponseEntity<>(result, httpStatus);
                            //
                        } catch (Exception e) {
                            logger.error(e.toString());
                            result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            return new ResponseEntity<>(result, httpStatus);
                        } finally {
                            // insert new transaction with orderId and sign
                            if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                                bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                            }
                            IAccountBankQR accountBankQR = accountBankReceiveService.getAccountBankQR(dto.getBankAccount(), bankTypeId);
                            if (accountBankQR != null) {
                                VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                                vietQRCreateDTO.setBankId(accountBankQR.getId());
                                vietQRCreateDTO.setAmount(dto.getAmount() + "");
                                vietQRCreateDTO.setContent(dto.getContent());
                                vietQRCreateDTO.setUserId(accountBankQR.getUserId());
                                vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
                                vietQRCreateDTO.setServiceCode(serviceCode);

                                if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                                    vietQRCreateDTO.setTransType("D");
                                    vietQRCreateDTO.setCustomerBankAccount(dto.getCustomerBankAccount());
                                    vietQRCreateDTO.setCustomerBankCode(dto.getCustomerBankCode());
                                    vietQRCreateDTO.setCustomerName(dto.getCustomerName());
                                } else {
                                    vietQRCreateDTO.setTransType("C");
                                }
                                if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
                                    vietQRCreateDTO.setUrlLink(dto.getUrlLink());
                                } else {
                                    vietQRCreateDTO.setUrlLink("");
                                }
                                VietQRDTO finalVietQRDTO = vietQRDTO;

                                Thread thread1 = new Thread(()->
                                        insertNewTransaction(transactionUUID, traceId, vietQRCreateDTO, finalVietQRDTO, dto.getOrderId(),
                                                dto.getSign(), true)
                                );
                                thread1.start();
                            }
                            Thread thread2 = new Thread(()-> logUserInfo(token));
                            thread2.start();
                        }
                    } else {
                        // Luồng 2
                        LocalDateTime requestLDT = LocalDateTime.now();
                        logger.info("generateVietQRMMS: start generate at: " + requestLDT.toEpochSecond(ZoneOffset.UTC));
                        String bankTypeMB = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                        AccountBankReceiveEntity accountBankEntity = null;
                        String qrCode = "";
                        try {
                            // 1. Validate input (amount, content, bankCode) => E34 if Invalid input data
                            if (checkRequestBodyFlow2(dto)) {
                                // 2. Find terminal bank by bank_account_raw_number
                                accountBankEntity = accountBankReceiveService
                                        .getAccountBankByBankAccountAndBankTypeId(dto.getBankAccount(), bankTypeMB);
                                if (accountBankEntity != null) {
                                    String terminalId = terminalBankService
                                            .getTerminalBankQRByBankAccount(dto.getBankAccount());
                                    if (terminalId == null) {
                                        // 3.A. If not found => E35 (terminal is not existed)
                                        logger.error("generateVietQRMMS: ERROR: Bank account is not existed.");
                                        result = new ResponseMessageDTO("FAILED", "E35");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    } else {
                                        // 3.B. If found => get bank token => create qr code
                                        TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();
                                        if (tokenBankDTO != null) {
                                            String content = "";
                                            if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                                                content = dto.getContent();
                                            } else {
                                                content = "VQR" + RandomCodeUtil.generateRandomUUID();
                                            }
                                            if (accountBankEntity.getBankAccount().equals("4144898989")) {
                                                content = !StringUtil.isNullOrEmpty(dto.getContent()) ?
                                                        (dto.getContent() + " " + "Ghe Massage AeonBT")
                                                        : "Ghe Massage AeonBT" ;
                                            }
                                            VietQRMMSRequestDTO requestDTO = new VietQRMMSRequestDTO();
                                            requestDTO.setToken(tokenBankDTO.getAccess_token());
                                            requestDTO.setTerminalId(terminalId);
                                            requestDTO.setAmount(dto.getAmount() + "");
                                            requestDTO.setContent(content);
                                            requestDTO.setOrderId(dto.getOrderId());
                                            ResponseMessageDTO responseMessageDTO = requestVietQRMMS(requestDTO);
                                            if (Objects.nonNull(responseMessageDTO)
                                                    && "SUCCESS".equals(responseMessageDTO.getStatus())) {
                                                qrCode = responseMessageDTO.getMessage();
                                                qrMMS = qrCode;
                                                // "MB Bank"
                                                String bankTypeId = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
                                                if (bankTypeId != null && !bankTypeId.trim().isEmpty()) {
                                                    vietQRDTO = new VietQRDTO();
                                                    // get cai value
                                                    IBankTypeQR bankTypeEntity = bankTypeService.getBankTypeQRById(bankTypeId);
                                                    String bankAccount = "";
                                                    String userBankName = "";
                                                    bankAccount = dto.getBankAccount();
                                                    userBankName = dto.getUserBankName().trim().toUpperCase();
                                                    // generate VietQRDTO
                                                    vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
                                                    vietQRDTO.setBankName(bankTypeEntity.getBankName());
                                                    vietQRDTO.setBankAccount(bankAccount);
                                                    vietQRDTO.setUserBankName(userBankName);
                                                    vietQRDTO.setAmount(dto.getAmount() + "");
                                                    vietQRDTO.setContent(content);
                                                    vietQRDTO.setQrCode(qrCode);
                                                    vietQRDTO.setImgId(bankTypeEntity.getImgId());
                                                    vietQRDTO.setExisting(1);
                                                    vietQRDTO.setTransactionId("");
                                                    vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                                    String refId = TransactionRefIdUtil
                                                            .encryptTransactionId(transactionUUID.toString());
                                                    vietQRDTO.setTransactionRefId(refId);
                                                    vietQRDTO.setQrLink(EnvironmentUtil.getQRLink() + refId);
                                                    vietQRDTO.setOrderId(dto.getOrderId());
                                                    vietQRDTO.setAdditionalData(new ArrayList<>());
                                                    if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
                                                        vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                                    }
                                                    vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                                    vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                                                    result = vietQRDTO;
                                                    httpStatus = HttpStatus.OK;
                                                } else {
                                                    result = new ResponseMessageDTO("FAILED", "E24");
                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                }
                                            } else {
                                                logger.error("generateVietQRMMS: ERROR: Invalid get QR Code");
                                                if (responseMessageDTO != null) {
                                                    result = new ResponseMessageDTO("FAILED", responseMessageDTO.getMessage());
                                                } else {
                                                    result = new ResponseMessageDTO("FAILED", "E05");
                                                }
                                                httpStatus = HttpStatus.BAD_REQUEST;
                                            }
                                        } else {
                                            logger.error("generateVietQRMMS: ERROR: Invalid get bank token");
                                            result = new ResponseMessageDTO("FAILED", "E05");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                        }
                                    }
                                } else {
                                    logger.error("generateVietQRMMS: ERROR: bankAccount is not existed in system");
                                    result = new ResponseMessageDTO("FAILED", "E36");
                                    httpStatus = HttpStatus.BAD_REQUEST;

                                }
                            } else {
                                logger.error("generateVietQRMMS: ERROR: Invalid request body");
                                result = new ResponseMessageDTO("FAILED", "E34");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } catch (Exception e) {
                            logger.error("generateVietQRMMS: ERROR: " + e.toString());
                            result = new ResponseMessageDTO("FAILED", "E05");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        } finally {
                            // 4. Insert transaction_receive
                            // (5. Insert notification)
                            if (accountBankEntity != null && qrCode != null && !qrCode.isEmpty()) {
                                String content = "";
                                if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                                    content = dto.getContent();
                                } else {
                                    content = "VQR" + RandomCodeUtil.generateRandomUUID();
                                }

                                if (accountBankEntity.getBankAccount().equals("4144898989")) {
                                    content = !StringUtil.isNullOrEmpty(dto.getContent()) ?
                                            (dto.getContent() + " " + "Ghe Massage AeonBT")
                                            : "Ghe Massage AeonBT" ;
                                }

                                LocalDateTime currentDateTime = LocalDateTime.now();
                                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                VietQRMMSCreateDTO vietQRMMSCreateDTO = new VietQRMMSCreateDTO();
                                vietQRMMSCreateDTO.setBankAccount(dto.getBankAccount());
                                vietQRMMSCreateDTO.setBankCode(dto.getBankCode());
                                vietQRMMSCreateDTO.setAmount(dto.getAmount() + "");
                                vietQRMMSCreateDTO.setContent(content);
                                vietQRMMSCreateDTO.setOrderId(dto.getOrderId());
                                vietQRMMSCreateDTO.setSign(dto.getSign());
                                vietQRMMSCreateDTO.setTerminalCode(dto.getTerminalCode());
                                vietQRMMSCreateDTO.setNote(dto.getNote());
                                vietQRMMSCreateDTO.setServiceCode(serviceCode);
                                vietQRMMSCreateDTO.setSubTerminalCode(dto.getSubTerminalCode());
                                if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
                                    vietQRMMSCreateDTO.setUrlLink(dto.getUrlLink());
                                } else {
                                    vietQRMMSCreateDTO.setUrlLink("");
                                }
                                String finalQrMMS = qrMMS;
                                AccountBankReceiveEntity finalAccountBankEntity = accountBankEntity;
                                Thread thread3 = new Thread(()->
                                        insertNewTransactionFlow2(finalQrMMS, transactionUUID.toString(), finalAccountBankEntity, vietQRMMSCreateDTO, time)
                                );
                                thread3.start();
                            }
                        }
                    }
                    break;
                case "BIDV":
//					String traceBIDVId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    String qr = "";
                    String billId = "";
                    String content = dto.getContent();
                    BankCaiTypeDTO bankCaiTypeDTOBIDV = null;
                    AccountBankGenerateBIDVDTO accountBankBIDV = null;
                    if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                        bankCaiTypeDTOBIDV = bankTypeService.getBankCaiByBankCode(dto.getBankCode());
                    } else {
                        bankCaiTypeDTOBIDV = bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
                    }
                    vietQRDTO = new VietQRDTO();
                    try {
                        if (dto.getContent().length() <= 50) {
                            // check if generate qr with transtype = D or C
                            // if D => generate with customer information
                            // if C => do normal
                            // find bankTypeId by bankcode
                            if (Objects.nonNull(bankCaiTypeDTOBIDV)) {
                                // find bank by bankAccount and banktypeId

                                if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                    accountBankBIDV = accountBankReceiveService
                                            .getAccountBankBIDVByBankAccountAndBankTypeId(dto.getBankAccount(),
                                                    bankCaiTypeDTOBIDV.getId());
                                } else {
                                    accountBankBIDV = accountBankReceiveService
                                            .getAccountBankBIDVByBankAccountAndBankTypeId(dto.getCustomerBankAccount(),
                                                    bankCaiTypeDTOBIDV.getId());
                                }
                                if (Objects.nonNull(accountBankBIDV)) {
                                    // get cai value
                                    billId = getRandomBillId();
                                    content = billId + " " + StringUtil.getValueNullChecker(dto.getContent());
                                    // generate qr BIDV
                                    VietQRCreateDTO vietQRCreateDTO = new VietQRCreateDTO();
                                    vietQRCreateDTO.setBankId(accountBankBIDV.getId());
                                    vietQRCreateDTO.setAmount(dto.getAmount() + "");
                                    vietQRCreateDTO.setContent(content);
                                    vietQRCreateDTO.setUserId(accountBankBIDV.getUserId());
                                    vietQRCreateDTO.setTerminalCode(dto.getTerminalCode());
                                    //
                                    if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                                        vietQRCreateDTO.setTransType("D");
                                        vietQRCreateDTO.setCustomerBankAccount(dto.getCustomerBankAccount());
                                        vietQRCreateDTO.setCustomerBankCode(dto.getCustomerBankCode());
                                        vietQRCreateDTO.setCustomerName(dto.getCustomerName());
                                    } else {
                                        vietQRCreateDTO.setTransType("C");
                                    }
                                    if (dto.getUrlLink() != null && !dto.getUrlLink().trim().isEmpty()) {
                                        vietQRCreateDTO.setUrlLink(dto.getUrlLink());
                                    } else {
                                        vietQRCreateDTO.setUrlLink("");
                                    }
                                    ResponseMessageDTO responseMessageDTO =
                                            insertNewCustomerInvoiceTransBIDV(vietQRCreateDTO, accountBankBIDV, billId);

                                    // insert success transaction_receive
                                    if ("SUCCESS".equals(responseMessageDTO.getStatus())) {
                                        VietQRVaRequestDTO vietQRVaRequestDTO = new VietQRVaRequestDTO();
                                        if ("0".equals(dto.getAmount())) {
                                            vietQRVaRequestDTO.setAmount("");
                                        } else {
                                            vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                        }
                                        vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                        vietQRVaRequestDTO.setBillId(billId);
                                        vietQRVaRequestDTO.setUserBankName(accountBankBIDV.getBankAccountName());
                                        vietQRVaRequestDTO.setDescription(StringUtil.getValueNullChecker(content));

                                        ResponseMessageDTO generateVaInvoiceVietQR = new ResponseMessageDTO("SUCCESS", "");
                                        if (!EnvironmentUtil.isProduction()) {
                                            String bankAccountRequest= "";
                                            if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                                bankAccountRequest = dto.getBankAccount();
                                            } else {
                                                bankAccountRequest = dto.getCustomerBankAccount();
                                            }
                                            VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                            vietQRGenerateDTO.setCaiValue(bankCaiTypeDTOBIDV.getCaiValue());
                                            vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                            vietQRGenerateDTO.setContent(content);
                                            vietQRGenerateDTO.setBankAccount(bankAccountRequest);
                                            qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                            generateVaInvoiceVietQR = new ResponseMessageDTO("SUCCESS", qr);
                                        } else {
                                            if ("0".equals(dto.getAmount())) {
                                                vietQRVaRequestDTO.setAmount("");
                                            } else {
                                                vietQRVaRequestDTO.setAmount(dto.getAmount() + "");
                                            }
                                            generateVaInvoiceVietQR = CustomerVaUtil.generateVaInvoiceVietQR(vietQRVaRequestDTO, accountBankBIDV.getCustomerId());
                                        }
                                        if ("SUCCESS".equals(generateVaInvoiceVietQR.getStatus())) {
                                            qr = generateVaInvoiceVietQR.getMessage();

                                            // generate VietQRDTO
                                            vietQRDTO.setBankCode(bankCaiTypeDTOBIDV.getBankCode());
                                            vietQRDTO.setBankName(bankCaiTypeDTOBIDV.getBankName());
                                            vietQRDTO.setBankAccount(accountBankBIDV.getBankAccount());
                                            vietQRDTO.setUserBankName(accountBankBIDV.getBankAccountName().toUpperCase());
                                            vietQRDTO.setAmount(dto.getAmount() + "");
                                            vietQRDTO.setContent(content);
                                            vietQRDTO.setQrCode(qr);
                                            vietQRDTO.setImgId(bankCaiTypeDTOBIDV.getImgId());
                                            vietQRDTO.setExisting(1);
                                            vietQRDTO.setTransactionId("");
                                            vietQRDTO.setTerminalCode(dto.getTerminalCode());
                                            String refId = TransactionRefIdUtil.encryptTransactionId(transactionUUID.toString());
                                            String qrLink = EnvironmentUtil.getQRLink() + refId;
                                            vietQRDTO.setTransactionRefId(refId);
                                            vietQRDTO.setQrLink(qrLink);
                                            vietQRDTO.setOrderId(dto.getOrderId());
                                            vietQRDTO.setAdditionalData(new ArrayList<>());
                                            if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
                                                vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                            }
                                            vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                            vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                                            //
                                            result = vietQRDTO;
                                            httpStatus = HttpStatus.OK;
                                        } else {
                                            result = new ResponseMessageDTO("FAILED", "E05");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                        }
                                    } else {
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    String bankAccount = "";
                                    String userBankName = "";
                                    content = "";
                                    if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                                        bankAccount = dto.getBankAccount();
                                        userBankName = dto.getUserBankName().trim().toUpperCase();
                                    } else {
                                        bankAccount = dto.getCustomerBankAccount();
                                        userBankName = dto.getCustomerName().trim().toUpperCase();
                                    }
                                    // generate VietQRGenerateDTO
                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                                    vietQRGenerateDTO.setCaiValue(bankCaiTypeDTOBIDV.getCaiValue());
                                    vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                                    content = billId + " " + dto.getContent();
                                    vietQRGenerateDTO.setContent(content);
                                    vietQRGenerateDTO.setBankAccount(bankAccount);
                                    qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                    //
                                    // generate VietQRDTO
                                    vietQRDTO.setBankCode(bankCaiTypeDTOBIDV.getBankCode());
                                    vietQRDTO.setBankName(bankCaiTypeDTOBIDV.getBankName());
                                    vietQRDTO.setBankAccount(bankAccount);
                                    vietQRDTO.setUserBankName(userBankName);
                                    vietQRDTO.setAmount(dto.getAmount() + "");
                                    vietQRDTO.setContent(content);
                                    vietQRDTO.setQrCode(qr);
                                    vietQRDTO.setImgId(bankCaiTypeDTOBIDV.getImgId());
                                    vietQRDTO.setExisting(0);
                                    vietQRDTO.setOrderId(dto.getOrderId());
                                    vietQRDTO.setAdditionalData(new ArrayList<>());
                                    if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
                                        vietQRDTO.setAdditionalData(dto.getAdditionalData());
                                    }
                                    vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                                    vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                                    result = vietQRDTO;
                                    httpStatus = HttpStatus.OK;
                                }
                            } else {
                                result = new ResponseMessageDTO("FAILED", "E24");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E26");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                        return new ResponseEntity<>(result, httpStatus);
                    } catch (Exception e) {
                        logger.error(e.toString());
                        //System.out.println(e.toString());
                        result = new ResponseMessageDTO("FAILED", "Unexpected Error");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } finally {
                        if (Objects.nonNull(accountBankBIDV) && !StringUtil.isNullOrEmpty(qr)) {
                            VietQRBIDVCreateDTO dto1 = new VietQRBIDVCreateDTO();
                            dto1.setContent(content);
                            dto1.setAmount(dto.getAmount() + "");
                            dto1.setTerminalCode(StringUtil.getValueNullChecker(dto.getTerminalCode()));
                            dto1.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                            dto1.setNote(StringUtil.getValueNullChecker(dto.getNote()));
                            dto1.setUrlLink(StringUtil.getValueNullChecker(dto.getUrlLink()));
                            dto1.setTransType(StringUtil.getValueNullChecker(dto.getTransType()));
                            dto1.setSign(StringUtil.getValueNullChecker(dto.getSign()));
                            dto1.setBillId(billId);
                            dto1.setCustomerBankAccount(StringUtil.getValueNullChecker(dto.getCustomerBankAccount()));
                            dto1.setCustomerBankCode(StringUtil.getValueNullChecker(dto.getCustomerBankCode()));
                            dto1.setCustomerName(StringUtil.getValueNullChecker(dto.getCustomerName()));
                            dto1.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                            dto1.setQr(qr);
                            AccountBankGenerateBIDVDTO finalAccountBankBIDV = accountBankBIDV;
                            Thread thread = new Thread(() -> {
                                insertNewTransactionBIDV(transactionUUID, dto1, false, "",
                                        finalAccountBankBIDV);
                            });
                            thread.start();
                        }
                    }
                    break;
                default:
                    String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                    BankCaiTypeDTO bankCaiTypeDTO = null;
                    if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                        bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getBankCode());
                    } else {
                        bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
                    }
                    try {
                        String bankAccount = "";
                        String userBankName = "";
                        content = "";
                        if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                            bankAccount = dto.getBankAccount();
                            userBankName = dto.getUserBankName().trim().toUpperCase();
                        } else {
                            bankAccount = dto.getCustomerBankAccount();
                            userBankName = dto.getCustomerName().trim().toUpperCase();
                        }
                        if (dto.getContent().length() <= 50) {
                            // generate VietQRGenerateDTO
                            VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                            vietQRGenerateDTO.setCaiValue(bankCaiTypeDTO.getCaiValue());
                            vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                            if (dto.getReconciliation() == null || dto.getReconciliation()) {
                                content = traceId + " " + dto.getContent();
                            } else {
                                content = dto.getContent();
                            }
                            vietQRGenerateDTO.setContent(content);
                            vietQRGenerateDTO.setBankAccount(bankAccount);
                            qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                            //
                            vietQRDTO = new VietQRDTO();
                            // generate VietQRDTO
                            vietQRDTO.setBankCode(bankCaiTypeDTO.getBankCode());
                            vietQRDTO.setBankName(bankCaiTypeDTO.getBankName());
                            vietQRDTO.setBankAccount(bankAccount);
                            vietQRDTO.setUserBankName(userBankName);
                            vietQRDTO.setAmount(dto.getAmount() + "");
                            vietQRDTO.setContent(content);
                            vietQRDTO.setQrCode(qr);
                            byte[] logoBytes = null;
                            String base64String = "data:image/jpg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCAKTAnUDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKRmCKWYhVHJJPArjtc+MXgzw+zpd+IrLzUOGjgk85gfTCZrKpVp0VepJJebsRKcaavN2OyorwjXv2uvDdjvTS9MvtTkHR5NsMZ/Ekt/wCO1514p/bQ1iysZp4dP0vSbdOGnumaQrnp3UZ/A15NTOsFB8qnzPsk2edUzPC0953Pryms6xqWYhVHJJOAK/Lbxx+3f421CWWHTNYuHXtLsWBPwCAMR9SK8J8V/GHxr42kc614m1K+Rj/q3uW2Y9MA9PrXZTxFSqrqm4rz0f3K58/W4ow0NKUHL8P8z9jvEXxo8BeE5Gj1fxjoenyr96Ka/iDjHqu7P6VwWsftrfBfRVcy+OLW4K9Fs7aecn6bEI/Wvx9JyTQK6udnj1OKsS/gpxXrd/5H6ha5/wAFKPhXppK2Nl4h1duzQWccafnJIp/SuR1H/gqH4fX/AI8PA+pTe9zeRx/yDV+dw68CnKKXOzzp8SZhLaSXyX63PuvUP+Co2oNxY+ALVD/euNSZv0EY/nXO3n/BTbx/MCLPwv4dtvQzJPL/AClWvjlRUq/dFRzs4J59mMv+Xr+5f5H1Hef8FGPizcsxjXQbX2hsGOP++pDWRcft9fGW4Py69Z249I9Ng/qpr51HbPWnhanmZxyzfHy3rS+895k/bg+M0uc+Ldp/2bC3H/tOo2/bU+MbcnxjL+Fpbj/2nXhlSAUuZnNLMsb/AM/5f+BP/M9u/wCG0vjH/wBDlN/4Cwf/ABum/wDDZfxiZif+E0uf/AaD/wCIrxTFPVfwqeaXczeZ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5Hr5akH6up7V8k+GfDt94u8RabommQm41DULiO1t4h/E7sFA+mTX7VfBn4X2Hwc+G+i+FNPO9LGEedPjBnmbmSQ/VifoMDtVxWp9Xw9gPrWJ9tNe7DX59P8AM7aiiitT9YCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooArajp9rq9hcWN7BHdWdxG0U0Eq7kdCMFSO4Ir8uP2rv2cLv4G+KvtWnRyT+EdRcmxuGJYwt1MLn1HOD3HuDX6oVg+OvBGkfEbwrqHh7XLYXWm30ZjkX+JfRlPZgcEHsRWVSmqiPAzjKaea0OTaa+F/p6M/FI/rURr1D9oD4Gaz8CfGsukX4a502fdLp2oBcLcQ5I5xwHHRl7cHoQT5dn3rjUXHRn4VWoVMPUlRqq0luGc0xjTuneo6ZkgppOaVqSgsSg0NTaCgoopDVIoQmkooo3KEzSZpWptMBDSUUfjQUIPWlopGbFAxGbio6D+lNansaJWAmiik5oSuUgLYplK1N6032RaF70M2KDxUdUMKKK6r4XfDvVPit480fwto8Re81CcR7sfLEg5eRvQKoJP0plxjKpJQirtn2D/wAE2/gW15qV78TdUhH2e23WWkqwyWkIxNL7YB2A/wC0/pX6EVgeA/BemfDrwdpHhrR4Fg07TbdbeJQOWwOWPqzHLE9ySa363Ssj9sy3BLAYaNFb7v16hRRRTPUCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAOH+MPwh0L41eC7rw9rkIww3212qgy2swBCyJn0zyO4JHevya+Lfwn134N+Mrzw9rtuUliO6C4VT5dzEfuyIe4P6EEHkV+zteY/Hz4DaH8evB8mlaiq2upwBn0/U1TMltIQPzQ4AZe+B3AIznDmR8ln2SRzOn7WlpVjt5+T/Q/HhutITXUfEr4c658K/F174e8QWbWl9bNweqSpn5ZEP8AEpHIP8iCK5XrXJsfjMqcqcnCas10EpaKaaRIlFFFNFCGkoNJT8igzikNLtpppjEp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QAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAH//Z";

                            if (base64String != null && !base64String.isEmpty()) {
                                // Loại bỏ phần tiền tố "data:image/jpeg;base64," nếu có
                                String base64Image = base64String.split(",")[1];

                                // Chuyển đổi base64 thành byte[]
                                logoBytes = java.util.Base64.getDecoder().decode(base64Image);
                            }

                            // Tạo QR code với logo và chuyển thành Base64
                            String qrCodeBase64 = VietQRUtil.generateTransactionQRWithLogoBase64(vietQRGenerateDTO, logoBytes);

                            vietQRDTO.setQrCodeBase64("data:image/jpg;base64,"+qrCodeBase64);
                            vietQRDTO.setImgId(bankCaiTypeDTO.getImgId());
                            vietQRDTO.setExisting(0);
                            vietQRDTO.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                            vietQRDTO.setAdditionalData(new ArrayList<>());
                            if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
                                vietQRDTO.setAdditionalData(dto.getAdditionalData());
                            }
                            vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                            vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                            result = vietQRDTO;
                            httpStatus = HttpStatus.OK;
                        } else {
                            result = new ResponseMessageDTO("FAILED", "E26");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } catch (Exception e) {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        logger.error("VietQRController: ERROR: generateQRCustomer: " + e.getMessage() + " at: " + System.currentTimeMillis());
                    }
                    break;
            }
        } else {
            String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
            BankCaiTypeDTO bankCaiTypeDTO = null;
            if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getBankCode());
            } else {
                bankCaiTypeDTO = bankTypeService.getBankCaiByBankCode(dto.getCustomerBankCode());
            }
            try {
                String bankAccount = "";
                String userBankName = "";
                String content = "";
                if (dto.getTransType() == null || dto.getTransType().trim().equalsIgnoreCase("C")) {
                    bankAccount = dto.getBankAccount();
                    userBankName = dto.getUserBankName().trim().toUpperCase();
                } else {
                    bankAccount = dto.getCustomerBankAccount();
                    userBankName = dto.getCustomerName().trim().toUpperCase();
                }
                if (dto.getContent().length() <= 50) {
                    // generate VietQRGenerateDTO
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(bankCaiTypeDTO.getCaiValue());
                    vietQRGenerateDTO.setAmount(dto.getAmount() + "");
                    if (dto.getReconciliation() == null || dto.getReconciliation()) {
                        content = traceId + " " + dto.getContent();
                    } else {
                        content = dto.getContent();
                    }
                    vietQRGenerateDTO.setContent(content);
                    vietQRGenerateDTO.setBankAccount(bankAccount);
                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    //
                    vietQRDTO = new VietQRDTO();
                    // generate VietQRDTO
                    vietQRDTO.setBankCode(bankCaiTypeDTO.getBankCode());
                    vietQRDTO.setBankName(bankCaiTypeDTO.getBankName());
                    vietQRDTO.setBankAccount(bankAccount);
                    vietQRDTO.setUserBankName(userBankName);
                    vietQRDTO.setAmount(dto.getAmount() + "");
                    vietQRDTO.setContent(content);
                    vietQRDTO.setQrCode(qr);
                    byte[] logoBytes = null;
                    String base64String = "data:image/jpg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAMCAgMCAgMDAwMEAwMEBQgFBQQEBQoHBwYIDAoMDAsKCwsNDhIQDQ4RDgsLEBYQERMUFRUVDA8XGBYUGBIUFRT/2wBDAQMEBAUEBQkFBQkUDQsNFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBT/wAARCAKTAnUDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKRmCKWYhVHJJPArjtc+MXgzw+zpd+IrLzUOGjgk85gfTCZrKpVp0VepJJebsRKcaavN2OyorwjXv2uvDdjvTS9MvtTkHR5NsMZ/Ekt/wCO1514p/bQ1iysZp4dP0vSbdOGnumaQrnp3UZ/A15NTOsFB8qnzPsk2edUzPC0953Pryms6xqWYhVHJJOAK/Lbxx+3f421CWWHTNYuHXtLsWBPwCAMR9SK8J8V/GHxr42kc614m1K+Rj/q3uW2Y9MA9PrXZTxFSqrqm4rz0f3K58/W4ow0NKUHL8P8z9jvEXxo8BeE5Gj1fxjoenyr96Ka/iDjHqu7P6VwWsftrfBfRVcy+OLW4K9Fs7aecn6bEI/Wvx9JyTQK6udnj1OKsS/gpxXrd/5H6ha5/wAFKPhXppK2Nl4h1duzQWccafnJIp/SuR1H/gqH4fX/AI8PA+pTe9zeRx/yDV+dw68CnKKXOzzp8SZhLaSXyX63PuvUP+Co2oNxY+ALVD/euNSZv0EY/nXO3n/BTbx/MCLPwv4dtvQzJPL/AClWvjlRUq/dFRzs4J59mMv+Xr+5f5H1Hef8FGPizcsxjXQbX2hsGOP++pDWRcft9fGW4Py69Z249I9Ng/qpr51HbPWnhanmZxyzfHy3rS+895k/bg+M0uc+Ldp/2bC3H/tOo2/bU+MbcnxjL+Fpbj/2nXhlSAUuZnNLMsb/AM/5f+BP/M9u/wCG0vjH/wBDlN/4Cwf/ABum/wDDZfxiZif+E0uf/AaD/wCIrxTFPVfwqeaXczeZ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5Hr5akH6up7V8k+GfDt94u8RabommQm41DULiO1t4h/E7sFA+mTX7VfBn4X2Hwc+G+i+FNPO9LGEedPjBnmbmSQ/VifoMDtVxWp9Xw9gPrWJ9tNe7DX59P8AM7aiiitT9YCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooArajp9rq9hcWN7BHdWdxG0U0Eq7kdCMFSO4Ir8uP2rv2cLv4G+KvtWnRyT+EdRcmxuGJYwt1MLn1HOD3HuDX6oVg+OvBGkfEbwrqHh7XLYXWm30ZjkX+JfRlPZgcEHsRWVSmqiPAzjKaea0OTaa+F/p6M/FI/rURr1D9oD4Gaz8CfGsukX4a502fdLp2oBcLcQ5I5xwHHRl7cHoQT5dn3rjUXHRn4VWoVMPUlRqq0luGc0xjTuneo6ZkgppOaVqSgsSg0NTaCgoopDVIoQmkooo3KEzSZpWptMBDSUUfjQUIPWlopGbFAxGbio6D+lNansaJWAmiik5oSuUgLYplK1N6032RaF70M2KDxUdUMKKK6r4XfDvVPit480fwto8Re81CcR7sfLEg5eRvQKoJP0plxjKpJQirtn2D/wAE2/gW15qV78TdUhH2e23WWkqwyWkIxNL7YB2A/wC0/pX6EVgeA/BemfDrwdpHhrR4Fg07TbdbeJQOWwOWPqzHLE9ySa363Ssj9sy3BLAYaNFb7v16hRRRTPUCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAOH+MPwh0L41eC7rw9rkIww3212qgy2swBCyJn0zyO4JHevya+Lfwn134N+Mrzw9rtuUliO6C4VT5dzEfuyIe4P6EEHkV+zteY/Hz4DaH8evB8mlaiq2upwBn0/U1TMltIQPzQ4AZe+B3AIznDmR8ln2SRzOn7WlpVjt5+T/Q/HhutITXUfEr4c658K/F174e8QWbWl9bNweqSpn5ZEP8AEpHIP8iCK5XrXJsfjMqcqcnCas10EpaKaaRIlFFFNFCGkoNJT8igzikNLtpppjEp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QAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAH//Z";

                    if (base64String != null && !base64String.isEmpty()) {
                        // Loại bỏ phần tiền tố "data:image/jpeg;base64," nếu có
                        String base64Image = base64String.split(",")[1];

                        // Chuyển đổi base64 thành byte[]
                        logoBytes = java.util.Base64.getDecoder().decode(base64Image);
                    }

                    // Tạo QR code với logo và chuyển thành Base64
                    String qrCodeBase64 = VietQRUtil.generateTransactionQRWithLogoBase64(vietQRGenerateDTO, logoBytes);
                    vietQRDTO.setQrCodeBase64("data:image/jpg;base64,"+qrCodeBase64);
                    vietQRDTO.setImgId(bankCaiTypeDTO.getImgId());
                    vietQRDTO.setExisting(0);
                    vietQRDTO.setOrderId(StringUtil.getValueNullChecker(dto.getOrderId()));
                    vietQRDTO.setAdditionalData(new ArrayList<>());
                    if (dto.getAdditionalData() != null && !dto.getAdditionalData().isEmpty()) {
                        vietQRDTO.setAdditionalData(dto.getAdditionalData());
                    }
                    vietQRDTO.setServiceCode(StringUtil.getValueNullChecker(dto.getServiceCode()));
                    vietQRDTO.setSubTerminalCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                    result = vietQRDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E26");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } catch (Exception e) {
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.error("VietQRController: ERROR: generateQRCustomer: " + e.getMessage() + " at: " + System.currentTimeMillis());
            }
        }

        if (Objects.nonNull(terminalBankReceiveEntity)) {
            sendMessageDynamicQrToQrBox("",
                    terminalBankReceiveEntity.getRawTerminalCode() != null ?
                            terminalBankReceiveEntity.getRawTerminalCode() : "",
                    vietQRDTO, "", vietQRDTO.getQrCode(), dto.getNote()
            );
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @Async
    protected void insertNewTransactionFlow2(String qrCode, String transcationUUID,
                                             AccountBankReceiveEntity accountBankReceiveEntity,
                                             VietQRMMSCreateDTO dto,
                                             long time) {
        try {
            TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
            transactionEntity.setId(transcationUUID);
            transactionEntity.setBankAccount(accountBankReceiveEntity.getBankAccount());
            transactionEntity.setBankId(accountBankReceiveEntity.getId());
            transactionEntity.setContent(dto.getContent());
            transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
            transactionEntity.setTime(time);
            transactionEntity.setRefId("");
            transactionEntity.setType(0);
            transactionEntity.setStatus(0);
            transactionEntity.setTraceId("");
            transactionEntity.setTransType("C");
            transactionEntity.setReferenceNumber("");
            transactionEntity.setOrderId(dto.getOrderId());
            transactionEntity.setSign(dto.getSign());
            transactionEntity.setTimePaid(time);
            transactionEntity.setTerminalCode(dto.getTerminalCode() != null ? dto.getTerminalCode() : "");
            transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
            transactionEntity.setQrCode(qrCode);
            transactionEntity.setUserId(accountBankReceiveEntity.getUserId());
            transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
            transactionEntity.setStatusResponse(0);
            transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
            transactionEntity.setServiceCode(dto.getServiceCode());
            transactionReceiveService.insertTransactionReceive(transactionEntity);
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("insertNewTransaction - end generateVietQRMMS at: " + endTimeLong);
        } catch (Exception e) {
            logger.error("insertNewTransaction - generateVietQRMMS: ERROR: " + e.toString());
        }
    }


    private ResponseMessageDTO sendMessageDynamicQrToQrBox(String transactionUUID, String boxCode, VietQRDTO result,
                                                           String terminalName, String qr, String note) {
        ResponseMessageDTO responseMessageDTO = null;
        try {
            String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(boxCode);
            Map<String, String> data = new HashMap<>();
            data.put("notificationType", NotificationUtil.getNotiSendDynamicQr());
            data.put("amount", StringUtil.formatNumberAsString(result.getAmount()));
            data.put("qrCode", qr);
            socketHandler.sendMessageToBoxId(boxId, data);

            try {
                ObjectMapper mapper = new ObjectMapper();
                DynamicQRBoxDTO dynamicQRBoxDTO = new DynamicQRBoxDTO();
                dynamicQRBoxDTO.setNotificationType(NotificationUtil.getNotiSendDynamicQr());
                dynamicQRBoxDTO.setAmount(StringUtil.formatNumberAsString(result.getAmount()));
                dynamicQRBoxDTO.setQrCode(qr);
                mqttMessagingService
                        .sendMessageToBoxId(boxId, mapper.writeValueAsString(dynamicQRBoxDTO));
            } catch (Exception e) {

            }
            responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
            return responseMessageDTO;
        } catch (Exception e) {
            logger.error("insertNewTransaction - sendMessageDynamicQrToQrBox: ERROR: " + e.toString()
                    + " at: " + System.currentTimeMillis());
            responseMessageDTO = new ResponseMessageDTO("FAILED", "E05");
        }
        return responseMessageDTO;
    }

    private boolean checkRequestBodyFlow2(VietQRCreateCustomerDTO dto) {
        boolean result = false;
        try {
            // content up to 19
            // orderId up to 13
            String content = "";
            String orderId = "";
            if (dto.getContent() != null) {
                content = dto.getContent();
            }
            if (dto.getOrderId() != null) {
                orderId = dto.getOrderId();
            }
            if (dto != null
                    && content.length() <= 19
                    && orderId.length() <= 13
                    && dto.getAmount() != null && !dto.getBankAccount().trim().isEmpty()
                    && dto.getBankAccount() != null && !dto.getBankAccount().trim().isEmpty()
                    && dto.getBankCode() != null && dto.getBankCode().equals("MB")
                    && StringUtil.isLatinAndNumeric(content)) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("checkRequestBody: ERROR: " + e.toString());
        }
        return result;
    }

    private ResponseMessageDTO requestVietQRMMS(VietQRMMSRequestDTO dto) {
        ResponseMessageDTO result = null;
        LocalDateTime requestLDT = LocalDateTime.now();
        long requestTime = requestLDT.toEpochSecond(ZoneOffset.UTC);
        logger.info("requestVietQRMMS: start request QR to MB at: " + requestTime);
        try {
            UUID clientMessageId = UUID.randomUUID();
            Map<String, Object> data = new HashMap<>();
            data.put("terminalID", dto.getTerminalId());
            data.put("qrcodeType", 4);
            data.put("partnerType", 2);
            data.put("initMethod", 12);
            data.put("transactionAmount", dto.getAmount());
            data.put("billNumber", "");
            data.put("additionalAddress", 0);
            data.put("additionalMobile", 0);
            data.put("additionalEmail", 0);
            data.put("referenceLabelCode", dto.getOrderId());
            String content = "";
            if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                content = dto.getContent();
            } else {
                String traceId = "VQR" + RandomCodeUtil.generateRandomUUID();
                content = traceId;
            }
            data.put("transactionPurpose", content);
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(EnvironmentUtil.getBankUrl()
                            + "ms/offus/public/payment-service/payment/v1.0/createqr")
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(
                            EnvironmentUtil.getBankUrl()
                                    + "ms/offus/public/payment-service/payment/v1.0/createqr")
                    .build();
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("clientMessageId", clientMessageId.toString())
                    .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                    .header("username", EnvironmentUtil.getUsernameAPI())
                    .header("Authorization", "Bearer " + dto.getToken())
                    .body(BodyInserters.fromValue(data))
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("requestVietQRMMS: RESPONSE: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("data") != null) {
                    if (rootNode.get("data").get("qrcode") != null) {
                        String qrCode = rootNode.get("data").get("qrcode").asText();
                        logger.info("requestVietQRMMS: RESPONSE qrcode: " + qrCode);
                        result = new ResponseMessageDTO("SUCCESS", qrCode);
                    } else {
                        logger.info("requestVietQRMMS: RESPONSE qrcode is null");
                    }
                } else {
                    logger.info("requestVietQRMMS: RESPONSE data is null");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.error("requestVietQRMMS: RESPONSE: ERROR " + response.statusCode().value() + " - " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("errorCode") != null) {
                    String getMessageBankCode = getMessageBankCode(rootNode.get("errorCode").asText());
                    result = new ResponseMessageDTO("FAILED", getMessageBankCode);
                } else {
                    logger.info("requestVietQRMMS: RESPONSE data is null");
                }
            }
        } catch (Exception e) {
            logger.error("requestVietQRMMS: ERROR: " + e.toString());
        } finally {
            LocalDateTime responseLDT = LocalDateTime.now();
            long responseTime = responseLDT.toEpochSecond(ZoneOffset.UTC);
            logger.info("requestVietQRMMS: response from MB at: " + responseTime);
        }
        return result;
    }

    private String getRandomBillId() {
        String result = "";
        try {
            result = EnvironmentUtil.getPrefixBidvBillIdCommon() + DateTimeUtil.getCurrentWeekYear() +
                    StringUtil.convertToHexadecimal(DateTimeUtil.getMinusCurrentDate()) + RandomCodeUtil.generateRandomId(4);
        } catch (Exception e) {
            logger.error("getRandomBillId: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private ResponseMessageDTO insertNewCustomerInvoiceTransBIDV(VietQRCreateDTO dto,
                                                                 AccountBankGenerateBIDVDTO bidvdto, String billId) {
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        logger.info("QR generate - start insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            long amount = 0;
            if (Objects.nonNull(bidvdto) && !StringUtil.isNullOrEmpty(billId)) {
                if (!StringUtil.isNullOrEmpty(bidvdto.getCustomerId())) {
                    CustomerInvoiceEntity entity = new CustomerInvoiceEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setCustomerId(bidvdto.getCustomerId());
                    try {
                        amount = Long.parseLong(dto.getAmount());
                    } catch (Exception e) {
                        logger.error("VietQRController: ERROR: insertNewCustomerInvoiceTransBIDV: " + e.getMessage());
                    }
                    entity.setAmount(amount);
                    entity.setBillId(billId);
                    entity.setStatus(0);
                    entity.setType(1);
                    entity.setName("");
                    entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    entity.setTimePaid(0L);
                    entity.setInquire(0);
                    entity.setQrType(1);
                    customerInvoiceService.insert(entity);
                    responseMessageDTO = new ResponseMessageDTO("SUCCESS", "");
                } else {
                    responseMessageDTO = new ResponseMessageDTO("FAILED", "");
                }
            } else {
                responseMessageDTO = new ResponseMessageDTO("FAILED", "");
            }
        } catch (Exception e) {
            logger.error("Error at insertNewCustomerInvoiceTransBIDV: " + e.toString());
        } finally {
            logger.info("QR generate - end insertNewCustomerInvoiceTransBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        }
        return responseMessageDTO;
    }

    private void insertNewTransactionBIDV(UUID transcationUUID, VietQRBIDVCreateDTO dto,
                                          boolean isFromMerchantSync,String traceId,
                                          AccountBankReceiveEntity entity) {
        logger.info("QR generate - start insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            if (Objects.nonNull(entity)) {
                TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
                transactionEntity.setId(transcationUUID.toString());
                transactionEntity.setBankAccount(entity.getBankAccount());
                transactionEntity.setBankId(entity.getId());
                transactionEntity.setContent(dto.getContent());
                transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
                transactionEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                transactionEntity.setRefId("");
                transactionEntity.setType(0);
                transactionEntity.setStatus(0);
                transactionEntity.setTraceId(traceId);
                transactionEntity.setTimePaid(0);
                transactionEntity.setTerminalCode(dto.getTerminalCode());
                transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                transactionEntity.setQrCode(dto.getQr());
                transactionEntity.setUserId(entity.getUserId());
                transactionEntity.setOrderId(dto.getOrderId());
                transactionEntity.setNote(dto.getNote());
                transactionEntity.setStatusResponse(0);
                transactionEntity.setUrlLink(dto.getUrlLink());
                transactionEntity.setTransType("C");
                transactionEntity.setReferenceNumber("");
                transactionEntity.setSign(dto.getSign());
                transactionEntity.setBillId(dto.getBillId());
                //
                if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                    transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
                    transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
                    transactionEntity.setCustomerName(dto.getCustomerName());
                }
                transactionReceiveService.insertTransactionReceive(transactionEntity);
                logger.info("After insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
            }
        } catch (Exception e) {
            logger.error("Error at insertNewTransactionBIDV: " + e.toString());
        } finally {
            logger.info("QR generate - end insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        }
    }

    private void insertNewTransactionBIDV(UUID transcationUUID, VietQRBIDVCreateDTO dto,
                                          boolean isFromMerchantSync,String traceId,
                                          AccountBankGenerateBIDVDTO accountBank) {
        logger.info("QR generate - start insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        try {
            if (Objects.nonNull(accountBank)) {
                TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
                transactionEntity.setId(transcationUUID.toString());
                transactionEntity.setBankAccount(accountBank.getBankAccount());
                transactionEntity.setBankId(accountBank.getId());
                transactionEntity.setContent(dto.getContent());
                transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
                transactionEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                transactionEntity.setRefId("");
                transactionEntity.setType(0);
                transactionEntity.setStatus(0);
                transactionEntity.setTraceId(traceId);
                transactionEntity.setTimePaid(0);
                transactionEntity.setTerminalCode(dto.getTerminalCode());
                transactionEntity.setSubCode(StringUtil.getValueNullChecker(dto.getSubTerminalCode()));
                transactionEntity.setQrCode(dto.getQr());
                transactionEntity.setUserId(accountBank.getUserId());
                transactionEntity.setOrderId(dto.getOrderId());
                transactionEntity.setNote(dto.getNote());
                transactionEntity.setStatusResponse(0);
                transactionEntity.setUrlLink(dto.getUrlLink());
                transactionEntity.setTransType("C");
                transactionEntity.setReferenceNumber("");
                transactionEntity.setSign(dto.getSign());
                transactionEntity.setBillId(dto.getBillId());
                //
                if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                    transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
                    transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
                    transactionEntity.setCustomerName(dto.getCustomerName());
                }
                transactionReceiveService.insertTransactionReceive(transactionEntity);
                logger.info("After insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
            }
        } catch (Exception e) {
            logger.error("Error at insertNewTransactionBIDV: " + e.toString());
        } finally {
            logger.info("QR generate - end insertNewTransactionBIDV at: " + DateTimeUtil.getCurrentDateTimeUTC());
        }
    }

    // isFromBusinessSync: From customer with extra flow - customer-sync.
    @Async
    protected void insertNewTransaction(UUID transcationUUID, String traceId, VietQRCreateDTO dto, VietQRDTO result,
                                        String orderId, String sign, boolean isFromMerchantSync) {
        LocalDateTime startTime = LocalDateTime.now();
        long startTimeLong = startTime.toEpochSecond(ZoneOffset.UTC);
        logger.info("QR generate - start insertNewTransaction at: " + startTimeLong);
        logger.info("QR generate - insertNewTransaction data: " + result.toString());
        try {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            // 2. Insert transaction_receive if branch_id and business_id != null
            // 3. Insert transaction_receive_branch if branch_id and business_id != null
            IAccountBankUserQR accountBankEntity = accountBankReceiveService.getAccountBankUserQRById(dto.getBankId());
            if (accountBankEntity != null) {
                LocalDateTime currentDateTime = LocalDateTime.now();
                TransactionReceiveEntity transactionEntity = new TransactionReceiveEntity();
                transactionEntity.setId(transcationUUID.toString());
                transactionEntity.setBankAccount(accountBankEntity.getBankAccount());
                transactionEntity.setBankId(dto.getBankId());
                transactionEntity.setContent(traceId + " " + dto.getContent());
                transactionEntity.setAmount(Long.parseLong(dto.getAmount()));
                transactionEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                transactionEntity.setRefId("");
                transactionEntity.setType(0);
                transactionEntity.setStatus(0);
                transactionEntity.setTraceId(traceId);
                transactionEntity.setTimePaid(0);
                transactionEntity.setTerminalCode(result.getTerminalCode() != null ? result.getTerminalCode() : "");
                transactionEntity.setSubCode(StringUtil.getValueNullChecker(result.getSubTerminalCode()));
                transactionEntity.setQrCode("");
                transactionEntity.setUserId(accountBankEntity.getUserId());
                transactionEntity.setOrderId(orderId);
                transactionEntity.setNote(dto.getNote() != null ? dto.getNote() : "");
                transactionEntity.setStatusResponse(0);
                transactionEntity.setUrlLink(dto.getUrlLink() != null ? dto.getUrlLink() : "");
                if (dto.getTransType() != null) {
                    transactionEntity.setTransType(dto.getTransType());
                } else {
                    transactionEntity.setTransType("C");
                }
                transactionEntity.setReferenceNumber("");
                transactionEntity.setOrderId(orderId);
                transactionEntity.setServiceCode(dto.getServiceCode());
                transactionEntity.setSign(sign);
                //
                if (dto.getTransType() != null && dto.getTransType().trim().equalsIgnoreCase("D")) {
                    transactionEntity.setCustomerBankAccount(dto.getCustomerBankAccount());
                    transactionEntity.setCustomerBankCode(dto.getCustomerBankCode());
                    transactionEntity.setCustomerName(dto.getCustomerName());
                }
                transactionReceiveService.insertTransactionReceive(transactionEntity);
                LocalDateTime afterInsertTransactionTime = LocalDateTime.now();
                long afterInsertTransactionTimeLong = afterInsertTransactionTime.toEpochSecond(ZoneOffset.UTC);
                logger.info("QR generate - after insertTransactionReceive at: " + afterInsertTransactionTimeLong);

                // insert notification
                UUID notificationUUID = UUID.randomUUID();
                NotificationEntity notiEntity = new NotificationEntity();
                String message = NotificationUtil.getNotiDescNewTransPrefix2()
                        + NotificationUtil.getNotiDescNewTransSuffix1()
                        + nf.format(Double.parseDouble(dto.getAmount()))
                        + NotificationUtil
                        .getNotiDescNewTransSuffix2();

                if (!isFromMerchantSync) {
                    // push notification
                    Map<String, String> data = new HashMap<>();
                    data.put("notificationType", NotificationUtil.getNotiTypeNewTransaction());
                    data.put("notificationId", notificationUUID.toString());
                    data.put("bankCode", result.getBankCode());
                    data.put("bankName", result.getBankName());
                    data.put("bankAccount", result.getBankAccount());
                    data.put("userBankName", result.getUserBankName());
                    data.put("amount", result.getAmount());
                    data.put("content", result.getContent());
                    data.put("qrCode", result.getQrCode());
                    data.put("imgId", result.getImgId());
                    socketHandler.sendMessageToUser(dto.getUserId(), data);
                }

                notiEntity.setId(notificationUUID.toString());
                notiEntity.setRead(false);
                notiEntity.setMessage(message);
                notiEntity.setTime(currentDateTime.toEpochSecond(ZoneOffset.UTC));
                notiEntity.setType(NotificationUtil.getNotiTypeNewTransaction());
                notiEntity.setUserId(dto.getUserId());
                notiEntity.setData(transcationUUID.toString());
                notificationService.insertNotification(notiEntity);
                LocalDateTime afterInsertNotificationTransaction = LocalDateTime.now();
                long afterInsertNotificationTransactionLong = afterInsertNotificationTransaction
                        .toEpochSecond(ZoneOffset.UTC);
                logger.info("QR generate - after InsertNotificationTransaction at: "
                        + afterInsertNotificationTransactionLong);
            }
        } catch (Exception e) {
            logger.error("Error at insertNewTransaction: " + e.toString());
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeLong = endTime.toEpochSecond(ZoneOffset.UTC);
            logger.info("QR generate - end insertNewTransaction at: " + endTimeLong);
        }
    }

    private void logUserInfo(String token){
        LocalDateTime currentDateTime = LocalDateTime.now();
        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
        String secretKey = "mySecretKey";
        String jwtToken = token.substring(7); // remove "Bearer " from the beginning
        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
        String user = (String) claims.get("user");
        if (user != null) {
            String decodedUser = new String(Base64.getDecoder().decode(user));
            logger.info("qr/generate-customer - user " + decodedUser + " call at " + time);
        } else {
            logger.info("qr/generate-customer - Sytem User call at " + time);
        }
    }

    String getMessageBankCode(String errBankCode) {
        switch (errBankCode) {
            case "404":
                return "E165";
            case "203":
                return "E165";
            case "205":
                return "E166";
            default:
                return "E05";
        }
    }

}
