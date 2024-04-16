package com.vietqr.org.controller.bidv;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.ResponseMessageDTO;
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
import com.vietqr.org.entity.FcmTokenEntity;
import com.vietqr.org.entity.NotificationEntity;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.entity.bidv.CustomerInvoiceTransactionEntity;
import com.vietqr.org.entity.bidv.CustomerItemInvoiceEntity;
import com.vietqr.org.service.AccountCustomerService;
import com.vietqr.org.service.FcmTokenService;
import com.vietqr.org.service.FirebaseMessagingService;
import com.vietqr.org.service.NotificationService;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.bidv.CustomerInvoiceTransactionService;
import com.vietqr.org.service.bidv.CustomerItemInvoiceService;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.util.BankEncryptUtil;
import com.vietqr.org.util.EnvironmentUtil;
import com.vietqr.org.util.JWTUtil;
import com.vietqr.org.util.NotificationUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.SocketHandler;
import com.vietqr.org.util.bank.bidv.BIDVUtil;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;

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
                    if (dto.getService_id().equals(serviceId)) {
                        // check valid checksum
                        String checksum = BankEncryptUtil.generateMD5GetBillForBankChecksum(secretKey, serviceId,
                                dto.getCustomer_id());
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
                            if (checkExistedCustomerId != null && !checkExistedCustomerId.trim().isEmpty()) {
                                // check bill_id tồn tại
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
            Thread thread = new Thread(() -> {
                //
                if (tempResult.getResult_code().equals("000")) {
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
                Long billAmount = 0L;
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
                // add item
                for (InvoiceItemDTO item : dto.getItems()) {
                    UUID itemId = UUID.randomUUID();
                    Long totalAmount = item.getAmount() * item.getQuantity();
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
            result = CustomerVaUtil.generateVaInvoiceVietQR(dto);
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
            String billId = RandomCodeUtil.generateRandomId(10);
            while (customerInvoiceService.checkExistedBillId(billId) != null) {
                // result = billId;
                // break;
                billId = RandomCodeUtil.generateRandomId(10);
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
}
