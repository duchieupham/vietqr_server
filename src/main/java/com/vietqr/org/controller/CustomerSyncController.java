package com.vietqr.org.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.vietqr.org.dto.*;
import com.vietqr.org.dto.mapper.ErrorCodeMapper;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.DateTimeUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.StringUtil;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.util.EnvironmentUtil;

import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class CustomerSyncController {
    private static final Logger logger = Logger.getLogger(CustomerSyncController.class);

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    MerchantConnectionService merchantConnectionService;

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    AccountBankReceivePersonalService accountBankReceivePersonalService;

    @Autowired
    CustomerSyncMappingService customerSyncMappingService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    MerchantSyncService merchantSyncService;

    @Autowired
    MerchantBankReceiveService merchantBankReceiveService;

    @Autowired
    BankReceiveConnectionService bankReceiveConnectionService;

    @Autowired
    AccountCustomerService accountCustomerService;

    @Autowired
    CustomerErrorLogService customerErrorLogService;

    private static final List<String> VALID_ERROR_CODES = Arrays.asList("R", "S", "F", "W");

    @GetMapping("/customer-error-log/example")
    public ResponseEntity<InputStreamResource> getCustomerErrorLogExample(HttpServletResponse response) throws IOException {
        ClassPathResource file = new ClassPathResource("templates/CustomerErrorLog.xlsx");
        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Đọc file Excel và tạo InputStreamResource
        InputStreamResource resource = new InputStreamResource(file.getInputStream());

        // Thiết lập headers để trả về file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=CustomerErrorLog.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/customer-error-log/update")
    public ResponseEntity<ResponseObjectDTO> updateCustomerErrorLog(
            @RequestParam("file") MultipartFile file,
            @RequestParam("customerId") String customerId) {
        ResponseObjectDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<CustomerErrorLogInsertDTO> errorDescriptions = new ArrayList<>();
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                // Lấy sheet có tên ErrorDescription
                Sheet sheet = workbook.getSheet("ErrorDescription");

                if (sheet == null) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                // Bỏ qua hàng tiêu đề (header)
                for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        // Lấy dữ liệu từ từng cột
                        String groupCode = row.getCell(1).getStringCellValue();
                        String errorCode = row.getCell(2).getStringCellValue();
                        String errorDescription = row.getCell(3).getStringCellValue();
                        if (!VALID_ERROR_CODES.contains(groupCode)) {
                            // Trả về thông báo chi tiết về dòng nào có ErrorCode không hợp lệ
                            result = new ResponseObjectDTO("FAILED", "E167");
                            httpStatus = HttpStatus.BAD_REQUEST;
                            return new ResponseEntity<>(result, httpStatus);
                        }
                        // Tạo đối tượng ErrorDescription và thêm vào danh sách
                        CustomerErrorLogInsertDTO error = new CustomerErrorLogInsertDTO(groupCode, errorCode, errorDescription);
                        errorDescriptions.add(error);
                    }
                }
            }
            Map<String, List<CustomerErrorLogInsertDTO>> groupedByErrorCode = errorDescriptions.stream()
                    .collect(Collectors.groupingBy(CustomerErrorLogInsertDTO::getGroupCode));
            List<CustomerErrorLogEntity> entities = new ArrayList<>();
            for (Map.Entry<String, List<CustomerErrorLogInsertDTO>> entry : groupedByErrorCode.entrySet()) {
                CustomerErrorLogEntity entity = new CustomerErrorLogEntity();
                entity.setId(UUID.randomUUID().toString());
                entity.setGroupCode(entry.getKey());
                List<ErrorCodeMapper> errorCodeMappers = new ArrayList<>();
                errorCodeMappers = entry.getValue().stream().map(e ->
                        new ErrorCodeMapper(e.getErrorCode(), e.getErrorDescription())).collect(Collectors.toList());
                entity.setErrorCodes(new ObjectMapper().writeValueAsString(errorCodeMappers));
                entity.setCustomerSyncId(customerId);
                entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                entities.add(entity);
            }
            customerErrorLogService.insertAll(entities);
            result = new ResponseObjectDTO("SUCCESS", errorDescriptions);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateCustomerErrorLog: ERROR: " + e.toString());
            result = new ResponseObjectDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("admin/customer-sync")
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

    @GetMapping("admin/customer-sync/sorted")
    public ResponseEntity<Object> getCustomerSyncList(
            @RequestParam int type,
            @RequestParam int typeSearch,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String value) {
        // type dùng để lấy ra danh sách
        // type = 9 => all
        // type = 0 => api service
        // type = 1 => ecommerce
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();
        try {
            int totalElements = 0;
            int offset = (page - 1) * size;

            List<CustomerSyncListDTO> data = new ArrayList<>();
            List<CustomerSyncListDTO> infos = new ArrayList<>();

            // type dùng để lấy ra danh sách
            if (type == 9) {
                // type = 9 => search all
                if (typeSearch == 9 || typeSearch == 0) { // type = 0 => search merchant name
                    data = customerSyncService.getCustomerSyncListByMerchant(value, offset, size);
                    totalElements = customerSyncService.countCustomerSyncListByMerchant(value);
                } else if (typeSearch == 9 || typeSearch == 1) { // type = 1 => search bank Account
                    data = customerSyncService.getCustomerSyncListByMerchantByBankAccount(value, offset, size);
                    totalElements = customerSyncService.countCustomerSyncListByMerchantByBankAccount(value);
                }
            } else if (type == 0) {
                if (typeSearch == 9 || typeSearch == 0) { // type = 0 => search merchant name
                    data = customerSyncService.getCustomerSyncAPIListByMerchant(value, offset, size);
                    totalElements = customerSyncService.countCustomerSyncAPIListByMerchant(value);
                } else if (typeSearch == 9 || typeSearch == 1) { // type = 1 => search bank Account
                    data = customerSyncService.getCustomerSyncAPIListByMerchantByBankAccount(value, offset, size);
                    totalElements = customerSyncService.countCustomerSyncAPIListByMerchantByBankAccount(value);
                }
            } else if (type == 1) {
                if (typeSearch == 9 || typeSearch == 0) { // type = 0 => search merchant name
                    data = customerSyncService.getCustomerSyncEcListByMerchant(value, offset, size);
                    totalElements = customerSyncService.countCustomerSyncEcListByMerchant(value);
                } else if (typeSearch == 9 || typeSearch == 1) { // type = 1 => search bank Account
                    data = customerSyncService.getCustomerSyncEcListByMerchantByBankAccount(value, offset, size);
                    totalElements = customerSyncService.countCustomerSyncEcListByMerchantByBankAccount(value);
                }
            }

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElements);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElements, size));

            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            result = pageResDTO;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getCustomerSyncList: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("admin/customer-sync/information")
    public ResponseEntity<ResponseMessageDTO> updateCustomerSync(@RequestBody CustomerSyncUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getCustomerSyncId() != null) {
                customerSyncService.updateCustomerSync(dto.getUrl(), dto.getIp(), dto.getPassword(), dto.getPort(),
                        dto.getSuffix(), dto.getUsername(), dto.getCustomerSyncId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("updateCustomerSync: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("admin/customer-sync/information")
    public ResponseEntity<Object> getCustomerSyncInfo(@RequestParam(value = "id") String id) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            // 0 => API Service
            // 1 => E-Commerce
            Integer cusSyncType = customerSyncService.checkCustomerSyncTypeById(id);
            if (cusSyncType != null && cusSyncType == 0) {
                CusSyncApiInfoDTO cusSyncDTO = customerSyncService.getCustomerSyncApiInfo(id);
                if (cusSyncDTO != null) {
                    result = cusSyncDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getCustomerSyncInfo: NOT FOUND CUS_SYNC");
                    result = new ResponseMessageDTO("FAILED", "E81");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else if (cusSyncType != null && cusSyncType == 1) {
                CusSyncEcInfoDTO cusSyncDTO = customerSyncService.getCustomerSyncEcInfo(id);
                if (cusSyncDTO != null) {
                    result = cusSyncDTO;
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("getCustomerSyncInfo: NOT FOUND CUS_SYNC");
                    result = new ResponseMessageDTO("FAILED", "E81");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("getCustomerSyncInfo: NOT FOUND TYPE");
                result = new ResponseMessageDTO("FAILED", "E80");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getCustomerSyncList: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // @GetMapping("customer-sync/information")
    // public ResponseEntity<Object>
    // getCustomerSyncInfoByAccountId(@RequestParam(value = "userId") String userId)
    // {
    // Object result = null;
    // HttpStatus httpStatus = null;
    // try {
    // // 0 => API Service
    // // 1 => E-Commerce
    // Integer cusSyncType = customerSyncService.checkCustomerSyncTypeById();
    // if (cusSyncType != null && cusSyncType == 0) {
    // CusSyncApiInfoDTO cusSyncDTO = customerSyncService.getCustomerSyncApiInfo();
    // if (cusSyncDTO != null) {
    // result = cusSyncDTO;
    // httpStatus = HttpStatus.OK;
    // } else {
    // logger.error("getCustomerSyncInfo: NOT FOUND CUS_SYNC");
    // result = new ResponseMessageDTO("FAILED", "E81");
    // httpStatus = HttpStatus.BAD_REQUEST;
    // }
    // } else if (cusSyncType != null && cusSyncType == 1) {
    // CusSyncEcInfoDTO cusSyncDTO = customerSyncService.getCustomerSyncEcInfo();
    // if (cusSyncDTO != null) {
    // result = cusSyncDTO;
    // httpStatus = HttpStatus.OK;
    // } else {
    // null");
    // logger.error("getCustomerSyncInfo: NOT FOUND CUS_SYNC");
    // result = new ResponseMessageDTO("FAILED", "E81");
    // httpStatus = HttpStatus.BAD_REQUEST;
    // }
    // } else {
    // logger.error("getCustomerSyncInfo: NOT FOUND TYPE");
    // result = new ResponseMessageDTO("FAILED", "E80");
    // httpStatus = HttpStatus.BAD_REQUEST;
    // }
    // } catch (Exception e) {
    // logger.error("getCustomerSyncList: ERROR: " + e.toString());
    // result = new ResponseMessageDTO("FAILED", "E05");
    // httpStatus = HttpStatus.BAD_REQUEST;
    // }
    // return new ResponseEntity<>(result, httpStatus);
    // }

    // test get token customer
    @PostMapping("admin/customer-sync/check-token")
    public ResponseEntity<ResponseMessageDTO> checkTokenCustomerSync(@RequestBody CustomerSyncTokenTestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            result = getCustomerSyncToken(dto.getUrl(), dto.getUsername(), dto.getPassword());
            if (result.getStatus().trim().equals("SUCCESS")) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("checkTokenCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get customer token
    private ResponseMessageDTO getCustomerSyncToken(String url, String username, String password) {
        ResponseMessageDTO result = null;
        try {
            String key = username.trim() + ":" + password.trim();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            logger.info("key: " + encodedKey + " - username: " + username.trim() + " - password: "
                    + password.trim());

            Map<String, Object> data = new HashMap<>();
            UriComponents uriComponents = null;
            WebClient webClient = null;
            uriComponents = UriComponentsBuilder
                    .fromHttpUrl(
                            url.trim() + "/api/token_generate")
                    .buildAndExpand();
            webClient = WebClient.builder()
                    .baseUrl(url.trim() + "/api/token_generate")
                    .build();
            Mono<ClientResponse> responseMono = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromValue(data))
                    .exchange();

            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response pushNewTransactionToCustomerSync: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("access_token") != null) {
                    String accessToken = rootNode.get("access_token").asText();
                    result = new ResponseMessageDTO("SUCCESS", accessToken);
                } else {
                    result = new ResponseMessageDTO("FAILED", "E82");
                }
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("Token could not be retrieved from: " + url + " - error: " + json);
                result = new ResponseMessageDTO("FAILED", "E05 - " + json);
            }
        } catch (Exception e) {
            logger.error("Error at getCustomerSyncToken: " + url + " - " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
        }
        return result;
    }

    // get systemPassword for customer
    @GetMapping("admin/customer-sync/system-password")
    public ResponseEntity<ResponseMessageDTO> getSystemPasswordForCustomer(
            @RequestParam(value = "username") String username) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (username != null && !username.trim().isEmpty()) {
                String password = encodeBase64(username.trim());
                result = new ResponseMessageDTO("SUCCESS", password);
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getSystemPasswordForCustomer: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String encodeBase64(String text) {
        byte[] bytes = text.trim().getBytes();
        String encodedText = Base64.getEncoder().encodeToString(bytes);
        return encodedText;
    }

    // update status customer sync
    @PostMapping("admin/customer-sync/status")
    public ResponseEntity<ResponseMessageDTO> updateCustomerSyncStatus(@RequestBody CustomerSyncStatusDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                boolean active = false;
                if (dto.getStatus() == 1) {
                    active = true;
                }
                customerSyncService.updateCustomerSyncStatus(active, dto.getCustomerSyncId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateCustomerSyncStatus: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // add customer sync admin side
    @PostMapping("admin/customer-sync")
    public ResponseEntity<ResponseMessageDTO> insertNewCustomerSync(
            @RequestHeader("Authorization") String token,
            @RequestBody CustomerSyncInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            token = removeBearer(token);
            UUID customerSyncId = UUID.randomUUID();
            result = insertCustomerSync(customerSyncId.toString(), dto, token, 0);
            if (result.getStatus().equals("SUCCESS")) {
                // find userId by bankAccount (with authenticated)
                String userId = getUserIdByBankAccountProduct(dto.getBankAccount(), token);
                // do insert/update mapping customer sync only product url
                int environment = 0;
                CustomerSyncMappingInsertDTO customerSyncMappingInsertDTO = new CustomerSyncMappingInsertDTO();
                customerSyncMappingInsertDTO.setUserId(userId);
                if (EnvironmentUtil.isProduction() == false) {
                    environment = 1;
                    customerSyncMappingInsertDTO.setCustomerSyncId("");
                    customerSyncMappingInsertDTO.setCustomerSyncTestId(customerSyncId.toString());
                } else {
                    environment = 2;
                    customerSyncMappingInsertDTO.setCustomerSyncId(customerSyncId.toString());
                    customerSyncMappingInsertDTO.setCustomerSyncTestId("");
                }
                customerSyncMappingInsertDTO.setEnvironment(environment);
                insertCustomerSyncMappingProduct(customerSyncMappingInsertDTO, token);
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertNewCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getUserIdByBankAccountProduct(String bankAccount, String token) {
        String result = "";
        try {
            token = removeBearer(token);
            logger.info("getUserIdByBankAccountProduct: token: " + token);
            WebClient webClient = WebClient.builder()
                    .baseUrl(EnvironmentUtil.getUrlVietqrVnProd() + "/customer-sync/bank-account?bankAccount="
                            + bankAccount)
                    .build();
            Mono<ClientResponse> responseMono = webClient.get()
                    .header("Authorization", "Bearer " + token)
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.error("getUserIdByBankAccountProduct: Response: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("status") != null) {
                    String status = rootNode.get("status").asText();
                    if (status != null && status.trim().equals("SUCCESS")) {
                        String message = rootNode.get("message").asText();
                        if (message != null && !message.trim().isEmpty()) {
                            result = message;
                        }
                    } else {
                        logger.error("getUserIdByBankAccountProduct: NOT FOUND USER ID BY BANK ACCOUNT");
                    }
                } else {
                    logger.error("getUserIdByBankAccountProduct: NOT FOUND status");
                }
            } else {
                logger.error("getUserIdByBankAccountProduct: ERROR GET USER ID BY BANK ACCOUNT - statusCode: "
                        + response.statusCode().toString());
                String json = response.bodyToMono(String.class).block();
                logger.error("getUserIdByBankAccountProduct: Response: " + json);
            }
        } catch (Exception e) {
            logger.error("getUserIdByBankAccountProduct: ERROR: " + e.toString());
        }
        return result;
    }

    // find userId by bankAccount (authenticated) in golive env
    @GetMapping("customer-sync/bank-account")
    public ResponseEntity<ResponseMessageDTO> getUserIdByBankAccountAuthenticated(
            @RequestParam String bankAccount) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String userId = accountBankReceiveService.getUserIdByBankAccountAuthenticated(bankAccount);
            if (userId != null && !userId.trim().isEmpty()) {
                result = new ResponseMessageDTO("SUCCESS", userId);
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("CHECK", "C10");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            logger.error("getUserIdByBankAccountAuthenticated: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private void insertCustomerSyncMappingProduct(
            CustomerSyncMappingInsertDTO dto, String token) {
        try {
            token = removeBearer(token);
            String url = EnvironmentUtil.getUrlVietqrVnProd() + "/customer-sync/mapping";
            Map<String, Object> data = new HashMap<>();
            data.put("userId", dto.getUserId());
            data.put("customerSyncId", dto.getCustomerSyncId());
            data.put("customerSyncTestId", dto.getCustomerSyncTestId());
            data.put("environment", dto.getEnvironment());
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
                    .build();
            Mono<ClientResponse> responseMono = webClient.post()
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(data))
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                logger.info("insertCustomerSyncMappingProduct: INSERT SUCCESS");
            }
        } catch (Exception e) {
            logger.error("insertCustomerSyncMappingProduct: ERROR: " + e.toString());
        }
    }

    // insert customer sync mapping
    @PostMapping("customer-sync/mapping")
    public ResponseEntity<ResponseMessageDTO> insertCustomerSyncMapping(
            @RequestBody CustomerSyncMappingInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                if (dto.getEnvironment() != null && dto.getEnvironment() == 1) {
                    // env = 1 => do insert
                    UUID uuid = UUID.randomUUID();
                    CustomerSyncMappingEntity entity = new CustomerSyncMappingEntity();
                    entity.setId(uuid.toString());
                    entity.setUserId(dto.getUserId());
                    entity.setCusSyncTestId(dto.getCustomerSyncTestId());
                    entity.setCusSyncId("");
                    customerSyncMappingService.insert(entity);
                    httpStatus = HttpStatus.OK;
                    result = new ResponseMessageDTO("SUCCESS", "");
                } else if (dto.getEnvironment() != null && dto.getEnvironment() == 2) {
                    // env = 2 => do update/ if not existed, do insert
                    customerSyncMappingService.updateCustomerSyncMapping(dto.getUserId(), dto.getCustomerSyncId());
                    httpStatus = HttpStatus.OK;
                    result = new ResponseMessageDTO("SUCCESS", "");
                } else {
                    logger.error("insertCustomerSyncMapping: INVALID ENVIRONMENT");
                    result = new ResponseMessageDTO("FAILED", "E106");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("insertCustomerSyncMapping: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("insertCustomerSyncMapping: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // env = 0: dynamic
    // env = 1: test
    // env = 2: golive
    private ResponseMessageDTO insertCustomerSync(
            String customerSyncId,
            CustomerSyncInsertDTO dto,
            String token,
            int environment) {
        ResponseMessageDTO result = null;
        try {
            // PROD ENVIRONMENT
            if (environment == 0) {
                // core
                // 1. check existed merchant name
                if (dto != null && dto.getMerchantName() != null && !dto.getMerchantName().trim().isEmpty()) {
                    List<String> checkExistedMerchant = customerSyncService.checkExistedMerchant(dto.getMerchantName());
                    if (checkExistedMerchant != null && !checkExistedMerchant.isEmpty()) {
                        // if existed: -> show msg
                        logger.error("insertNewCustomerSync: EXISTED MERCHANT NAME");
                        result = new ResponseMessageDTO("FAILED", "E85");
                        ///
                    } else {
                        // if not: pass
                        //
                        String checkExistedAddress = terminalBankService.checkExistedTerminalAddress(dto.getAddress());
                        // 2. check address
                        if (checkExistedAddress != null && !checkExistedAddress.isEmpty()) {
                            // if existed -> show msg
                            logger.error("insertNewCustomerSync: EXISTED TERMINAL ADDRESS");
                            result = new ResponseMessageDTO("FAILED", "E86");
                        } else {
                            // if not -> pass
                            //
                            // 4. check Env
                            if (EnvironmentUtil.isProduction() == false) {
                                //////////
                                String userId = getUserIdByBankAccountProduct(dto.getBankAccount(), token);
                                if (userId != null && !userId.trim().isEmpty()) {
                                    // test:
                                    // 5. check bankAccount existed
                                    UUID uuidAccountBank = UUID.randomUUID();
                                    String checkExistedBankAccount = accountBankReceiveService
                                            .checkExistedBankAccountByBankAccount(dto.getBankAccount());
                                    if (checkExistedBankAccount != null && !checkExistedBankAccount.trim().isEmpty()) {
                                        // if existed -> passed
                                    } else {
                                        // if not -> add bank_Account (is_sync = true and is_authenticated = true)
                                        AccountBankReceiveEntity accountBankReceiveEntity = new AccountBankReceiveEntity();
                                        accountBankReceiveEntity.setId(uuidAccountBank.toString());
                                        accountBankReceiveEntity.setBankAccount(dto.getBankAccount());
                                        accountBankReceiveEntity.setBankAccountName(dto.getUserBankName());
                                        if (!StringUtil.isNullOrEmpty(dto.getBankCode())) {
                                            String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
                                            accountBankReceiveEntity.setBankTypeId(bankTypeId);
                                        } else {
                                            accountBankReceiveEntity.setBankTypeId(EnvironmentUtil.getBankTypeIdRecharge());
                                        }
                                        accountBankReceiveEntity.setAuthenticated(true);
                                        accountBankReceiveEntity.setSync(true);
                                        accountBankReceiveEntity.setWpSync(false);
                                        accountBankReceiveEntity.setMmsActive(false);
                                        accountBankReceiveEntity.setNationalId("");
                                        accountBankReceiveEntity.setPhoneAuthenticated("");
                                        accountBankReceiveEntity.setStatus(true);
                                        accountBankReceiveEntity.setUserId(EnvironmentUtil.getDefaultUserIdTest());
                                        accountBankReceiveEntity.setRpaSync(false);
                                        accountBankReceiveEntity.setPassword("");
                                        accountBankReceiveEntity.setUsername("");
                                        accountBankReceiveEntity.setTerminalLength(10);
                                        accountBankReceiveEntity.setValidFeeTo(0L);
                                        accountBankReceiveEntity.setValidFeeFrom(0L);
                                        accountBankReceiveEntity.setValidService(false);
                                        accountBankReceiveService.insertAccountBank(accountBankReceiveEntity);
                                        //
                                        UUID uuid2 = UUID.randomUUID();
                                        BankReceivePersonalEntity bankReceivePersonalEntity = new BankReceivePersonalEntity();
                                        bankReceivePersonalEntity.setId(uuid2.toString());
                                        bankReceivePersonalEntity.setBankId(uuidAccountBank.toString());
                                        bankReceivePersonalEntity.setUserId(EnvironmentUtil.getDefaultUserIdTest());
                                        accountBankReceivePersonalService
                                                .insertAccountBankReceivePersonal(bankReceivePersonalEntity);
                                        //
                                    }
                                    //
                                    // 6. add customer_sync (with status)
                                    // check valid IP+PORT OR url and suffix URL
                                    String url = "";
                                    String ip = "";
                                    String port = "";
                                    String suffix = "";
                                    if (dto.getSuffixUrl() != null && !dto.getSuffixUrl().trim().isEmpty()) {
                                        if (dto.getSuffixUrl().trim().endsWith("/")) {
                                            suffix = dto.getSuffixUrl().substring(0,
                                                    dto.getSuffixUrl().trim().length() - 1);
                                        } else {
                                            suffix = dto.getSuffixUrl().trim();
                                        }
                                    }
                                    if (dto.getUrl() != null && !dto.getUrl().trim().isEmpty()) {
                                        // if valid -> pass
                                        if (dto.getUrl().trim().endsWith("/")) {
                                            url = dto.getUrl().substring(0, dto.getUrl().trim().length() - 1);
                                        } else {
                                            url = dto.getUrl().trim();
                                        }
                                    } else if (dto.getIp() != null && dto.getPort() != null
                                            && !dto.getIp().trim().isEmpty() && !dto.getPort().trim().isEmpty()) {
                                        // if valid -> pass
                                        ip = dto.getIp().trim();
                                        port = dto.getPort().trim();
                                    } else {
                                        // if not -> show msg
                                        logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                        result = new ResponseMessageDTO("FAILED", "E87");

                                    }
                                    if (!url.trim().isEmpty() || (!ip.trim().isEmpty() && !port.trim().isEmpty())) {
                                        // do insert customer sync
                                        // UUID uuid = UUID.randomUUID();
                                        boolean active = false;
                                        if (dto.getCustomerSyncActive() == 1) {
                                            active = true;
                                        }
                                        CustomerSyncEntity customerSyncEntity = new CustomerSyncEntity();
                                        customerSyncEntity.setId(customerSyncId);
                                        customerSyncEntity.setActive(active);
                                        customerSyncEntity.setInformation(url);
                                        customerSyncEntity.setIpAddress(ip);
                                        customerSyncEntity.setPassword(dto.getCustomerPassword());
                                        customerSyncEntity.setPort(port);
                                        customerSyncEntity.setSuffixUrl(suffix);
                                        customerSyncEntity.setToken("");
                                        customerSyncEntity.setUserId("");
                                        customerSyncEntity.setUsername(dto.getCustomerUsername());
                                        customerSyncEntity.setMerchant(dto.getMerchantName());
                                        customerSyncEntity.setAddress(dto.getAddress());
                                        customerSyncService.insertCustomerSync(customerSyncEntity);
                                        ///
                                        // 7. add account_customer
                                        UUID uuidAccountCustomer = UUID.randomUUID();
                                        String systemPassword = encodeBase64(dto.getSystemUsername());
                                        AccountCustomerEntity accountCustomerEntity = new AccountCustomerEntity();
                                        accountCustomerEntity.setId(uuidAccountCustomer.toString());
                                        accountCustomerEntity.setAvailable(true);
                                        accountCustomerEntity.setPassword(systemPassword);
                                        accountCustomerEntity.setRole("ROLE_USER");
                                        accountCustomerEntity.setUsername(dto.getSystemUsername());
                                        accountCustomerService.insert(accountCustomerEntity);

                                        String midConnectionId = UUID.randomUUID().toString();
                                        try {
                                            MerchantSyncEntity merchantSyncEntity = new MerchantSyncEntity();
                                            merchantSyncEntity.setId(customerSyncId);
                                            merchantSyncEntity.setName(dto.getMerchantName());
                                            merchantSyncEntity.setFullName(dto.getMerchantName());
                                            merchantSyncEntity.setVso("");
                                            merchantSyncEntity.setBusinessType("");
                                            merchantSyncEntity.setCareer("");
                                            merchantSyncEntity.setAddress(dto.getAddress());
                                            merchantSyncEntity.setNationalId("");
                                            merchantSyncEntity.setIsActive(false);
                                            merchantSyncEntity.setUserId("");
                                            merchantSyncEntity.setAccountCustomerId(uuidAccountCustomer.toString());
                                            merchantSyncEntity.setEmail("");
                                            merchantSyncEntity.setPhoneNo("");
                                            merchantSyncEntity.setPublishId("MER" + RandomCodeUtil.generateOTP(8));
                                            merchantSyncEntity.setRefId("");
                                            merchantSyncEntity.setIsMaster(false);
                                            merchantSyncEntity.setCertificate("");
                                            merchantSyncEntity.setWebhook("");
                                            merchantSyncEntity.setClientId("");
                                            merchantSyncEntity.setCode("");
                                            merchantSyncEntity.setInformation(!StringUtil.isNullOrEmpty(url) ?
                                                    StringUtil.getValueNullChecker(url): (ip + "/" + port));

                                            MerchantConnectionEntity merchantConnectionEntity = new MerchantConnectionEntity();
                                            merchantConnectionEntity.setId(midConnectionId);
                                            merchantConnectionEntity.setMid(customerSyncId);
                                            String finalUrl = StringUtil.isNullOrEmpty(url)
                                                    ? url + "/" + suffix : ip + "/" + port;
                                            merchantConnectionEntity.setUrlGetToken(finalUrl + "/api/token_generate");
                                            merchantConnectionEntity.setUrlCallback(finalUrl + "/bank/api/transaction-sync");
                                            merchantConnectionEntity.setUsername(dto.getCustomerUsername());
                                            merchantConnectionEntity.setPassword(dto.getCustomerPassword());
                                            merchantConnectionEntity.setToken("");
                                            merchantConnectionEntity.setType(1);
                                            merchantSyncService.insert(merchantSyncEntity);
                                            merchantConnectionService.insert(merchantConnectionEntity);
                                        } catch (Exception e) {
                                            logger.info("Merchant Sync insert ERROR: " + e.getMessage());
                                        }

                                        // 8. add account_customer_bank
                                        UUID accountCustomerBankUUID = UUID.randomUUID();
                                        AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                                        String bankId = "";
                                        if (checkExistedBankAccount != null
                                                && !checkExistedBankAccount.trim().isEmpty()) {
                                            bankId = checkExistedBankAccount;
                                        } else {
                                            bankId = uuidAccountBank.toString();
                                        }
                                        accountCustomerBankEntity.setId(accountCustomerBankUUID.toString());
                                        accountCustomerBankEntity.setAccountCustomerId(uuidAccountCustomer.toString());
                                        accountCustomerBankEntity.setBankAccount(dto.getBankAccount());
                                        accountCustomerBankEntity.setBankId(bankId);
                                        accountCustomerBankEntity.setCustomerSyncId(customerSyncId);
                                        accountCustomerBankService.insert(accountCustomerBankEntity);

                                        try {
                                            BankReceiveConnectionEntity bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                                            bankReceiveConnectionEntity.setId(accountCustomerBankUUID.toString());
                                            bankReceiveConnectionEntity.setBankId(bankId);
                                            bankReceiveConnectionEntity.setMidConnectId(midConnectionId);
                                            bankReceiveConnectionEntity.setData("{}");
                                            bankReceiveConnectionEntity.setTerminalBankId("");
                                            bankReceiveConnectionEntity.setMid(customerSyncId);
                                            bankReceiveConnectionEntity.setActive(false);

                                            MerchantBankReceiveEntity merchantBankReceiveEntity = new MerchantBankReceiveEntity();
                                            merchantBankReceiveEntity.setId(UUID.randomUUID().toString());
                                            merchantBankReceiveEntity.setBankId(bankId);
                                            merchantBankReceiveEntity.setMerchantId(customerSyncId);

                                            bankReceiveConnectionService.insert(bankReceiveConnectionEntity);
                                            merchantBankReceiveService.insert(merchantBankReceiveEntity);
                                        } catch (Exception e) {
                                            logger.info("Merchant Sync insert ERROR: " + e.getMessage());
                                        }

                                        result = new ResponseMessageDTO("SUCCESS", "");

                                    } else {
                                        // if not -> show msg
                                        logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                        result = new ResponseMessageDTO("FAILED", "E87");

                                    }
                                } else {
                                    // else -> show msg
                                    logger.info("insertNewCustomerSync: BANK IS NOT LINKED INTO SYSTEM BEFORE.");
                                    result = new ResponseMessageDTO("FAILED", "E84");
                                }
                            } else {

                                //////////
                                // prod:
                                // 5. check bankAccount existed and linked
                                String checkExistedBankAccount = "";
                                if (!StringUtil.isNullOrEmpty(dto.getBankCode())) {
                                    checkExistedBankAccount = accountBankReceiveService
                                            .checkExistedBankAccountByBankAccountAndBankCode(dto.getBankAccount(), dto.getBankCode());
                                } else {
                                    checkExistedBankAccount = accountBankReceiveService
                                            .checkExistedBankAccountByBankAccount(dto.getBankAccount());
                                }

                                if (checkExistedBankAccount != null && !checkExistedBankAccount.trim().isEmpty()) {
                                    // if existed and linked -> pass
                                    //
                                    // 6. update is_sync = true into bank account
                                    accountBankReceiveService.updateBankAccountSync(true, checkExistedBankAccount);
                                    // 7. add customer_sync (with status)
                                    String url = "";
                                    String ip = "";
                                    String port = "";
                                    String suffix = "";
                                    if (dto.getSuffixUrl() != null && !dto.getSuffixUrl().trim().isEmpty()) {
                                        if (dto.getSuffixUrl().trim().endsWith("/")) {
                                            suffix = dto.getSuffixUrl().substring(0,
                                                    dto.getSuffixUrl().trim().length() - 1);
                                        } else {
                                            suffix = dto.getSuffixUrl().trim();
                                        }
                                    }
                                    if (dto.getUrl() != null && !dto.getUrl().trim().isEmpty()) {
                                        // if valid -> pass
                                        if (dto.getUrl().trim().endsWith("/")) {
                                            url = dto.getUrl().substring(0, dto.getUrl().trim().length() - 1);
                                        } else {
                                            url = dto.getUrl().trim();
                                        }
                                    } else if (dto.getIp() != null && dto.getPort() != null
                                            && !dto.getIp().trim().isEmpty() && !dto.getPort().trim().isEmpty()) {
                                        // if valid -> pass
                                        ip = dto.getIp().trim();
                                        port = dto.getPort().trim();
                                    } else {
                                        // if not -> show msg
                                        logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                        result = new ResponseMessageDTO("FAILED", "E87");

                                    }
                                    if (!url.trim().isEmpty() || (!ip.trim().isEmpty() && !port.trim().isEmpty())) {
                                        // do insert customer sync
                                        boolean active = false;
                                        if (dto.getCustomerSyncActive() == 1) {
                                            active = true;
                                        }
                                        CustomerSyncEntity customerSyncEntity = new CustomerSyncEntity();
                                        customerSyncEntity.setId(customerSyncId);
                                        customerSyncEntity.setActive(active);
                                        customerSyncEntity.setInformation(url);
                                        customerSyncEntity.setIpAddress(ip);
                                        customerSyncEntity.setPassword(dto.getCustomerPassword());
                                        customerSyncEntity.setPort(port);
                                        customerSyncEntity.setSuffixUrl(suffix);
                                        customerSyncEntity.setToken("");
                                        customerSyncEntity.setUserId("");
                                        customerSyncEntity.setUsername(dto.getCustomerUsername());
                                        customerSyncEntity.setMerchant(dto.getMerchantName());
                                        customerSyncEntity.setAddress(dto.getAddress());
                                        customerSyncService.insertCustomerSync(customerSyncEntity);
                                        ///
                                        // 7. add account_customer
                                        UUID uuidAccountCustomer = UUID.randomUUID();
                                        String systemPassword = encodeBase64(dto.getSystemUsername());
                                        AccountCustomerEntity accountCustomerEntity = new AccountCustomerEntity();
                                        accountCustomerEntity.setId(uuidAccountCustomer.toString());
                                        accountCustomerEntity.setAvailable(true);
                                        accountCustomerEntity.setPassword(systemPassword);
                                        accountCustomerEntity.setRole("ROLE_USER");
                                        accountCustomerEntity.setUsername(dto.getSystemUsername());
                                        accountCustomerService.insert(accountCustomerEntity);

                                        //insert v2:
                                        String midConnectionId = UUID.randomUUID().toString();
                                        try {
                                            MerchantSyncEntity merchantSyncEntity = new MerchantSyncEntity();
                                            merchantSyncEntity.setId(customerSyncId);
                                            merchantSyncEntity.setName(dto.getMerchantName());
                                            merchantSyncEntity.setFullName(dto.getMerchantName());
                                            merchantSyncEntity.setVso("");
                                            merchantSyncEntity.setBusinessType("");
                                            merchantSyncEntity.setCareer("");
                                            merchantSyncEntity.setAddress(dto.getAddress());
                                            merchantSyncEntity.setNationalId("");
                                            merchantSyncEntity.setIsActive(false);
                                            merchantSyncEntity.setUserId("");
                                            merchantSyncEntity.setAccountCustomerId(uuidAccountCustomer.toString());
                                            merchantSyncEntity.setEmail("");
                                            merchantSyncEntity.setPhoneNo("");
                                            merchantSyncEntity.setPublishId("MER" + RandomCodeUtil.generateOTP(8));
                                            merchantSyncEntity.setRefId("");
                                            merchantSyncEntity.setIsMaster(false);
                                            merchantSyncEntity.setCertificate("");
                                            merchantSyncEntity.setWebhook("");
                                            merchantSyncEntity.setClientId("");
                                            merchantSyncEntity.setCode("");
                                            merchantSyncEntity.setInformation(!StringUtil.isNullOrEmpty(url) ?
                                                    StringUtil.getValueNullChecker(url): (ip + "/" + port));

                                            MerchantConnectionEntity merchantConnectionEntity = new MerchantConnectionEntity();
                                            merchantConnectionEntity.setId(midConnectionId);
                                            merchantConnectionEntity.setMid(customerSyncId);
                                            String finalUrl = StringUtil.isNullOrEmpty(url)
                                                    ? url + "/" + suffix : ip + "/" + port;
                                            merchantConnectionEntity.setUrlGetToken(finalUrl + "/api/token_generate");
                                            merchantConnectionEntity.setUrlCallback(finalUrl + "/bank/api/transaction-sync");
                                            merchantConnectionEntity.setUsername(dto.getCustomerUsername());
                                            merchantConnectionEntity.setPassword(dto.getCustomerPassword());
                                            merchantConnectionEntity.setToken("");
                                            merchantConnectionEntity.setType(1);
                                            merchantSyncService.insert(merchantSyncEntity);
                                            merchantConnectionService.insert(merchantConnectionEntity);
                                        } catch (Exception e) {
                                            logger.info("Merchant Sync insert ERROR: " + e.getMessage());
                                        }

                                        // 8. add account_customer
                                        UUID accountCustomerBankUUID = UUID.randomUUID();
                                        AccountCustomerBankEntity accountCustomerBankEntity = new AccountCustomerBankEntity();
                                        // 9. add account_customer_bank
                                        accountCustomerBankEntity.setId(accountCustomerBankUUID.toString());
                                        accountCustomerBankEntity.setAccountCustomerId(uuidAccountCustomer.toString());
                                        accountCustomerBankEntity.setBankAccount(dto.getBankAccount());
                                        accountCustomerBankEntity.setBankId(checkExistedBankAccount);
                                        accountCustomerBankEntity.setCustomerSyncId(customerSyncId);
                                        accountCustomerBankService.insert(accountCustomerBankEntity);


                                        try {
                                            BankReceiveConnectionEntity bankReceiveConnectionEntity = new BankReceiveConnectionEntity();
                                            bankReceiveConnectionEntity.setId(accountCustomerBankUUID.toString());
                                            bankReceiveConnectionEntity.setBankId(checkExistedBankAccount);
                                            bankReceiveConnectionEntity.setMidConnectId(midConnectionId);
                                            bankReceiveConnectionEntity.setData("{}");
                                            bankReceiveConnectionEntity.setTerminalBankId("");
                                            bankReceiveConnectionEntity.setMid(customerSyncId);
                                            bankReceiveConnectionEntity.setActive(false);

                                            MerchantBankReceiveEntity merchantBankReceiveEntity = new MerchantBankReceiveEntity();
                                            merchantBankReceiveEntity.setId(UUID.randomUUID().toString());
                                            merchantBankReceiveEntity.setBankId(checkExistedBankAccount);
                                            merchantBankReceiveEntity.setMerchantId(customerSyncId);

                                            bankReceiveConnectionService.insert(bankReceiveConnectionEntity);
                                            merchantBankReceiveService.insert(merchantBankReceiveEntity);
                                        } catch (Exception e) {
                                            logger.info("Merchant Sync insert ERROR: " + e.getMessage());
                                        }

                                        result = new ResponseMessageDTO("SUCCESS", "");
                                    } else {
                                        // if not -> show msg
                                        logger.error("insertNewCustomerSync: INVALID DOMAIN/IP+PORT");
                                        result = new ResponseMessageDTO("FAILED", "E87");
                                    }

                                } else {
                                    // else -> show msg
                                    logger.info("insertNewCustomerSync: BANK IS NOT LINKED INTO SYSTEM BEFORE.");
                                    result = new ResponseMessageDTO("FAILED", "E84");

                                }
                            }
                        }
                    }
                } else {
                    logger.error("insertNewCustomerSync: INVALID REQUEST BODY");
                    result = new ResponseMessageDTO("FAILED", "E46");

                }
                // UAT ENVIRONMENT
            } else if (environment == 1) {
                // insert test environment
                token = removeBearer(token);
                String url = EnvironmentUtil.getUrlVietqrVnUat() + "/admin/customer-sync";
                Map<String, Object> data = new HashMap<>();
                data.put("merchantName", dto.getMerchantName());
                data.put("url", dto.getUrl());
                data.put("ip", dto.getIp());
                data.put("port", dto.getPort());
                data.put("suffixUrl", dto.getSuffixUrl());
                data.put("address", dto.getAddress());
                data.put("bankAccount", dto.getBankAccount());
                data.put("bankCode", dto.getBankCode());
                data.put("userBankName", dto.getUserBankName());
                data.put("customerUsername", dto.getCustomerUsername());
                data.put("customerPassword", dto.getCustomerPassword());
                data.put("systemUsername", dto.getSystemUsername());
                data.put("customerSyncActive", dto.getCustomerSyncActive());
                WebClient webClient = WebClient.builder()
                        .baseUrl(url)
                        .build();
                Mono<ClientResponse> responseMono = webClient.post()
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(data))
                        .exchange();
                ClientResponse response = responseMono.block();
                if (response.statusCode().is2xxSuccessful()) {
                    result = new ResponseMessageDTO("SUCCESS", "");
                } else {
                    String json = response.bodyToMono(String.class).block();
                    logger.info("insertNewCustomerSync env1: Response: " + json);
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(json);
                    if (rootNode.get("message") != null) {
                        String message = rootNode.get("message").asText();
                        if (message != null && !message.trim().isEmpty()) {
                            logger.info("insertNewCustomerSync: RESPONSE ERROR");
                            result = new ResponseMessageDTO("FAILED", message);
                        } else {
                            logger.error("insertNewCustomerSync: INVALID RESPONSE");
                            result = new ResponseMessageDTO("FAILED", "E05");
                        }
                    } else {
                        logger.error("insertNewCustomerSync: INVALID RESPONSE");
                        result = new ResponseMessageDTO("FAILED", "E05");
                    }
                }
            } else {
                logger.error("insertNewCustomerSync: INVALID ENVIRONMENT");
                result = new ResponseMessageDTO("FAILED", "E107");
            }

        } catch (Exception e) {
            result = new ResponseMessageDTO("FAILED", "E05");
        }
        return result;
    }

    public String removeBearer(String input) {
        String prefix = "Bearer ";
        if (input.startsWith(prefix)) {
            return input.substring(prefix.length());
        }
        return input;
    }

    // add customer sync vietqr side (for customer)
    // ONLY APPLE TEST ENV
    // when add, status active = 0 (inactive). Admin has to enable
    // save customerSyncId test into customerSyncId golive
    @PostMapping("customer-sync/uat/request")
    public ResponseEntity<ResponseMessageDTO> requestInsertCustomerSync(
            @RequestHeader("Authorization") String token,
            @RequestBody CustomerSyncInsertDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            UUID customerSyncId = UUID.randomUUID();
            result = insertCustomerSync(customerSyncId.toString(), dto, token, 1);
            if (result != null && result.getStatus().equals("SUCCESS")) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("requestInsertCustomerSync: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //
    // get connection information merchant

    // check existed merchant name
    @PostMapping("customer-sync/account/check-merchant")
    public ResponseEntity<ResponseMessageDTO> checkValidMerchantName(@RequestBody AccountCustomerInputDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getMerchantName() != null && !dto.getMerchantName().trim().isEmpty()) {
                String checkExistedMerchantName = customerSyncService.checkExistedMerchantName(dto.getMerchantName());
                if (checkExistedMerchantName == null || checkExistedMerchantName.trim().isEmpty()) {
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("generateAccountCustomer: INVALID REQUEST BODY");
                    result = new ResponseMessageDTO("FAILED", "E105");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("generateAccountCustomer: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("generateAccountCustomer: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get username-password from merchant name
    @PostMapping("customer-sync/account/generate")
    public ResponseEntity<AccountCustomerGenerateDTO> generateAccountCustomer(
            @RequestBody AccountCustomerInputDTO dto) {
        AccountCustomerGenerateDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null && dto.getMerchantName() != null && !dto.getMerchantName().trim().isEmpty()) {
                // get count account customer
                Integer customerCounting = customerSyncService.getCountingCustomerSync();
                // //System.out.println("customerCounting: " + customerCounting);
                // generate username - password
                String prefix = "customer";
                String merchantName = dto.getMerchantName().trim().toLowerCase();
                String suffix = "user";
                // Lấy thời gian hiện tại
                LocalDate currentDate = LocalDate.now();
                // Lấy giá trị hai chữ số cuối của năm
                int lastTwoDigitsOfYear = currentDate.getYear() % 100;
                String username = prefix + "-" + merchantName + "-" + suffix + lastTwoDigitsOfYear
                        + (customerCounting + 1);
                //
                String password = encodeBase64(username.trim());
                result = new AccountCustomerGenerateDTO(username, password);
                httpStatus = HttpStatus.OK;
            } else {
                logger.error("generateAccountCustomer: INVALID REQUEST BODY");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("generateAccountCustomer: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    //

    // get list merchant-bankaccounts to mapping service
    @GetMapping("admin/customer-sync/service-mapping")
    public ResponseEntity<List<MerchantServiceDTO>> getMerchantsToMappingService() {
        List<MerchantServiceDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<MerchantServiceItemDTO> merchants = customerSyncService.getMerchantsMappingService();
            if (merchants != null && !merchants.isEmpty()) {
                for (MerchantServiceItemDTO merchant : merchants) {
                    MerchantServiceDTO merchantService = new MerchantServiceDTO();
                    merchantService.setCustomerSyncId(merchant.getCustomerSyncId());
                    merchantService.setMerchant(merchant.getMerchant());
                    if (merchant.getCustomerSyncId() != null) {
                        List<AccountBankReceiveServiceItemDTO> bankAccounts = accountCustomerBankService
                                .getBankAccountsByMerchantId(merchant.getCustomerSyncId());
                        if (bankAccounts != null && !bankAccounts.isEmpty()) {
                            merchantService.setBankAccounts(bankAccounts);
                        }
                    }
                    if (merchantService.getBankAccounts() != null && !merchantService.getBankAccounts().isEmpty()) {
                        result.add(merchantService);
                    }
                }
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getMerchantsToMappingService: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get customerSyncByAccountId
    @GetMapping("customer-sync/information")
    public ResponseEntity<Object> getCustomerSyncInformation(
            @RequestParam(value = "accountId") String accountId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            if (accountId != null && !accountId.trim().isEmpty()) {
                List<CustomerSyncMappingEntity> entities = customerSyncMappingService
                        .getCustomerSyncMappingByUserId(accountId);
                // List<CustomerSyncEntity> entities =
                // customerSyncService.getCustomerSyncByAccountId(accountId);
                if (entities != null && !entities.isEmpty()) {
                    List<MerchantInformationCheckDTO> list = new ArrayList<>();
                    for (CustomerSyncMappingEntity entity : entities) {
                        MerchantInformationCheckDTO dto = new MerchantInformationCheckDTO();
                        dto.setId(entity.getId());
                        dto.setCustomerSyncId(entity.getCusSyncId());
                        dto.setCustomerSyncTestId(entity.getCusSyncTestId());
                        dto.setUserId(entity.getUserId());
                        // dto.setCustomerSyncId(entity.getId());
                        // dto.setMerchantName(entity.getMerchant());
                        // dto.setIp(entity.getIpAddress());
                        // dto.setPort(entity.getPort());
                        // dto.setSuffix(entity.getSuffixUrl());
                        // dto.setIsActive(entity.isActive());
                        // dto.setUrl(entity.getInformation());
                        // dto.setIsMasterMerchant(entity.getMaster());
                        // dto.setAccountId(entity.getAccountId());
                        // dto.setRefId(entity.getRefId());
                        // String platform = "";
                        // if (entity.getUserId() != null && !entity.getUserId().trim().isEmpty()) {
                        // platform = "Ecomerce";
                        // } else {
                        // platform = "API Service";
                        // }
                        // dto.setPlatform(platform);
                        list.add(dto);
                    }
                    result = list;
                    httpStatus = HttpStatus.OK;
                } else {
                    httpStatus = HttpStatus.OK;
                    result = new ResponseMessageDTO("CHECK", "C07");
                }
            } else {
                logger.error("getCustomerSyncInformation: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getCustomerSyncInformation: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // get customer sync (2 env.)
    // @GetMapping("customer-sync/information")
    // public ResponseEntity<List<Object>> getCustomerSyncInformation(
    // @RequestParam(value = "customerSyncId") String customerSyncId,
    // @RequestParam(value = "customerSyncTestId") String customerSyncTestId) {
    // List<Object> result = new ArrayList<>();
    // HttpStatus httpStatus = null;
    // try {
    // //
    // } catch (Exception e) {
    // logger.error("getCustomerSyncInformation: ERROR: " + e.toString());
    // httpStatus = HttpStatus.BAD_REQUEST;
    // }
    // return new ResponseEntity<>(result, httpStatus);
    // }

    // update customer sync info
    @DeleteMapping("customer-sync/{id}")
    public ResponseEntity<Object> deleteCustomerSync(@PathVariable String id) {
        Object result = null;
        HttpStatus httpStatus = null;

        try {
            customerSyncService.deleteCustomerSync(id);
            httpStatus = HttpStatus.OK;
            result = new ResponseMessageDTO("SUCCESS", "");
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            result = new ResponseMessageDTO("FAILED", "E05");
        }

        return new ResponseEntity<>(result, httpStatus);
    }
}
