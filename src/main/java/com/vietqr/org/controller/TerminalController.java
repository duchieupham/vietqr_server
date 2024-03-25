package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import com.vietqr.org.util.bank.mb.MBVietQRUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalController {
    private static final Logger logger = Logger.getLogger(TerminalController.class);
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 10;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    private MerchantConnectionService merchantConnectionService;

    @Autowired
    private PartnerConnectService partnerConnectService;

    @Autowired
    private MerchantBankReceiveService merchantBankReceiveService;

    @Autowired
    private AccountCustomerService accountCustomerService;

    @Autowired
    private TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    private TransactionTerminalTempService transactionTerminalTempService;

    @Autowired
    private TransactionReceiveService transactionReceiveService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AccountInformationService accountInformationService;

    @Autowired
    private AccountBankReceiveService accountBankReceiveService;

    @Autowired
    private AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    private TerminalBankService terminalBankService;

    @Autowired
    private MerchantMemberService merchantMemberService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FcmTokenService fcmTokenService;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private SocketHandler socketHandler;

    @GetMapping("terminal/generate-code")
    public ResponseEntity<ResponseDataDTO> generateTerminalCode() {
        ResponseDataDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String code = getRandomUniqueCode();
            result = new ResponseDataDTO(code);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseDataDTO("");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
    @GetMapping("terminal/web")
    public ResponseEntity<MerchantDetailDTO> getTerminalByUserId(
            @RequestParam String userId,
            @RequestParam int offset,
            @RequestParam String merchantId,
            @RequestParam String value
    ) {
        MerchantDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            MerchantDetailDTO dto = new MerchantDetailDTO();
            List<TerminalDetailWebDTO> terminals = new ArrayList<>();
            if (StringUtil.isNullOrEmpty(merchantId)) {
//                MerchantWebResponseDTO merchantDTO = merchantService.getMerchantByUserIdLimit(userId);
                dto.setMerchantId("");
                dto.setMerchantName("");
                dto.setMerchantAddress("");
                int countTerminal = terminalService.countNumberOfTerminalByUserId(userId);
                dto.setTotalTerminals(countTerminal);

                // chua truyen time, query xuong db nhieu lan
                List<ITerminalDetailWebDTO> terminalResponses = terminalService.getTerminalByUserId(userId, offset, value);
                terminals = terminalResponses.stream().map(terminal -> {
                    TerminalDetailWebDTO terminalDetailWebDTO = new TerminalDetailWebDTO();
                    terminalDetailWebDTO.setTerminalId(terminal.getTerminalId());
                    terminalDetailWebDTO.setTerminalName(terminal.getTerminalName());
                    terminalDetailWebDTO.setTerminalAddress(terminal.getTerminalAddress());
                    List<String> listCode = new ArrayList<>();
                    listCode = terminalBankReceiveService
                            .getSubTerminalCodeByTerminalCode(terminal.getTerminalCode());
                    listCode.add(terminal.getTerminalCode());
                    RevenueTerminalDTO revenueTerminalDTO = transactionTerminalTempService.getTotalTranByTerminalCodeAndTimeBetween(
                            listCode,
                            DateTimeUtil.getCurrentDateAsString(),
                            DateTimeUtil.getCurrentDateAsString());
                    if (revenueTerminalDTO != null) {
                        terminalDetailWebDTO.setTotalTrans(revenueTerminalDTO.getTotalTrans());
                        terminalDetailWebDTO.setTotalAmount(revenueTerminalDTO.getTotalAmount());
                    } else {
                        terminalDetailWebDTO.setTotalTrans(0);
                        terminalDetailWebDTO.setTotalAmount(0);
                    }
                    terminalDetailWebDTO.setTerminalCode(terminal.getTerminalCode());
                    // old
                    int totalMembers = accountBankReceiveShareService.countMembersByTerminalId(terminal.getTerminalId());
                    // new
//                    int totalMembers = merchantMemberService.countTerminalMember(terminal.get, terminal.getTerminalId());
                    terminalDetailWebDTO.setTotalMember(totalMembers);
                    terminalDetailWebDTO.setBankName(terminal.getBankName());
                    terminalDetailWebDTO.setBankAccount(terminal.getBankAccount());
                    terminalDetailWebDTO.setBankShortName(terminal.getBankShortName());
                    terminalDetailWebDTO.setBankAccountName(terminal.getBankAccountName());
                    return terminalDetailWebDTO;
                }).collect(Collectors.toList());
            } else {
                List<ITerminalDetailWebDTO> terminalResponses = new ArrayList<>();
//                List<ITerminalDetailWebDTO> terminalResponses = terminalService
//                        .getTerminalByUserIdAndMerchantId(merchantId, userId, offset, value);
//                terminals = terminalResponses.stream().map(terminal -> {
//                    TerminalDetailWebDTO terminalDetailWebDTO = new TerminalDetailWebDTO();
//                    terminalDetailWebDTO.setTerminalId(terminal.getTerminalId());
//                    terminalDetailWebDTO.setTerminalName(terminal.getTerminalName());
//                    terminalDetailWebDTO.setTerminalAddress(terminal.getTerminalAddress());
//                    terminalDetailWebDTO.setTotalTrans(terminal.getTotalTrans());
//                    terminalDetailWebDTO.setTotalAmount(terminal.getTotalAmount());
//                    terminalDetailWebDTO.setTotalMember(terminal.getTotalMember());
//                    terminalDetailWebDTO.setTerminalCode(terminal.getTerminalCode());
//                    terminalDetailWebDTO.setBankName(terminal.getBankName());
//                    terminalDetailWebDTO.setBankAccount(terminal.getBankAccount());
//                    terminalDetailWebDTO.setBankShortName(terminal.getBankShortName());
//                    terminalDetailWebDTO.setBankAccountName(terminal.getBankAccountName());
//                    return terminalDetailWebDTO;
//                }).collect(Collectors.toList());
            }
            // get detail of merchant
            dto.setTerminals(terminals);
            result = dto;
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/web/export")
    public ResponseEntity<byte[]> exportExcelTerminal(
            @RequestParam String merchantId,
            HttpServletResponse response
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            MerchantDetailDTO dto = new MerchantDetailDTO();
            List<TerminalDetailWebDTO> list = new ArrayList<>();
            for (int i = 0; i <= 43; i++) {
                TerminalDetailWebDTO terminalDetailWebDTO =
                        new TerminalDetailWebDTO();
                terminalDetailWebDTO.setTerminalId(UUID.randomUUID().toString());
                terminalDetailWebDTO.setTerminalName("Tous les Jours Vincom Center");
                terminalDetailWebDTO.setTerminalAddress("Hầm B3, Le Thanh Ton, P. Ben Nghe Q1, Tp.Hồ Chí Minh");
                terminalDetailWebDTO.setTotalTrans(100);
                terminalDetailWebDTO.setTotalAmount(1000000);
                terminalDetailWebDTO.setTotalMember(10);
                terminalDetailWebDTO.setTerminalCode("TLJ00" + i);
                terminalDetailWebDTO.setBankName("MBBank - Ngân hàng Quân đội");
                terminalDetailWebDTO.setBankAccount("1234567890");
                terminalDetailWebDTO.setBankShortName("MB");
                terminalDetailWebDTO.setBankAccountName("Tour les Jours");
                list.add(terminalDetailWebDTO);
            }
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("VietQRVN-MerchantId");

                // Tạo hàng tiêu đề
                Row headerRow = sheet.createRow(0);
                String[] headers = {"STT", "Tên Cửa Hàng", "Thành viên", "Mã điểm bán", "TK Ngân hàng", "Địa chỉ"};

                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                int counter = 1;
                for (TerminalDetailWebDTO item : list) {
                    Row row = sheet.createRow(counter++);
                    row.createCell(0).setCellValue(String.valueOf(counter));
                    row.createCell(1).setCellValue(item.getTerminalName());
                    row.createCell(2).setCellValue(item.getTotalMember());
                    row.createCell(3).setCellValue(item.getTerminalCode());
                    row.createCell(4).setCellValue(item.getBankShortName() + " - " + item.getBankAccount());
                    row.createCell(5).setCellValue(item.getTerminalAddress());
                }

                // Tạo một mảng byte từ workbook
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                byte[] fileContent = outputStream.toByteArray();

                // Thiết lập các thông số của response
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=list-shops.xlsx");
                response.setContentLength(fileContent.length);

                // Ghi dữ liệu vào response
                response.getOutputStream().write(fileContent);
            }
            response.getOutputStream().flush();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("terminal/web/member-detail/export/{terminalId}")
    public ResponseEntity<byte[]> exportMemberExcel(
            @PathVariable String terminalId,
            HttpServletResponse response
    ) {
        HttpStatus httpStatus = null;
        try {
            MerchantDetailDTO dto = new MerchantDetailDTO();
            List<AccountTerminalMemberDTO> list = new ArrayList<>();
            for (int i = 0; i <= 43; i++) {
                AccountTerminalMemberDTO accountTerminalMemberDTO =
                        new AccountTerminalMemberDTO();
                accountTerminalMemberDTO.setId(UUID.randomUUID().toString());
                accountTerminalMemberDTO.setPhoneNo("0987654321");
                accountTerminalMemberDTO.setFullName("Nguyễn Văn A");
                accountTerminalMemberDTO.setImgId("1234567890");
                accountTerminalMemberDTO.setRole("Nhân viên");
                list.add(accountTerminalMemberDTO);
            }
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("VietQRVN-ListMember");

                // Tạo hàng tiêu đề
                Row headerRow = sheet.createRow(0);
                String[] headers = {"STT", "Họ tên", "Số điện thoại", "Vai trò"};

                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                int counter = 1;
                for (AccountTerminalMemberDTO item : list) {
                    Row row = sheet.createRow(counter++);
                    row.createCell(0).setCellValue(String.valueOf(counter));
                    row.createCell(1).setCellValue(item.getFullName());
                    row.createCell(2).setCellValue(item.getPhoneNo());
                    row.createCell(3).setCellValue(item.getRole());
                }

                // Tạo một mảng byte từ workbook
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                byte[] fileContent = outputStream.toByteArray();

                // Thiết lập các thông số của response
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=list-members.xlsx");
                response.setContentLength(fileContent.length);

                // Ghi dữ liệu vào response
                response.getOutputStream().write(fileContent);
            }
            response.getOutputStream().flush();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("terminal/web/detail/{terminalId}")
    public ResponseEntity<Object> getTerminalDetailByTerminalId(
            @PathVariable String terminalId,
            @RequestParam String userId
    ) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            TerminalWebDetailResponseDTO dto = new TerminalWebDetailResponseDTO();
            TerminalBankResponseDTO terminal = new TerminalBankResponseDTO();
            ITerminalBankResponseDTO terminalResponse = terminalService.getTerminalResponseById(terminalId, userId);
            if (terminalResponse == null) {
                result = new ResponseMessageDTO("FAILED", "E113");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                terminal.setBankName(terminalResponse.getBankName());
                terminal.setBankAccount(terminalResponse.getBankAccount());
                terminal.setBankShortName(terminalResponse.getBankShortName());
                terminal.setUserBankName(terminalResponse.getUserBankName());
                terminal.setImgId(terminalResponse.getImgId());
                terminal.setBankCode(terminalResponse.getBankCode());
                terminal.setBankId(terminalResponse.getBankId());
                terminal.setQrCode(terminalResponse.getQrCode());
                terminal.setTerminalId(terminalId);
                dto.setId(terminalId);
                ITerminalWebResponseDTO terminalWebResponseDTO = terminalService.getTerminalWebById(terminalId);
                dto.setId(terminalWebResponseDTO.getId());
                dto.setName(terminalWebResponseDTO.getName());
                dto.setAddress(terminalWebResponseDTO.getAddress());
                dto.setCode(terminalWebResponseDTO.getCode());
                List<String> listCode = new ArrayList<>();
                listCode = terminalBankReceiveService
                        .getSubTerminalCodeByTerminalCode(terminalWebResponseDTO.getCode());
                listCode.add(terminalWebResponseDTO.getCode());
                RevenueTerminalDTO revenueTerminalDTO = transactionTerminalTempService.getTotalTranByTerminalCodeAndTimeBetween(
                        listCode, DateTimeUtil.getCurrentDateAsString(), DateTimeUtil.getCurrentDateAsString());
                dto.setTotalTrans(revenueTerminalDTO.getTotalTrans());
                dto.setTotalAmount(revenueTerminalDTO.getTotalAmount());
                LocalDateTime now = LocalDateTime.now();
                long time = now.toEpochSecond(ZoneOffset.UTC);
                // + 7 xem đã qua ngày chưa;
                time += DateTimeUtil.GMT_PLUS_7_OFFSET;
                // đổi sang DateTime - đây là thời gian hiện tại
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
                // đây là thời gian bắt ầu ngày hiện tại
                LocalDateTime startOfDay = localDateTime.toLocalDate().atStartOfDay();
                RevenueTerminalDTO revenueTerminalDTOPrevDate = transactionTerminalTempService.getTotalTranByTerminalCodeAndTimeBetweenWithCurrentTime(
                        listCode, startOfDay.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND,
                        localDateTime.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND);
                int revGrowthPrevDate = revenueTerminalDTOPrevDate.getTotalAmount() == 0 ? 0 :
                        (int) ((revenueTerminalDTO.getTotalAmount() - revenueTerminalDTOPrevDate.getTotalAmount()) * 100 / revenueTerminalDTOPrevDate.getTotalAmount());
                dto.setRevGrowthPrevDate(revGrowthPrevDate);
                RevenueTerminalDTO revenueTerminalDTOPrevMonth = transactionTerminalTempService.getTotalTranByTerminalCodeAndTimeBetween(
                        listCode, DateTimeUtil.getPrevMonthAsString(), DateTimeUtil.getPrevMonthAsString());
                int revGrowthPrevMonth = revenueTerminalDTOPrevMonth.getTotalAmount() == 0 ? 0 :
                        (int) ((revenueTerminalDTO.getTotalAmount() - revenueTerminalDTOPrevMonth.getTotalAmount()) * 100 / revenueTerminalDTOPrevDate.getTotalAmount());
                dto.setRevGrowthPrevMonth(revGrowthPrevMonth);
                dto.setBank(terminal);
                result = dto;
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/web/transaction-detail/export/{terminalId}")
    public ResponseEntity<byte[]> getTerminalTransactionByTerminalId(
            @PathVariable String terminalId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate,
            HttpServletResponse response) throws IOException {
        List<TransactionRelatedDetailDTO> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            TransactionRelatedDetailDTO transactionRelatedDetailDTO = new TransactionRelatedDetailDTO();
            transactionRelatedDetailDTO.setTransactionId(UUID.randomUUID().toString());
            transactionRelatedDetailDTO.setAmount(1000000);
            transactionRelatedDetailDTO.setBankAccount("1234567890");
            transactionRelatedDetailDTO.setBankName("MBBank - Ngân hàng Quân đội");
            transactionRelatedDetailDTO.setBankShortName("MBBank");
            transactionRelatedDetailDTO.setBankCode("MB");
            transactionRelatedDetailDTO.setContent("SQRTLJ0001, Đoạn text này nhằm mục đich kéo nó dài ra để test chứ cũng không có gì đặc biệt");
            transactionRelatedDetailDTO.setTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            transactionRelatedDetailDTO.setTimePaid(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            transactionRelatedDetailDTO.setStatus(1);
            transactionRelatedDetailDTO.setType(1);
            transactionRelatedDetailDTO.setNote("");
            transactionRelatedDetailDTO.setReferenceNumber("FT1234567890");
            transactionRelatedDetailDTO.setOrderId(UUID.randomUUID().toString());
            transactionRelatedDetailDTO.setTerminalCode("TLJ0001");
            list.add(transactionRelatedDetailDTO);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("VietQRVN-ListMember");

            // Tạo hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Thời gian tạo", "Mã điểm bán", "Số tiền"};

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int counter = 1;
            for (TransactionRelatedDetailDTO item : list) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(String.valueOf(counter));
                row.createCell(1).setCellValue(item.getTime());
                row.createCell(2).setCellValue(item.getTerminalCode());
                row.createCell(3).setCellValue(item.getAmount());
            }

            // Tạo một mảng byte từ workbook
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] fileContent = outputStream.toByteArray();

            // Thiết lập các thông số của response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=list-trans.xlsx");
            response.setContentLength(fileContent.length);

            // Ghi dữ liệu vào response
            response.getOutputStream().write(fileContent);
        }
        response.getOutputStream().flush();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("terminal/export-excel")
    public void test() {
        try {
            ExcelExportService excelExportService = new ExcelExportService();
            excelExportService.testRowAccessWindowSize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("terminal/web/transaction-detail/{terminalId}")
    public ResponseEntity<Object> getTerminalTransactionByTerminalId(
            @PathVariable String terminalId,
            @RequestParam String userId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "value") String value,
            @RequestParam(value = "fromDate") String fromDate,
            @RequestParam(value = "toDate") String toDate,
            @RequestParam(value = "offset") int offset
    ) {
        Object result = null;
        List<ITransactionRelatedDetailDTO> dtos = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // type = 9: all
            // type = 1: reference_number
            // type = 2: order_id
            // type = 3: content
            // type = 5: status
//            String checkInTerminal = accountBankReceiveShareService.checkUserExistedFromTerminal(terminalId, userId);
            String checkInTerminal = merchantMemberService.checkUserExistedFromTerminal(terminalId, userId);
            if (StringUtil.isNullOrEmpty(checkInTerminal)) {
                result = new ResponseMessageDTO("FAILED", "E113");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                switch (type) {
                    case 1:
                        dtos = transactionReceiveService.getTransTerminalByIdAndByFtCode(terminalId, value, fromDate, toDate, offset);
                        result = dtos;
                        httpStatus = HttpStatus.OK;
                        break;
                    case 2:
                        dtos = transactionReceiveService.getTransTerminalByIdAndByOrderId(terminalId, value, fromDate, toDate, offset);
                        result = dtos;
                        httpStatus = HttpStatus.OK;
                        break;
                    case 3:
                        value = value.replace("-", " ").trim();
                        dtos = transactionReceiveService.getTransTerminalByIdAndByContent(terminalId, value, fromDate, toDate, offset);
                        result = dtos;
                        httpStatus = HttpStatus.OK;
                        break;
                    case 5:
                        dtos = transactionReceiveService.getTransTerminalByIdAndByStatus(terminalId, Integer.parseInt(value), fromDate, toDate, offset);
                        result = dtos;
                        httpStatus = HttpStatus.OK;
                        break;
                    case 9:
                        dtos = transactionReceiveService.getAllTransTerminalById(terminalId, fromDate, toDate, offset);
                        result = dtos;
                        httpStatus = HttpStatus.OK;
                        break;
                    default:
                        logger.error("getTransactionUser: ERROR: INVALID TYPE");
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("getTransactionUser: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/member-detail/{terminalId}")
    public ResponseEntity<Object> getTerminalMemberMobileByTerminalId(
            @PathVariable String terminalId,
            @RequestParam String userId
    ) {
        List<AccountTerminalMemberDTO> result = new ArrayList<>();
        List<AccountTerminalMemberDTO> dtos = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<IAccountTerminalMemberDTO> response = accountInformationService
                    .getMembersByTerminalId(terminalId);
            dtos = response.stream().map(item -> {
                AccountTerminalMemberDTO dto = new AccountTerminalMemberDTO();
                dto.setId(item.getId());
                dto.setPhoneNo(item.getPhoneNo());
                dto.setFullName(item.getFullName());
                dto.setImgId(item.getImgId());
                dto.setBirthDate(item.getBirthDate());
                dto.setEmail(item.getEmail());
                dto.setNationalId(item.getNationalId());
                dto.setGender(item.getGender());
                boolean isOwner = item.getRole() == 1;
                dto.setRole(isOwner ? "Quản lý" : "Nhân viên");
                if (userId.equals(item.getId())) {
                    dto.setRole("Admin");
                }
                return dto;
            }).collect(Collectors.toList());
            result = dtos.stream()
                    .sorted(Comparator.comparing(AccountTerminalMemberDTO::getRole))
                    .collect(Collectors.toList());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/web/member-detail/{terminalId}")
    public ResponseEntity<Object> getTerminalMemberByTerminalId(
            @PathVariable String terminalId,
            @RequestParam String userId,
            @RequestParam int offset,
            @RequestParam String value,
            @RequestParam int type
    ) {
        Object result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
//            String checkInTerminal = accountBankReceiveShareService.checkUserExistedFromTerminal(terminalId, userId);
            List<AccountTerminalMemberDTO> dtos = new ArrayList<>();
            String checkInTerminal = merchantMemberService.checkUserExistedFromTerminal(terminalId, userId);
            if (StringUtil.isNullOrEmpty(checkInTerminal)) {
                result = new ResponseMessageDTO("FAILED", "E113");
                httpStatus = HttpStatus.OK;
            } else {
                switch (type) {
                    case 0:
                        List<IAccountTerminalMemberDTO> responsePhoneNo = accountInformationService.getMembersWebByTerminalIdAndPhoneNo(terminalId, value, offset);
//                        List<IAccountTerminalMemberDTO> responsePhoneNo = merchantMemberService.getMembersWebByTerminalIdAndPhoneNo(terminalId, value, offset);
                        if (FormatUtil.isListNullOrEmpty(responsePhoneNo)) {
                            dtos = new ArrayList<>();
                        } else {
                            dtos = responsePhoneNo.stream().map(item -> {
                                AccountTerminalMemberDTO dto = new AccountTerminalMemberDTO();
                                dto.setId(item.getId());
                                dto.setPhoneNo(item.getPhoneNo());
                                dto.setFullName(item.getFullName());
                                dto.setImgId(item.getImgId());
                                dto.setBirthDate(item.getBirthDate());
                                dto.setEmail(item.getEmail());
                                dto.setNationalId(item.getNationalId());
                                dto.setGender(item.getGender());
                                boolean isOwner = false;
                                if (item.getRole() == 1) {
                                    isOwner = true;
                                }
                                dto.setRole(isOwner ? "Quản lý" : "Nhân viên");
                                if (userId.equals(item.getId())) {
                                    dto.setRole("Admin");
                                }
                                return dto;
                            }).collect(Collectors.toList());
                        }
                        httpStatus = HttpStatus.OK;
                        break;
                    case 1:
                        List<IAccountTerminalMemberDTO> responseFullName = accountInformationService
                                .getMembersWebByTerminalIdAndFullName(terminalId, value, offset);
                        dtos = responseFullName.stream().map(item -> {
                            AccountTerminalMemberDTO dto = new AccountTerminalMemberDTO();
                            dto.setId(item.getId());
                            dto.setPhoneNo(item.getPhoneNo());
                            dto.setFullName(item.getFullName());
                            dto.setImgId(item.getImgId());
                            dto.setBirthDate(item.getBirthDate());
                            dto.setEmail(item.getEmail());
                            dto.setNationalId(item.getNationalId());
                            dto.setGender(item.getGender());
                            boolean isOwner = item.getRole() == 1;
                            dto.setRole(isOwner ? "Quản lý" : "Nhân viên");
                            if (userId.equals(item.getId())) {
                                dto.setRole("Admin");
                            }
                            return dto;
                        }).collect(Collectors.toList());
                        httpStatus = HttpStatus.OK;
                        break;
                    default:
                        httpStatus = HttpStatus.BAD_REQUEST;
                        break;
                }
            }
            result = dtos.stream()
                    .sorted(Comparator.comparing(AccountTerminalMemberDTO::getRole))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("terminal")
    public ResponseEntity<ResponseMessageDTO> insertTerminal(@Valid @RequestBody TerminalInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (dto.getBankIds() != null && dto.getBankIds().size() > 1) {
                result = new ResponseMessageDTO("FAILED", "E111");
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.error("TerminalController: insertTerminal: bankIds size > 1");
            } else {
                UUID uuid = UUID.randomUUID();
                //return terminal id if the code is existed
                String checkExistedCode = terminalService.checkExistedTerminal(dto.getCode());
                if (!StringUtil.isNullOrEmpty(checkExistedCode)) {
                    result = new ResponseMessageDTO("FAILED", "E110");
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                    TerminalEntity entity = new TerminalEntity();
                    entity.setId(uuid.toString());
                    entity.setName(dto.getName());
                    entity.setCode(dto.getCode());
                    entity.setAddress(StringUtil.isNullOrEmpty(dto.getAddress()) ? "" : dto.getAddress());
                    if (dto.getMerchantId() != null && !dto.getMerchantId().trim().isEmpty()) {
                        entity.setMerchantId(dto.getMerchantId());
                    } else {
                        entity.setMerchantId("");
                    }
                    entity.setUserId(dto.getUserId());
                    entity.setDefault(false);
                    entity.setTimeCreated(time);
                    terminalService.insertTerminal(entity);
                    // insert merchant member
                    List<MerchantMemberEntity> entities = new ArrayList<>();
                    List<MerchantMemberRoleEntity> merchantMemberRoleEntities = new ArrayList<>();
                    List<TerminalBankReceiveEntity> terminalBankReceiveEntities = new ArrayList<>();
                    if (!FormatUtil.isListNullOrEmpty(dto.getUserIds())) {
                        for (String userId : dto.getUserIds()) {
                            MerchantMemberEntity merchantMemberEntity = new MerchantMemberEntity();
                            String merchantMemberId = UUID.randomUUID().toString();
                            merchantMemberEntity.setId(merchantMemberId);
                            merchantMemberEntity.setUserId(userId);
                            merchantMemberEntity.setTerminalId(uuid.toString());
                            if (dto.getMerchantId() != null && !dto.getMerchantId().trim().isEmpty()) {
                                merchantMemberEntity.setMerchantId(dto.getMerchantId());
                            } else {
                                merchantMemberEntity.setMerchantId("");
                            }
                            merchantMemberEntity.setActive(true);
                            merchantMemberEntity.setTimeAdded(time);

                            List<String> roleReceives = new ArrayList<>();
                            List<String> roleRefunds = new ArrayList<>();
                            roleReceives.add(EnvironmentUtil.getOnlyReadReceiveTerminalRoleId());
                            MerchantMemberRoleEntity merchantMemberRoleEntity = new MerchantMemberRoleEntity();
                            merchantMemberRoleEntity.setId(UUID.randomUUID().toString());
                            merchantMemberRoleEntity.setMerchantMemberId(merchantMemberId);
                            merchantMemberRoleEntity.setUserId(dto.getUserId());
                            merchantMemberRoleEntity.setTransReceiveRoleIds(mapper
                                    .writeValueAsString(roleReceives));
                            merchantMemberRoleEntity.setTransRefundRoleIds(mapper
                                    .writeValueAsString(roleRefunds));
                            merchantMemberRoleEntities.add(merchantMemberRoleEntity);
                            entities.add(merchantMemberEntity);
                        }
                    }
                    if (!FormatUtil.isListNullOrEmpty(dto.getBankIds())) {
                        for (String bankId : dto.getBankIds()) {
                            TerminalBankReceiveEntity terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                            terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
                            terminalBankReceiveEntity.setBankId(bankId);
                            terminalBankReceiveEntity.setRawTerminalCode("");
                            terminalBankReceiveEntity.setTerminalCode("");
                            terminalBankReceiveEntity.setTypeOfQR(0);
                            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(bankId);
                            if (accountBankReceiveEntity != null) {
                                // luồng ưu tiên
                                if (accountBankReceiveEntity.isMmsActive()) {
                                    TerminalBankEntity terminalBankEntity =
                                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                    if (terminalBankEntity != null) {
                                        String qr = MBVietQRUtil.generateStaticVietQRMMS(
                                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                                        terminalBankEntity.getTerminalId(), dto.getCode()));
                                        terminalBankReceiveEntity.setData1(qr);
                                        terminalBankReceiveEntity.setData2(qr);
                                        String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                                        terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
                                    } else {
                                        logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                                    }
                                } else {
                                    // luồng thuong
                                    String qrCodeContent = "SQR" + dto.getCode();
                                    String bankAccount = accountBankReceiveEntity.getBankAccount();
                                    String caiValue = accountBankReceiveService.getCaiValueByBankId(bankId);
                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                    terminalBankReceiveEntity.setData1(qr);
                                    terminalBankReceiveEntity.setData2("");
                                    terminalBankReceiveEntity.setTraceTransfer("");
                                }
                            }
                            terminalBankReceiveEntities.add(terminalBankReceiveEntity);
                        }
                    }
                    merchantMemberRoleService.insertAll(merchantMemberRoleEntities);
                    terminalBankReceiveService.insertAll(terminalBankReceiveEntities);
                    merchantMemberService.insertAll(entities);
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                }
            }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        } finally {
            if (httpStatus.is2xxSuccessful()) {
                Thread thread = new Thread(() -> {
                    if (dto.getUserIds() != null && !dto.getUserIds().isEmpty()) {
                        int numThread = dto.getUserIds().size();
                        ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                        for (String userId : dto.getUserIds()) {
                            Map<String, String> data = new HashMap<>();
                            // insert notification
                            UUID notificationUUID = UUID.randomUUID();
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                            String title = NotificationUtil.getNotiTitleAddMember();
                            String message = String.format(NotificationUtil.getNotiDescAddMember(), dto.getName());
                            NotificationEntity notiEntity = new NotificationEntity();
                            notiEntity.setId(notificationUUID.toString());
                            notiEntity.setRead(false);
                            notiEntity.setMessage(message);
                            notiEntity.setTime(time);
                            notiEntity.setType(NotificationUtil.getNotiTypeAddMember());
                            notiEntity.setUserId(userId);
                            notiEntity.setData(dto.getCode());
                            // data thay đổi
                            data.put("notificationType", NotificationUtil.getNotiTypeAddMember());
                            data.put("notificationId", notificationUUID.toString());
                            data.put("terminalCode", dto.getCode());
                            data.put("terminalName", dto.getName());
                            executorService.submit(() -> pushNotification(title, message, notiEntity, data, userId));
                        }
                        executorService.shutdown();
                    }
                });
                thread.start();
            }
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("terminal/update")
    public ResponseEntity<ResponseMessageDTO> updateTerminal(@Valid @RequestBody TerminalUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            //return terminal id if the code is existed
            String checkExistedCode = "";
            if (!StringUtil.isNullOrEmpty(dto.getCode())) {
                checkExistedCode = terminalService.checkExistedTerminal(dto.getCode());
            }
            if (!StringUtil.isNullOrEmpty(checkExistedCode) && !checkExistedCode.equals(dto.getId())) {
                result = new ResponseMessageDTO("FAILED", "E110");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                TerminalEntity entity = terminalService.findTerminalById(dto.getId());
                if (entity != null) {
                    entity.setName(StringUtil.isNullOrEmpty(dto.getName()) ? entity.getName() : dto.getName());
//                    entity.setCode(StringUtil.isNullOrEmpty(dto.getCode()) ? entity.getCode() : dto.getCode());
                    entity.setAddress(StringUtil.isNullOrEmpty(dto.getAddress()) ? entity.getAddress() : dto.getAddress());
                    terminalService.insertTerminal(entity);
                }

//                if (StringUtil.isNullOrEmpty(checkExistedCode) &&
//                        !StringUtil.isNullOrEmpty(dto.getCode())) {
//                    // update account-bank-receive-share
//                    // get all account-bank-receive-share have bank_id by terminal id
//                    List<AccountBankReceiveShareEntity> entities =
//                            accountBankReceiveShareService.getAccountBankReceiveShareByTerminalId(dto.getId());
//                    Map<String, QRStaticCreateDTO> qrMap = new HashMap<>();
//                    if (!FormatUtil.isListNullOrEmpty(entities)) {
//                        List<String> bankIds = entities.stream().map(AccountBankReceiveShareEntity::getBankId)
//                                .distinct().collect(Collectors.toList());
//
//                        for (String bankId : bankIds) {
//                            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(bankId);
//                            if (accountBankReceiveEntity != null) {
//                                // luồng ưu tiên
//                                if (accountBankReceiveEntity.isMmsActive()) {
//                                    TerminalBankEntity terminalBankEntity =
//                                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
//                                    if (terminalBankEntity != null) {
//                                        String qr = MBVietQRUtil.generateStaticVietQRMMS(
//                                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
//                                                        terminalBankEntity.getTerminalId(), dto.getCode()));
//                                        String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
//                                        qrMap.put(bankId, new QRStaticCreateDTO(qr, traceTransfer));
//                                    } else {
//                                        logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
//                                    }
//                                } else {
//                                    // luồng thuong
//                                    String qrCodeContent = "SQR" + dto.getCode();
//                                    String bankAccount = accountBankReceiveEntity.getBankAccount();
//                                    String caiValue = accountBankReceiveService.getCaiValueByBankId(bankId);
//                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
//                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
//                                    qrMap.put(bankId, new QRStaticCreateDTO(qr, ""));
//                                }
//                            }
//                        }
//
//                        // update qr and trance transfer
//                        for (AccountBankReceiveShareEntity accountBankReceiveShareEntity : entities) {
//                            QRStaticCreateDTO qrStaticCreateDTO = qrMap.get(accountBankReceiveShareEntity.getBankId());
//                            if (qrStaticCreateDTO != null) {
//                                accountBankReceiveShareEntity.setQrCode(qrStaticCreateDTO.getQrCode());
//                                accountBankReceiveShareEntity.setTraceTransfer(qrStaticCreateDTO.getTraceTransfer());
//                            }
//                        }
//                        // update all
//                        accountBankReceiveShareService.insertAccountBankReceiveShare(entities);
//                    }
//                }

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/bank-account")
    public ResponseEntity<List<TerminalBankReceiveDTO>> getBankAccountNotAvailable(
            @Valid @RequestParam String terminalId,
            @Valid @RequestParam String userId) {
        List<TerminalBankReceiveDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            if (terminalId == null || terminalId.trim().isEmpty()) {
                result = accountBankReceiveService
                        .getAccountBankReceiveByUseId(userId);
            } else {
                result = terminalBankReceiveService
                        .getTerminalBankReceiveResponseByTerminalId(terminalId);
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("TerminalController: getBankAccountNotAvailable: " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // not update
    @GetMapping("terminal/bank")
    public ResponseEntity<TerminalShareResponseDTO> getTerminalsOfBank(
            @Valid @RequestParam String userId,
            @Valid @RequestParam String bankId,
            @Valid @RequestParam int offset) {
        TerminalShareResponseDTO result = null;
        HttpStatus httpStatus = null;
        try {
            TerminalShareResponseDTO dto = new TerminalShareResponseDTO();
            List<TerminalResponseInterfaceDTO> terminalInters = terminalService
                    .getTerminalsByUserIdAndBankId(userId, bankId, offset);
            List<TerminalResponseDTO> terminals = mapInterfToTerminalResponse(terminalInters);
            int total = terminalService.countNumberOfTerminalByUserIdAndBankId(userId, bankId);
            dto.setTotalTerminals(total);
            dto.setUserId(userId);

            // Fetch all banks associated with the terminals in a single database call
            List<ITerminalBankResponseDTO> allBankInters = accountBankReceiveShareService.getTerminalBanksByTerminalIds(
                    terminals.stream().map(TerminalResponseInterfaceDTO::getId).collect(Collectors.toList())
            );
            List<TerminalBankResponseDTO> allBanks = mapInterfTerminalBankToDto(allBankInters);

            // Map the banks to the respective terminals
            Map<String, List<TerminalBankResponseDTO>> terminalBanksMap = allBanks.stream()
                    .collect(Collectors.groupingBy(TerminalBankResponseDTO::getTerminalId));

            terminals.forEach(terminal -> {
                terminal.setBanks(terminalBanksMap.getOrDefault(terminal.getId(), new ArrayList<>()));
            });
            dto.setTerminals(terminals);

            result = dto;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("terminal/bank-account")
    public ResponseEntity<ResponseMessageDTO> insertBankAccountTerminal(@Valid @RequestBody TerminalBankInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            int countBank = accountBankReceiveShareService.countBankAccountByTerminalId(dto.getTerminalId());
            if (countBank >= 1) {
                result = new ResponseMessageDTO("FAILED", "E111");
                httpStatus = HttpStatus.BAD_REQUEST;
                logger.error("TerminalController: insertBankAccountTerminal: countBank > 1");
            } else {
                // get list userIds in terminal
                AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById(dto.getBankId());
                String qr = "";
                String traceTransfer = "";
                if (accountBankReceiveEntity != null) {
                    // luồng thường
                    TerminalEntity terminalEntity = terminalService.findTerminalById(dto.getTerminalId());
                    if (!accountBankReceiveEntity.isMmsActive()) {
                        String qrCodeContent = "SQR" + terminalEntity.getCode();
                        String bankAccount = accountBankReceiveEntity.getBankAccount();
                        String caiValue = accountBankReceiveService.getCaiValueByBankId(dto.getBankId());
                        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                        qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    } else {
                        // luồng ưu tien
                        TerminalBankEntity terminalBankEntity =
                                terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                        if (terminalBankEntity != null) {
                            qr = MBVietQRUtil.generateStaticVietQRMMS(
                                    new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                            terminalBankEntity.getTerminalId(), ""));
                            traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                        } else {
                            logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                        }
                    }
                }
                // get all userIds in terminal is_owner = false
                List<String> userIds = accountBankReceiveShareService.getUserIdsFromTerminalId(dto.getTerminalId(), dto.getUserId());
                // insert account-bank-receive-share
                List<AccountBankReceiveShareEntity> entities = new ArrayList<>();
                if (!FormatUtil.isListNullOrEmpty(userIds)) {
                    for (String userId : userIds) {
                        AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                        accountBankReceiveShareEntity.setId(UUID.randomUUID().toString());
                        accountBankReceiveShareEntity.setBankId(dto.getBankId());
                        accountBankReceiveShareEntity.setUserId(userId);
                        accountBankReceiveShareEntity.setOwner(false);
                        accountBankReceiveShareEntity.setTerminalId(dto.getTerminalId());
                        accountBankReceiveShareEntity.setQrCode(qr);
                        accountBankReceiveShareEntity.setTraceTransfer(traceTransfer);
                        entities.add(accountBankReceiveShareEntity);
                    }
                }
                UUID uuidShare = UUID.randomUUID();
                AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
                accountBankReceiveShareEntity.setId(uuidShare.toString());
                accountBankReceiveShareEntity.setBankId(dto.getBankId());
                accountBankReceiveShareEntity.setUserId(dto.getUserId());
                accountBankReceiveShareEntity.setOwner(true);
                accountBankReceiveShareEntity.setQrCode(qr);
                accountBankReceiveShareEntity.setTraceTransfer(traceTransfer);
                accountBankReceiveShareEntity.setTerminalId(dto.getTerminalId());
                entities.add(accountBankReceiveShareEntity);
                accountBankReceiveShareService.insertAccountBankReceiveShare(entities);

                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("terminal/bank-account")
    public ResponseEntity<ResponseMessageDTO> removeBankAccountTerminal(@Valid @RequestBody TerminalRemoveBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            accountBankReceiveShareService.removeBankAccountFromTerminal(dto.getTerminalId(), dto.getBankId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("terminal/remove")
    public ResponseEntity<ResponseMessageDTO> removeTerminalById(@Valid @RequestBody TerminalRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            terminalService.removeTerminalById(dto.getTerminalId());
            accountBankReceiveShareService.removeTerminalGroupByTerminalId(dto.getTerminalId());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/sub-detail/{terminalId}")
    public ResponseEntity<TerminalDetailDTO> getTerminal(@PathVariable String terminalId,
                                                         @RequestParam String fromDate,
                                                         @RequestParam String toDate) {
        TerminalDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            TerminalDetailDTO responseDTO = new TerminalDetailDTO();
//            ITerminalDetailResponseDTO dto = terminalService.getTerminalById(terminalId);
            ITerminalDetailResponseDTO dto = terminalService.getTerminalById(terminalId);
            if (dto!= null) {
                responseDTO.setTerminalId(dto.getId());
                responseDTO.setTerminalName(dto.getName());
                responseDTO.setTerminalAddress(dto.getAddress());
                responseDTO.setTerminalCode(dto.getCode());
                responseDTO.setUserId(dto.getUserId());
            }
//            ITerminalBankResponseDTO bank = accountBankReceiveShareService
//                    .getTerminalBanksByTerminalId(terminalId);
            ITerminalBankResponseDTO bank = terminalBankReceiveService
                    .getTerminalBanksByTerminalId(terminalId);
            if (bank!= null) {
                responseDTO.setBankAccount(bank.getBankAccount());
                responseDTO.setBankShortName(bank.getBankShortName());
                responseDTO.setQrCode(bank.getQrCode());
                responseDTO.setUserBankName(bank.getUserBankName());
            }
            List<String> listCode = new ArrayList<>();
            listCode = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(responseDTO.getTerminalCode());
            listCode.add(responseDTO.getTerminalCode());
            RevenueTerminalDTO revGrowthToday = transactionTerminalTempService.getTotalTranByTerminalCodeAndTimeBetween(
                    listCode, DateTimeUtil.removeTimeInDateTimeString(fromDate), DateTimeUtil.removeTimeInDateTimeString(toDate));
            LocalDateTime now = LocalDateTime.now();
            long time = now.toEpochSecond(ZoneOffset.UTC);
            // + 7 xem đã qua ngày chưa;
            time += DateTimeUtil.GMT_PLUS_7_OFFSET;
            // đổi sang DateTime - đây là thời gian hiện tại
            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
            // đây là thời gian bắt ầu ngày hiện tại
            LocalDateTime startOfDay = localDateTime.toLocalDate().atStartOfDay();
            RevenueTerminalDTO revenueTerminalDTOPrevDate = transactionTerminalTempService
                    .getTotalTranByUserIdAndTimeBetweenWithCurrentTime(
                            listCode, startOfDay.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND,
                            localDateTime.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND);
            if (revenueTerminalDTOPrevDate != null && revenueTerminalDTOPrevDate.getTotalAmount() != 0 && revenueTerminalDTOPrevDate.getTotalTrans() != 0) {
                double revGrowthPrevDate = revenueTerminalDTOPrevDate.getTotalAmount() == 0 ? 0 :
                        (double) (revGrowthToday.getTotalAmount() - revenueTerminalDTOPrevDate.getTotalAmount())
                                / revenueTerminalDTOPrevDate.getTotalAmount();
                responseDTO.setRatePrevDate((int) (revGrowthPrevDate * 100));
            } else {
                responseDTO.setRatePrevDate(0);
            }
            responseDTO.setTotalTrans(revGrowthToday.getTotalTrans());
            responseDTO.setTotalAmount(revGrowthToday.getTotalAmount());
            List<ISubTerminalDTO> iSubTerminalDTOS = new ArrayList<>();
            iSubTerminalDTOS = terminalBankReceiveService
                    .getListSubTerminalByTerminalId(terminalId);
            List<SubTerminalDTO> subTerminals = new ArrayList<>();
            subTerminals = iSubTerminalDTOS.stream().map(item -> {
                SubTerminalDTO subTerminalDTO = new SubTerminalDTO();
                subTerminalDTO.setSubTerminalId(item.getSubTerminalId());
                subTerminalDTO.setSubTerminalName(item.getRawTerminalCode() == null ? "" : item.getRawTerminalCode());
                subTerminalDTO.setSubTerminalAddress("");
                subTerminalDTO.setBankId(item.getBankId());
                subTerminalDTO.setTraceTransfer(item.getTraceTransfer()
                        == null ? "" : item.getTraceTransfer());
                subTerminalDTO.setTotalTrans(0);
                long totalAmount = transactionTerminalTempService
                        .getTotalAmountByTerminalCodeAndTime(item.getTerminalCode(), fromDate, toDate);
                subTerminalDTO.setTotalAmount(totalAmount);
                LocalDateTime now2 = LocalDateTime.now();
                long time2 = now2.toEpochSecond(ZoneOffset.UTC);
                // + 7 xem đã qua ngày chưa;
                time2 += DateTimeUtil.GMT_PLUS_7_OFFSET;
                // đổi sang DateTime - đây là thời gian hiện tại
                LocalDateTime localDateTime2 = LocalDateTime.ofEpochSecond(time2, 0, ZoneOffset.UTC);
                // đây là thời gian bắt ầu ngày hiện tại
                LocalDateTime startOfDay2 = localDateTime.toLocalDate().atStartOfDay();
                long revenueTerminalDTOPrevDate2 = transactionTerminalTempService.getTotalTranByTerminalCodeWithCurrentTime(
                        item.getTerminalCode(), startOfDay.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND,
                        localDateTime.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND);
                int revGrowthPrevDate = revenueTerminalDTOPrevDate2 == 0 ? 0 :
                        (int) ((totalAmount - revenueTerminalDTOPrevDate2) * 100 / revenueTerminalDTOPrevDate2);
                subTerminalDTO.setRatePrevDate(revGrowthPrevDate);
                if (item.getTraceTransfer() != null && !item.getTraceTransfer().trim().isEmpty()) {
                    subTerminalDTO.setQrCode(item.getQrCode2());
                } else {
                    subTerminalDTO.setQrCode(item.getQrCode1());
                }

                subTerminalDTO.setRatePrevDate(revGrowthPrevDate);
                subTerminalDTO.setSubTerminalCode(item.getTerminalCode());
                subTerminalDTO.setSubRawTerminalCode(item.getRawTerminalCode());
                return subTerminalDTO;
            }).collect(Collectors.toList());
            int totalSubTerminal = subTerminals.size();
            responseDTO.setTotalSubTerminal(totalSubTerminal);
            responseDTO.setSubTerminals(subTerminals);
            result = responseDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("TerminalController: getTerminal: ERROR: " + e.getMessage() +
                    " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/detail/{terminalId}")
    public ResponseEntity<TerminalDetailResponseDTO> getTerminalById(@PathVariable String terminalId) {
        TerminalDetailResponseDTO result = null;
        HttpStatus httpStatus = null;
        try {
            TerminalDetailResponseDTO responseDTO = new TerminalDetailResponseDTO();
//            ITerminalDetailResponseDTO dto = terminalService.getTerminalById(terminalId);
            ITerminalDetailResponseDTO dto = terminalService.getTerminalById(terminalId);
            responseDTO.setId(dto.getId());
            responseDTO.setName(dto.getName());
            responseDTO.setAddress(dto.getAddress());
            responseDTO.setCode(dto.getCode());
            responseDTO.setUserId(dto.getUserId());
            responseDTO.setDefault(dto.getIsDefault());
            responseDTO.setTotalMember(dto.getTotalMember());
//            responseDTO.setQrCode("");
            List<String> terminalIds = new ArrayList<>();
            terminalIds.add(terminalId);
//            List<ITerminalBankResponseDTO> iTerminalBankResponseDTOS = accountBankReceiveShareService.getTerminalBanksByTerminalIds(terminalIds);
            List<ITerminalBankResponseDTO> iTerminalBankResponseDTOS = terminalBankReceiveService.getTerminalBanksByTerminalIds(terminalIds);
            List<TerminalBankResponseDTO> banks = mapInterfTerminalBankToDto(iTerminalBankResponseDTOS);
            List<AccountMemberDTO> members = new ArrayList<>();
//            members = accountBankReceiveShareService.getMembersFromTerminalId(terminalId);
            members = merchantMemberService.getMembersFromTerminalId(terminalId);
            responseDTO.setBanks(banks);
            responseDTO.setMembers(members);
            result = responseDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal")
    public ResponseEntity<Object> getTerminalsByUserId(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "offset") int offset) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            switch (type) {
                // 0: Filter theo ‘Đã chia sẻ - Nhóm chia sẻ’
                // 1: Filter theo ‘Đã chia sẻ - Tài khoản ngân hàng’
                // 2: Filter theo ‘Chia sẻ với tôi - Nhóm chia sẻ’
                // 3: Filter theo ‘Chia sẻ với tôi - Tài khoản ngân hàng’
                case 0:
                    TerminalShareResponseDTO dto = new TerminalShareResponseDTO();
                    List<TerminalResponseInterfaceDTO> terminalInters = terminalService.getTerminalsByUserId(userId, offset);
                    List<TerminalResponseDTO> terminals = mapInterfToTerminalResponse(terminalInters);
//                    int total = terminalService.countNumberOfTerminalByUserId(userId);
                    int total = terminalService.countNumberOfTerminalByUserIdOwner(userId);
                    dto.setTotalTerminals(total);
                    dto.setUserId(userId);

                    // Fetch all banks associated with the terminals in a single database call
//                    List<ITerminalBankResponseDTO> allBankInters = accountBankReceiveShareService.getTerminalBanksByTerminalIds(
//                            terminals.stream().map(TerminalResponseInterfaceDTO::getId).collect(Collectors.toList())
//                    );
                    List<ITerminalBankResponseDTO> allBankInters = terminalBankReceiveService.getTerminalBanksByTerminalIds(
                            terminals.stream().map(TerminalResponseInterfaceDTO::getId).collect(Collectors.toList())
                    );
                    List<TerminalBankResponseDTO> allBanks = mapInterfTerminalBankToDto(allBankInters);

                    // Map the banks to the respective terminals
                    Map<String, List<TerminalBankResponseDTO>> terminalBanksMap = allBanks.stream()
                            .collect(Collectors.groupingBy(TerminalBankResponseDTO::getTerminalId));

                    terminals.forEach(terminal -> {
                        terminal.setBanks(terminalBanksMap.getOrDefault(terminal.getId(), new ArrayList<>()));
                    });
                    dto.setTerminals(terminals);

                    result = dto;
                    httpStatus = HttpStatus.OK;
                    break;
                case 1:
                    TerminalBankShareResponseDTO terminalBankShareResponseDTO = new TerminalBankShareResponseDTO();

//                    List<IBankShareResponseDTO> iBankShareResponseDTOS = accountBankReceiveShareService
//                            .getTerminalBankByUserId(userId, offset);
                    List<IBankShareResponseDTO> iBankShareResponseDTOS = terminalBankReceiveService
                            .getTerminalBankByUserId(userId, offset);
                    List<BankShareResponseDTO> bankShareResponseDTOs = mapInterfToBankShareResponse(iBankShareResponseDTOS);
//                    int totalBanks = accountBankReceiveShareService.countNumberOfBankShareByUserId(userId);
                    int totalBanks = terminalBankReceiveService.countNumberOfBankShareByUserId(userId);
                    terminalBankShareResponseDTO.setTotalBankShares(totalBanks);
                    terminalBankShareResponseDTO.setUserId(userId);

                    // Fetch all terminals associated with the banks in a single database call
//                    List<ITerminalShareDTO> iTerminalShareDTOS = terminalService.getTerminalSharesByBankIds(
//                            bankShareResponseDTOs.stream().map(BankShareResponseDTO::getBankId).collect(Collectors.toList()), userId
//                    );
                    List<ITerminalShareDTO> iTerminalShareDTOS = terminalService.getTerminalSharesByBankIds(
                            bankShareResponseDTOs.stream().map(BankShareResponseDTO::getBankId).collect(Collectors.toList()), userId
                    );
                    List<TerminalShareDTO> allTerminals = mapInterfToTerminalShare(iTerminalShareDTOS);

                    // Map the terminals to the respective banks
                    Map<String, List<TerminalShareDTO>> bankTerminalsMap = allTerminals.stream()
                            .collect(Collectors.groupingBy(TerminalShareDTO::getBankId));

                    bankShareResponseDTOs.forEach(bank -> {
                        bank.setTerminals(bankTerminalsMap.getOrDefault(bank.getBankId(), new ArrayList<>()));
                    });
                    terminalBankShareResponseDTO.setBankShares(bankShareResponseDTOs);
                    result = terminalBankShareResponseDTO;
                    httpStatus = HttpStatus.OK;
                    break;
                case 2:
                    // skip các terminal của role đại lý
                    TerminalShareResponseDTO terminalShareResponseDTO = new TerminalShareResponseDTO();
                    //2.2 lấy danh sách detail của terminal đó
                    List<TerminalResponseInterfaceDTO> iTerminalResponseDTOs = terminalService
                            .getTerminalSharesByUserId(userId, offset);
                    List<TerminalResponseDTO> terminalResponseDTOs = mapInterfToTerminalResponse(iTerminalResponseDTOs);
                    int totalTerminalShare = terminalService.countNumberOfTerminalShareByUserId(userId);
                    terminalShareResponseDTO.setTotalTerminals(totalTerminalShare);
                    terminalShareResponseDTO.setUserId(userId);

                    //2.3 Fetch all banks associated with the terminals in a single database call
                    List<ITerminalBankResponseDTO> iTerminalBankResponseDTOS = accountBankReceiveShareService
                            .getTerminalBanksByTerminalIds(
                            terminalResponseDTOs.stream().map(TerminalResponseDTO::getId).collect(Collectors.toList())
                    );

                    List<TerminalBankResponseDTO> allBankShares = mapInterfTerminalBankToDto(iTerminalBankResponseDTOS);

//                     Map the banks to the respective terminals
                    Map<String, List<TerminalBankResponseDTO>> terminalBankSharesMap = allBankShares.stream()
                            .collect(Collectors.groupingBy(TerminalBankResponseDTO::getTerminalId));

                    terminalResponseDTOs.forEach(terminal -> {
                        terminal.setBanks(terminalBankSharesMap.getOrDefault(terminal.getId(), new ArrayList<>()));
                    });
                    terminalShareResponseDTO.setTerminals(terminalResponseDTOs);

                    result = terminalShareResponseDTO;
                    httpStatus = HttpStatus.OK;
                    break;
                case 3:
                    TerminalBankShareResponseDTO responseDTO = new TerminalBankShareResponseDTO();

                    // only get terminal for accept terminal not merchant
//                    List<IBankShareResponseDTO> iBankShareResponseDTOList = accountBankReceiveShareService
//                            .getTerminalBankShareByUserId(userId, offset);
                    List<IBankShareResponseDTO> iBankShareResponseDTOList = terminalBankReceiveService
                            .getTerminalBankShareByUserId(userId, offset);
                    List<BankShareResponseDTO> shareResponseDTOList = mapInterfToBankShareResponse(iBankShareResponseDTOList);
                    int totalBankShares = accountBankReceiveShareService.countNumberOfTerminalBankShareByUserId(userId);
                    responseDTO.setTotalBankShares(totalBankShares);
                    responseDTO.setUserId(userId);

                    // Fetch all terminals associated with the banks in a single database call
                    // not correct
//                    List<ITerminalShareDTO> iTerminalShareDTOList = terminalService.getTerminalSharesByBankIds(
//                            shareResponseDTOList.stream().map(BankShareResponseDTO::getBankId)
//                                    .collect(Collectors.toList()), userId
//                    );
                    List<ITerminalShareDTO> iTerminalShareDTOList = terminalService.getTerminalSharesByBankIds2(
                            shareResponseDTOList.stream().map(BankShareResponseDTO::getBankId)
                                    .collect(Collectors.toList()), userId
                    );
                    List<TerminalShareDTO> terminalShareDTOS = mapInterfToTerminalShare(iTerminalShareDTOList);

                    // Map the terminals to the respective banks
                    Map<String, List<TerminalShareDTO>> listMap = terminalShareDTOS.stream()
                            .collect(Collectors.groupingBy(TerminalShareDTO::getBankId));

                    shareResponseDTOList.forEach(bank -> {
                        bank.setTerminals(listMap.getOrDefault(bank.getBankId(), new ArrayList<>()));
                    });
                    responseDTO.setBankShares(shareResponseDTOList);
                    result = responseDTO;
                    httpStatus = HttpStatus.OK;
                    break;
                default:
                    result = new ResponseMessageDTO("FAILED", "E88");
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
            }
        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("tid/synchronize")
    public ResponseEntity<Object> syncTerminal(@RequestHeader("Authorization") String token,
                                               @Valid @RequestBody List<TerminalSyncDTO> dtos) {
        Object result = null;
        HttpStatus httpStatus = null;
        List<TerminalTidResponseDTO> responses = new ArrayList<>();
        String terminalCode = "";
        try {
            String username = getUsernameFromToken(token);
            if (username != null && !username.trim().isEmpty()) {
                List<String> checkExistedCustomerSync = merchantConnectionService
                        .checkExistedCustomerSyncByUsername(username);
                if (checkExistedCustomerSync != null && !checkExistedCustomerSync.isEmpty()) {
                    if (dtos != null && !dtos.isEmpty()) {
                        boolean checkValid = true;
                        // for each dto validate and insert
                        for (TerminalSyncDTO dto : dtos) {
                            // check sum of bank account
                            String accessKey = accountCustomerService.getAccessKeyByUsername(username);
                            String checkSum = BankEncryptUtil.generateMD5SyncTidChecksum(accessKey, dto.getBankCode(),
                                    dto.getBankAccount());
                            if (BankEncryptUtil.isMatchChecksum(dto.getCheckSum(), checkSum)) {
                                //check bank account is_authenticated
                                AccountBankReceiveEntity bankReceiveEntity = accountBankReceiveService
                                        .checkExistedBankAccountAuthenticated(dto.getBankAccount(), dto.getBankCode());
                                if (bankReceiveEntity != null) {
                                    String checkValidBankAccount = merchantBankReceiveService
                                            .checkExistedBankAccountInOtherMerchant(
                                                    bankReceiveEntity.getId(), checkExistedCustomerSync.get(0));
                                    if (checkValidBankAccount == null || checkValidBankAccount.trim().isEmpty()) {
                                        // co terminalId
                                        if (dto.getTerminalId() != null && !dto.getTerminalId().trim().isEmpty()) {
                                            // dang bao tri
                                            logger.error("syncTerminal: ERROR: NOT IMPLEMENT");
                                            httpStatus = HttpStatus.BAD_REQUEST;
                                            result = new ResponseMessageDTO("FAILED", "E116");
                                            checkValid = false;
                                            break;
//                                                String terminalId = terminalService.checkExistedTerminalIntoMerchant(dto.getTerminalId(), checkExistedCustomerSync.get(0));
//                                                if (terminalId != null && !terminalId.trim().isEmpty()) {
//                                                    checkValid = true;
//                                                    String checkExistedTerminalCode = "";
//                                                    if (dto.getTerminalCode() != null && !dto.getTerminalCode().trim().isEmpty()
//                                                            && dto.getTerminalCode().trim().length() <= 20) {
//                                                        TerminalEntity entity = terminalService.findTerminalByPublicId(dto.getTerminalId());
//                                                        checkExistedTerminalCode = terminalService.checkExistedTerminal(dto.getTerminalCode());
//                                                        if (checkExistedTerminalCode != null && !checkExistedTerminalCode.trim().isEmpty() && !Objects.equals(entity.getId(), checkExistedTerminalCode)) {
//                                                            result = new ResponseMessageDTO("FAILED", "E110");
//                                                            httpStatus = HttpStatus.BAD_REQUEST;
//                                                            checkValid = false;
//                                                        } else {
//                                                            checkValid = true;
//                                                        }
//                                                    } else {
//                                                        dto.setTerminalCode(getRandomUniqueCode());
//                                                        checkValid = true;
//                                                    }
//                                                } else {
//                                                    logger.error("syncTerminal: ERROR: TERMINAL IS NOT EXISTED OR BELONG TO OTHER MERCHANT");
//                                                    httpStatus = HttpStatus.BAD_REQUEST;
//                                                    result = new ResponseMessageDTO("FAILED", "E113");
//                                                    checkValid = false;
//                                                    break;
//                                                }
//                                                if (dto.getTerminalCode() != null && !dto.getTerminalCode().trim().isEmpty()
//                                                && dto.getTerminalCode().trim().length() <= 20) {
//                                                    checkValid = true;
//                                                } else {
//                                                    checkValid = true;
//                                                    result = new ResponseMessageDTO("FAILED", "E46");
//                                                    httpStatus = HttpStatus.BAD_REQUEST;
//                                                    break;
//                                                }
                                        } else {
                                            // khong co terminalId
                                            if (dto.getTerminalCode() != null && !dto.getTerminalCode().trim().isEmpty()
                                                    && dto.getTerminalCode().trim().length() <= 20) {
                                                String checkExistedTerminalCodeRaw = terminalService.checkExistedRawTerminalCode(dto.getTerminalCode());
                                                if (checkExistedTerminalCodeRaw != null && !checkExistedTerminalCodeRaw.trim().isEmpty()) {
                                                    result = new ResponseMessageDTO("FAILED", "E110");
                                                    httpStatus = HttpStatus.BAD_REQUEST;
                                                    checkValid = false;
                                                    break;
                                                } else {
                                                    terminalCode = getRandomUniqueCodeInTerminalCode();
                                                    checkValid = true;
                                                }
                                            } else {
                                                terminalCode = getRandomUniqueCodeInTerminalCode();
                                                dto.setTerminalCode(terminalCode);
                                            }
                                        }
                                    } else {
                                        logger.error("syncTerminal: ERROR: INVALID BANK ACCOUNT");
                                        httpStatus = HttpStatus.BAD_REQUEST;
                                        result = new ResponseMessageDTO("FAILED", "E40");
                                        checkValid = false;
                                        break;
                                    }
                                } else {
                                    logger.error("syncTerminal: ERROR: INVALID BANK ACCOUNT");
                                    httpStatus = HttpStatus.BAD_REQUEST;
                                    result = new ResponseMessageDTO("FAILED", "E40");
                                    checkValid = false;
                                    break;
                                }

                            } else {
                                logger.error(
                                        "syncTerminal: ERROR: INVALID CHECKSUM");
                                httpStatus = HttpStatus.BAD_REQUEST;
                                result = new ResponseMessageDTO("FAILED", "E39");
                                checkValid = false;
                                break;
                            }
                        }

                        if (checkValid == true) {
                            List<TerminalEntity> terminalEntities = new ArrayList<>();
                            List<TerminalBankReceiveEntity> terminalBankReceiveEntities = new ArrayList<>();
                            List<MerchantBankReceiveEntity> merchantBankReceiveEntities = new ArrayList<>();
                            for (TerminalSyncDTO dto : dtos) {
                                TerminalTidResponseDTO response = new TerminalTidResponseDTO();
                                //update Terminal
                                if (dto.getTerminalId() != null && !dto.getTerminalId().trim().isEmpty()) {
//                            TerminalEntity terminalEntity = terminalService.findTerminalByPublicId(dto.getTerminalId());
//                            terminalEntity.setAddress(dto.getTerminalAddress());
//                            terminalEntity.setPublicId(dto.getTerminalId());
//                            terminalEntity.setName(dto.getTerminalName());
//
//                            terminalEntity.setRawTerminalCode(dto.getTerminalCode());
//                            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
//                                    .checkExistedBankAccountAuthenticated(dto.getBankAccount(), dto.getBankCode());
//                            if (accountBankReceiveEntity != null) {
//                                TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
//                                        .getTerminalBankReceiveByTerminalIdAndBankId(terminalEntity.getId(), accountBankReceiveEntity.getId());
//                                terminalBankReceiveEntity.setBankId(accountBankReceiveEntity.getId());
//                                // luồng ưu tiên
//                                if (accountBankReceiveEntity.isMmsActive()) {
//                                    TerminalBankEntity terminalBankEntity =
//                                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
//                                    if (terminalBankEntity != null) {
//                                        String qr = MBVietQRUtil.generateStaticVietQRMMS(
//                                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
//                                                        terminalBankEntity.getTerminalId(), dto.getTerminalCode()));
//                                        terminalBankReceiveEntity.setData2(qr);
//                                        String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
//                                        terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
//                                        terminalBankReceiveEntity.setData1("");
//                                        terminalBankReceiveEntities.add(terminalBankReceiveEntity);
//                                    } else {
//                                        logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
//                                    }
//                                } else {
//                                    // luồng thuong
//                                    String qrCodeContent = "SQR" + dto.getTerminalCode();
//                                    String bankAccount = accountBankReceiveEntity.getBankAccount();
//                                    String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
//                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
//                                    String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
//                                    terminalBankReceiveEntity.setData1(qr);
//                                    terminalBankReceiveEntity.setData2("");
//                                    terminalBankReceiveEntity.setTraceTransfer("");
//                                    terminalBankReceiveEntities.add(terminalBankReceiveEntity);
//                                }
//                                String bankName = accountBankReceiveService.getBankNameByBankId(accountBankReceiveEntity.getBankTypeId());
//                                response.setBankName(bankName);
//                                if (accountBankReceiveEntity.isMmsActive()) {
//                                    response.setQrCode(terminalBankReceiveEntity.getData2());
//                                } else {
//                                    response.setQrCode(terminalBankReceiveEntity.getData1());
//                                }
//                                response.setTerminalAddress(terminalEntity.getAddress());
//                                response.setBankAccount(accountBankReceiveEntity.getBankAccount());
//                                response.setTerminalId(terminalEntity.getPublicId());
//                                response.setTerminalCode(terminalEntity.getCode());
//                                response.setBankAccountName(accountBankReceiveEntity.getBankAccountName());
//                                response.setBankCode(dto.getBankCode());
//                                response.setTerminalName(terminalEntity.getName());
//                            }
//                            responses.add(response);
//                            terminalEntities.add(terminalEntity);
                                } else {
                                    // insert Terminal
                                    UUID terminalId = UUID.randomUUID();
                                    TerminalEntity entity = new TerminalEntity();
                                    entity.setId(terminalId.toString());
                                    entity.setName(dto.getTerminalName());
                                    entity.setRawTerminalCode(dto.getTerminalCode());
                                    entity.setCode(terminalCode);
                                    entity.setPublicId(UUID.randomUUID().toString());
                                    entity.setAddress(dto.getTerminalAddress());
                                    entity.setMerchantId(checkExistedCustomerSync.get(0));
                                    entity.setDefault(false);
                                    LocalDateTime now = LocalDateTime.now();
                                    entity.setTimeCreated(now.toEpochSecond(ZoneOffset.UTC));
                                    AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                                            .checkExistedBankAccountAuthenticated(dto.getBankAccount(), dto.getBankCode());
                                    TerminalBankReceiveEntity terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                                    terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
                                    terminalBankReceiveEntity.setTypeOfQR(0);
                                    terminalBankReceiveEntity.setRawTerminalCode("");
                                    terminalBankReceiveEntity.setTerminalCode("");
                                    terminalBankReceiveEntity.setTerminalId(terminalId.toString());
                                    if (accountBankReceiveEntity != null) {
                                        entity.setUserId(accountBankReceiveEntity.getUserId());
                                        terminalBankReceiveEntity.setBankId(accountBankReceiveEntity.getId());
                                        // luồng ưu tiên
                                        if (accountBankReceiveEntity.isMmsActive()) {
                                            TerminalBankEntity terminalBankEntity =
                                                    terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                                            if (terminalBankEntity != null) {
                                                String qr = MBVietQRUtil.generateStaticVietQRMMS(
                                                        new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                                                terminalBankEntity.getTerminalId(), terminalCode));
                                                terminalBankReceiveEntity.setData2(qr);
                                                String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                                                terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
                                                terminalBankReceiveEntity.setData1("");
                                                terminalBankReceiveEntities.add(terminalBankReceiveEntity);
                                            } else {
                                                logger.error("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
                                            }
                                        } else {
                                            // luồng thuong
                                            String qrCodeContent = "SQR" + terminalCode;
                                            String bankAccount = accountBankReceiveEntity.getBankAccount();
                                            String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
                                            VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                                            String qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                                            terminalBankReceiveEntity.setData1(qr);
                                            terminalBankReceiveEntity.setData2("");
                                            terminalBankReceiveEntity.setTraceTransfer("");
                                            terminalBankReceiveEntities.add(terminalBankReceiveEntity);
                                        }

                                        MerchantBankReceiveEntity merchantBankReceiveEntity = merchantBankReceiveService
                                                .getMerchantBankReceiveByMerchantAndBankId(checkExistedCustomerSync.get(0), accountBankReceiveEntity.getId());
                                        if (merchantBankReceiveEntity == null) {
                                            merchantBankReceiveEntity = new MerchantBankReceiveEntity();
                                            merchantBankReceiveEntity.setId(UUID.randomUUID().toString());
                                            merchantBankReceiveEntity.setBankId(accountBankReceiveEntity.getId());
                                            merchantBankReceiveEntity.setMerchantId(checkExistedCustomerSync.get(0));
                                            merchantBankReceiveEntities.add(merchantBankReceiveEntity);
                                        }
                                        String bankName = accountBankReceiveService.getBankNameByBankId(accountBankReceiveEntity.getBankTypeId());
                                        response.setBankName(bankName);
                                        if (accountBankReceiveEntity.isMmsActive()) {
                                            response.setQrCode(terminalBankReceiveEntity.getData2());
                                        } else {
                                            response.setQrCode(terminalBankReceiveEntity.getData1());
                                        }
                                        response.setTerminalAddress(entity.getAddress());
                                        response.setBankAccount(accountBankReceiveEntity.getBankAccount());
                                        response.setTerminalId(entity.getPublicId());
                                        response.setTerminalCode(entity.getRawTerminalCode());
                                        response.setBankAccountName(accountBankReceiveEntity.getBankAccountName());
                                        response.setBankCode(dto.getBankCode());
                                        response.setTerminalName(entity.getName());
                                    }
                                    responses.add(response);
                                    terminalEntities.add(entity);
                                }
                            }
                            terminalService.insertAllTerminal(terminalEntities);
                            merchantBankReceiveService.insertAllMerchantBankReceive(merchantBankReceiveEntities);
                            terminalBankReceiveService.insertAll(terminalBankReceiveEntities);
                            result = responses;
                            httpStatus = HttpStatus.OK;
                        }


                    } else {
                        System.out.println("syncTerminal: Request Body is null or empty");
                        logger.error("syncTerminal: Request Body is null or empty");
                        result = new ResponseMessageDTO("FAILED", "E46");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    // merchant is not existed
                    System.out.println("refundForMerchant: MERCHANT IS NOT EXISTED");
                    logger.error("refundForMerchant: MERCHANT IS NOT EXISTED");
                    result = new ResponseMessageDTO("FAILED", "E104");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                System.out.println("syncTerminal: INVALID TOKEN");
                logger.error("syncTerminal: INVALID TOKEN");
                result = new ResponseMessageDTO("FAILED", "E74");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("tid/list")
    public ResponseEntity<Object> getSyncTerminal(@RequestHeader("Authorization") String token,
                                                  @RequestParam int page,
                                                  @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            logger.error("syncTerminal: ERROR: NOT IMPLEMENT");
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E116");
//                String username = getUsernameFromToken(token);
//                if (username != null && !username.trim().isEmpty()) {
//                    List<String> checkExistedCustomerSync = merchantConnectionService
//                            .checkExistedCustomerSyncByUsername(username);
//                    if (checkExistedCustomerSync != null && !checkExistedCustomerSync.isEmpty()) {
//                        TerminalSyncResponseDTO dto = new TerminalSyncResponseDTO();
//                        dto.setPage(page);
//                        dto.setSize(size);
//                        List<TerminalTidResponseDTO> responseDTOS = new ArrayList<>();
//                        List<ITerminalTidResponseDTO> list = terminalService
//                                .getTerminalByMerchantId(checkExistedCustomerSync.get(0), page * size, size);
//                        responseDTOS = list.stream().map(item -> {
//                            TerminalTidResponseDTO terminalTidResponseDTO = new TerminalTidResponseDTO();
//                            terminalTidResponseDTO.setTerminalId(item.getTerminalId());
//                            terminalTidResponseDTO.setTerminalName(item.getTerminalName());
//                            terminalTidResponseDTO.setTerminalCode(item.getTerminalCode());
//                            terminalTidResponseDTO.setTerminalAddress(item.getTerminalAddress());
//                            terminalTidResponseDTO.setBankCode(item.getBankCode());
//                            terminalTidResponseDTO.setBankAccount(item.getBankAccount());
//                            terminalTidResponseDTO.setBankAccountName(item.getBankAccountName());
//                            if (item.getIsMmsActive()) {
//                                terminalTidResponseDTO.setQrCode(item.getData2());
//                            } else {
//                                terminalTidResponseDTO.setQrCode(item.getData1());
//                            }
//                            terminalTidResponseDTO.setBankName(item.getBankName());
//                            return terminalTidResponseDTO;
//                        }).collect(Collectors.toList());
//                        dto.setItems(responseDTOS);
//                        result = dto;
//                        httpStatus = HttpStatus.OK;
//
//                    } else {
//                        // merchant is not existed
//                        System.out.println("refundForMerchant: MERCHANT IS NOT EXISTED");
//                        logger.error("refundForMerchant: MERCHANT IS NOT EXISTED");
//                        result = new ResponseMessageDTO("FAILED", "E104");
//                        httpStatus = HttpStatus.BAD_REQUEST;
//                    }
//                } else {
//                    System.out.println("syncTerminal: INVALID TOKEN");
//                    logger.error("syncTerminal: INVALID TOKEN");
//                    result = new ResponseMessageDTO("FAILED", "E74");
//                    httpStatus = HttpStatus.BAD_REQUEST;
//                }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/list")
    public ResponseEntity<List<TerminalCodeResponseDTO>> getListTerminalResponse(
            @RequestParam String userId,
            @RequestParam String bankId) {
        List<TerminalCodeResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = terminalService
                    .getListTerminalResponseByBankIdAndUserId(userId, bankId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/sub-terminal/{terminalId}")
    public ResponseEntity<PageResultDTO> getListSubTerminal(
            @RequestParam String userId,
            @PathVariable String terminalId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String value,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        PageResultDTO result = new PageResultDTO();
        double totalPage = 0;
        List<SubTerminalDTO> items = new ArrayList<>();
        List<ISubTerminalDTO> dtos = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            dtos = terminalBankReceiveService
                    .getListSubTerminalByTerminalId(terminalId, (page - 1) * size, size, value);
            items = dtos.stream().map(item -> {
                SubTerminalDTO subTerminalDTO = new SubTerminalDTO();
                subTerminalDTO.setSubTerminalId(item.getSubTerminalId());
                subTerminalDTO.setSubTerminalName(item.getRawTerminalCode() == null ? "" : item.getRawTerminalCode());
                subTerminalDTO.setSubTerminalAddress("");
                subTerminalDTO.setBankId(item.getBankId());
                subTerminalDTO.setTraceTransfer(item.getTraceTransfer()
                        == null ? "" : item.getTraceTransfer());

                long totalAmount = transactionTerminalTempService
                        .getTotalAmountByTerminalCodeAndTime(item.getTerminalCode(), fromDate, toDate);
                LocalDateTime now = LocalDateTime.now();
                long time = now.toEpochSecond(ZoneOffset.UTC);
                // + 7 xem đã qua ngày chưa;
                time += DateTimeUtil.GMT_PLUS_7_OFFSET;
                // đổi sang DateTime - đây là thời gian hiện tại
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
                // đây là thời gian bắt ầu ngày hiện tại
                LocalDateTime startOfDay = localDateTime.toLocalDate().atStartOfDay();
                long revenueTerminalDTOPrevDate = transactionTerminalTempService.getTotalTranByTerminalCodeWithCurrentTime(
                        item.getTerminalCode(), startOfDay.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND,
                        localDateTime.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND);
                int revGrowthPrevDate = revenueTerminalDTOPrevDate == 0 ? 0 :
                        (int) ((totalAmount - revenueTerminalDTOPrevDate) * 100 / revenueTerminalDTOPrevDate);
                subTerminalDTO.setRatePrevDate(revGrowthPrevDate);
                if (item.getTraceTransfer() != null && !item.getTraceTransfer().trim().isEmpty()) {
                    subTerminalDTO.setQrCode(item.getQrCode2());
                } else {
                    subTerminalDTO.setQrCode(item.getQrCode1());
                }

                subTerminalDTO.setRatePrevDate(revGrowthPrevDate);
                subTerminalDTO.setSubTerminalCode(item.getTerminalCode());
                subTerminalDTO.setSubRawTerminalCode(item.getRawTerminalCode());
                return subTerminalDTO;
            }).collect(Collectors.toList());
            int totalElement = terminalBankReceiveService.countSubTerminalByTerminalId(terminalId, value);
            result.setItems(items);
            result.setTotalElement(totalElement);
            totalPage = (int) Math.ceil((double) totalElement / size);
            result.setPage(page);
            result.setTotalPage((int) totalPage);
            result.setTotalElement(totalElement);
            result.setSize(size);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("TerminalController: getListSubTerminal: ERROR: " + e.getMessage()
            + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/detail-qr/{terminalId}")
    public ResponseEntity<TerminalQrDTO> getDetailQrTerminal(@PathVariable String terminalId,
                                                             @RequestParam String userId) {

        TerminalQrDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = new TerminalQrDTO();
//            ITerminalDetailResponseDTO dto = terminalService.getTerminalById(terminalId);
            ITerminalDetailResponseDTO dto = terminalService.getTerminalById(terminalId);
            if (dto!= null) {
                result.setTerminalId(dto.getId());
                result.setTerminalName(dto.getName());
                result.setTerminalAddress(dto.getAddress());
                result.setTerminalCode(dto.getCode());
                result.setUserId(dto.getUserId());
            }
            ITerminalBankResponseDTO bank = terminalBankReceiveService
                    .getTerminalBanksByTerminalId(terminalId);
            if (bank!= null) {
                result.setBankAccount(bank.getBankAccount());
                result.setBankShortName(bank.getBankShortName());
                result.setQrCode(bank.getQrCode());
                result.setUserBankName(bank.getUserBankName());
                result.setImgId(bank.getImgId());
            }
            httpStatus = HttpStatus.OK;
            if (result.getTerminalCode() == null || result.getTerminalCode().trim().isEmpty()) {
                result = null;
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/list-sub-terminal/{terminalId}")
    public ResponseEntity<List<ISubTerminalResponseDTO>> getDetailQrTerminal(@PathVariable String terminalId) {

        List<ISubTerminalResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = terminalBankReceiveService.getListSubTerminalByTerId(terminalId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal/sub-terminal/detail/{subTerminalId}")
    public ResponseEntity<SubTerminalDTO> getSubTerminalDetail(
            @PathVariable String subTerminalId) {
        SubTerminalDTO result = new SubTerminalDTO();
        HttpStatus httpStatus = null;
        try {
            ISubTerminalDTO dto = terminalBankReceiveService
                    .getSubTerminalDetailBySubTerminalId(subTerminalId);
            if (dto != null) {
                result.setSubTerminalId(dto.getSubTerminalId());
                result.setBankId(dto.getBankId());
                result.setTraceTransfer(dto.getTraceTransfer()
                        == null ? "" : dto.getTraceTransfer());
                if (dto.getTraceTransfer() != null && !dto.getTraceTransfer().trim().isEmpty()) {
                    result.setQrCode(dto.getQrCode2());
                } else {
                    result.setQrCode(dto.getQrCode1());
                }
                result.setSubTerminalCode(dto.getTerminalCode());
                result.setSubRawTerminalCode(dto.getRawTerminalCode());
                result.setSubTerminalName(dto.getRawTerminalCode());
                result.setSubTerminalAddress("");
                result.setRatePrevDate(0);
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("TerminalController: getSubTerminalDetail: ERROR: " + e.getMessage()
            + " at: " + System.currentTimeMillis());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("terminal/qr-sync")
    public ResponseEntity<ResponseMessageDTO> createQRSyncToTerminal(@Valid @RequestBody QRSyncRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        String terminalCode = "";
        String qr = "";
        try {
            logger.info("TerminalController: createQRSyncToTerminal: Request: " + dto.toString());
            terminalCode = getRandomUniqueCodeInTerminalCode();
            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                    .getAccountBankById(dto.getBankId());
            TerminalBankReceiveEntity terminalBankReceiveEntity = terminalBankReceiveService
                    .getTerminalBankReceiveByRawTerminalCode(dto.getMachineCode());
            if (terminalBankReceiveEntity == null) {
                terminalBankReceiveEntity = new TerminalBankReceiveEntity();
                terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
            }
            terminalBankReceiveEntity.setTerminalId(dto.getTerminalId());
            terminalBankReceiveEntity.setRawTerminalCode(dto.getMachineCode());
            terminalBankReceiveEntity.setTerminalCode(terminalCode);
            terminalBankReceiveEntity.setTypeOfQR(1);

            if (accountBankReceiveEntity != null) {
                terminalBankReceiveEntity.setBankId(accountBankReceiveEntity.getId());
                // luồng ưu tiên
                if (accountBankReceiveEntity.isMmsActive()) {
                    TerminalBankEntity terminalBankEntity =
                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
                    if (terminalBankEntity != null) {
                        qr = MBVietQRUtil.generateStaticVietQRMMS(
                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
                                        terminalBankEntity.getTerminalId(), terminalCode));
                        terminalBankReceiveEntity.setData2(qr);
                        String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
                        terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
                        terminalBankReceiveEntity.setData1("");
                    } else {
                        logger.error("TerminalController: createQRSyncToTerminal: terminalBankEntity is null or bankCode is not MB");
                    }
                } else {
                    // luồng thuong
                    String qrCodeContent = "SQR" + terminalCode;
                    String bankAccount = accountBankReceiveEntity.getBankAccount();
                    String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", qrCodeContent, bankAccount);
                    qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
                    terminalBankReceiveEntity.setData1(qr);
                    terminalBankReceiveEntity.setData2("");
                    terminalBankReceiveEntity.setTraceTransfer("");
                }
            }

            String serviceVhitekActive = EnvironmentUtil.getServiceVhitekActive();
            PartnerConnectEntity partnerConnectEntity = partnerConnectService
                    .getPartnerConnectByServiceName(serviceVhitekActive);

            if (partnerConnectEntity != null) {
                // get token
                TokenDTO tokenDTO = getCustomerSyncToken(partnerConnectEntity);
                if (tokenDTO != null) {

                    Map<String, Object> data = new HashMap<>();
                    data.put("mid", dto.getMachineCode());
                    data.put("qrCode", qr);
                    // call api
                    LocalDateTime now = LocalDateTime.now();
                    long time = now.toEpochSecond(ZoneOffset.UTC);
                    System.out.println("TerminalController: createQRSyncToTerminal: Request: "
                            + data + " token: " + tokenDTO.getAccess_token() + " at: " + time);
                    logger.info("TerminalController: createQRSyncToTerminal: Request: "
                            + data + " token: " + tokenDTO.getAccess_token() + " at: " + time);
                    WebClient webClient = WebClient.builder()
                            .baseUrl(partnerConnectEntity.getUrl5())
                            .build();
                    Mono<ClientResponse> responseMono = webClient.post()
                            .header("Authorization", "Bearer " + tokenDTO.getAccess_token())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(data))
                            .exchange();
                    ClientResponse response = responseMono.block();
                    LocalDateTime responseTime = LocalDateTime.now();
                    time = responseTime.toEpochSecond(ZoneOffset.UTC);
                    if (response.statusCode().is2xxSuccessful()) {
                        String json = response.bodyToMono(String.class).block();
                        System.out.println("TerminalController: createQRSyncToTerminal: Response: " + json
                        + " at: " + time);
                        logger.info("TerminalController: createQRSyncToTerminal: Response: " + json + " success status: " + response.statusCode()
                        + " at: " + time);

                        terminalBankReceiveService.insert(terminalBankReceiveEntity);
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        String json = response.bodyToMono(String.class).block();
                        System.out.println("createQRSyncToTerminal: Response: " + json);
                        logger.error("TerminalController: createQRSyncToTerminal: Response: " + json + " error status: " + response.statusCode()
                        + " at: " + time);
                        result = new ResponseMessageDTO("FAILED", "E05");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }

                } else {
                    result = new ResponseMessageDTO("FAILED", "E05");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E05");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("TerminalController: createQRSyncToTerminal: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private TokenDTO getCustomerSyncToken(PartnerConnectEntity entity) {
        TokenDTO result = null;
        try {
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(entity.getUrl1())
                    .buildAndExpand();
            WebClient webClient = WebClient.builder()
                    .baseUrl(entity.getUrl1())
                    .build();
            Map<String, Object> data = new HashMap<>();
            String key = entity.getUsernameBasic() + ":" + entity.getPasswordBasic();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("VhitekActiveController: getCustomerSyncToken: encodedKey: " + encodedKey);
            System.out.println("VhitekActiveController: getCustomerSyncToken: encodedKey: " + encodedKey);
            Mono<TokenDTO> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange()
                    .flatMap(clientResponse -> {
                        System.out.println(
                                "VhitekActiveController: get token: status code: " + clientResponse.statusCode());
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
                logger.info("VhitekActiveController: getCustomerSyncToken: token got: " + result.getAccess_token());
                System.out.println(
                        "VhitekActiveController: getCustomerSyncToken: token got: " + result.getAccess_token());
            } else {
                logger.info("VhitekActiveController: getCustomerSyncToken: Token could not be retrieved");
                System.out.println("VhitekActiveController: getCustomerSyncToken: Token could not be retrieved");
            }
        } catch (Exception e) {
            logger.info("VhitekActiveController: getCustomerSyncToken: ERROR: " + e.toString());
            System.out.println("VhitekActiveController: getCustomerSyncToken:  ERROR: " + e.toString());
        }
        return result;
    }

    private String getUsernameFromToken(String token) {
        String result = "";
        try {
            if (token != null && !token.trim().isEmpty()) {
                String secretKey = "mySecretKey";
                String jwtToken = token.substring(7); // remove "Bearer " from the beginning
                Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
                String userId = (String) claims.get("user");
                if (userId != null) {
                    result = new String(Base64.getDecoder().decode(userId));
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    private List<BankShareResponseDTO> mapInterfToBankShareResponse(List<IBankShareResponseDTO> iBankShareResponseDTOList) {
        List<BankShareResponseDTO> shareResponseDTOList = iBankShareResponseDTOList.stream().map(item -> {
            BankShareResponseDTO bankShareResponseDTO = new BankShareResponseDTO();
            bankShareResponseDTO.setBankName(item.getBankName());
            bankShareResponseDTO.setBankId(item.getBankId());
            bankShareResponseDTO.setBankCode(item.getBankCode());
            bankShareResponseDTO.setBankAccount(item.getBankAccount());
            bankShareResponseDTO.setUserBankName(item.getUserBankName());
            bankShareResponseDTO.setBankShortName(item.getBankShortName());
            bankShareResponseDTO.setImgId(item.getImgId());
            return bankShareResponseDTO;
        }).collect(Collectors.toList());
        return shareResponseDTOList;
    }

    private List<TerminalResponseDTO> mapInterfToTerminalResponse(List<TerminalResponseInterfaceDTO> terminalInters) {
        List<TerminalResponseDTO> terminals = terminalInters.stream().map(item -> {
            TerminalResponseDTO terminalResponseDTO = new TerminalResponseDTO();
            terminalResponseDTO.setId(item.getId());
            terminalResponseDTO.setName(item.getName());
            terminalResponseDTO.setAddress(item.getAddress());
            terminalResponseDTO.setCode(item.getCode());
            terminalResponseDTO.setDefault(item.getIsDefault());
            terminalResponseDTO.setUserId(item.getUserId());
            terminalResponseDTO.setTotalMembers(item.getTotalMembers());
            return terminalResponseDTO;
        }).collect(Collectors.toList());
        return terminals;
    }

    private List<TerminalShareDTO> mapInterfToTerminalShare(List<ITerminalShareDTO> iTerminalShareDTOList) {
        List<TerminalShareDTO> terminalShareDTOS = iTerminalShareDTOList.stream().map(item -> {
            TerminalShareDTO terminalShareDTO = new TerminalShareDTO();
            terminalShareDTO.setId(item.getTerminalId());
            terminalShareDTO.setBankId(item.getBankId());
            terminalShareDTO.setTerminalName(item.getTerminalName());
            terminalShareDTO.setTerminalCode(item.getTerminalCode());
            terminalShareDTO.setTerminalAddress(item.getTerminalAddress());
            terminalShareDTO.setTotalMembers(item.getTotalMembers());
            terminalShareDTO.setDefault(item.getIsDefault());
            return terminalShareDTO;
        }).collect(Collectors.toList());
        return terminalShareDTOS;
    }

    private List<TerminalBankResponseDTO> mapInterfTerminalBankToDto(List<ITerminalBankResponseDTO> iTerminalBankResponseDTOS) {
        List<TerminalBankResponseDTO> allBankShares = iTerminalBankResponseDTOS.stream().map(item -> {
            TerminalBankResponseDTO terminalBankResponseDTO = new TerminalBankResponseDTO();
            terminalBankResponseDTO.setBankId(item.getBankId());
            terminalBankResponseDTO.setTerminalId(item.getTerminalId());
            terminalBankResponseDTO.setBankName(item.getBankName());
            terminalBankResponseDTO.setBankCode(item.getBankCode());
            terminalBankResponseDTO.setBankAccount(item.getBankAccount());
            terminalBankResponseDTO.setUserBankName(item.getUserBankName());
            terminalBankResponseDTO.setBankShortName(item.getBankShortName());
            terminalBankResponseDTO.setImgId(item.getImgId());
            terminalBankResponseDTO.setQrCode(item.getQrCode() != null ? item.getQrCode() : "");
            return terminalBankResponseDTO;
        }).collect(Collectors.toList());
        return allBankShares;
    }


    private String getTerminalCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }

    private String getRandomUniqueCode() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = getTerminalCode();
                checkExistedCode = terminalService.checkExistedTerminal(code);
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception e) {
        }
        return result;
    }

    private String getRandomUniqueCodeInTerminalCode() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = getTerminalCode();
                checkExistedCode = terminalBankReceiveService.checkExistedTerminalCode(code);
                if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
                    checkExistedCode = terminalService.checkExistedTerminal(code);
                }
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception e) {
        }
        return result;
    }

    private void pushNotification(String title, String message, NotificationEntity notiEntity, Map<String, String> data,
                                  String userId) {
        try {
            if (notiEntity != null) {
                notificationService.insertNotification(notiEntity);
            }
            List<FcmTokenEntity> fcmTokens = new ArrayList<>();
            fcmTokens = fcmTokenService.getFcmTokensByUserId(userId);
            firebaseMessagingService.sendUsersNotificationWithData(data,
                    fcmTokens,
                    title, message);
            socketHandler.sendMessageToUser(userId,
                    data);
        } catch (IOException e) {
            logger.error(
                    "Add member to terminal: WS: push Notification - RECHARGE ERROR: "
                            + e.toString());
        }
    }
}
