package com.vietqr.org.controller.bidv;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
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
    LarkAccountBankService larkAccountBankService;

    @Autowired
    GoogleChatAccountBankService googleChatAccountBankService;

    @Autowired
    TelegramAccountBankService telegramAccountBankService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    TransReceiveTempService transReceiveTempService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    TransactionReceiveLogService transactionReceiveLogService;

    @Autowired
    SocketHandler socketHandler;

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
                            CustomerInvoiceInfoDataDTO customerInvoiceInfoDataDTO = customerInvoiceService
                                    .getCustomerInvoiceInfo(dto.getCustomer_id());

                            if (customerInvoiceInfoDataDTO != null) {
                                CustomerVaInfoDataDTO customerVaInfoDataDTO = customerVaService
                                        .getCustomerVaInfo(dto.getCustomer_id());
                                CustomerInvoiceDTO customerInvoiceDTO = new CustomerInvoiceDTO();
                                customerInvoiceDTO.setResult_code("000");
                                customerInvoiceDTO.setResult_desc("success");
                                customerInvoiceDTO.setService_id(dto.getService_id());
                                customerInvoiceDTO.setCustomer_id(customerVaInfoDataDTO.getCustomer_id());
                                customerInvoiceDTO.setCustomer_name(customerVaInfoDataDTO.getCustomer_name());
                                customerInvoiceDTO.setCustomer_addr("");
                                InvoiceDTO invoiceDTO = new InvoiceDTO();
                                invoiceDTO.setType(customerInvoiceInfoDataDTO.getType());
                                invoiceDTO.setAmount(customerInvoiceInfoDataDTO.getAmount());
                                invoiceDTO.setBill_id(customerInvoiceInfoDataDTO.getBill_id());
                                customerInvoiceDTO.setData(invoiceDTO);
                                // update inquired
                                customerInvoiceService.updateInquiredInvoiceByBillId(1,
                                        customerInvoiceInfoDataDTO.getBill_id());
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
                if (dto != null && dto.getTrans_id() != null && dto.getTrans_date() != null
                        && dto.getCustomer_id() != null && dto.getService_id() != null && dto.getBill_id() != null
                        && dto.getAmount() != null && dto.getChecksum() != null) {
                    if (dto.getService_id().equals(serviceId)) {
                        // check valid checksum
                        String checksum = BankEncryptUtil.generateMD5PayBillForBankChecksum(secretKey,
                                dto.getTrans_id(), dto.getBill_id(), dto.getAmount());
                        if (BankEncryptUtil.isMatchChecksum(dto.getChecksum(), checksum)) {
                            // check customer_id tồn tại
                            String checkExistedCustomerId = customerVaService
                                    .checkExistedCustomerId(dto.getCustomer_id());
                            logger.info("BILL ID: " + dto.getBill_id() + " at: " + System.currentTimeMillis());
                            if (checkExistedCustomerId != null && !checkExistedCustomerId.trim().isEmpty()) {
                                // check bill_id tồn tại
                                logger.info("BILL ID: " + dto.getBill_id());
                                if (dto.getBill_id().startsWith(EnvironmentUtil.getBidvTransactionPrefix())) {
                                    CustomerInvoiceDataDTO customerInvoiceDataDTO = customerInvoiceService
                                            .getCustomerInvoiceByBillId(dto.getBill_id());
                                    if (customerInvoiceDataDTO != null) {
                                        // check invoice đã thanh toán hay chưa
                                        if (customerInvoiceDataDTO.getStatus() == 0) {
                                            // check số tiền có khớp hay không
                                            Long paymentAmount = 0L;
                                            Long amountParsing = Long.parseLong(dto.getAmount());
                                            if (amountParsing != null) {
                                                paymentAmount = amountParsing;
                                            }
                                            System.out.println("customerInvoiceDataDTO.getAmount(): "
                                                    + customerInvoiceDataDTO.getAmount());
                                            System.out.println("paymentAmount: " + paymentAmount);
                                            if (customerInvoiceDataDTO.getAmount().equals(paymentAmount)) {
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
                                            } else {
                                                // nếu chưa: báo lỗi số tiền không khớp
                                                result = new ResponseMessageBidvDTO("022",
                                                        "Số tiền gửi lên không đúng với số tiền trong hóa đơn");
                                                httpStatus = HttpStatus.BAD_REQUEST;
                                            }
                                        } else {
                                            // hoá đơn đã gạch nợ rồi
                                            result = new ResponseMessageBidvDTO("023",
                                                    "Hóa đơn đã gạch nợ rồi (mỗi hóa đơn chỉ gạch nợ 1 lần)");
                                            httpStatus = HttpStatus.OK;
                                        }
                                    } else {
                                        // mã hoá đơn không tồn tại
                                        result = new ResponseMessageBidvDTO("021",
                                                "Mã hóa đơn không tồn tại");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
                                } else {
                                    CustomerInvoiceDataDTO customerInvoiceDataDTO = customerInvoiceService
                                            .getCustomerInvoiceByBillId(dto.getBill_id());
                                    if (customerInvoiceDataDTO != null) {
                                        // check invoice đã thanh toán hay chưa
                                        if (customerInvoiceDataDTO.getStatus() == 0) {
                                            // check số tiền có khớp hay không
                                            Long paymentAmount = 0L;
                                            Long amountParsing = Long.parseLong(dto.getAmount());
                                            if (amountParsing != null) {
                                                paymentAmount = amountParsing;
                                            }
                                            System.out.println("customerInvoiceDataDTO.getAmount(): "
                                                    + customerInvoiceDataDTO.getAmount());
                                            System.out.println("paymentAmount: " + paymentAmount);
                                            if (customerInvoiceDataDTO.getAmount().equals(paymentAmount)) {
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
                                            } else {
                                                // nếu chưa: báo lỗi số tiền không khớp
                                                result = new ResponseMessageBidvDTO("022",
                                                        "Số tiền gửi lên không đúng với số tiền trong hóa đơn");
                                                httpStatus = HttpStatus.BAD_REQUEST;
                                            }
                                        } else {
                                            // hoá đơn đã gạch nợ rồi
                                            result = new ResponseMessageBidvDTO("023",
                                                    "Hóa đơn đã gạch nợ rồi (mỗi hóa đơn chỉ gạch nợ 1 lần)");
                                            httpStatus = HttpStatus.OK;
                                        }
                                    } else {
                                        // mã hoá đơn không tồn tại
                                        result = new ResponseMessageBidvDTO("021",
                                                "Mã hóa đơn không tồn tại");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                    }
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
            // push notification
            final ResponseMessageBidvDTO tempResult = result;
            AccountBankReceiveEntity finalAccountBankReceiveEntity = accountBankReceiveEntity;
            Thread thread = new Thread(() -> {
                //
                if (tempResult.getResult_code().equals("000")) {

                    // Transaction Receive Entity
                    if (dto.getBill_id().startsWith(EnvironmentUtil.getBidvTransactionPrefix())) {
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
                                updateTransaction(dto, transactionReceiveEntity, finalAccountBankReceiveEntity, boxIdRef);
                            }
                        }

                    //
                    } else {
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
                    }
                }

            });
            thread.start();
        }
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
                String billId = generateRandomBillId(10);
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

    private String generateRandomBillId(int length) {
        String result = "";
        try {
            String billId = EnvironmentUtil.getBidvInvoiceTransactionPrefix() + RandomCodeUtil.generateRandomId(10);
            while (customerInvoiceService.checkExistedBillId(billId) != null) {
                billId = EnvironmentUtil.getBidvInvoiceTransactionPrefix() + RandomCodeUtil.generateRandomId(10);
            }
            result = billId;
        } catch (Exception e) {
            logger.error("generateRandomBillId: ERROR: " + e.toString());
        }
        return result;
    }

    private String getAccessKeyFromToken(String key) {
        return accountCustomerService.getAccessKey(key);
    }

    private void pushNotification(String title, String msg, NotificationEntity notiEntity, Map<String, String> data,
            String userId) {
        if (notiEntity != null) {
            notificationService.insertNotification(notiEntity);
        }
        List<FcmTokenEntity> fcmTokens = new ArrayList<>();
        fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
        firebaseMessagingService.sendUsersNotificationWithData(data,
                fcmTokens,
                title, msg);
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
        String result = formatAmountNumber(amount + "");
        try {
            long currentStartDate = DateTimeUtil.getCurrentDateTimeUTC();
            result = amount + "";
            if (isValidService) {
                result = formatAmountNumber(amount + "");

                // Save transactionReceiveId if user expired active
                Thread thread = new Thread(() -> {
                    SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();
                    if (systemSetting.getServiceActive() <= currentStartDate) {
                        TransReceiveTempEntity entity = transReceiveTempService
                                .getLastTimeByBankId(bankId);
                        if (entity == null) {
                            List<String> transIds = new ArrayList<>();
                            entity = new TransReceiveTempEntity();
                            entity.setId(UUID.randomUUID().toString());
                            entity.setBankId(bankId);
                            entity.setLastTimes(currentStartDate);
                            entity.setTransIds(String.join(",", transIds));
                        } else {
                            List<String> transIds = new ArrayList<>();
                            if (entity.getTransIds() != null && !entity.getTransIds().isEmpty()) {
                                transIds = new ArrayList<>(Arrays.asList(entity.getTransIds().split(",")));
                            }
                            if (transIds.size() < 5) {
                                transIds.add(transactionId);
                                entity.setLastTimes(currentStartDate);
                                entity.setTransIds(String.join(",", transIds));
                                transReceiveTempService.insert(entity);
                            }
                        }
                        transReceiveTempService.insert(entity);
                    }
                });
                thread.start();
            } else {
                SystemSettingEntity systemSetting = systemSettingService.getSystemSetting();

                if (systemSetting.getServiceActive() <= currentStartDate) {
                    TransReceiveTempEntity entity = transReceiveTempService
                            .getLastTimeByBankId(bankId);
                    List<String> transIds;
                    if (entity == null) {
                        transIds = new ArrayList<>();
                        result = formatAmountNumber(amount + "");
                        entity = new TransReceiveTempEntity();
                        transIds.add(transactionId);
                        entity.setId(UUID.randomUUID().toString());
                        entity.setBankId(bankId);
                        entity.setLastTimes(currentStartDate);
                        entity.setTransIds(String.join(",", transIds));
                    } else {
                        if (entity.getTransIds() != null && !entity.getTransIds().isEmpty()) {
                            transIds = new ArrayList<>(Arrays.asList(entity.getTransIds().split(",")));
                        } else {
                            transIds = new ArrayList<>();
                        }

                        if (transIds.size() < 5) {
                            transIds.add(transactionId);
                            entity.setLastTimes(currentStartDate);
                            entity.setTransIds(String.join(",", transIds));

                            transReceiveTempService.insert(entity);
                        } else {
                            if (!isValidService) {
                                result = "*****";
                            }
                        }
                    }

                    // Save
                    if (!"*****".equals(result)) {
                        TransReceiveTempEntity finalEntity = entity;
                        Thread thread = new Thread(() -> {
                            finalEntity.setId(UUID.randomUUID().toString());
                            finalEntity.setBankId(bankId);
                            finalEntity.setLastTimes(currentStartDate);
                            finalEntity.setTransIds(String.join(",", transIds));
                            transReceiveTempService.insert(finalEntity);
                        });
                        thread.start();
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
                transactionBankCustomerDTO.setTransactiontime(time);
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
                transactionBankCustomerDTO.setTerminalCode(rawTerminalCode);
                transactionBankCustomerDTO.setUrlLink(urlLink);
                logger.info("getCustomerSyncEntities: Order ID: " + orderId);
                logger.info("getCustomerSyncEntities: Signature: " + sign);
                List<AccountCustomerBankEntity> accountCustomerBankEntities = new ArrayList<>();
                accountCustomerBankEntities = accountCustomerBankService
                        .getAccountCustomerBankByBankId(accountBankEntity.getId());
                if (accountCustomerBankEntities != null && !accountCustomerBankEntities.isEmpty()) {
                    for (AccountCustomerBankEntity accountCustomerBankEntity : accountCustomerBankEntities) {
                        CustomerSyncEntity customerSyncEntity = customerSyncService
                                .getCustomerSyncById(accountCustomerBankEntity.getCustomerSyncId());
                        if (customerSyncEntity != null) {
                            System.out.println("customerSyncEntity: " + customerSyncEntity.getId() + " - "
                                    + customerSyncEntity.getInformation());
                            result = pushNewTransactionToCustomerSync(transReceiveId, customerSyncEntity,
                                    transactionBankCustomerDTO,
                                    time);
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
                                                                long time) {
        ResponseMessageDTO result = null;
        try {
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
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                System.out.println("Response pushNewTransactionToCustomerSync: " + json);
                logger.info("Response pushNewTransactionToCustomerSync: " + json + " status: " + response.statusCode());
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
                logEntity.setTime(time);
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }

    private TokenDTO getCustomerSyncToken(String transReceiveId, CustomerSyncEntity entity, long time) {
        TokenDTO result = null;
        ResponseMessageDTO msgDTO = null;
        try {
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
                logEntity.setTime(time);
                logEntity.setUrlCallback(address);
                transactionReceiveLogService.insert(logEntity);
            }
        }
        return result;
    }


    private void updateTransaction(CustomerInvoicePaymentRequestDTO dto,
                                   TransactionReceiveEntity transactionReceiveEntity,
                                   AccountBankReceiveEntity accountBankReceiveEntity,
                                   String boxIdRef) {
        String amount = processHiddenAmount(transactionReceiveEntity.getAmount(), accountBankReceiveEntity.getId(),
                accountBankReceiveEntity.isValidService(), transactionReceiveEntity.getId());
        long time = DateTimeUtil.getCurrentDateTimeUTC();
        String amountForVoice = amount;
        amount = formatAmountNumber(amount);
        BankTypeEntity bankTypeEntity = bankTypeService
                .getBankTypeById(accountBankReceiveEntity.getBankTypeId());
        // update transaction receive
        transactionReceiveService.updateTransactionReceiveStatus(1,
                dto.getTrans_id(),
                dto.getBill_id(),
                DateTimeUtil.getCurrentDateTimeUTC(),
                transactionReceiveEntity.getId());

        if (!StringUtil.isNullOrEmpty(transactionReceiveEntity.getTerminalCode())) {
            TerminalEntity terminalEntity = terminalService
                    .getTerminalByTerminalCode(transactionReceiveEntity.getTerminalCode(),
                            accountBankReceiveEntity.getBankAccount());

            if (Objects.nonNull(terminalEntity)) {
                List<String> userIds = terminalService
                        .getUserIdsByTerminalCode(transactionReceiveEntity.getTerminalCode());
                String prefix = "";
                if (transactionReceiveEntity.getTransType().equalsIgnoreCase("D")) {
                    prefix = "-";
                } else {
                    prefix = "+";
                }
                Thread thread = new Thread(() -> {
                    TransactionTerminalTempEntity transactionTerminalTempEntity = new TransactionTerminalTempEntity();
                    transactionTerminalTempEntity.setId(UUID.randomUUID().toString());
                    transactionTerminalTempEntity.setTransactionId(transactionReceiveEntity.getId());
                    transactionTerminalTempEntity.setTerminalCode(terminalEntity.getCode());
                    transactionTerminalTempEntity.setTime(DateTimeUtil.getCurrentDateTimeUTC());
                    transactionTerminalTempEntity.setAmount(Long.parseLong(dto.getAmount() + ""));
                    transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTempEntity);
                });
                thread.start();

                if (userIds != null && !userIds.isEmpty()) {
                    int numThread = userIds.size();
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    for (String userId : userIds) {
                        UUID notificationUUID = UUID.randomUUID();
                        NotificationEntity notiEntity = new NotificationEntity();
                        String message = NotificationUtil.getNotiDescUpdateTransSuffix1()
                                + accountBankReceiveEntity.getBankAccount()
                                + NotificationUtil.getNotiDescUpdateTransSuffix2()
                                + prefix + amount
                                + NotificationUtil.getNotiDescUpdateTransSuffix3()
                                + terminalEntity.getName()
                                + NotificationUtil.getNotiDescUpdateTransSuffix4()
                                + transactionReceiveEntity.getContent();
                        notiEntity.setId(notificationUUID.toString());
                        notiEntity.setRead(false);
                        notiEntity.setMessage(message);
                        notiEntity.setTime(time);
                        notiEntity.setType(NotificationUtil.getNotiTypeUpdateTransaction());
                        notiEntity.setUserId(userId);
                        notiEntity.setData(transactionReceiveEntity.getId());
                        Map<String, String> data = autoMapUpdateTransPushNotification(
                                new NotificationFcmMapDTO(
                                        notificationUUID.toString(),
                                        bankTypeEntity,
                                        "", "", "",
                                        transactionReceiveEntity
                                )
                        );
                        executorService.submit(() -> pushNotification(NotificationUtil
                                .getNotiTitleUpdateTransaction(), message, notiEntity, data, userId));
                    }
                    executorService.shutdown();
                }

                String message = prefix + amount + " VND"
                        + " | TK: " + bankTypeEntity.getBankShortName() + " - "
                        + accountBankReceiveEntity.getBankAccount()
                        + " | " + convertLongToDate(time)
                        + " | " + dto.getBill_id()
                        + " | ND: " + transactionReceiveEntity.getContent();
                // INSERT TELEGRAM, GG CHAT, LARK
                doInsertSocialMedia(accountBankReceiveEntity.getId(), message);

                Map<String, String> data = autoMapUpdateTransPushNotification(
                        new NotificationFcmMapDTO(
                                UUID.randomUUID().toString(),
                                bankTypeEntity,
                                StringUtil.getValueNullChecker(terminalEntity.getName()),
                                StringUtil.getValueNullChecker(terminalEntity.getCode()),
                                StringUtil.getValueNullChecker(terminalEntity.getRawTerminalCode()),
                                transactionReceiveEntity
                        )
                );

                pushNotificationQrBox(boxIdRef, amountForVoice, data);


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
                BoxEnvironmentResDTO messageBox = systemSettingService.getSystemSettingBoxEnv();
                String messageForBox = StringUtil.getMessageBox(messageBox.getBoxEnv());
                data.put("message", String.format(messageForBox, amountForVoice));
                String idRefBox = BoxTerminalRefIdUtil.encryptQrBoxId(boxIdRef);
                socketHandler.sendMessageToBoxId(idRefBox, data);
                logger.info("WS: socketHandler.sendMessageToQRBox - "
                        + boxIdRef + " at: " + System.currentTimeMillis());
            } catch (IOException e) {
                logger.error(
                        "WS: socketHandler.sendMessageToBox - updateTransaction ERROR: " + e.toString());
            }
        }
    }
}
