package com.vietqr.org.controller.bidv;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.mapper.ErrorCodeMapper;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.service.mqtt.MqttMessagingService;
import com.vietqr.org.util.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.bidv.CustomerInvoiceDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceDataDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceDetailDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInfoDataDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceDetailDTO.CustomerInvoiceItemDetailDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInsertDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceRequestBankDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInsertDTO.InvoiceItemDTO;
import com.vietqr.org.dto.bidv.CustomerInvoicePaymentRequestDTO;
import com.vietqr.org.dto.bidv.CustomerItemInvoiceDataDTO;
import com.vietqr.org.dto.bidv.CustomerVaInfoDataDTO;
import com.vietqr.org.dto.bidv.ResponseMessageBidvDTO;
import com.vietqr.org.dto.bidv.VietQRVaRequestDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceDTO.InvoiceDTO;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.entity.bidv.CustomerInvoiceTransactionEntity;
import com.vietqr.org.entity.bidv.CustomerItemInvoiceEntity;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.bidv.CustomerInvoiceTransactionService;
import com.vietqr.org.service.bidv.CustomerItemInvoiceService;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CustomerInvoiceController {
    private static final Logger logger = Logger.getLogger(CustomerInvoiceController.class);

    @Autowired
    CustomerInvoiceService customerInvoiceService;

    @Autowired
    CustomerItemInvoiceService customerItemInvoiceService;

    @Autowired
    CustomerVaService customerVaService;

    @Autowired
    AccountCustomerService accountCustomerService;

    @Autowired
    CustomerInvoiceTransactionService customerInvoiceTransactionService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FcmTokenService fcmTokenService;

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    TransactionBidvService transactionBidvService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    TerminalService terminalService;

    @Autowired
    TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    TransactionTerminalTempService transactionTerminalTempService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    CustomerErrorLogService customerErrorLogService;

    @Autowired
    LarkAccountBankService larkAccountBankService;

    @Autowired
    GoogleChatAccountBankService googleChatAccountBankService;

    @Autowired
    TelegramAccountBankService telegramAccountBankService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    MqttMessagingService mqttMessagingService;

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    TransReceiveTempService transReceiveTempService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    TransactionReceiveLogService transactionReceiveLogService;

    @Autowired
    AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    SocketHandler socketHandler;

    @Autowired
    BankReceiveConnectionService bankReceiveConnectionService;

    @Autowired
    MerchantSyncService merchantSyncService;

    @Autowired
    MerchantConnectionService merchantConnectionService;

    // API get invoice for BIDV
    // CustomerInvoiceDTO
    @PostMapping("bidv/getbill")
    public ResponseEntity<Object> getbill(
            @RequestBody CustomerInvoiceRequestBankDTO dto,
            @RequestHeader("Authorization") String token) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // for check valid token
            String accessKey = EnvironmentUtil.getBidvAccessKey();
            String keyDecoded = JWTUtil.getKeyFromToken(token);
            String accessKeyFromToken = getAccessKeyFromToken(keyDecoded);
            // for authen, valid dto, valid checksum
            String secretKey = EnvironmentUtil.getBidvSecretKey();
            String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
            //
            // check valid token
            if (accessKeyFromToken != null && accessKeyFromToken.equals(accessKey)) {
                // check valid dto
                if (dto != null && dto.getCustomer_id() != null && dto.getService_id() != null
                        && dto.getChecksum() != null) {
                    logger.info("BIDV: getbill: token: " + token);
                    logger.info("BIDV: getbill: customer_id: " + dto.getCustomer_id());
                    logger.info("BIDV: getbill: service_id: " + dto.getService_id());
                    logger.info("BIDV: getbill: getChecksum: " + dto.getChecksum());
                    System.out.println("dto.getService_id(): " + dto.getService_id());
                    System.out.println("serviceId: " + serviceId);
                    if (dto.getService_id().equals(serviceId)) {
                        // check valid checksum
                        String checksum = BankEncryptUtil.generateMD5GetBillForBankChecksum(secretKey, serviceId,
                                dto.getCustomer_id());
                        logger.info("BIDV: checksum generated from data request body: " + checksum);
                        if (BankEncryptUtil.isMatchChecksum(dto.getChecksum(), checksum)) {
                            // get bill info
                            // get customer va info
                            List<CustomerInvoiceInfoDataDTO> customerInvoiceInfoDataDTOs = customerInvoiceService
                                    .getCustomerInvoiceInfo(dto.getCustomer_id());

                            if (customerInvoiceInfoDataDTOs != null) {
                                CustomerVaInfoDataDTO customerVaInfoDataDTO = customerVaService
                                        .getCustomerVaInfo(dto.getCustomer_id());
                                CustomerInvoiceDTO customerInvoiceDTO = new CustomerInvoiceDTO();
                                customerInvoiceDTO.setResult_code("000");
                                customerInvoiceDTO.setResult_desc("success");
//                                customerInvoiceDTO.setService_id(dto.getService_id());
                                customerInvoiceDTO.setCustomer_id(customerVaInfoDataDTO.getCustomer_id());
                                customerInvoiceDTO.setCustomer_name(customerVaInfoDataDTO.getCustomer_name());
                                customerInvoiceDTO.setCustomer_addr("");
                                List<InvoiceDTO> invoiceDTOs = new ArrayList<>();
                                invoiceDTOs = customerInvoiceInfoDataDTOs.stream()
                                                .map(item -> {
                                                    InvoiceDTO invoiceDTO = new InvoiceDTO();
                                                    invoiceDTO.setType(item.getType());
                                                    invoiceDTO.setAmount(item.getAmount());
                                                    invoiceDTO.setBill_id(item.getBill_id());
                                                    return invoiceDTO;
                                                })
                                                        .collect(Collectors.toList());
//                                invoiceDTO.setType(customerInvoiceInfoDataDTO.getType());
//                                invoiceDTO.setAmount(customerInvoiceInfoDataDTO.getAmount());
//                                invoiceDTO.setBill_id(customerInvoiceInfoDataDTO.getBill_id());
                                customerInvoiceDTO.setData(invoiceDTOs);
                                // update inquired
//                                customerInvoiceService.updateInquiredInvoiceByBillId(1,
//                                        customerInvoiceInfoDataDTO.getBill_id());
                                List<String> billIds = customerInvoiceInfoDataDTOs.stream()
                                        .map(CustomerInvoiceInfoDataDTO::getBill_id)
                                        .collect(Collectors.toList());
                                customerInvoiceService.updateInquiredInvoiceByBillIds(1,
                                        billIds);
                                // response
                                result = customerInvoiceDTO;
                                httpStatus = HttpStatus.OK;
                            } else {
                                // khách hàng không có hoá đơn nào
                                result = new ResponseMessageBidvDTO("012", "Khách hàng không có hóa đơn");
                                httpStatus = HttpStatus.OK;
                            }
                        } else {
                            // check sum không hợp lệ
                            result = new ResponseMessageBidvDTO("004", "Checksum không hợp lệ");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        // service ID không đúng
                        result = new ResponseMessageBidvDTO("006", "Service ID không đúng/ không tồn tại");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    // request body không hợp lệ
                    result = new ResponseMessageBidvDTO("001", "Thiếu tham số");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                // token không có quyền truy vấn API này
                result = new ResponseMessageBidvDTO("003", "Thông tin đăng nhập không đúng");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getbill: ERROR: " + e.toString());
            result = new ResponseMessageBidvDTO("031", "Có lỗi phát sinh từ hệ thống");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("bidv/paybill")
    public ResponseEntity<ResponseMessageBidvDTO> paybill(
            @RequestBody CustomerInvoicePaymentRequestDTO dto,
            @RequestHeader("Authorization") String token) {
        ResponseMessageBidvDTO result = null;
        HttpStatus httpStatus = null;
        AccountBankReceiveEntity accountBankReceiveEntity = null;
        CustomerInvoiceDataDTO customerInvoiceDataDTO = null;
        try {
            logger.info("CustomerInvoiceController: INFO : receive paybill from BIDV: " + dto.toString()
                    + " at: " + System.currentTimeMillis());
            // for check valid token
            String accessKey = EnvironmentUtil.getBidvAccessKey();
            String keyDecoded = JWTUtil.getKeyFromToken(token);
            String accessKeyFromToken = getAccessKeyFromToken(keyDecoded);
            // for authen, valid dto, valid checksum
            String secretKey = EnvironmentUtil.getBidvSecretKey();
            String serviceId = EnvironmentUtil.getBidvLinkedServiceId();
            //
            // check valid token
            if (accessKeyFromToken != null && accessKeyFromToken.equals(accessKey)) {
                // check valid dto
                if (dto != null && dto.getTrans_id() != null && dto.getTrans_date() != null
                        && dto.getCustomer_id() != null && dto.getService_id() != null && dto.getBill_id() != null
                        && dto.getAmount() != null && dto.getChecksum() != null) {
                    if (dto.getService_id().equals(serviceId)) {
                        // check valid checksum
                        String checksum = BankEncryptUtil.generateMD5PayBillForBankChecksum(secretKey,
                                dto.getTrans_id(), dto.getBill_id(), dto.getAmount());
                        if (BankEncryptUtil.isMatchChecksum(dto.getChecksum(), checksum)) {
//                        if (true) {
                            // check customer_id tồn tại
                            String checkExistedCustomerId = customerVaService
                                    .checkExistedCustomerId(dto.getCustomer_id());
                            // 0: Default là hóa đơn BIDV
                            // 1: Transaction_receive
                            // 2: Static qr
                            if (checkExistedCustomerId != null && !checkExistedCustomerId.trim().isEmpty()) {
                                // check bill_id tồn tại
                                customerInvoiceDataDTO = customerInvoiceService
                                        .getCustomerInvoiceByBillId(dto.getBill_id());
                                // Transaction QR
                                Long paymentAmount = 0L;
                                Long amountParsing = Long.parseLong(dto.getAmount());
                                if (amountParsing != null) {
                                    paymentAmount = amountParsing;
                                }
                                if (customerInvoiceDataDTO != null
                                        && customerInvoiceDataDTO.getQrType() == 1
//                                        && customerInvoiceDataDTO.getStatus() == 0
                                        && customerInvoiceDataDTO.getAmount().equals(paymentAmount)) {

                                        if (customerInvoiceDataDTO.getStatus() == 0) {
                                    // check số tiền có khớp hay không
                                    System.out.println("customerInvoiceDataDTO.getAmount(): "
                                            + customerInvoiceDataDTO.getAmount());
                                    System.out.println("paymentAmount: " + paymentAmount);
//                                    if (customerInvoiceDataDTO.getAmount().equals(paymentAmount)) {
                                        // transaction_receive type = 0
                                        // insert invoice payment
                                        UUID uuid = UUID.randomUUID();
                                        CustomerInvoiceTransactionEntity customerInvoiceTransactionEntity = new CustomerInvoiceTransactionEntity();
                                        customerInvoiceTransactionEntity.setId(uuid.toString());
                                        customerInvoiceTransactionEntity.setTrans_id(dto.getTrans_id());
                                        customerInvoiceTransactionEntity.setTrans_date(dto.getTrans_date());
                                        customerInvoiceTransactionEntity.setCustomer_id(dto.getCustomer_id());
                                        customerInvoiceTransactionEntity.setService_id(dto.getService_id());
                                        customerInvoiceTransactionEntity.setBill_id(dto.getBill_id());
                                        customerInvoiceTransactionEntity.setAmount(dto.getAmount());
                                        customerInvoiceTransactionEntity.setChecksum(dto.getChecksum());
                                        customerInvoiceTransactionService.insert(customerInvoiceTransactionEntity);
                                        // update status + timepaid invoice
                                        LocalDateTime currentDateTime = LocalDateTime.now();
                                        long timePaid = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                        customerInvoiceService.updateCustomerVaInvoice(1,
                                                timePaid, dto.getBill_id());

                                        accountBankReceiveEntity = accountBankReceiveService
                                                .getAccountBankByCustomerIdAndByServiceId(dto.getCustomer_id());
                                        TransactionBidvEntity transactionBidvEntity = new TransactionBidvEntity();
                                        transactionBidvEntity.setId(UUID.randomUUID().toString());
                                        transactionBidvEntity.setCustomerId(dto.getCustomer_id());
                                        transactionBidvEntity.setServiceId(dto.getService_id());
                                        transactionBidvEntity.setAmount(dto.getAmount());
                                        transactionBidvEntity.setBillId(dto.getBill_id());
                                        transactionBidvEntity.setTransDate(dto.getTrans_date());
                                        transactionBidvEntity.setCheckSum(dto.getChecksum());
                                        Thread thread = new Thread(() -> {
                                            transactionBidvService.insert(transactionBidvEntity);
                                        });
                                        thread.start();
                                        // response
                                        result = new ResponseMessageBidvDTO("000",
                                                "Thành công");
                                        httpStatus = HttpStatus.OK;
//                                            } else {
//                                                // nếu chưa: báo lỗi số tiền không khớp
////                                                result = new ResponseMessageBidvDTO("022",
////                                                        "Số tiền gửi lên không đúng với số tiền trong hóa đơn");
////                                                httpStatus = HttpStatus.BAD_REQUEST;
//                                                result = new ResponseMessageBidvDTO("000",
//                                                        "Thành công");
//                                                httpStatus = HttpStatus.OK;
//                                            }
                                    } else {
                                         // hoá đơn đã gạch nợ rồi
                                            result = new ResponseMessageBidvDTO("023",
                                                    "Hóa đơn đã gạch nợ rồi (mỗi hóa đơn chỉ gạch nợ 1 lần)");
                                            httpStatus = HttpStatus.OK;
//                                        result = new ResponseMessageBidvDTO("000",
//                                                "Thành công");
//                                        httpStatus = HttpStatus.OK;
                                    }
                                    // Hóa đơn BIDV
                                } else if (customerInvoiceDataDTO != null
                                        && customerInvoiceDataDTO.getQrType() == 0
                                        && customerInvoiceDataDTO.getStatus() == 0
                                        && customerInvoiceDataDTO.getAmount().equals(paymentAmount)) {
//                                        if (customerInvoiceDataDTO.getStatus() == 0) {
                                    // check số tiền có khớp hay không
                                    System.out.println("customerInvoiceDataDTO.getAmount(): "
                                            + customerInvoiceDataDTO.getAmount());
                                    System.out.println("paymentAmount: " + paymentAmount);
//                                    if (customerInvoiceDataDTO.getAmount().equals(paymentAmount)) {
                                        // nếu có: insert invoice payment + update trạng thái hoá đơn
                                        // insert invoice payment
                                        UUID uuid = UUID.randomUUID();
                                        CustomerInvoiceTransactionEntity customerInvoiceTransactionEntity = new CustomerInvoiceTransactionEntity();
                                        customerInvoiceTransactionEntity.setId(uuid.toString());
                                        customerInvoiceTransactionEntity.setTrans_id(dto.getTrans_id());
                                        customerInvoiceTransactionEntity.setTrans_date(dto.getTrans_date());
                                        customerInvoiceTransactionEntity.setCustomer_id(dto.getCustomer_id());
                                        customerInvoiceTransactionEntity.setService_id(dto.getService_id());
                                        customerInvoiceTransactionEntity.setBill_id(dto.getBill_id());
                                        customerInvoiceTransactionEntity.setAmount(dto.getAmount());
                                        customerInvoiceTransactionEntity.setChecksum(dto.getChecksum());
                                        customerInvoiceTransactionService.insert(customerInvoiceTransactionEntity);
                                        // update status + timepaid invoice
                                        LocalDateTime currentDateTime = LocalDateTime.now();
                                        long timePaid = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                                        customerInvoiceService.updateCustomerVaInvoice(1,
                                                timePaid, dto.getBill_id());
                                        // response
                                        result = new ResponseMessageBidvDTO("000",
                                                "Thành công");
                                        httpStatus = HttpStatus.OK;
//                                    } else {
//                                        // nếu chưa: báo lỗi số tiền không khớp
////                                                result = new ResponseMessageBidvDTO("022",
////                                                        "Số tiền gửi lên không đúng với số tiền trong hóa đơn");
////                                                httpStatus = HttpStatus.BAD_REQUEST;
//                                        result = new ResponseMessageBidvDTO("000",
//                                                "Thành công");
//                                        httpStatus = HttpStatus.OK;
//                                    }
//                                        } else {
//                                            // hoá đơn đã gạch nợ rồi
////                                            result = new ResponseMessageBidvDTO("023",
////                                                    "Hóa đơn đã gạch nợ rồi (mỗi hóa đơn chỉ gạch nợ 1 lần)");
////                                            httpStatus = HttpStatus.OK;
//                                            result = new ResponseMessageBidvDTO("000",
//                                                    "Thành công");
//                                            httpStatus = HttpStatus.OK;
//                                        }
                                    // static qr
                                } else if (Objects.nonNull(customerInvoiceDataDTO)
                                        && customerInvoiceDataDTO.getQrType() == 2) {
                                    // transaction_receive type = 1
                                    accountBankReceiveEntity = accountBankReceiveService
                                            .getAccountBankByCustomerIdAndByServiceId(dto.getCustomer_id());
                                    TransactionBidvEntity transactionBidvEntity = new TransactionBidvEntity();
                                    transactionBidvEntity.setId(UUID.randomUUID().toString());
                                    transactionBidvEntity.setCustomerId(dto.getCustomer_id());
                                    transactionBidvEntity.setServiceId(dto.getService_id());
                                    transactionBidvEntity.setAmount(dto.getAmount());
                                    transactionBidvEntity.setBillId(dto.getBill_id());
                                    transactionBidvEntity.setTransDate(dto.getTrans_date());
                                    transactionBidvEntity.setCheckSum(dto.getChecksum());
                                    Thread thread = new Thread(() -> {
                                        transactionBidvService.insert(transactionBidvEntity);
                                    });
                                    thread.start();
                                    result = new ResponseMessageBidvDTO("000",
                                            "Thành công");
                                    httpStatus = HttpStatus.OK;
                                } else if (Objects.nonNull(customerInvoiceDataDTO)) {
                                    // transaction_receive type = 2
                                    accountBankReceiveEntity = accountBankReceiveService
                                            .getAccountBankByCustomerIdAndByServiceId(dto.getCustomer_id());
                                    TransactionBidvEntity transactionBidvEntity = new TransactionBidvEntity();
                                    transactionBidvEntity.setId(UUID.randomUUID().toString());
                                    transactionBidvEntity.setCustomerId(dto.getCustomer_id());
                                    transactionBidvEntity.setServiceId(dto.getService_id());
                                    transactionBidvEntity.setAmount(dto.getAmount());
                                    transactionBidvEntity.setBillId(dto.getBill_id());
                                    transactionBidvEntity.setTransDate(dto.getTrans_date());
                                    transactionBidvEntity.setCheckSum(dto.getChecksum());
                                    Thread thread = new Thread(() -> {
                                        transactionBidvService.insert(transactionBidvEntity);
                                    });
                                    thread.start();
                                    // hoá đơn đã gạch nợ rồi
//                                        result = new ResponseMessageBidvDTO("023",
//                                                "Hóa đơn đã gạch nợ rồi (mỗi hóa đơn chỉ gạch nợ 1 lần)");
//                                        httpStatus = HttpStatus.OK;
                                    result = new ResponseMessageBidvDTO("000",
                                            "Thành công");
                                    httpStatus = HttpStatus.OK;
                                } else {
                                    // transaction_receive type = 2
                                    accountBankReceiveEntity = accountBankReceiveService
                                            .getAccountBankByCustomerIdAndByServiceId(dto.getCustomer_id());
                                    TransactionBidvEntity transactionBidvEntity = new TransactionBidvEntity();
                                    transactionBidvEntity.setId(UUID.randomUUID().toString());
                                    transactionBidvEntity.setCustomerId(dto.getCustomer_id());
                                    transactionBidvEntity.setServiceId(dto.getService_id());
                                    transactionBidvEntity.setAmount(dto.getAmount());
                                    transactionBidvEntity.setBillId(dto.getBill_id());
                                    transactionBidvEntity.setTransDate(dto.getTrans_date());
                                    transactionBidvEntity.setCheckSum(dto.getChecksum());
                                    Thread thread = new Thread(() -> {
                                        transactionBidvService.insert(transactionBidvEntity);
                                    });
                                    thread.start();
                                    // mã hoá đơn không tồn tại
//                                        result = new ResponseMessageBidvDTO("021",
//                                                "Mã hóa đơn không tồn tại");
//                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    result = new ResponseMessageBidvDTO("000",
                                            "Thành công");
                                    httpStatus = HttpStatus.OK;
                                }
                            } else {
                                // customer_id không tồn tại
                                result = new ResponseMessageBidvDTO("011",
                                        "Mã khách hàng không đúng/ không tồn tại");
                                httpStatus = HttpStatus.BAD_REQUEST;
                            }
                        } else {
                            // check sum không hợp lệ
                            result = new ResponseMessageBidvDTO("004", "Checksum không hợp lệ");
                            httpStatus = HttpStatus.BAD_REQUEST;
                        }
                    } else {
                        // service ID không đúng
                        result = new ResponseMessageBidvDTO("006", "Service ID không đúng/ không tồn tại");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    // request body không hợp lệ
                    result = new ResponseMessageBidvDTO("001", "Thiếu tham số");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                // token không có quyền truy vấn API này
                result = new ResponseMessageBidvDTO("003", "Thông tin đăng nhập không đúng");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            return new ResponseEntity<>(result, httpStatus);
        } catch (Exception e) {
            logger.error("paybill: ERROR: " + e.toString());
            result = new ResponseMessageBidvDTO("031", "Có lỗi phát sinh từ hệ thống");
            httpStatus = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(result, httpStatus);
        } finally {
            // 022: Số tiền ko khớp, 023: Hóa đơn đã gạch nợ, 021: Mã hóa đơn không tồn tại
            // push notification
            final ResponseMessageBidvDTO tempResult = result;
            AccountBankReceiveEntity finalAccountBankReceiveEntity = accountBankReceiveEntity;
            CustomerInvoiceDataDTO finalCustomerInvoiceDataDTO = customerInvoiceDataDTO;
            Thread thread = new Thread(() -> {
                //
                // khi result = 021, 022, 02 insert transaction mới
                if (tempResult.getResult_code().equals("000")
                || tempResult.getResult_code().equals("023")) {

//                    CustomerInvoiceDataDTO customerInvoiceDataDTO = customerInvoiceService
//                            .getCustomerInvoiceByBillId(dto.getBill_id());
                    // Transaction Receive Entity
                    Long paymentAmount = 0L;
                    paymentAmount = Long.parseLong(dto.getAmount());
                    if (finalCustomerInvoiceDataDTO != null
                            && finalCustomerInvoiceDataDTO.getQrType() == 1
                            && finalCustomerInvoiceDataDTO.getStatus() == 0
                            && finalCustomerInvoiceDataDTO.getAmount().equals(paymentAmount)) {
                        String orderId = "";
                        String sign = "";
                        String rawCode = "";
                        String boxIdRef = "";
                        ISubTerminalCodeDTO rawDTO = null;
                        if (Objects.nonNull(finalAccountBankReceiveEntity)) {
                            logger.info("bidv/paybill - bill_id detect: " + dto.getBill_id());
                            TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                                    .getTransactionReceiveByBillId(dto.getBill_id());

                            if (Objects.nonNull(transactionReceiveEntity)) {
                                orderId = transactionReceiveEntity.getOrderId();
                                sign = transactionReceiveEntity.getSign();
                                if (transactionReceiveEntity.getTerminalCode() != null
                                        && !transactionReceiveEntity.getTerminalCode().trim().isEmpty()) {
                                    TerminalEntity terminalEntity = terminalService
                                            .getTerminalByTerminalCode(
                                                    transactionReceiveEntity.getTerminalCode());
                                    if (terminalEntity != null) {
                                        rawCode = terminalEntity.getRawTerminalCode();
                                    } else {
                                        rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(
                                                transactionReceiveEntity.getTerminalCode());
                                        if (rawDTO != null) {
                                            rawCode = rawDTO.getRawCode();
                                            if (rawDTO.getQrType() == 2) {
                                                boxIdRef = rawDTO.getRawCode();
                                            }
                                        }
                                    }
                                }
                                String urlLink = transactionReceiveEntity.getUrlLink() != null
                                        ? transactionReceiveEntity.getUrlLink()
                                        : "";
                                getCustomerSyncEntities(transactionReceiveEntity.getId(), dto, transactionReceiveEntity,
                                        finalAccountBankReceiveEntity, DateTimeUtil.getCurrentDateTimeUTC(),
                                        orderId, sign, rawCode, urlLink, transactionReceiveEntity.getTerminalCode());
                                getCustomerSyncEntitiesV2(transactionReceiveEntity.getId(), dto, transactionReceiveEntity,
                                        finalAccountBankReceiveEntity, DateTimeUtil.getCurrentDateTimeUTC(),
                                        orderId, sign, rawCode, urlLink, transactionReceiveEntity.getTerminalCode());
                                updateTransaction(dto, transactionReceiveEntity, finalAccountBankReceiveEntity, boxIdRef, rawDTO);
                            }
                        }

                        //
                    } else if (Objects.nonNull(finalCustomerInvoiceDataDTO)
                            && finalCustomerInvoiceDataDTO.getQrType() == 2) {
                        // static qr
                        String rawCode = "";
                        String boxIdRef = "";
                        UUID transactionUUID = UUID.randomUUID();
                        ISubTerminalCodeDTO rawDTO = null;
                        TerminalEntity terminalEntity = terminalService.getTerminalByTerminalCode(finalCustomerInvoiceDataDTO.getName());
                        if (Objects.nonNull(terminalEntity)) {
                            rawCode = terminalEntity.getRawTerminalCode();
                        } else {
                            rawDTO = terminalBankReceiveService.getSubTerminalCodeBySubTerminalCode(
                                    finalCustomerInvoiceDataDTO.getName());
                            if (rawDTO != null) {
                                rawCode = rawDTO.getRawCode();
                                if (rawDTO.getQrType() == 2) {
                                    boxIdRef = rawDTO.getRawCode();
                                }
                            }
                        }

                        if (Objects.nonNull(finalAccountBankReceiveEntity)) {
                            getCustomerSyncEntities(transactionUUID.toString(), dto, rawCode,
                                    finalAccountBankReceiveEntity, DateTimeUtil.getCurrentDateTimeUTC());
                            insertNewTransaction(dto, transactionUUID.toString(), finalAccountBankReceiveEntity, boxIdRef, terminalEntity, rawDTO);
                        }
                        // hoa đơn BIDV
                    } else if (Objects.nonNull(finalCustomerInvoiceDataDTO)
                            && finalCustomerInvoiceDataDTO.getQrType() == 0
                            && finalCustomerInvoiceDataDTO.getStatus() == 0
                            && finalCustomerInvoiceDataDTO.getAmount().equals(paymentAmount)) {
                        String userId = "";
                        String userIdResult = customerVaService.getUserIdByCustomerId(dto.getCustomer_id());
                        if (userIdResult != null && !userIdResult.trim().isEmpty()) {
                            userId = userIdResult;
                        }
                        String notiType = NotificationUtil.getNotiTypePaymentSuccessVaInvoice();
                        String title = NotificationUtil.getNotiTitlePaymentSuccessVaInvoice();
                        String desc1 = NotificationUtil.getNotiDescPaymentSuccessVaInvoice1();
                        String desc2 = NotificationUtil.getNotiDescPaymentSuccessVaInvoice2();
                        String msg = desc1 + dto.getAmount() + desc2 + dto.getBill_id();
                        UUID notificationUUID = UUID.randomUUID();
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                        NotificationEntity notiEntity = new NotificationEntity();
                        notiEntity.setId(notificationUUID.toString());
                        notiEntity.setRead(false);
                        notiEntity.setMessage(msg);
                        notiEntity.setTime(time);
                        notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                        notiEntity.setUserId(userId);
                        notiEntity.setData(dto.getBill_id());
                        Map<String, String> data = new HashMap<>();
                        data.put("notificationType", notiType);
                        data.put("notificationId", notificationUUID.toString());
                        data.put("billId", dto.getBill_id());
                        data.put("customerId", dto.getCustomer_id());
                        data.put("amount", dto.getAmount());
                        data.put("timePaid", time + "");
                        pushNotification(title, msg, notiEntity, data, userId);
                    } else {
                        AccountBankReceiveEntity rawAccount = null;
                        if (Objects.isNull(finalAccountBankReceiveEntity)) {
                            rawAccount = accountBankReceiveService
                                    .getAccountBankByCustomerIdAndByServiceId(dto.getCustomer_id());
                        } else {
                            rawAccount = finalAccountBankReceiveEntity;
                        }
                        // qr khac
                        if (Objects.nonNull(rawAccount)) {
                            UUID transactionUUID = UUID.randomUUID();
                            logger.info("bidv/paybill - bill_id detect: " + dto.getBill_id());
                            getCustomerSyncEntities(transactionUUID.toString(), dto, "",
                                    rawAccount, DateTimeUtil.getCurrentDateTimeUTC());
                            insertNewTransaction(dto, transactionUUID.toString(), rawAccount, "", null, null);
                        }
                    }
                }
            });
            thread.start();
        }
    }

    private void getCustomerSyncEntitiesV2(String transReceiveId,
                                           CustomerInvoicePaymentRequestDTO dto,
                                           TransactionReceiveEntity transactionReceiveEntity,
                                           AccountBankReceiveEntity accountBankEntity,
                                           long time, String orderId, String sign, String rawTerminalCode,
                                           String urlLink, String terminalCode) {
        try {
            // 1. Check bankAccountEntity with sync = true (add sync boolean field)
            // 2. Find account_customer_bank by bank_id/bank_account AND auth = true.
            // 3. Find customer_sync and push data to customer.
            if (accountBankEntity.isSync() || accountBankEntity.isWpSync()) {
                TransactionBankCustomerDTO transactionBankCustomerDTO = new TransactionBankCustomerDTO();
                transactionBankCustomerDTO.setTransactionid(dto.getTrans_id());
                transactionBankCustomerDTO.setTransactiontime(time * 1000);
                transactionBankCustomerDTO.setReferencenumber(transactionReceiveEntity.getBillId());
                transactionBankCustomerDTO.setAmount(transactionReceiveEntity.getAmount());
                transactionBankCustomerDTO.setContent(transactionReceiveEntity.getContent());
                transactionBankCustomerDTO.setBankaccount(transactionReceiveEntity.getBankAccount());
                transactionBankCustomerDTO.setTransType(transactionReceiveEntity.getTransType());
                transactionBankCustomerDTO.setReciprocalAccount(StringUtil.getValueNullChecker(transactionReceiveEntity.getCustomerBankAccount()));
                transactionBankCustomerDTO.setReciprocalBankCode(StringUtil.getValueNullChecker(transactionReceiveEntity.getCustomerBankCode()));
                transactionBankCustomerDTO.setVa(StringUtil.getValueNullChecker(transactionReceiveEntity.getCustomerName()));
                transactionBankCustomerDTO.setValueDate(time);
                transactionBankCustomerDTO.setSign(sign);
                transactionBankCustomerDTO.setOrderId(orderId);
                if (!StringUtil.isNullOrEmpty(rawTerminalCode)) {
                    transactionBankCustomerDTO.setTerminalCode(rawTerminalCode);
                } else if (!StringUtil.isNullOrEmpty(terminalCode)) {
                    transactionBankCustomerDTO.setTerminalCode(terminalCode);
                } else {
                    transactionBankCustomerDTO.setTerminalCode("");
                }
                transactionBankCustomerDTO.setUrlLink(urlLink);
                transactionBankCustomerDTO.setSubTerminalCode(transactionReceiveEntity.getSubCode());
                logger.info("getCustomerSyncEntities: Order ID: " + orderId);
                logger.info("getCustomerSyncEntities: Signature: " + sign);
                List<BankReceiveConnectionEntity> bankReceiveConnectionEntities = new ArrayList<>();
                bankReceiveConnectionEntities = bankReceiveConnectionService
                        .getBankReceiveConnectionByBankId(accountBankEntity.getId());

                try {
                    if (bankReceiveConnectionEntities != null && !bankReceiveConnectionEntities.isEmpty()) {
                        List<String> merchantIds = bankReceiveConnectionEntities.stream()
                                .map(BankReceiveConnectionEntity::getMid) // Replace with the actual method to get the merchant ID
                                .distinct() // Ensures the list is unique
                                .collect(Collectors.toList());
                        List<MerchantSyncEntity> merchantSyncEntities = merchantSyncService.getMerchantSyncByIds(merchantIds);
                        if (merchantSyncEntities != null && !merchantSyncEntities.isEmpty()) {
                            for (MerchantSyncEntity entity: merchantSyncEntities) {
                                pushTransactionSyncForClientId(entity, transactionBankCustomerDTO);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("getCustomerSyncEntitiesV2 WSS: ERROR: " + e.toString());
                }

                if (bankReceiveConnectionEntities != null && !bankReceiveConnectionEntities.isEmpty()) {
                    int numThread = bankReceiveConnectionEntities.size();
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    try {
                        for (BankReceiveConnectionEntity bankReceiveConnectionEntity : bankReceiveConnectionEntities) {
                            MerchantConnectionEntity merchantConnectionEntity = merchantConnectionService
                                    .getMerchanConnectionById(bankReceiveConnectionEntity.getMidConnectId());
                            String errorCode = customerErrorLogService.getRetryErrorsByCustomerId(merchantConnectionEntity.getMid());
                            List<String> errorCodes = new ArrayList<>();
                            errorCodes = mapperErrors(errorCode);
                            if (merchantConnectionEntity != null) {
                                List<String> finalErrorCodes = errorCodes;
                                executorService.submit(() -> pushNewTransactionToCustomerSyncV2(transReceiveId, merchantConnectionEntity,
                                        transactionBankCustomerDTO, 1, finalErrorCodes));
                            }
                        }
                    } finally {
                        executorService.shutdown(); // Yêu cầu các luồng dừng khi hoàn tất công việc
                        try {
                            if (!executorService.awaitTermination(700, TimeUnit.SECONDS)) {
                                executorService.shutdownNow(); // Nếu vẫn chưa dừng sau 60 giây, cưỡng chế dừng
                            }
                        } catch (InterruptedException e) {
                            executorService.shutdownNow(); // Nếu bị ngắt khi chờ, cưỡng chế dừng
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("CustomerSync: Error: " + e.toString());
            System.out.println("CustomerSync: Error: " + e.toString());
        }
    }

    private void pushNewTransactionToCustomerSyncV2(String transReceiveId, MerchantConnectionEntity entity,
                                                    TransactionBankCustomerDTO dto, int retryCount, List<String> errorCodes) {
        ResponseMessageDTO result = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        if (retryCount > 1 && retryCount <= 5) {
            try {
                Thread.sleep(1000 * (retryCount - 1) + retryCount); // Sleep for 12000 milliseconds (12 seconds)
            } catch (InterruptedException e) {
                // Handle the exception if the thread is interrupted during sleep
                e.printStackTrace();
            }
        } else if (retryCount > 5 && retryCount <= 10) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // Handle the exception if the thread is interrupted during sleep
                e.printStackTrace();
            }
        }
        long time = DateTimeUtil.getCurrentDateTimeUTC();
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            logger.info("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
            logger.info("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            System.out.println("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
            System.out.println("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            TokenDTO tokenDTO = null;
            if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() &&
                    entity.getPassword() != null
                    && !entity.getPassword().trim().isEmpty()) {
                tokenDTO = getCustomerSyncTokenV2(transReceiveId, entity, time);
            } else if (entity.getToken() != null && !entity.getToken().trim().isEmpty()) {
                logger.info("Get token from record: " + entity.getId());
                tokenDTO = new TokenDTO(entity.getToken(), "Bearer", 0);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("transactionid", dto.getTransactionid());
            data.put("transactiontime", dto.getTransactiontime());
            data.put("referencenumber", dto.getReferencenumber());
            data.put("amount", dto.getAmount());
            data.put("content", dto.getContent());
            data.put("bankaccount", dto.getBankaccount());
            data.put("transType", dto.getTransType());
            data.put("orderId", dto.getOrderId());
            data.put("sign", dto.getSign());
            data.put("terminalCode", dto.getTerminalCode());
            data.put("urlLink", dto.getUrlLink());
            data.put("serviceCode", "");
            data.put("subTerminalCode", dto.getSubTerminalCode());
            System.out.println("Push data V2: Request: " + data);
            String suffixUrl = "";
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(entity.getUrlCallback());

            // Create SSL context to ignore SSL handshake exception
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

            logger.info("uriComponents: " + entity.getUrlCallback() + " " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            System.out
                    .println("uriComponents: " + entity.getUrlCallback() + " " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            // Mono<TransactionResponseDTO> responseMono = null;
            Mono<ClientResponse> responseMono = null;
            if (tokenDTO != null) {
                responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                // .retrieve()
                // .bodyToMono(TransactionResponseDTO.class);
            } else {
                responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                // .retrieve()
                // .bodyToMono(TransactionResponseDTO.class);
            }

            ClientResponse response = responseMono.block();
            System.out.println("response status code: " + response.statusCode());
            try {
                transactionLogResponseDTO.setStatusCode(response.statusCode().value());
                transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
            } catch (Exception e) {
            }
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSyncV2(transReceiveId, entity,
                                    dto, ++retryCount, errorCodes);
                        }
                    }
                }
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("object") != null) {
                    String reftransactionid = rootNode.get("object").get("reftransactionid").asText();
                    if (reftransactionid != null) {
                        result = new ResponseMessageDTO("SUCCESS", "");
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E05 - " + json);
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05 - " + json);
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                // nếu trả sai format retry callback
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSyncV2(transReceiveId, entity,
                                    dto, ++retryCount, errorCodes);
                        }
                    }
                }
                System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                result = new ResponseMessageDTO("FAILED", "E05 - " + json);
            }
        } catch (Exception e) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            logger.error(
                    "Error Unexpected at pushNewTransactionToCustomerSync: " +
                            entity.getUrlCallback() + " - "
                            + e.toString()
                            + " at: " + responseTime);

//            // retry callback
            if (retryCount < 10) {
                pushNewTransactionToCustomerSyncV2(transReceiveId, entity,
                        dto, ++retryCount, errorCodes);
            }
        } finally {
            if (result != null) {
                UUID logUUID = UUID.randomUUID();
                String address = entity.getUrlCallback();
                TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
                logEntity.setId(logUUID.toString());
                logEntity.setTransactionId(transReceiveId);
                logEntity.setStatus(result.getStatus());
                logEntity.setMessage(result.getMessage());
                logEntity.setStatusCode(transactionLogResponseDTO.getStatusCode());
                logEntity.setType(1);
                logEntity.setTimeResponse(transactionLogResponseDTO.getTimeResponse());
                logEntity.setTime(transactionLogResponseDTO.getTimeRequest());
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
    }

    private TokenDTO getCustomerSyncTokenV2(String transReceiveId, MerchantConnectionEntity entity, long time) {
        TokenDTO result = null;
        ResponseMessageDTO msgDTO = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            String key = entity.getUsername() + ":" + entity.getPassword();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
                    + entity.getPassword());
            UriComponents uriComponents = null;
            WebClient webClient = null;
            Map<String, Object> data = new HashMap<>();
            uriComponents = UriComponentsBuilder
                    .fromHttpUrl(entity.getUrlGetToken())
                    .buildAndExpand();
            webClient = WebClient.builder()
                    .baseUrl(entity.getUrlGetToken())
                    .build();
            System.out.println("uriComponents: " + uriComponents.getPath());
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
                        System.out.println("status code: " + clientResponse.statusCode());
                        try {
                            transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                            transactionLogResponseDTO.setStatusCode(clientResponse.statusCode().value());
                        } catch (Exception e) {
                        }
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(TokenDTO.class);
                        } else {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        logger.info("Error response: " + error);
                                        return Mono.empty();
                                    });
                        }
                    });
            Optional<TokenDTO> resultOptional = responseMono.subscribeOn(Schedulers.boundedElastic())
                    .blockOptional();
            if (resultOptional.isPresent()) {
                result = resultOptional.get();
                msgDTO = new ResponseMessageDTO("SUCCESS", "");
                logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getUrlGetToken());
            } else {
                msgDTO = new ResponseMessageDTO("FAILED", "E05");
                logger.info("Token could not be retrieved from: " + entity.getUrlGetToken());
            }
        } catch (Exception e) {
            msgDTO = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            logger.error("Error at getCustomerSyncToken: " + entity.getUrlGetToken() + " - " + e.toString());
        } finally {
            if (msgDTO != null) {
                UUID logUUID = UUID.randomUUID();
                String address = entity.getUrlGetToken();
                TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
                logEntity.setId(logUUID.toString());
                logEntity.setTransactionId(transReceiveId);
                logEntity.setStatus(msgDTO.getStatus());
                logEntity.setMessage(msgDTO.getMessage());
                logEntity.setStatusCode(StringUtil.getValueNullChecker(transactionLogResponseDTO.getStatusCode()));
                logEntity.setType(0);
                logEntity.setTimeResponse(transactionLogResponseDTO.getTimeResponse());
                logEntity.setTime(transactionLogResponseDTO.getTimeRequest());
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }

    private void pushTransactionSyncForClientId(MerchantSyncEntity merchantSyncEntity, TransactionBankCustomerDTO dto) {
        try {
            logger.info("transaction-sync: WS: pushTransactionSyncForClientId - orderId: " + dto.getOrderId() + " clientId: " + merchantSyncEntity.getClientId());
            Thread thread = new Thread(() -> {
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("transactionid", dto.getTransactionid());
                    data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                    data.put("transactiontime", dto.getTransactiontime() + "");
                    data.put("referencenumber", dto.getReferencenumber());
                    data.put("amount", dto.getAmount() + "");
                    data.put("content", dto.getContent());
                    data.put("bankaccount", dto.getBankaccount());
                    data.put("transType", dto.getTransType());
                    data.put("orderId", dto.getOrderId());
                    data.put("terminalCode", dto.getTerminalCode());
                    data.put("serviceCode", dto.getServiceCode());
                    data.put("subTerminalCode", dto.getSubTerminalCode());
                    socketHandler.sendMessageToClientId(merchantSyncEntity.getClientId(),
                            data);
                } catch (IOException e) {
                    logger.error(
                            "transaction-sync: WS: socketHandler.pushTransactionSyncForClientId - RECHARGE ERROR: "
                                    + e.toString());
                }
            });
            thread.start();
        } catch (Exception e) {
            logger.error("CustomerSync: Error: " + e.toString());
        }
    }

    private void insertNewTransaction(CustomerInvoicePaymentRequestDTO dto, String transactionUUID,
                                      AccountBankReceiveEntity accountBankReceiveEntity, String boxIdRef,
                                      TerminalEntity terminalEntity, ISubTerminalCodeDTO subTerminalCodeDTO) {
        String amountForVoice = dto.getAmount();
        String amount = processHiddenAmount(Long.parseLong(dto.getAmount()), accountBankReceiveEntity.getId(),
                accountBankReceiveEntity.isValidService(), transactionUUID);
        long time = DateTimeUtil.getCurrentDateTimeUTC();
        amount = formatAmountNumber(amount);
        BankTypeEntity bankTypeEntity = bankTypeService
                .getBankTypeById(accountBankReceiveEntity.getBankTypeId());
        // insert new transaction receive
        TransactionReceiveEntity transactionReceiveEntity = new TransactionReceiveEntity();
        transactionReceiveEntity.setId(transactionUUID);
        transactionReceiveEntity.setBankAccount(accountBankReceiveEntity.getBankAccount());
        transactionReceiveEntity.setBankId(accountBankReceiveEntity.getId());
        transactionReceiveEntity.setContent(dto.getBill_id());
        transactionReceiveEntity.setAmount(Long.parseLong(dto.getAmount()));
        transactionReceiveEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
        transactionReceiveEntity.setTimePaid(DateTimeUtil.getCurrentDateTimeUTC());
        transactionReceiveEntity.setRefId(UUID.randomUUID().toString());
        if (Objects.nonNull(terminalEntity)) {
            transactionReceiveEntity.setType(1);
            transactionReceiveEntity.setTerminalCode(terminalEntity.getCode());
        } else if (Objects.nonNull(subTerminalCodeDTO)) {
            transactionReceiveEntity.setType(1);
            transactionReceiveEntity.setTerminalCode(subTerminalCodeDTO.getCode());
        } else {
            transactionReceiveEntity.setType(2);
            transactionReceiveEntity.setTerminalCode("");
        }
        transactionReceiveEntity.setStatus(1);
        transactionReceiveEntity.setTraceId("");
        transactionReceiveEntity.setTransType("C");
        transactionReceiveEntity.setReferenceNumber(UUID.randomUUID().toString());
        transactionReceiveEntity.setOrderId("");
        transactionReceiveEntity.setSign("");
        transactionReceiveEntity.setCustomerBankAccount("");
        transactionReceiveEntity.setCustomerBankCode("");
        transactionReceiveEntity.setCustomerName("");
        transactionReceiveEntity.setServiceCode("");
        transactionReceiveEntity.setQrCode("");
        transactionReceiveEntity.setUserId(accountBankReceiveEntity.getUserId());
        transactionReceiveEntity.setNote("");
        transactionReceiveEntity.setTransStatus(0);
        transactionReceiveEntity.setUrlLink("");
        transactionReceiveEntity.setBillId(dto.getBill_id());
        transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);

        // Insert transaction for statistic
        try {
            if ((transactionReceiveEntity.getType() == 1 || transactionReceiveEntity.getType() == 0)
                    && !StringUtil.isNullOrEmpty(transactionReceiveEntity.getTerminalCode())
            ) {
                Thread thread = new Thread(() -> {
                    TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                    transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                    transactionTerminalTempEntity.setTransactionId(transactionReceiveEntity.getId());
                    transactionTerminalTempEntity.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                    transactionTerminalTempEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                    transactionTerminalTempEntity.setAmount(Long.parseLong(dto.getAmount() + ""));
                    transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);
                });
                thread.start();
            }
        } catch (Exception e) {
            logger.error("paybill: - Insert transaction for statistic: " + e.getMessage());
        }

        logger.info("transaction-sync - no have terminal is empty.");
        // insert notification
        UUID notificationUUID = UUID.randomUUID();
        NotificationEntity notiEntity = new NotificationEntity();
        String prefix = "";
        if (transactionReceiveEntity.getTransType().equalsIgnoreCase("D")) {
            prefix = "-";
        } else {
            prefix = "+";
        }
        String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                + accountBankReceiveEntity.getBankAccount()
                + NotificationUtil.getNotiDescUpdateTransSuffix2()
                + prefix + amount
                + NotificationUtil.getNotiDescUpdateTransSuffix4()
                + transactionReceiveEntity.getContent();
        notiEntity.setId(notificationUUID.toString());
        notiEntity.setRead(false);
        notiEntity.setMessage(message);
        notiEntity.setTime(time);
        notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
        notiEntity.setUserId(accountBankReceiveEntity.getUserId());
        notiEntity.setData(transactionReceiveEntity.getId());
        Map<String, String> data = autoMapUpdateTransPushNotification(new NotificationFcmMapDTO(
                        notificationUUID.toString(),
                        bankTypeEntity,
                        terminalEntity != null ? terminalEntity.getName() : "",
                        terminalEntity != null ? terminalEntity.getCode() : "", "",
                        amount,
                        transactionReceiveEntity
                )
        );
        if (!StringUtil.isNullOrEmpty(boxIdRef)) {
            try {
                Map<String, String> dataBox = new HashMap<>();
                BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
                String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
                dataBox.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                dataBox.put("amount", "" + amount);
                dataBox.put("message", String.format(messageForBox, amountForVoice));
                String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
                socketHandler.sendMessageToBoxId(idRefBox, dataBox);
                try {
                    MessageBoxDTO messageBoxDTO = new MessageBoxDTO();
                    messageBoxDTO.setNotificationType(NotificationUtil.getNotiTypeUpdateTransaction());
                    messageBoxDTO.setAmount(amount);
                    messageBoxDTO.setMessage(String.format(messageForBox, amountForVoice));
                    ObjectMapper mapper = new ObjectMapper();
                    mqttMessagingService.sendMessageToBoxId(idRefBox, mapper.writeValueAsString(messageBoxDTO));
                } catch (Exception e) {
                    logger.error("MQTT: socketHandler.sendMessageToQRBox - "
                            + boxIdRef + " at: " + System.currentTimeMillis());
                }
                logger.info("WS: socketHandler.sendMessageToQRBox - "
                        + boxIdRef + " at: " + System.currentTimeMillis());
            } catch (IOException e) {
                logger.error(
                        "WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
            }
        }
        if (terminalEntity != null ||
                (Objects.nonNull(subTerminalCodeDTO)
                        && !StringUtil.isNullOrEmpty(subTerminalCodeDTO.getTerminalId()))) {
            List<String> userIds = new ArrayList<>();
            if (Objects.nonNull(terminalEntity)) {
                userIds = accountBankReceiveShareService.getUserIdsFromTerminalId(terminalEntity.getId(),
                        accountBankReceiveEntity.getUserId());
            } else {
                userIds = accountBankReceiveShareService.getUserIdsFromTerminalId(subTerminalCodeDTO.getTerminalId(),
                        accountBankReceiveEntity.getUserId());
            }

            if (Objects.nonNull(userIds)) {
                for (String userId : userIds) {
                    notiEntity.setId(UUID.randomUUID().toString());
                    pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                            message, notiEntity, data, userId);
                }
            }
        }
        notiEntity.setId(UUID.randomUUID().toString());
        pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                message, notiEntity, data, accountBankReceiveEntity.getUserId());

        String messageSocial = prefix + amount + " VND"
                + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                + accountBankReceiveEntity.getBankAccount()
                + " | " + convertLongToDate(time)
                + " | " + dto.getBill_id()
                + " | ND: " + transactionReceiveEntity.getContent();
        /////// DO INSERT TELEGRAM, GG CHAT, LARK
        doInsertSocialMedia(accountBankReceiveEntity.getId(), messageSocial);
    }

    private ResponseMessageDTO getCustomerSyncEntities(String transactionUUID, CustomerInvoicePaymentRequestDTO dto, String rawCode,
                                         AccountBankReceiveEntity accountBankEntity, long time) {
        ResponseMessageDTO result = new ResponseMessageDTO("SUCCESS", "");
        try {
            // 1. Check bankAccountEntity with sync = true (add sync boolean field)
            // 2. Find account_customer_bank by bank_id/bank_account AND auth = true.
            // 3. Find customer_sync and push data to customer.
            if (accountBankEntity.isSync() || accountBankEntity.isWpSync()) {
                TransactionBankCustomerDTO transactionBankCustomerDTO = new TransactionBankCustomerDTO();
                transactionBankCustomerDTO.setTransactionid(dto.getTrans_id());
                transactionBankCustomerDTO.setTransactiontime(time * 1000);
                transactionBankCustomerDTO.setReferencenumber(dto.getBill_id());
                transactionBankCustomerDTO.setAmount(Long.parseLong(dto.getAmount()));
                transactionBankCustomerDTO.setContent(dto.getBill_id());
                transactionBankCustomerDTO.setBankaccount(accountBankEntity.getBankAccount());
                transactionBankCustomerDTO.setTransType("C");
                transactionBankCustomerDTO.setReciprocalAccount("");
                transactionBankCustomerDTO.setReciprocalBankCode("");
                transactionBankCustomerDTO.setVa("");
                transactionBankCustomerDTO.setValueDate(time);
                transactionBankCustomerDTO.setSign("");
                transactionBankCustomerDTO.setOrderId("");
                transactionBankCustomerDTO.setTerminalCode(rawCode);
                transactionBankCustomerDTO.setServiceCode("");
                transactionBankCustomerDTO.setUrlLink("");
                List<AccountCustomerBankEntity> accountCustomerBankEntities = new ArrayList<>();
                accountCustomerBankEntities = accountCustomerBankService
                        .getAccountCustomerBankByBankId(accountBankEntity.getId());
                if (accountCustomerBankEntities != null && !accountCustomerBankEntities.isEmpty()) {
                    int numThread = accountCustomerBankEntities.size();
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    try {
                        for (AccountCustomerBankEntity accountCustomerBankEntity : accountCustomerBankEntities) {
                            CustomerSyncEntity customerSyncEntity = customerSyncService
                                    .getCustomerSyncById(accountCustomerBankEntity.getCustomerSyncId());
                            if (customerSyncEntity != null) {
                                String retryErrors = customerErrorLogService.getRetryErrorsByCustomerId(customerSyncEntity.getId());
                                List<String> errors = new ArrayList<>();
                                errors = mapperErrors(retryErrors);
                                System.out.println("customerSyncEntity: " + customerSyncEntity.getId() + " - "
                                        + customerSyncEntity.getInformation());
                                List<String> finalErrors = errors;
                                executorService.submit(() -> pushNewTransactionToCustomerSync(transactionUUID, customerSyncEntity,
                                        transactionBankCustomerDTO,
                                        time, 1, finalErrors));
                            }
                        }
                    } finally {
                        executorService.shutdown(); // Yêu cầu các luồng dừng khi hoàn tất công việc
                        try {
                            if (!executorService.awaitTermination(700, TimeUnit.SECONDS)) {
                                executorService.shutdownNow(); // Nếu vẫn chưa dừng sau 60 giây, cưỡng chế dừng
                            }
                        } catch (InterruptedException e) {
                            executorService.shutdownNow(); // Nếu bị ngắt khi chờ, cưỡng chế dừng
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("CustomerSync: Error: " + e.toString());
            System.out.println("CustomerSync: Error: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
        }
        return result;
    }

    // API get list invoice for system
    // param: customerId, offset
    @GetMapping("customer-va/invoice/list")
    public ResponseEntity<List<CustomerInvoiceDataDTO>> getCustomerInvoices(
            @RequestParam(value = "customerId") String customerId,
            @RequestParam(value = "offset") int offset) {
        List<CustomerInvoiceDataDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = customerInvoiceService.getCustomerInvoiceAllStatus(customerId, offset);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCustomerInvoices: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // API get detail invoice for system
    @GetMapping("customer-va/invoice/detail")
    public ResponseEntity<CustomerInvoiceDetailDTO> getCustomerInvoiceDetail(
            @RequestParam(value = "billId") String billId) {
        CustomerInvoiceDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            CustomerInvoiceDataDTO invoiceDataDTO = customerInvoiceService.getCustomerInvoiceByBillId(billId);
            List<CustomerItemInvoiceDataDTO> itemDTOs = customerItemInvoiceService
                    .getCustomerInvoiceItemByBillId(billId);
            result = new CustomerInvoiceDetailDTO();
            result.setBillId(invoiceDataDTO.getBillId());
            result.setAmount(invoiceDataDTO.getAmount());
            result.setStatus(invoiceDataDTO.getStatus());
            result.setType(invoiceDataDTO.getType());
            result.setName(invoiceDataDTO.getName());
            result.setTimeCreated(invoiceDataDTO.getTimeCreated());
            result.setTimePaid(invoiceDataDTO.getTimePaid());
            result.setBankAccount(invoiceDataDTO.getBankAccount());
            result.setUserBankName(invoiceDataDTO.getUserBankName());
            result.setCustomerId(invoiceDataDTO.getCustomerId());
            List<CustomerInvoiceItemDetailDTO> items = new ArrayList<>();
            for (CustomerItemInvoiceDataDTO item : itemDTOs) {
                CustomerInvoiceItemDetailDTO data = new CustomerInvoiceItemDetailDTO();
                data.setId(item.getId());
                data.setAmount(item.getAmount());
                data.setBillId(item.getBillId());
                data.setDescription(item.getDescription());
                data.setName(item.getName());
                data.setQuantity(item.getQuantity());
                data.setTotalAmount(item.getTotalAmount());
                items.add(data);
            }
            result.setItems(items);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCustomerInvoices: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // API create invoice
    @PostMapping("customer-va/invoice/create")
    public ResponseEntity<ResponseMessageDTO> createInvoice(
            @RequestBody CustomerInvoiceInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getItems() != null && !dto.getItems().isEmpty()) {
                // initial data
                UUID invoiceId = UUID.randomUUID();
                String billId = getRandomBillId();
                long billAmount = 0L;
                LocalDateTime currentDateTime = LocalDateTime.now();
                long timeCreated = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                //
                CustomerInvoiceEntity customerInvoiceEntity = new CustomerInvoiceEntity();
                customerInvoiceEntity.setId(invoiceId.toString());
                customerInvoiceEntity.setCustomerId(dto.getCustomerId());
                customerInvoiceEntity.setName(dto.getName());
                customerInvoiceEntity.setType(1);
                customerInvoiceEntity.setBillId(billId);
                customerInvoiceEntity.setTimeCreated(timeCreated);
                customerInvoiceEntity.setTimePaid(0L);
                customerInvoiceEntity.setStatus(0);
                customerInvoiceEntity.setInquire(0);
                // add item
                for (InvoiceItemDTO item : dto.getItems()) {
                    UUID itemId = UUID.randomUUID();
                    long totalAmount = item.getAmount() * item.getQuantity();
                    billAmount += totalAmount;
                    CustomerItemInvoiceEntity customerItemInvoiceEntity = new CustomerItemInvoiceEntity();
                    customerItemInvoiceEntity.setId(itemId.toString());
                    customerItemInvoiceEntity.setBillId(billId);
                    customerItemInvoiceEntity.setName(item.getName());
                    customerItemInvoiceEntity.setDescription(item.getDescription());
                    customerItemInvoiceEntity.setQuantity(item.getQuantity());
                    customerItemInvoiceEntity.setAmount(item.getAmount());
                    customerItemInvoiceEntity.setTotalAmount(totalAmount);
                    customerItemInvoiceService.insert(customerItemInvoiceEntity);
                }
                // add invoice
                customerInvoiceEntity.setAmount(billAmount);
                customerInvoiceService.insert(customerInvoiceEntity);
                //
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("createInvoice: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("createInvoice: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private List<String> mapperErrors(String errors) {
        List<String> result = new ArrayList<>();
        try {
            if (StringUtil.isNullOrEmpty(errors)) {
                ObjectMapper mapper = new ObjectMapper();
                List<ErrorCodeMapper> list = mapper.readValue(errors, new TypeReference<List<ErrorCodeMapper>>() {
                });
                for (ErrorCodeMapper dto : list) {
                    result.add(dto.getErrorCode());
                }
            }

        } catch (Exception e) {
            logger.error("mapperErrors: Error: " + e.toString());
        }
        return result;
    }

    // API remove invoice
    @DeleteMapping("customer-va/invoice/remove")
    public ResponseEntity<ResponseMessageDTO> removeInvoice(
            @RequestParam(value = "billId") String billId) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            customerInvoiceService.removeInvocieByBillId(billId);
            customerItemInvoiceService.removeInvocieItemsByBillId(billId);
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("removeInvoice: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // API create VietQR VA invoice
    @PostMapping("customer-va/invoice/vietqr")
    public ResponseEntity<ResponseMessageDTO> createVietQRVaInvoice(
            @RequestBody VietQRVaRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String customerId = customerInvoiceService.getCustomerIdByBillId(dto.getBillId());
            if ("0".equals(dto.getAmount())) {
                dto.setAmount("");
            } else {
                dto.setAmount(dto.getAmount() + "");
            }
            result = CustomerVaUtil.generateVaInvoiceVietQR(dto, customerId);
            if (result.getStatus().equals("SUCCESS")) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("createVietQRVaInvoice: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getAccessKeyFromToken(String key) {
        return accountCustomerService.getAccessKey(key);
    }

    private void pushNotification(String title, String msg, NotificationEntity notiEntity, Map<String, String> data,
            String userId) {
        try {
            if (notiEntity != null) {
                notificationService.insertNotification(notiEntity);
            }
        } catch (Exception e) {}

        try {
            Thread thread = new Thread(() -> {
                List<FcmTokenEntity> fcmTokens = new ArrayList<>();
                fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
                firebaseMessagingService.sendUsersNotificationWithData(data,
                        fcmTokens,
                        title, msg);
            });
            thread.start();
        } catch (Exception e) {}

        try {
            socketHandler.sendMessageToUser(userId,
                    data);
        } catch (IOException e) {
            logger.error(
                    "CustomerInvoiceController: pay bill: WS: socketHandler.sendMessageToUser - RECHARGE ERROR: "
                            + e.toString());
        }
    }

    public String convertLongToDate(long timestamp) {
        String result = "";
        try {
            // Tạo một đối tượng Instant từ timestamp
            Instant instant = Instant.ofEpochSecond(timestamp);

            // Tạo một đối tượng LocalDateTime từ Instant và ZoneOffset.UTC
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

            // Chuyển đổi múi giờ từ UTC sang UTC+7
            ZoneOffset offset = ZoneOffset.ofHours(7);
            dateTime = dateTime.plusHours(offset.getTotalSeconds() / 3600);

            // Định dạng ngày tháng năm
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Chuyển đổi thành chuỗi ngày tháng năm
            result = dateTime.format(formatter);

        } catch (Exception e) {
            logger.error("convertLongToDate: ERROR: " + e.toString());
        }
        return result;
    }

    private void doInsertSocialMedia(String bankId, String message) {
        Thread thread = new Thread(() -> {
            // DO INSERT TELEGRAM
            try {
                List<String> chatIds = telegramAccountBankService.getChatIdsByBankId(bankId);
                if (chatIds != null && !chatIds.isEmpty()) {
                    TelegramUtil telegramUtil = new TelegramUtil();
                    for (String chatId : chatIds) {
                        telegramUtil.sendMsg(chatId, message);
                    }
                }
            } catch (Exception e) {
                logger.error("CustomerInvoiceController: ERROR: doInsertSocialMedia: Telegram: "
                + e.getMessage() + " at: " + System.currentTimeMillis());
            }

            // DO INSERT LARK
            try {
                List<String> webhooks = larkAccountBankService.getWebhooksByBankId(bankId);
                if (webhooks != null && !webhooks.isEmpty()) {
                    LarkUtil larkUtil = new LarkUtil();
                    for (String webhook : webhooks) {
                        larkUtil.sendMessageToLark(message, webhook);
                    }
                }
            } catch (Exception e) {
                logger.error("CustomerInvoiceController: ERROR: doInsertSocialMedia: Lark: "
                        + e.getMessage() + " at: " + System.currentTimeMillis());
            }

            // DO INSERT GOOGLE CHAT
            try {
                List<String> ggChatWebhooks = googleChatAccountBankService.getWebhooksByBankId(bankId);
                if (ggChatWebhooks != null && !ggChatWebhooks.isEmpty()) {
                    GoogleChatUtil googleChatUtil = new GoogleChatUtil();
                    for (String webhook : ggChatWebhooks) {
                        googleChatUtil.sendMessageToGoogleChat(message, webhook);
                    }
                }
            } catch (Exception e) {
                logger.error("CustomerInvoiceController: ERROR: doInsertSocialMedia: Telegram: "
                        + e.getMessage() + " at: " + System.currentTimeMillis());
            }
        });
        thread.start();
    }

    private Map<String, String> autoMapUpdateTransPushNotification(NotificationFcmMapDTO dto) {
        Map<String, String> data = new HashMap<>();
        data.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
        data.put("notificationId", dto.getNotificationUUID());
        data.put("transactionReceiveId", dto.getTransId());
        data.put("bankAccount", dto.getBankAccount());
        data.put("bankName", dto.getBankName());
        data.put("bankCode", dto.getBankCode());
        data.put("bankId", dto.getBankId());
        data.put("terminalName", dto.getTerminalName());
        data.put("terminalCode", dto.getTerminalCode());
        data.put("rawTerminalCode", dto.getTerminalCode());
        data.put("orderId", dto.getOrderId());
        data.put("referenceNumber", dto.getReferenceNumber());
        data.put("content", dto.getContent());
        data.put("amount", dto.getAmount());
        data.put("timePaid", dto.getTimePaid());
        data.put("type", dto.getType());
        data.put("time", dto.getTime());
        data.put("refId", dto.getRefId());
        data.put("status", "1");
        data.put("traceId", dto.getTraceId());
        data.put("transType", dto.getTransType());
        data.put("urlLink", dto.getUrlLink());
        return data;
    }

    private String processHiddenAmount(long amount, String bankId, boolean isValidService, String transactionId) {
        String result = "";
        try {
            long currentStartDate = DateTimeUtil.getStartDateUTCPlus7();
            result = amount + "";
            if (isValidService) {
                result = formatAmountNumber(amount + "");

                // Save transactionReceiveId if user expired active
                Thread thread = new Thread(() -> {
                    SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
                    if (systemSetting.getServiceActive() <= currentStartDate) {
                        TransReceiveTempEntity entity = transReceiveTempService
                                .getLastTimeByBankId(bankId);
                        String transIds = "";
                        if (entity == null) {
                            entity = new TransReceiveTempEntity();
                            entity.setId(UUID.randomUUID().toString());
                            entity.setBankId(bankId);
                            entity.setLastTimes(currentStartDate);
                            entity.setTransIds(transactionId);
                            entity.setNums(1);
                            transReceiveTempService.insert(entity);
                        } else {
                            processSaveTransReceiveTemp(bankId, entity.getNums(), entity.getLastTimes(), transactionId,
                                    currentStartDate, entity, 1);
                        }
                    }
                });
                thread.start();
            } else {
                SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();

                if (systemSetting.getServiceActive() <= currentStartDate) {
                    TransReceiveTempEntity entity = transReceiveTempService
                            .getLastTimeByBankId(bankId);
                    if (entity == null) {
                        result = formatAmountNumber(amount + "");
                        entity = new TransReceiveTempEntity();
                        entity.setNums(1);
                        entity.setId(UUID.randomUUID().toString());
                        entity.setBankId(bankId);
                        entity.setTransIds(transactionId);
                        entity.setLastTimes(currentStartDate);
                        transReceiveTempService.insert(entity);
                    } else {
                        boolean checkFiveTrans = processSaveTransReceiveTemp(bankId, entity.getNums(),
                                entity.getLastTimes(), transactionId,
                                currentStartDate, entity, 1);
                        if (checkFiveTrans) {
                            result = formatAmountNumber(amount + "");
                        } else {
                            result = "*****";
                        }
                    }
                } else {
                    result = formatAmountNumber(amount + "");
                }
            }
        } catch (Exception e) {
            result = formatAmountNumber(amount + "");
            logger.error("TransactionBankController: ERROR: processHiddenAmount: "
                    + e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private boolean processSaveTransReceiveTemp(String bankId, int preNum,
                                                long lastTime, String transactionId,
                                                long currentStartDate, TransReceiveTempEntity entity,
                                                int numBreak) {
        boolean result = false;
        if (numBreak <= 5) {
            ++numBreak;
            try {
                if (preNum == 5 && lastTime == currentStartDate) {
                    result = false;
                } else {
                    int aftNum = preNum;
                    String transIds = transactionId + "," + entity.getTransIds();
                    int nums = entity.getNums();
                    if (entity.getLastTimes() < currentStartDate) {
                        aftNum = 1;
                        if (StringUtil.isNullOrEmpty(entity.getTransIds())) {
                            transIds = transactionId;
                        } else {
                            transIds = transactionId + "," + entity.getTransIds();
                        }
                    } else if (entity.getNums() < 5) {
                        aftNum = preNum + 1;
                        transIds = transactionId + "," + entity.getTransIds();
                    }
                    int checkUpdateSuccess = transReceiveTempService
                            .updateTransReceiveTemp(transIds, aftNum, currentStartDate,
                                    lastTime, preNum, entity.getTransIds(), entity.getId());
                    if (checkUpdateSuccess == 0) {
                        TransReceiveTempEntity updateReceiveEntity = transReceiveTempService
                                .getLastTimeByBankId(bankId);
                        result = processSaveTransReceiveTemp(updateReceiveEntity.getBankId(), updateReceiveEntity.getNums(),
                                updateReceiveEntity.getLastTimes(), transactionId, currentStartDate, updateReceiveEntity, numBreak);
                    } else {
                        result = true;
                    }
                }
            } catch (Exception e) {
                logger.error("processSaveTransReceiveTemp: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            }
        } else {
            result = false;
        }
        return result;
    }

    private String formatAmountNumber(String amount) {
        String result = amount;
        try {
            if (StringUtil.containsOnlyDigits(amount)) {
                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                Long numberAmount = Long.parseLong(amount);
                result = nf.format(numberAmount);
            }
        } catch (Exception ignored) {}
        return result;
    }

    private ResponseMessageDTO getCustomerSyncEntities(String transReceiveId,
                                                       CustomerInvoicePaymentRequestDTO dto,
                                                       TransactionReceiveEntity transactionReceiveEntity,
                                                       AccountBankReceiveEntity accountBankEntity,
                                                       long time, String orderId, String sign, String rawTerminalCode,
                                                       String urlLink, String terminalCode) {
        ResponseMessageDTO result = new ResponseMessageDTO("SUCCESS", "");
        try {
            // 1. Check bankAccountEntity with sync = true (add sync boolean field)
            // 2. Find account_customer_bank by bank_id/bank_account AND auth = true.
            // 3. Find customer_sync and push data to customer.
            if (accountBankEntity.isSync() || accountBankEntity.isWpSync()) {
                TransactionBankCustomerDTO transactionBankCustomerDTO = new TransactionBankCustomerDTO();
                transactionBankCustomerDTO.setTransactionid(dto.getTrans_id());
                transactionBankCustomerDTO.setTransactiontime(time * 1000);
                transactionBankCustomerDTO.setReferencenumber(transactionReceiveEntity.getBillId());
                transactionBankCustomerDTO.setAmount(transactionReceiveEntity.getAmount());
                transactionBankCustomerDTO.setContent(transactionReceiveEntity.getContent());
                transactionBankCustomerDTO.setBankaccount(transactionReceiveEntity.getBankAccount());
                transactionBankCustomerDTO.setTransType(transactionReceiveEntity.getTransType());
                transactionBankCustomerDTO.setReciprocalAccount(StringUtil.getValueNullChecker(transactionReceiveEntity.getCustomerBankAccount()));
                transactionBankCustomerDTO.setReciprocalBankCode(StringUtil.getValueNullChecker(transactionReceiveEntity.getCustomerBankCode()));
                transactionBankCustomerDTO.setVa(StringUtil.getValueNullChecker(transactionReceiveEntity.getCustomerName()));
                transactionBankCustomerDTO.setValueDate(time);
                transactionBankCustomerDTO.setSign(sign);
                transactionBankCustomerDTO.setOrderId(orderId);
                if (!StringUtil.isNullOrEmpty(rawTerminalCode)) {
                    transactionBankCustomerDTO.setTerminalCode(rawTerminalCode);
                } else if (!StringUtil.isNullOrEmpty(terminalCode)) {
                    transactionBankCustomerDTO.setTerminalCode(terminalCode);
                } else {
                    transactionBankCustomerDTO.setTerminalCode("");
                }
                transactionBankCustomerDTO.setUrlLink(urlLink);
                transactionBankCustomerDTO.setSubTerminalCode(transactionReceiveEntity.getSubCode());
                logger.info("getCustomerSyncEntities: Order ID: " + orderId);
                logger.info("getCustomerSyncEntities: Signature: " + sign);
                List<AccountCustomerBankEntity> accountCustomerBankEntities = new ArrayList<>();
                accountCustomerBankEntities = accountCustomerBankService
                        .getAccountCustomerBankByBankId(accountBankEntity.getId());
                if (accountCustomerBankEntities != null && !accountCustomerBankEntities.isEmpty()) {
                    int numThread = accountCustomerBankEntities.size();
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    try {
                        for (AccountCustomerBankEntity accountCustomerBankEntity : accountCustomerBankEntities) {
                            CustomerSyncEntity customerSyncEntity = customerSyncService
                                    .getCustomerSyncById(accountCustomerBankEntity.getCustomerSyncId());
                            if (customerSyncEntity != null) {
                                String retryErrors = customerErrorLogService.getRetryErrorsByCustomerId(customerSyncEntity.getId());
                                List<String> errors = new ArrayList<>();
                                errors = mapperErrors(retryErrors);
                                System.out.println("customerSyncEntity: " + customerSyncEntity.getId() + " - "
                                        + customerSyncEntity.getInformation());
                                List<String> finalErrors = errors;
                                executorService.submit(() -> pushNewTransactionToCustomerSync(transReceiveId, customerSyncEntity,
                                        transactionBankCustomerDTO,
                                        time, 1, finalErrors));
                            }
                        }
                    } finally {
                        executorService.shutdown(); // Yêu cầu các luồng dừng khi hoàn tất công việc
                        try {
                            if (!executorService.awaitTermination(700, TimeUnit.SECONDS)) {
                                executorService.shutdownNow(); // Nếu vẫn chưa dừng sau 60 giây, cưỡng chế dừng
                            }
                        } catch (InterruptedException e) {
                            executorService.shutdownNow(); // Nếu bị ngắt khi chờ, cưỡng chế dừng
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("CustomerSync: Error: " + e.toString());
            System.out.println("CustomerSync: Error: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
        }
        return result;
    }

    private ResponseMessageDTO pushNewTransactionToCustomerSync(String transReceiveId, CustomerSyncEntity entity,
                                                                TransactionBankCustomerDTO dto,
                                                                long time, int retryCount, List<String> errorCodes) {
        ResponseMessageDTO result = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        if (retryCount > 1 && retryCount <= 5) {
            try {
                Thread.sleep(1000 * (retryCount - 1) + retryCount); // Sleep for 12000 milliseconds (12 seconds)
            } catch (InterruptedException e) {
                // Handle the exception if the thread is interrupted during sleep
                e.printStackTrace();
            }
        } else if (retryCount > 5 && retryCount <= 10) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // Handle the exception if the thread is interrupted during sleep
                e.printStackTrace();
            }
        }
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            logger.info("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
            logger.info("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            System.out.println("pushNewTransactionToCustomerSync: orderId: " +
                    dto.getOrderId());
            System.out.println("pushNewTransactionToCustomerSync: sign: " + dto.getSign());
            TokenDTO tokenDTO = null;
            if (entity.getUsername() != null && !entity.getUsername().trim().isEmpty() &&
                    entity.getPassword() != null
                    && !entity.getPassword().trim().isEmpty()) {
                tokenDTO = getCustomerSyncToken(transReceiveId, entity, time);
            } else if (entity.getToken() != null && !entity.getToken().trim().isEmpty()) {
                logger.info("Get token from record: " + entity.getId());
                tokenDTO = new TokenDTO(entity.getToken(), "Bearer", 0);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("transactionid", dto.getTransactionid());
            data.put("transactiontime", dto.getTransactiontime());
            data.put("referencenumber", dto.getReferencenumber());
            data.put("amount", dto.getAmount());
            data.put("content", dto.getContent());
            data.put("bankaccount", dto.getBankaccount());
            data.put("transType", dto.getTransType());
            data.put("orderId", dto.getOrderId());
            data.put("sign", dto.getSign());
            data.put("terminalCode", dto.getTerminalCode());
            data.put("urlLink", dto.getUrlLink());
            data.put("serviceCode", "");
            data.put("subTerminalCode", dto.getSubTerminalCode());
            String suffixUrl = "";
            if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
                suffixUrl = entity.getSuffixUrl();
            }
            WebClient.Builder webClientBuilder = WebClient.builder()
                    .baseUrl(entity.getInformation() + "/" + suffixUrl +
                            "/bank/api/transaction-sync");

            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                webClientBuilder.baseUrl("http://" + entity.getIpAddress() + ":" +
                        entity.getPort() + "/" + suffixUrl
                        + "/bank/api/transaction-sync");
            }

            // Create SSL context to ignore SSL handshake exception
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

            logger.info("uriComponents: " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            System.out
                    .println("uriComponents: " + webClient.get().uri(builder -> builder.path("/").build()).toString());
            // Mono<TransactionResponseDTO> responseMono = null;
            Mono<ClientResponse> responseMono = null;
            if (tokenDTO != null) {
                responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                // .retrieve()
                // .bodyToMono(TransactionResponseDTO.class);
            } else {
                responseMono = webClient.post()
                        // .uri("/bank/api/transaction-sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                // .retrieve()
                // .bodyToMono(TransactionResponseDTO.class);
            }

            ClientResponse response = responseMono.block();
            System.out.println("response status code: " + response.statusCode());
            try {
                transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                transactionLogResponseDTO.setStatusCode(response.statusCode().value());
            } catch (Exception e) {}
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSync(transReceiveId, entity,
                                    dto, time, ++retryCount, errorCodes);
                        }
                    }
                }
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("object") != null) {
                    String reftransactionid = rootNode.get("object").get("reftransactionid").asText();
                    if (reftransactionid != null) {
                        result = new ResponseMessageDTO("SUCCESS", "");
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E05 - " + json);
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E05 - " + json);
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
                String errorCode = validateFormatCallbackResponse(json);
                if (!StringUtil.isNullOrEmpty(errorCode)) {
                    // retry callback
                    if (Objects.nonNull(errorCodes) && errorCodes.contains(errorCode)) {
                        if (retryCount < 10) {
                            pushNewTransactionToCustomerSync(transReceiveId, entity,
                                    dto, time, ++retryCount, errorCodes);
                        }
                    }
                }
                result = new ResponseMessageDTO("FAILED", "E05 - " + json);
            }
        } catch (Exception e) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            long responseTime = currentDateTime.toEpochSecond(ZoneOffset.UTC);
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                logger.error(
                        "Error Unexpected at pushNewTransactionToCustomerSync: " +
                                entity.getIpAddress() + " - "
                                + e.toString()
                                + " at: " + responseTime);
            } else {
                logger.error(
                        "Error Unexpected at pushNewTransactionToCustomerSync: " +
                                entity.getInformation() + " - "
                                + e.toString()
                                + " at: " + responseTime);
            }
            if (retryCount < 10) {
                pushNewTransactionToCustomerSync(transReceiveId, entity,
                        dto, time, ++retryCount, errorCodes);
            }
        } finally {
            if (result != null) {
                UUID logUUID = UUID.randomUUID();
                String suffixUrl = "";
                if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
                    suffixUrl = "/" + entity.getSuffixUrl();
                }
                String address = "";
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    address = "http://" + entity.getIpAddress() + ":" + entity.getPort() + suffixUrl
                            + "/bank/api/transaction-sync";
                } else {
                    address = entity.getInformation() + suffixUrl + "/bank/api/transaction-sync";
                }
                TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
                logEntity.setId(logUUID.toString());
                logEntity.setTransactionId(transReceiveId);
                logEntity.setStatus(result.getStatus());
                logEntity.setMessage(result.getMessage());
                logEntity.setStatusCode(StringUtil.getValueNullChecker(transactionLogResponseDTO.getStatusCode()));
                logEntity.setType(1);
                logEntity.setTimeResponse(transactionLogResponseDTO.getTimeResponse());
                logEntity.setTime(transactionLogResponseDTO.getTimeRequest());
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }

    private TokenDTO getCustomerSyncToken(String transReceiveId, CustomerSyncEntity entity, long time) {
        TokenDTO result = null;
        ResponseMessageDTO msgDTO = null;
        TransactionLogResponseDTO transactionLogResponseDTO = new TransactionLogResponseDTO();
        try {
            transactionLogResponseDTO.setTimeRequest(DateTimeUtil.getCurrentDateTimeUTC());
            String key = entity.getUsername() + ":" + entity.getPassword();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + entity.getUsername() + " - password: "
                    + entity.getPassword());

            System.out.println("key: " + encodedKey + " - username: " +
                    entity.getUsername() + " - password: "
                    + entity.getPassword());
            String suffixUrl = entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()
                    ? entity.getSuffixUrl()
                    : "";
            UriComponents uriComponents = null;
            WebClient webClient = null;
            Map<String, Object> data = new HashMap<>();
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                uriComponents = UriComponentsBuilder
                        .fromHttpUrl(
                                "http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
                                        + "/api/token_generate")
                        .buildAndExpand();
                webClient = WebClient.builder()
                        .baseUrl("http://" + entity.getIpAddress() + ":" + entity.getPort() + "/" + suffixUrl
                                + "/api/token_generate")
                        .build();
            } else {
                uriComponents = UriComponentsBuilder
                        .fromHttpUrl(
                                entity.getInformation() + "/" + suffixUrl
                                        + "/api/token_generate")
                        .buildAndExpand();
                webClient = WebClient.builder()
                        .baseUrl(entity.getInformation() + "/" + suffixUrl
                                + "/api/token_generate")
                        .build();
            }
            System.out.println("uriComponents: " + uriComponents.getPath());
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
                        System.out.println("status code: " + clientResponse.statusCode());
                        try {
                            transactionLogResponseDTO.setTimeResponse(DateTimeUtil.getCurrentDateTimeUTC());
                            transactionLogResponseDTO.setStatusCode(clientResponse.statusCode().value());
                        } catch (Exception e) {}
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(TokenDTO.class);
                        } else {
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        logger.info("Error response: " + error);
                                        return Mono.empty();
                                    });
                        }
                    });
            Optional<TokenDTO> resultOptional = responseMono.subscribeOn(Schedulers.boundedElastic())
                    .blockOptional();
            if (resultOptional.isPresent()) {
                result = resultOptional.get();
                msgDTO = new ResponseMessageDTO("SUCCESS", "");
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getIpAddress());
                } else {
                    logger.info("Token got: " + result.getAccess_token() + " - from: " + entity.getInformation());
                }
            } else {
                msgDTO = new ResponseMessageDTO("FAILED", "E05");
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    logger.info("Token could not be retrieved from: " + entity.getIpAddress());
                } else {
                    logger.info("Token could not be retrieved from: " + entity.getInformation());
                }
            }
        } catch (Exception e) {
            msgDTO = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                logger.error("Error at getCustomerSyncToken: " + entity.getIpAddress() + " - " + e.toString());
                // System.out.println("Error at getCustomerSyncToken: " + entity.getIpAddress()
                // + " - " + e.toString());
            } else {
                logger.error("Error at getCustomerSyncToken: " + entity.getInformation() + " - " + e.toString());
            }
        } finally {
            if (msgDTO != null) {
                UUID logUUID = UUID.randomUUID();
                String suffixUrl = "";
                if (entity.getSuffixUrl() != null && !entity.getSuffixUrl().isEmpty()) {
                    suffixUrl = "/" + entity.getSuffixUrl();
                }
                String address = "";
                if (entity.getIpAddress() != null && !entity.getIpAddress().isEmpty()) {
                    address = "http://" + entity.getIpAddress() + ":" + entity.getPort() + suffixUrl
                            + "/api/token_generate";
                } else {
                    address = entity.getInformation() + suffixUrl + "/api/token_generate";
                }
                TransactionReceiveLogEntity logEntity = new TransactionReceiveLogEntity();
                logEntity.setId(logUUID.toString());
                logEntity.setTransactionId(transReceiveId);
                logEntity.setStatus(msgDTO.getStatus());
                logEntity.setMessage(msgDTO.getMessage());
                logEntity.setStatusCode(StringUtil.getValueNullChecker(transactionLogResponseDTO.getStatusCode()));
                logEntity.setType(0);
                logEntity.setTimeResponse(transactionLogResponseDTO.getTimeResponse());
                logEntity.setTime(transactionLogResponseDTO.getTimeRequest());
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }

    private String validateFormatCallbackResponse(String json) {
        String result = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            if (rootNode.has("error") &&
                    rootNode.has("errorReason") &&
                    rootNode.has("toastMessage") &&
                    rootNode.has("object")) {
                result = rootNode.get("errorReason").asText();
            }
        } catch (Exception e) {
            logger.error("validateFormatCallbackResponse: ERROR: " +
                    e.getMessage() + " at: " + System.currentTimeMillis());
        }
        return result;
    }

    private void updateTransaction(CustomerInvoicePaymentRequestDTO dto,
                                   TransactionReceiveEntity transactionReceiveEntity,
                                   AccountBankReceiveEntity accountBankReceiveEntity,
                                   String boxIdRef, ISubTerminalCodeDTO iSubTerminalCodeDTO) {
        String amount = processHiddenAmount(transactionReceiveEntity.getAmount(), accountBankReceiveEntity.getId(),
                accountBankReceiveEntity.isValidService(), transactionReceiveEntity.getId());
        long time = DateTimeUtil.getCurrentDateTimeUTC();
        String amountForVoice = dto.getAmount();
        amount = formatAmountNumber(amount);
        BankTypeEntity bankTypeEntity = bankTypeService
                .getBankTypeById(accountBankReceiveEntity.getBankTypeId());
        // update transaction receive
        String referenceNumber = UUID.randomUUID().toString();
        transactionReceiveService.updateTransactionReceiveStatus(1,
                dto.getTrans_id(),
                referenceNumber,
                DateTimeUtil.getCurrentDateTimeUTC(),
                transactionReceiveEntity.getId());
        transactionReceiveEntity.setReferenceNumber(referenceNumber);
        if (!StringUtil.isNullOrEmpty(transactionReceiveEntity.getTerminalCode())) {
            TerminalEntity terminalEntity = terminalService
                    .getTerminalByTerminalCode(transactionReceiveEntity.getTerminalCode(),
                            accountBankReceiveEntity.getBankAccount());

            if (Objects.nonNull(terminalEntity) ||
                    (Objects.nonNull(iSubTerminalCodeDTO))
            && !StringUtil.isNullOrEmpty(iSubTerminalCodeDTO.getTerminalId())) {
                List<String> userIds = new ArrayList<>();
                if (Objects.nonNull(terminalEntity)) {
                    userIds = accountBankReceiveShareService
                            .getUserIdsFromTerminalId(terminalEntity.getId(), accountBankReceiveEntity.getUserId());
                } else {
                    userIds = accountBankReceiveShareService
                            .getUserIdsFromTerminalId(iSubTerminalCodeDTO.getTerminalId(), accountBankReceiveEntity.getUserId());
                }

                String prefix = "";
                if (transactionReceiveEntity.getTransType().equalsIgnoreCase("D")) {
                    prefix = "-";
                } else {
                    prefix = "+";
                }
                try {
                    if ((transactionReceiveEntity.getType() == 1 || transactionReceiveEntity.getType() == 0)
                            && !StringUtil.isNullOrEmpty(transactionReceiveEntity.getTerminalCode())
                    ) {
                        Thread thread = new Thread(() -> {
                            TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                            transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                            transactionTerminalTempEntity.setTransactionId(transactionReceiveEntity.getId());
                            transactionTerminalTempEntity.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                            transactionTerminalTempEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                            transactionTerminalTempEntity.setAmount(Long.parseLong(dto.getAmount() + ""));
                            transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);
                        });
                        thread.start();
                    }
                } catch (Exception e) {
                    logger.error("paybill: - Insert transaction for statistic: " + e.getMessage());
                }

                if (userIds != null && !userIds.isEmpty()) {
                    int numThread = userIds.size();
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    try {
                        for (String userId : userIds) {
                            UUID notificationUUID = UUID.randomUUID();
                            NotificationEntity notiEntity = new NotificationEntity();
                            String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                    + accountBankReceiveEntity.getBankAccount()
                                    + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                    + prefix + amount
                                    + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                    + ""
                                    + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                    + transactionReceiveEntity.getContent();
                            notiEntity.setId(notificationUUID.toString());
                            notiEntity.setRead(false);
                            notiEntity.setMessage(message);
                            notiEntity.setTime(time);
                            notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                            notiEntity.setUserId(userId);
                            notiEntity.setData(transactionReceiveEntity.getId());
                            Map<String, String> data = new HashMap<>();
                            if (terminalEntity != null) {
                                data = autoMapUpdateTransPushNotification(
                                        new NotificationFcmMapDTO(
                                                notificationUUID.toString(),
                                                bankTypeEntity,
                                                StringUtil.getValueNullChecker(terminalEntity.getName()),
                                                StringUtil.getValueNullChecker(terminalEntity.getCode()),
                                                StringUtil.getValueNullChecker(terminalEntity.getRawTerminalCode()),
                                                amount,
                                                transactionReceiveEntity
                                        )
                                );
                            } else {
                                data = autoMapUpdateTransPushNotification(
                                        new NotificationFcmMapDTO(
                                                notificationUUID.toString(),
                                                bankTypeEntity,
                                                "",
                                                "",
                                                "",
                                                amount,
                                                transactionReceiveEntity
                                        )
                                );
                            }
                            Map<String, String> finalData = data;
                            executorService.submit(() -> pushNotification(NotificationUtil
                                    .getNotiTitleUpdateTransaction(), message, notiEntity, finalData, userId));
                        }
                    } finally {
                        executorService.shutdown(); // Yêu cầu các luồng dừng khi hoàn tất công việc
                        try {
                            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                                executorService.shutdownNow(); // Nếu vẫn chưa dừng sau 60 giây, cưỡng chế dừng
                            }
                        } catch (InterruptedException e) {
                            executorService.shutdownNow(); // Nếu bị ngắt khi chờ, cưỡng chế dừng
                        }
                    }
                }
                String message = prefix + amount + " VND"
                        + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                        + accountBankReceiveEntity.getBankAccount()
                        + " | " + convertLongToDate(time)
                        + " | " + dto.getBill_id()
                        + " | ND: " + transactionReceiveEntity.getContent();

                String notificationUUID = UUID.randomUUID().toString();
                Map<String, String> data = new HashMap<>();
                if (terminalEntity != null) {
                    data = autoMapUpdateTransPushNotification(
                            new NotificationFcmMapDTO(
                                    notificationUUID,
                                    bankTypeEntity,
                                    StringUtil.getValueNullChecker(terminalEntity.getName()),
                                    StringUtil.getValueNullChecker(terminalEntity.getCode()),
                                    StringUtil.getValueNullChecker(terminalEntity.getRawTerminalCode()),
                                    amount,
                                    transactionReceiveEntity
                            )
                    );
                } else {
                    data = autoMapUpdateTransPushNotification(
                            new NotificationFcmMapDTO(
                                    notificationUUID,
                                    bankTypeEntity,
                                    "",
                                    "",
                                    "",
                                    amount,
                                    transactionReceiveEntity
                            )
                    );
                }

                NotificationEntity notiEntity = new NotificationEntity();
                String msg = NotificationUtil.getNotiDescUpdateTransSuffix1()
                        + accountBankReceiveEntity.getBankAccount()
                        + NotificationUtil.getNotiDescUpdateTransSuffix2()
                        + prefix + amount
                        + NotificationUtil.getNotiDescUpdateTransSuffix3()
                        + ""
                        + NotificationUtil.getNotiDescUpdateTransSuffix4()
                        + transactionReceiveEntity.getContent();
                notiEntity.setId(notificationUUID);
                notiEntity.setRead(false);
                notiEntity.setMessage(msg);
                notiEntity.setTime(time);
                notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                notiEntity.setUserId(accountBankReceiveEntity.getUserId());
                notiEntity.setData(transactionReceiveEntity.getId());
                pushNotification(NotificationUtil
                        .getNotiTitleUpdateTransaction(), msg, notiEntity, data, accountBankReceiveEntity.getUserId());
                pushNotificationQrBox(boxIdRef, amountForVoice, data);
                // INSERT TELEGRAM, GG CHAT, LARK
                doInsertSocialMedia(accountBankReceiveEntity.getId(), message);
            } else {
                logger.info("transaction-sync - userIds empty.");
                // not have terminal in terminal table but still available in
                // transaction_receive
                // insert notification
                UUID notificationUUID = UUID.randomUUID();
                NotificationEntity notiEntity = new NotificationEntity();
                String prefix = "";
                if (transactionReceiveEntity.getTransType().equalsIgnoreCase("D")) {
                    prefix = "-";
                } else {
                    prefix = "+";
                }
                String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                        + accountBankReceiveEntity.getBankAccount()
                        + NotificationUtil.getNotiDescUpdateTransSuffix2()
                        + prefix + amount
                        + NotificationUtil.getNotiDescUpdateTransSuffix4()
                        + transactionReceiveEntity.getContent();
                notiEntity.setId(notificationUUID.toString());
                notiEntity.setRead(false);
                notiEntity.setMessage(message);
                notiEntity.setTime(time);
                notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                notiEntity.setUserId(accountBankReceiveEntity.getUserId());
                notiEntity.setData(transactionReceiveEntity.getId());
                Map<String, String> data = autoMapUpdateTransPushNotification( new NotificationFcmMapDTO(
                                notificationUUID.toString(),
                                bankTypeEntity,
                                "", "", "",
                                amount,
                                transactionReceiveEntity
                        )
                );
                pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                        message, notiEntity, data, accountBankReceiveEntity.getUserId());
                try {
                    String refId = TransactionRefIdUtil.encryptTransactionId(transactionReceiveEntity.getId());
                    socketHandler.sendMessageToTransactionRefId(refId, data);
                    pushNotificationQrBox(boxIdRef, amountForVoice, data);
                } catch (IOException e) {
                    logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
                }
                String messageSocial = prefix + amount + " VND"
                        + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                        + accountBankReceiveEntity.getBankAccount()
                        + " | " + convertLongToDate(time)
                        + " | " + dto.getBill_id()
                        + " | ND: " + transactionReceiveEntity.getContent();
                doInsertSocialMedia(accountBankReceiveEntity.getId(), messageSocial);
            }
        } else {
            logger.info("transaction-sync - no have terminal is empty.");
            // insert notification
            UUID notificationUUID = UUID.randomUUID();
            NotificationEntity notiEntity = new NotificationEntity();
            String prefix = "";
            if (transactionReceiveEntity.getTransType().equalsIgnoreCase("D")) {
                prefix = "-";
            } else {
                prefix = "+";
            }
            String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                    + accountBankReceiveEntity.getBankAccount()
                    + NotificationUtil.getNotiDescUpdateTransSuffix2()
                    + prefix + amount
                    + NotificationUtil.getNotiDescUpdateTransSuffix4()
                    + transactionReceiveEntity.getContent();
            notiEntity.setId(notificationUUID.toString());
            notiEntity.setRead(false);
            notiEntity.setMessage(message);
            notiEntity.setTime(time);
            notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
            notiEntity.setUserId(accountBankReceiveEntity.getUserId());
            notiEntity.setData(transactionReceiveEntity.getId());
            Map<String, String> data = autoMapUpdateTransPushNotification(new NotificationFcmMapDTO(
                            notificationUUID.toString(),
                            bankTypeEntity,
                            "", "", "",
                            amount,
                            transactionReceiveEntity
                    )
            );
            pushNotification(NotificationUtil.getNotiTitleUpdateTransaction(),
                    message, notiEntity, data, accountBankReceiveEntity.getUserId());
            try {
                String refId = TransactionRefIdUtil.encryptTransactionId(transactionReceiveEntity.getId());
                socketHandler.sendMessageToTransactionRefId(refId, data);
                pushNotificationQrBox(boxIdRef, amountForVoice, data);
            } catch (IOException e) {
                logger.error("WS: socketHandler.sendMessageToUser - updateTransaction ERROR: " + e.toString());
            }
            String messageSocial = prefix + amount + " VND"
                    + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                    + accountBankReceiveEntity.getBankAccount()
                    + " | " + convertLongToDate(time)
                    + " | " + dto.getBill_id()
                    + " | ND: " + transactionReceiveEntity.getContent();
            /////// DO INSERT TELEGRAM, GG CHAT, LARK
            doInsertSocialMedia(accountBankReceiveEntity.getId(), messageSocial);
        }
    }

    private void pushNotificationQrBox(String boxIdRef, String amountForVoice, Map<String, String> data) {
        if (!StringUtil.isNullOrEmpty(boxIdRef)) {
            try {
                // send msg to QR Link
                Map<String, String> dataBox = new HashMap<>();
                BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
                String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
                dataBox.put("notificationType", NotificationUtil.getNotiTypeUpdateTransaction());
                dataBox.put("amount", data.get("amount"));
                dataBox.put("message", String.format(messageForBox, amountForVoice));
                String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
                socketHandler.sendMessageToBoxId(idRefBox, dataBox);
                try {
                    MessageBoxDTO messageBoxDTO = new MessageBoxDTO();
                    messageBoxDTO.setNotificationType(NotificationUtil.getNotiTypeUpdateTransaction());
                    messageBoxDTO.setAmount(data.get("amount"));
                    messageBoxDTO.setMessage(String.format(messageForBox, amountForVoice));
                    ObjectMapper mapper = new ObjectMapper();
                    mqttMessagingService.sendMessageToBoxId(idRefBox, mapper.writeValueAsString(messageBoxDTO));
                } catch (Exception e) {
                    logger.error("MQTT: socketHandler.sendMessageToQRBox - "
                            + boxIdRef + " at: " + System.currentTimeMillis());
                }
                logger.info("WS: socketHandler.sendMessageToQRBox - "
                        + boxIdRef + " at: " + System.currentTimeMillis());
            } catch (IOException e) {
                logger.error(
                        "WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
            }
        }
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
}
