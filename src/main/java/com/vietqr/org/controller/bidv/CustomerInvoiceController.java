package com.vietqr.org.controller.bidv;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.bidv.CustomerInvoiceDTO;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CustomerInvoiceController {
    private static final Logger logger = Logger.getLogger(CustomerInvoiceController.class);

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
}
