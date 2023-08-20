package com.vietqr.org.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.CustomerSyncInformationDTO;
import com.vietqr.org.dto.CustomerSyncListDTO;
import com.vietqr.org.service.CustomerSyncService;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class CustomerSyncController {
    private static final Logger logger = Logger.getLogger(CustomerSyncController.class);

    @Autowired
    CustomerSyncService customerSyncService;

    @GetMapping("customer-sync")
    public ResponseEntity<List<CustomerSyncListDTO>> getCustomerSyncList() {
        List<CustomerSyncListDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = customerSyncService.getCustomerSyncList();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCustomerSyncList: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("customer-sync/information")
    public ResponseEntity<CustomerSyncInformationDTO> getCustomerSyncList(@RequestParam(value = "id") String id) {
        CustomerSyncInformationDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = customerSyncService.getCustomerSyncInformationById(id);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCustomerSyncList: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
