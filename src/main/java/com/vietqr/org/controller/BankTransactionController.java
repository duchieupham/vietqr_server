package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.TransactionSyncDTO;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("test/bank/api")
public class BankTransactionController {
    private static final Logger logger = Logger.getLogger(BankTransactionController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jsonFilePath = "D:/mock/response-return.json";

    @PostMapping("/transaction-sync")
    public ResponseEntity<Object> handleTransactionSync(@RequestBody TransactionSyncDTO transactionSyncDTO) {
        try {
            File file = new File(jsonFilePath);
            JsonNode rootNode = objectMapper.readTree(file);

            int statusCode = rootNode.get("statusCode").asInt();
            JsonNode body = rootNode.get("body");

            return ResponseEntity.status(statusCode).body(body);
        } catch (IOException e) {
            logger.error("ERROR handleTransactionSync: " + e.getMessage() + " at " + System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading JSON file");
        }
    }
}
