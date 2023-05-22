package com.vietqr.org.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TransactionExportController {

    @GetMapping(value = "/export-transactions", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> exportTransactions(@RequestParam("date") String dateString,
            HttpServletResponse response) throws Exception {
        // Parse the date string into a Date object
        // ...

        // Get the transactions for the specified date from your database
        // List<Transaction> transactions =
        // transactionService.getTransactionsByDate(date);

        // Create a new workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Transaction ID");
        headerRow.createCell(1).setCellValue("Amount");
        headerRow.createCell(2).setCellValue("Date");

        // Add the transaction data to the sheet
        int rowNum = 1;
        for (int i = 0; i < 200; i++) {
            UUID uuid = UUID.randomUUID();
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(uuid.toString());
            row.createCell(1).setCellValue(1000 * i);
            row.createCell(2).setCellValue("18:00:00 21/05/2023");
        }

        // Auto-size the columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // Set the response headers
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");

        // Write the workbook data directly to the response output stream
        workbook.write(response.getOutputStream());
        workbook.close();

        // Return an empty response entity with a 200 status code
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
