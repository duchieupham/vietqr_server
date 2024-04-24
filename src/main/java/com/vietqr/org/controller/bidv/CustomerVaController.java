package com.vietqr.org.controller.bidv;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.ResponseObjectDTO;
import com.vietqr.org.dto.bidv.ConfirmCustomerVaDTO;
import com.vietqr.org.dto.bidv.CustomerVaInsertDTO;
import com.vietqr.org.dto.bidv.CustomerVaItemDTO;
import com.vietqr.org.dto.bidv.RequestCustomerVaDTO;
import com.vietqr.org.entity.bidv.CustomerVaEntity;
import com.vietqr.org.service.AccountBankReceiveService;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CustomerVaController {
    private static final Logger logger = Logger.getLogger(CustomerVaController.class);

    @Autowired
    CustomerVaService customerVaService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    // BIDV Service - create merchant BIDV
    @PostMapping("customer-va/request")
    public ResponseEntity<Object> requestCustomerVa(
            @RequestBody RequestCustomerVaDTO dto) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // String checkExistedBankAccount = accountBankReceiveService
            // .checkExistedBankAccountByBankAccountAndBankCode(dto.getBankAccount(),
            // dto.getBankCode());
            Long customerVaLength = customerVaService.getCustomerVaLength() + 1;
            System.out.println("customerVaLength: " + customerVaLength);
            String merchantId = CustomerVaUtil.generateMerchantId(dto.getMerchantName(), customerVaLength);
            String checkExistedMerchantId = customerVaService.checkExistedMerchantId(merchantId);
            result = CustomerVaUtil.requestCustomerVa(dto, merchantId, "1", customerVaLength, checkExistedMerchantId);
            if (result instanceof ResponseObjectDTO) {
                httpStatus = HttpStatus.OK;
            } else if (result instanceof ResponseMessageDTO) {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("requestCustomerVa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // BIDV Service - confirm merchant BIDV
    @PostMapping("customer-va/confirm")
    public ResponseEntity<ResponseMessageDTO> confirmCustomerVa(
            @RequestBody ConfirmCustomerVaDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = CustomerVaUtil.confirmCustomerVa(dto);
            if (result.getStatus().equals("SUCCESS")) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("confirmCustomerVa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // BIDV Service - unregister merchant BIDV
    @DeleteMapping("customer-va/unregister")
    public ResponseEntity<ResponseMessageDTO> unregisterCustomerVa(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "merchantId") String merchantId) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = CustomerVaUtil.unregisterCustomerVa(merchantId);
            if (result.getStatus().equals("SUCCESS")) {
                // remove record from database
                customerVaService.removeCustomerVa(userId, merchantId);
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("unregisterCustomerVa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // insert customer VA information
    @PostMapping("customer-va/insert")
    public ResponseEntity<ResponseMessageDTO> insertCustomerVa(
            @RequestBody CustomerVaInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID uuid = UUID.randomUUID();
            CustomerVaEntity entity = new CustomerVaEntity();
            entity.setId(uuid.toString());
            entity.setMerchantId(dto.getMerchantId());
            entity.setMerchantName(dto.getMerchantName());
            entity.setBankId(dto.getBankId());
            entity.setUserId(dto.getUserId());
            entity.setCustomerId(dto.getVaNumber().substring(4));
            entity.setBankAccount(dto.getBankAccount());
            entity.setUserBankName(dto.getUserBankName());
            entity.setNationalId(dto.getNationalId());
            entity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
            entity.setMerchantType("1");
            entity.setVaNumber(dto.getVaNumber());
            customerVaService.insert(entity);
            //
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            logger.error("unregisterCustomerVa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get customer VA info
    @GetMapping("customer-va/information")
    public ResponseEntity<Object> getCustomerVaInformation(
            @RequestParam(value = "id") String id) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            CustomerVaEntity entity = customerVaService.getCustomerVaInfoById(id);
            if (entity != null) {
                result = entity;
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("CHECK", "C11");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getCustomerVaInformation: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // check existed bank account that linked into customer_va
    @GetMapping("customer-va/check-existed")
    public ResponseEntity<ResponseMessageDTO> checkExistedBankAccountVa(
            @RequestParam(value = "bankAccount") String bankAccount,
            @RequestParam(value = "bankCode") String bankCode) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String checkExisted = customerVaService.checkExistedLinkedBankAccount(bankAccount);
            if (checkExisted != null && !checkExisted.trim().isEmpty()) {
                result = new ResponseMessageDTO("CHECKED", "C12");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("checkExistedBankAccountVa: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get list customer_va by user_id
    @GetMapping("customer-va/list")
    public ResponseEntity<List<CustomerVaItemDTO>> getCustomerVasByUserId(
            @RequestParam(value = "userId") String userId) {
        List<CustomerVaItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = customerVaService.getCustomerVasByUserId(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCustomerVasByUserId: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //

    // BIDV Service - create VietQR (Transaction MMS)

    // BIDV Service - callback BDSD

}
