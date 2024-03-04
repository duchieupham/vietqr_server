package com.vietqr.org.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ExcelExportService {

    public void testRowAccessWindowSize() throws IOException {
        int[] windowSizes = {10, 100, 1000, 10000};
        for (int windowSize : windowSizes) {
            long startTime = System.currentTimeMillis();
            exportLargeExcel(windowSize);
            long endTime = System.currentTimeMillis();
            System.out.println("Row Access Window Size: " + windowSize + ", Time: " + (endTime - startTime) + "ms");
        }
    }

    private void exportLargeExcel(int rowAccessWindowSize) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        workbook.setCompressTempFiles(true);

        SXSSFSheet sheet = workbook.createSheet("Sheet 1");
        sheet.setRandomAccessWindowSize(rowAccessWindowSize);

        for (int rowIdx = 0; rowIdx < 1000000; rowIdx++) {
            Row row = sheet.createRow(rowIdx);
            for (int colIdx = 0; colIdx < 10; colIdx++) {
                Cell cell = row.createCell(colIdx);
                cell.setCellValue("Data " + rowIdx + "-" + colIdx);
            }
        }

        FileOutputStream fileOut = new FileOutputStream("output_" + rowAccessWindowSize + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.dispose();
    }
}