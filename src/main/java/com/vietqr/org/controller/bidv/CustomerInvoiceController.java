package com.vietqr.org.controller.bidv;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
import com.vietqr.org.dto.bidv.CustomerInvoiceDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceDataDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceDetailDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceDetailDTO.CustomerInvoiceItemDetailDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInsertDTO;
import com.vietqr.org.dto.bidv.CustomerInvoiceInsertDTO.InvoiceItemDTO;
import com.vietqr.org.dto.bidv.CustomerItemInvoiceDataDTO;
import com.vietqr.org.entity.bidv.CustomerInvoiceEntity;
import com.vietqr.org.entity.bidv.CustomerItemInvoiceEntity;
import com.vietqr.org.service.bidv.CustomerInvoiceService;
import com.vietqr.org.service.bidv.CustomerItemInvoiceService;
import com.vietqr.org.util.RandomCodeUtil;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CustomerInvoiceController {
    private static final Logger logger = Logger.getLogger(CustomerInvoiceController.class);

    @Autowired
    CustomerInvoiceService customerInvoiceService;

    @Autowired
    CustomerItemInvoiceService customerItemInvoiceService;

    // API get invoice for BIDV
    @PostMapping("bidv/getbill")
    public ResponseEntity<CustomerInvoiceDTO> getbill() {
        CustomerInvoiceDTO result = null;
        HttpStatus httpStatus = null;
        try {
            //
        } catch (Exception e) {
            logger.error("getbill: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
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

}
