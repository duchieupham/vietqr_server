package com.vietqr.org.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.*;
import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TransCLSftpDTO;
import com.vietqr.org.dto.TransTCSftpDTO;
import com.vietqr.org.dto.TransTHSftpDTO;
import com.vietqr.org.entity.TransactionMMSEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.service.TransactionMMSService;
import com.vietqr.org.service.TransactionReceiveService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class SFTPUtil {

    @Autowired
    TransactionMMSService transactionMMSService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    public void readRemoteFile() {
        String host = "112.78.1.209";
        int port = 22;
        String username = "root";
        String password = "5uQ26Jwa!Ab1";
        String remoteFilePath = "/home/mbuser/IN/RV_20230704_TC.txt";

        JSch jsch = new JSch();

        try {
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(channelSftp.get(remoteFilePath)))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    count++;
                    System.out.println("\nLine " + count + ": ");
                    String[] data = line.split("\\|");
                    for (String part : data) {
                        System.out.println(part);
                    }
                }
            }

            channelSftp.disconnect();
            session.disconnect();

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile() {
        String remoteFilePath = "/home/mbuser/OUT/RV.txt";
        String textToWrite = "Hello";

        String username = "your_username";
        String password = "your_password";
        String hostname = "sftp.example.com";
        int port = 22;

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;

        try {
            // create session
            session = jsch.getSession(username, hostname, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            // create channel
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            // write text to remote file
            ByteArrayInputStream inputStream = new ByteArrayInputStream(textToWrite.getBytes());
            channel.put(inputStream, remoteFilePath);

            System.out.println("Text written to " + remoteFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // disconnect channel and session
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @PostMapping("sftp/check/{date}")
    public ResponseEntity<ResponseMessageDTO> checkTransactions(
            @PathVariable(value = "date") String date) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        // String date = "20230704";
        //
        String host = "112.78.1.209";
        int port = 22;
        String username = "root";
        String password = "5uQ26Jwa!Ab1";
        String remoteFilePath = "/home/mbuser/IN/RV_" + date + "_TC.txt";

        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // 1. tạo bộ đếm giao dịch đúng, đếm số tiền đúng
            int totalCorrectTransaction = 0;
            long totalCorrectAmount = 0;
            // 2. tạo bộ đếm giao dịch sai, đếm số tiền sai.
            int totalWrongTransaction = 0;
            long totalWrongAmount = 0;
            // 3. tạo list object TransTCSftpDTO
            List<TransTCSftpDTO> transTCs = new ArrayList<>();
            // 4. tạo list object TransactionCLSftpDTO nếu không khớp
            List<TransCLSftpDTO> transCLs = new ArrayList<>();
            //
            // VÒNG 1:
            // 1. Quét file x_TC.txt bằng SFTP
            // 2. tạo DTO
            // 3. get cả entity by FT trong bảng transaction mms.
            // 4. check tiền khớp không
            // 4.1. Nếu khớp, xử lý bộ đếm đúng
            // 4.2. Nếu không khớp, xử lý bộ đếm lệch
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(channelSftp.get(remoteFilePath)))) {
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    count++;
                    System.out.println("\nLine " + count + ": ");
                    String[] data = line.split("\\|");
                    //
                    if (count != 1) {
                        TransTCSftpDTO transTCSftpDTO = new TransTCSftpDTO();
                        transTCSftpDTO.setNo(data[0]);
                        transTCSftpDTO.setChannel(data[1]);
                        transTCSftpDTO.setChannelName(data[2]);
                        transTCSftpDTO.setTransType(data[3]);
                        transTCSftpDTO.setRequestId(data[4]);
                        transTCSftpDTO.setBankTransId(data[5]);
                        transTCSftpDTO.setFt(data[6]);
                        transTCSftpDTO.setDatetime(data[7]);
                        transTCSftpDTO.setAmount(data[8]);
                        transTCSftpDTO.setCurrency(data[9]);
                        transTCSftpDTO.setContent(data[10]);
                        transTCSftpDTO.setStatus(data[11]);
                        transTCSftpDTO.setDebitAccount(data[12]);
                        transTCSftpDTO.setDebitAccountName(data[13]);
                        transTCSftpDTO.setCreditAccount(data[14]);
                        transTCSftpDTO.setCreditAccountName(data[15]);
                        transTCSftpDTO.setBankAccount(data[16]);
                        transTCSftpDTO.setBankName(data[17]);
                        transTCSftpDTO.setNapasKey(data[18]);
                        transTCSftpDTO.setAddInfo(data[19]);
                        transTCSftpDTO.setCheckSum(data[20]);
                        transTCs.add(transTCSftpDTO);
                        // for (String part : data) {
                        // System.out.println(part);
                        // }

                        //
                        System.out.println("FT Get from TC file: " + transTCSftpDTO.getFt());
                        if (transTCSftpDTO.getFt() != null) {
                            TransactionMMSEntity transactionMMSEntity = transactionMMSService
                                    .getTransactionMMSByFtCode(transTCSftpDTO.getFt(), date);
                            if (transactionMMSEntity != null) {
                                // compare số tiền
                                if (transTCSftpDTO.getAmount().trim()
                                        .equals(transactionMMSEntity.getDebitAmount().trim())) {
                                    // MATCHED
                                    System.out.println("Transaction " + transTCSftpDTO.getNo() + " matched amount.");
                                    totalCorrectTransaction++;
                                    totalCorrectAmount += Long.parseLong(transactionMMSEntity.getDebitAmount());
                                } else {
                                    // SAL LECH
                                    System.out
                                            .println("Transaction " + transTCSftpDTO.getNo() + " NOT matched amount.");
                                    totalWrongTransaction++;
                                    totalWrongAmount += Long.parseLong(transactionMMSEntity.getDebitAmount());
                                    // add Wrong record into list
                                    String no = "";
                                    if (transCLs.isEmpty()) {
                                        no = "1";
                                    } else {
                                        no = transCLs.size() + 1 + "";
                                    }
                                    TransCLSftpDTO transCLSftpDTO = new TransCLSftpDTO(no, "RV", "BLUECOM", "1011",
                                            transTCSftpDTO.getRequestId(),
                                            "", transTCSftpDTO.getFt(), transTCSftpDTO.getDatetime(),
                                            transTCSftpDTO.getAmount(), "VND", transTCSftpDTO.getContent(),
                                            "", "", "", "",
                                            "", "",
                                            "", transTCSftpDTO.getNapasKey(), "SAI LECH", transTCSftpDTO.getAddInfo(),
                                            transTCSftpDTO.getCheckSum());
                                    transCLs.add(transCLSftpDTO);
                                }
                            } else {
                                // Xử lý tiếp là tìm trong transaction_receive có nhận được không
                                // Nếu có, ghi nhận matched
                                // Nếu không, ghi nhận Thừa MB
                                TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                                        .findTransactionReceiveByFtCode(transTCSftpDTO.getFt());
                                if (transactionReceiveEntity == null) {
                                    System.out.println("Transaction " + transTCSftpDTO.getNo() + " NOT FOUND");
                                    totalWrongTransaction++;
                                    totalWrongAmount += Long.parseLong(transTCSftpDTO.getAmount());
                                    // add Wrong record into list
                                    String no = "";
                                    if (transCLs.isEmpty()) {
                                        no = "1";
                                    } else {
                                        no = transCLs.size() + 1 + "";
                                    }
                                    TransCLSftpDTO transCLSftpDTO = new TransCLSftpDTO(no, "RV", "BLUECOM", "1011",
                                            transTCSftpDTO.getRequestId(),
                                            "", transTCSftpDTO.getFt(), transTCSftpDTO.getDatetime(),
                                            transTCSftpDTO.getAmount(), "VND", transTCSftpDTO.getContent(),
                                            "", "", "", "",
                                            "", "",
                                            "", transTCSftpDTO.getNapasKey(), "THUA MB", transTCSftpDTO.getAddInfo(),
                                            transTCSftpDTO.getCheckSum());
                                    transCLs.add(transCLSftpDTO);
                                } else {
                                    // MATCHED
                                    System.out.println("Transaction " + transTCSftpDTO.getNo() + " matched amount.");
                                    totalCorrectTransaction++;
                                    totalCorrectAmount += transactionReceiveEntity.getAmount();
                                }

                            }
                        }
                    }
                }
            }
            System.out.println("\n\nCONCLUSION ROUND 1: ");
            System.out.println("totalCorrectTransaction: " + totalCorrectTransaction);
            System.out.println("totalCorrectAmount: " + totalCorrectAmount);
            System.out.println("totalWrongTransaction: " + totalWrongTransaction);
            System.out.println("totalWrongAmount: " + totalWrongAmount);

            // VÒNG 2:
            // 1. Quét transactionmms theo ngày
            // 2. Vòng For check FT code có tồn tại trong list TC không
            // 2.1. Nếu có, không làm gì
            // 2.2. Nếu không, note lại thông tin thừa BLUECOM
            List<TransactionMMSEntity> transactionMMSEntities = transactionMMSService
                    .getTransactionMMSByDate(date);
            if (transactionMMSEntities != null && !transactionMMSEntities.isEmpty()) {
                Set<String> ftCodes = new HashSet<>();
                for (TransTCSftpDTO transTC : transTCs) {
                    ftCodes.add(transTC.getFt());
                }
                for (TransactionMMSEntity transactionMMSEntity : transactionMMSEntities) {
                    if (ftCodes.contains(transactionMMSEntity.getFtCode())) {
                        System.out.println("Transaction matched: " + transactionMMSEntity.getFtCode());
                    } else {
                        totalWrongTransaction++;
                        totalWrongAmount += Long.parseLong(transactionMMSEntity.getDebitAmount());
                        // add Wrong record into list
                        String no = "";
                        if (transCLs.isEmpty()) {
                            no = "1";
                        } else {
                            no = transCLs.size() + 1 + "";
                        }
                        // convert date time
                        String datetime = convertSftpDate(transactionMMSEntity.getPayDate());
                        //
                        TransCLSftpDTO transCLSftpDTO = new TransCLSftpDTO(no, "RV", "BLUECOM", "1011",
                                "",
                                "", transactionMMSEntity.getFtCode(), datetime,
                                transactionMMSEntity.getDebitAmount() + "", "VND", "",
                                "", "", "", "",
                                "", "",
                                "", "", "THUA BLUECOM", "",
                                transactionMMSEntity.getCheckSum());
                        transCLs.add(transCLSftpDTO);
                    }
                }
            } else {
                System.out.println("transactionMMSEntities is NULL or EMPTY");
            }

            System.out.println("\n\nCONCLUSION ROUND 2: ");
            System.out.println("totalCorrectTransaction: " + totalCorrectTransaction);
            System.out.println("totalCorrectAmount: " + totalCorrectAmount);
            System.out.println("totalWrongTransaction: " + totalWrongTransaction);
            System.out.println("totalWrongAmount: " + totalWrongAmount);
            System.out.println("\n\n");
            TransTHSftpDTO transTHTitle = new TransTHSftpDTO("STT", "Ngay giao dich", "Dich vu",
                    "So luong can khop",
                    "Gia tri can khop", "So luong chenh lech", "Gia tri chenh lech", "Ket qua", "Add_Info",
                    "Check sum");
            TransTHSftpDTO transTHSftpDTO = new TransTHSftpDTO("1", date, "1011", totalCorrectTransaction + "",
                    totalCorrectAmount + "", totalWrongTransaction + "", totalWrongAmount + "", "", "", "");
            System.out.println(transTHTitle.toString());
            System.out.println(transTHSftpDTO.toString());
            //
            System.out.println("\n\n");
            TransCLSftpDTO transCLSftpTitle = new TransCLSftpDTO("STT", "Channel", "Channel name", "Loai giao dich",
                    "Request id",
                    "Bank trans id", "FT", "Ngay giao dich",
                    "So tien giao dich", "Loai tien te", "Noi dung giao dich",
                    "Trang thai giao dich", "Tai khoan ghi no", "Ten chu tai khoan ghi no", "Tai khoan ghi co",
                    "Ten chu tai khoan ghi co", "So tai khoan don vi thu huong",
                    "Ten ngan hang nhan", "Key doi soat napas", "Trang thai giao dich", "Add_Info",
                    "Check sum");
            System.out.println(transCLSftpTitle.toString());
            for (TransCLSftpDTO transCL : transCLs) {
                System.out.println(transCL.toString());
            }
            // disconnect SFTP reader
            channelSftp.disconnect();
            session.disconnect();
            ///
            // open SFTP writter
            Session sessionWritter = jsch.getSession(username, host, port);
            sessionWritter.setPassword(password);
            sessionWritter.setConfig("StrictHostKeyChecking", "no");
            sessionWritter.connect();

            ChannelSftp channelSftpWritter = (ChannelSftp) sessionWritter.openChannel("sftp");
            channelSftpWritter.connect();

            String remoteTHFilePath = "/home/mbuser/OUT/RV_" + date + "_TH.txt";
            String remoteCLFilePath = "/home/mbuser/OUT/RV_" + date + "_CL.txt";
            // write TH
            ByteArrayInputStream inputTHTitle = new ByteArrayInputStream((transTHTitle.toString() + "\n").getBytes());
            channelSftpWritter.put(inputTHTitle, remoteTHFilePath, ChannelSftp.APPEND);
            ByteArrayInputStream inputTHValue = new ByteArrayInputStream(transTHSftpDTO.toString().getBytes());
            channelSftpWritter.put(inputTHValue, remoteTHFilePath, ChannelSftp.APPEND);
            // write CL
            if (!transCLs.isEmpty()) {
                ByteArrayInputStream inputCLTitle = new ByteArrayInputStream(
                        (transCLSftpTitle.toString() + "\n").getBytes());
                channelSftpWritter.put(inputCLTitle, remoteCLFilePath, ChannelSftp.APPEND);
                for (TransCLSftpDTO transCL : transCLs) {
                    ByteArrayInputStream inputCLValue = new ByteArrayInputStream(
                            (transCL.toString() + "\n").getBytes());
                    channelSftpWritter.put(inputCLValue, remoteCLFilePath, ChannelSftp.APPEND);
                }
            }
            // disconnect
            channelSftp.disconnect();
            sessionWritter.disconnect();
            // response API
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
            result = new ResponseMessageDTO("FAILED", e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String convertSftpDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}