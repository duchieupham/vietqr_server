package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.entity.bidv.CustomerVaEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.AccountBankReceiveDetailDTO.TransactionBankListDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AccountBankReceiveController {
    private static final Logger logger = Logger.getLogger(AccountBankReceiveController.class);

    @Autowired
    AccountBankReceiveService accountBankReceiveService;

    @Autowired
    TerminalAddressService terminalAddressService;

    @Autowired
    TerminalBankService terminalBankService;

    @Autowired
    TerminalService terminalService;

    @Autowired
    BankTypeService bankTypeService;

    @Autowired
    TransReceiveTempService transReceiveTempService;

    @Autowired
    AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    AccountBankReceivePersonalService accountBankReceivePersonalService;

    @Autowired
    BankReceiveBranchService bankReceiveBranchService;

    @Autowired
    BankReceiveActiveHistoryService bankReceiveActiveHistoryService;

    @Autowired
    CaiBankService caiBankService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    ContactService contactService;

    @Autowired
    CustomerVaService customerVaService;

    @Autowired
    MerchantSyncService merchantSyncService;

    @Autowired
    InvoiceService invoiceService;

    @PostMapping("admin/account/update-flow-2")
    public ResponseEntity<Object> updateFlow2(
            @Valid @RequestBody AccountUpdateMMSActiveDTO dto
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();
        try {
            //-Check Tài khoản này đã đăng ký luồng 2 trước đó chưa,
            AccountBankReceiveEntity checkAccount =
                    accountBankReceiveService.checkExistedBankAccountAuthenticated(dto.getBankAccount(), dto.getBankCode());
            //check bank account is_authenticated
            AccountBankReceiveEntity bankReceiveEntity = accountBankReceiveService
                    .checkExistedBankAccountAuthenticated(dto.getBankAccount(), dto.getBankCode());
            //-check tồn tại record trong terminal_bank, terminal_address.
            TerminalAddressEntity terminalAddress =
                    terminalAddressService.getTerminalAddressByBankIdAndTerminalBankId(dto.getBankId());
            //-Nếu tồn tại, thì chỉ cần đổi biến mms_active = true.
            if (Objects.nonNull(checkAccount)) {
                if (Objects.nonNull(bankReceiveEntity)) {
                    if (Objects.isNull(terminalAddress)) {
                        //-Nếu chưa tồn tại, gọi API sync TID của MBBank để đăng ký mới.
                        // gọi api sync TID nó sẽ trả về 1 đoãn json
                        TerminalRequestDTO terminals = new TerminalRequestDTO();
                        TerminalBankEntity terminalBank =
                                terminalBankService.getTerminalBankByBankAccount(dto.getBankAccount());
                        // check bankAccountNumber
                        String bankAccountNumberEncrypted = BankEncryptUtil.encrypt(dto.getBankAccount());
                        // check bankAccountName
                        String bankAccountName = dto.getBankAccountName().toUpperCase();
                        // check terminalName - BLC = merchant (từ customerSyncID)
                        String customerSynId = customerSyncService.getCustomerSyncByBankId(dto.getBankId());

                        // lấy terminalName generated combine with terminal_name trong terminal_bank
                        String merchantName = customerSyncService.getMerchantNameById(customerSynId);
                        String terminalNameGenerate = "BLC" + merchantName;

                        // lấy terminalAddress theo terminalName
                        String terminalAddressCheck = customerSyncService.getCustomerAddressById(customerSynId);

                        List<String> getTerminalNames = terminalBankService.getListTerminalNames();
                        int i = 0;
                        for (String terminalName : getTerminalNames) {
                            if (terminalNameGenerate.equals(terminalName)) {
                                i++;
                                terminalNameGenerate = terminalNameGenerate + i;
                            } else {
                                i++;
                                if (i == 1) {
                                    terminalNameGenerate = terminalNameGenerate;
                                    terminalAddressCheck = terminalAddressCheck;
                                    break;
                                }
                                terminalNameGenerate = terminalNameGenerate + i;
                                terminalAddressCheck = terminalAddressCheck + i;
                                break;
                            }
                        }
                        // get token MB
                        TokenMBResponseDTO tokenMBResponseDTO = getToken();

                        // Sync TID MB Bank
                        TerminalResponseSyncTidDTO terminalRequestDTO = syncTerminals(tokenMBResponseDTO.getAccess_token());
                        String terminalIdBySyncTID = terminalRequestDTO.getData().getResult().get(0).getTerminalId();

                        // get TID MB Bank
                        TerminalResponseFlow2 terminalResponseFlow2 = getTerminals(tokenMBResponseDTO.getAccess_token());
                        String getTerminalID = terminalResponseFlow2.getData().getTerminals().get(0).getTerminalId();
                        String getBankAccountNumberNew = terminalResponseFlow2.getData().getTerminals().get(0).getBankAccountNumber();

                        // insert vào 2 bảng terminal_bank, terminal_address
                        TerminalBankEntity terminalBankEntity = new TerminalBankEntity();
                        UUID idTerminalBank = UUID.randomUUID();
                        terminalBankEntity.setId(idTerminalBank.toString());
                        terminalBankEntity.setBankAccountName(dto.getBankAccountName());
                        terminalBankEntity.setBankAccountNumber(getBankAccountNumberNew); // này lấy từ api get TID từ MB
                        terminalBankEntity.setBankAccountRawNumber(dto.getBankAccount());
                        terminalBankEntity.setBankCode("311");
                        terminalBankEntity.setBankCurrencyCode("1");
                        terminalBankEntity.setBankCurrencyName("VND");
                        terminalBankEntity.setBankName("311 - TMCP Quan Doi");
                        terminalBankEntity.setBranchName("NH TMCP QUAN DOI CN SGD 3");
                        terminalBankEntity.setDistrictCode("6");
                        terminalBankEntity.setDistrictName("Quận Đống Đa");
                        terminalBankEntity.setFee(0);
                        terminalBankEntity.setMccCode("1024");
                        terminalBankEntity.setMccName("Dịch vụ tài chính");
                        terminalBankEntity.setMerchantId(customerSynId);
                        terminalBankEntity.setProvinceCode("1");
                        terminalBankEntity.setProvinceName("Hà Nội update");
                        terminalBankEntity.setStatus(1);
                        terminalBankEntity.setTerminalAddress(terminalAddressCheck);
                        terminalBankEntity.setTerminalId(getTerminalID); // terminalID lấy từ API syncTID trả về response
                        terminalBankEntity.setTerminalName(terminalNameGenerate);
                        terminalBankEntity.setWardsCode("178");
                        terminalBankEntity.setWardsName("Phường Cát Linh");
                        terminalBankService.insertTerminalBank(terminalBankEntity);

                        TerminalAddressEntity terminalAddressEntity = new TerminalAddressEntity();
                        UUID idTerminalAddress = UUID.randomUUID();
                        terminalAddressEntity.setId(idTerminalAddress.toString());
                        terminalAddressEntity.setBankAccount(dto.getBankAccount());
                        terminalAddressEntity.setBankId(dto.getBankId());
                        terminalAddressEntity.setTerminalBankId(idTerminalBank.toString()); // terminalID lấy từ API syncTID trả về response
                        terminalAddressEntity.setCustomerSyncId(customerSynId);
                        terminalAddressService.insert(terminalAddressEntity);

                        // check syncTID is SUCESS to update MMS Active
                        if (terminalIdBySyncTID.equals(getTerminalID)) {
                            // đổi mms_active = true.
                            if (!checkAccount.isSync()) {
                                checkAccount.setSync(true);
                            }
                            accountBankReceiveService.updateMMSActive(true, true, dto.getBankId());
                        }
                        // cái này để test
//                        String syncTIDTest = "Checked successfully";
//                        if (syncTIDTest.equals("Checked successfully")) {
//                            // Nếu tồn tại, thì chỉ cần đổi mms_active = true.
//                            if (!checkAccount.isSync()) {
//                                checkAccount.setSync(true);
//                            }
//                            accountBankReceiveService.updateMMSActive(true, true, dto.getBankId());
//                        }

                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        // nếu tồn tại thì cho update luồng 2
                        if (!checkAccount.isSync()) {
                            checkAccount.setSync(true);
                        }
                        accountBankReceiveService.updateMMSActive(true, true, dto.getBankId());
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E171");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E172");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Update flow account: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED: " + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("admin/account/update-flow-1")
    public ResponseEntity<Object> updateFlow1(
            @Valid @RequestBody AccountUpdateMMSActiveDTO dto
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();
        try {
            //-Check Tài khoản này đã đăng ký luồng 2 trước đó chưa,
            AccountBankReceiveEntity checkAccount =
                    accountBankReceiveService.checkExistedBankAccountAuthenticated(dto.getBankAccount(), dto.getBankCode());
            //check bank account is_authenticated
            AccountBankReceiveEntity bankReceiveEntity = accountBankReceiveService
                    .checkExistedBankAccountAuthenticated(dto.getBankAccount(), dto.getBankCode());
            //-check tồn tại record trong terminal_bank, terminal_address.
            TerminalAddressEntity terminalAddress =
                    terminalAddressService.getTerminalAddressByBankIdAndTerminalBankId(dto.getBankId());
            //-Nếu tồn tại, thì chỉ cần đổi biến mms_active = true.
            if (Objects.nonNull(checkAccount)) {
                if (Objects.nonNull(bankReceiveEntity)) {
                    if (Objects.nonNull(terminalAddress)) {
//                        //-Nếu chưa tồn tại, gọi API sync TID của MBBank để đăng ký mới.
//                        // để LINH CONFIRM LÀM
//                        // gọi api sync TID nó sẽ trả về 1 đoãn json
//                        TerminalRequestDTO terminals = new TerminalRequestDTO();
//                        TerminalBankEntity terminalBank =
//                                terminalBankService.getTerminalBankByBankAccount(dto.getBankAccount());
//                        // check bankAccountNumber
//                        String bankAccountNumberEncrypted = BankEncryptUtil.encrypt(dto.getBankAccount());
//                        // check bankAccountName
//                        String bankAccountName = dto.getBankAccountName().toUpperCase();
//                        // check terminalName - BLC = merchant (từ customerSyncID)
//                        String customerSynId = customerSyncService.getCustomerSyncByBankId(dto.getBankId());
//
//                        // lấy terminalName generated combine with terminal_name trong terminal_bank
//                        String merchantName = customerSyncService.getMerchantNameById(customerSynId);
//                        String terminalNameGenerate = "BLC" + merchantName;
//
//                        // lấy terminalAddress theo terminalName
//                        String terminalAddressCheck = customerSyncService.getCustomerAddressById(customerSynId);
//
//                        List<String> getTerminalNames = terminalBankService.getListTerminalNames();
//                        int i = 0;
//                        for (String terminalName : getTerminalNames) {
//                            if (terminalNameGenerate.equals(terminalName)) {
//                                i++;
//                                terminalNameGenerate = terminalNameGenerate + i;
//                            } else {
//                                i++;
//                                if (i == 1) {
//                                    terminalNameGenerate = terminalNameGenerate;
//                                    terminalAddressCheck = terminalAddressCheck;
//                                    break;
//                                }
//                                terminalNameGenerate = terminalNameGenerate + i;
//                                terminalAddressCheck = terminalAddressCheck + i;
//                                break;
//                            }
//                        }
//                        // get token MB
//                        TokenMBResponseDTO tokenMBResponseDTO = getToken();
//
//                        // Sync TID MB Bank
//                        TerminalResponseSyncTidDTO terminalRequestDTO = syncTerminals(tokenMBResponseDTO.getAccess_token());
//                        String terminalIdBySyncTID = terminalRequestDTO.getData().getResult().get(0).getTerminalId();
//
//                        // get TID MB Bank
//                        TerminalResponseFlow2 terminalResponseFlow2 = getTerminals(tokenMBResponseDTO.getAccess_token());
//                        String getTerminalID = terminalResponseFlow2.getData().getTerminals().get(0).getTerminalId();
//                        String getBankAccountNumberNew = terminalResponseFlow2.getData().getTerminals().get(0).getBankAccountNumber();
//
//                        // insert vào 2 bảng terminal_bank, terminal_address
//                        TerminalBankEntity terminalBankEntity = new TerminalBankEntity();
//                        UUID idTerminalBank = UUID.randomUUID();
//                        terminalBankEntity.setId(idTerminalBank.toString());
//                        terminalBankEntity.setBankAccountName(dto.getBankAccountName());
//                        terminalBankEntity.setBankAccountNumber(getBankAccountNumberNew); // này lấy từ api get TID từ MB
//                        terminalBankEntity.setBankAccountRawNumber(dto.getBankAccount());
//                        terminalBankEntity.setBankCode("311");
//                        terminalBankEntity.setBankCurrencyCode("1");
//                        terminalBankEntity.setBankCurrencyName("VND");
//                        terminalBankEntity.setBankName("311 - TMCP Quan Doi");
//                        terminalBankEntity.setBranchName("NH TMCP QUAN DOI CN SGD 3");
//                        terminalBankEntity.setDistrictCode("6");
//                        terminalBankEntity.setDistrictName("Quận Đống Đa");
//                        terminalBankEntity.setFee(0);
//                        terminalBankEntity.setMccCode("1024");
//                        terminalBankEntity.setMccName("Dịch vụ tài chính");
//                        terminalBankEntity.setMerchantId(customerSynId);
//                        terminalBankEntity.setProvinceCode("1");
//                        terminalBankEntity.setProvinceName("Hà Nội update");
//                        terminalBankEntity.setStatus(1);
//                        terminalBankEntity.setTerminalAddress(terminalAddressCheck);
//                        terminalBankEntity.setTerminalId(getTerminalID); // terminalID lấy từ API syncTID trả về response
//                        terminalBankEntity.setTerminalName(terminalNameGenerate);
//                        terminalBankEntity.setWardsCode("178");
//                        terminalBankEntity.setWardsName("Phường Cát Linh");
//                        terminalBankService.insertTerminalBank(terminalBankEntity);
//
//                        TerminalAddressEntity terminalAddressEntity = new TerminalAddressEntity();
//                        UUID idTerminalAddress = UUID.randomUUID();
//                        terminalAddressEntity.setId(idTerminalAddress.toString());
//                        terminalAddressEntity.setBankAccount(dto.getBankAccount());
//                        terminalAddressEntity.setBankId(dto.getBankId());
//                        terminalAddressEntity.setTerminalBankId(idTerminalBank.toString()); // terminalID lấy từ API syncTID trả về response
//                        terminalAddressEntity.setCustomerSyncId(customerSynId);
//                        terminalAddressService.insert(terminalAddressEntity);

//                        // cái này để test
//                        String syncTIDTest = "Checked successfully";
//                        if (syncTIDTest.equals("Checked successfully")) {
//                            // Nếu tồn tại, thì chỉ cần đổi mms_active = true.
//                            if (!checkAccount.isSync()) {
//                                checkAccount.setSync(true);
//                            }
//                            accountBankReceiveService.updateMMSActive(true, false, dto.getBankId());
//                        }

                        // Chỉ cần đổi mms_active = false.
                        if (!checkAccount.isSync()) {
                            accountBankReceiveService.updateMMSActive(false, false, dto.getBankId());
                        }
                        accountBankReceiveService.updateMMSActive(true, false, dto.getBankId());

                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        result = new ResponseMessageDTO("FAILED", "E170");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    result = new ResponseMessageDTO("FAILED", "E171");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                result = new ResponseMessageDTO("FAILED", "E172");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Update flow account: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED: " + e.getMessage(), "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("account/admin-update")
    public ResponseEntity<ResponseMessageDTO> updateBankAccountByAdmin(@RequestBody BankAccountAdminUpdateDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String bankId = "";
            if (StringUtil.isNullOrEmpty(dto.getBankId())) {
                bankId = accountBankReceiveService.getBankIdByBankAccount(dto.getBankAccount(), dto.getBankShortName());
            } else {
                bankId = dto.getBankId();
            }
            BankAccountAdminDTO bankAccountAdminDTO = accountBankReceiveService.getUserIdAndMidByBankId(bankId);
            if (Objects.nonNull(bankAccountAdminDTO)) {
                if (!StringUtil.isNullOrEmpty(dto.getEmail())) {
                    accountLoginService.updateEmailByUserId(dto.getEmail(), bankAccountAdminDTO.getUserId());
                }
                if (!StringUtil.isNullOrEmpty(dto.getVso())) {
                    accountBankReceiveService.updateVsoBankAccount(dto.getVso(), bankId);
                }
                if (!StringUtil.isNullOrEmpty(dto.getMidName()) && !StringUtil.isNullOrEmpty(bankAccountAdminDTO.getMid())) {
                    merchantSyncService.updateMerchantName(dto.getMidName(), bankAccountAdminDTO.getMid());
                }

                // Update invoice
                InvoiceUpdateVsoDTO invoicesByBankId = invoiceService.getInvoicesByBankId(bankId);
                if (invoicesByBankId != null) {
                    MerchantBankMapperDTO merchantBankMapperDTO = getMerchantBankMapperDTO(invoicesByBankId.getData());
                    merchantBankMapperDTO.setVso(StringUtil.isNullOrEmpty(dto.getVso()) ? merchantBankMapperDTO.getVso() : dto.getVso());
                    merchantBankMapperDTO.setMerchantName(StringUtil.isNullOrEmpty(dto.getMidName()) ? merchantBankMapperDTO.getMerchantName() : dto.getMidName());
                    merchantBankMapperDTO.setEmail(StringUtil.isNullOrEmpty(dto.getEmail()) ? merchantBankMapperDTO.getEmail() : dto.getEmail());
                    ObjectMapper mapper = new ObjectMapper();
                    String data = mapper.writeValueAsString(merchantBankMapperDTO);
                    invoiceService.updateDataInvoiceByBankId(data, bankId);
                }
                httpStatus = HttpStatus.OK;
                result = new ResponseMessageDTO("SUCCESS", "");
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
                result = new ResponseMessageDTO("FAILED", "");
            }

        } catch (Exception e) {
            logger.error("AccountBankReceiveController: ERROR: updateBankAccountByAdmin: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account/admin-list-bank-account")
    public ResponseEntity<Object> getListBankAccounts(
            @RequestParam int type,
            @RequestParam String value,
            @RequestParam int page,
            @RequestParam int size) {
        Object result = null;
        HttpStatus httpStatus = null;
        PageResDTO pageResDTO = new PageResDTO();

        try {
            int totalElement = 0;
            int offset = (page - 1) * size;
            List<ListAccountBankDTO> data = new ArrayList<>();
            List<IListAccountBankDTO> infos = new ArrayList<>();
            infos = accountBankReceiveService.getListBankAccounts(value, offset, size);
            totalElement = accountBankReceiveService.countListBankAccounts();

            data = infos.stream().map(item -> {
                ListAccountBankDTO dto = new ListAccountBankDTO();
                dto.setId(item.getBankId());
                dto.setBankAccount(item.getBankAccount());
                dto.setBankAccountName(item.getBankAccountName());
                dto.setBankTypeId(item.getBankTypeId());
                dto.setNationalId(item.getNationalId());
                dto.setPhoneAuthenticated(item.getPhoneAuthenticated());
                dto.setUserId(item.getUserId());
                dto.setStatus(item.getStatus() != true ? item.getStatus() : false);
                dto.isRpaSync(item.getIsRpaSync());
                dto.setWpSync(item.getIsWpSync());
                dto.setMmsActive(item.getMmsActive());
                dto.isSync(item.getIsSync());
                dto.isAuthenticated(item.getIsAuthenticated());
                dto.setType(item.getType() != 0 ? item.getType() : 1);
                return dto;
            }).collect(Collectors.toList());

            PageDTO pageDTO = new PageDTO();
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageDTO.setTotalElement(totalElement);
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageResDTO.setMetadata(pageDTO);
            pageResDTO.setData(data);

            httpStatus = HttpStatus.OK;
            result = pageResDTO;
        } catch (Exception e) {
            logger.error("AccountBankReceiveController: ERROR: getBankAccountList: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // @GetMapping("account-bank/check/{bankAccount}/{bankTypeId}/{userId}")
    @GetMapping("account-bank/check/{bankAccount}/{bankTypeId}")
    public ResponseEntity<ResponseMessageDTO> checkExistedBankAccount(
            @PathVariable(value = "bankAccount") String bankAccount,
            @PathVariable(value = "bankTypeId") String bankTypeId
            // @PathVariable(value = "userId") String userId
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check existed if bank account is authenticated
            String check = accountBankReceiveService.checkExistedBank(bankAccount, bankTypeId);
            if (check == null || check.isEmpty()) {
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("CHECK", "C03");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account-bank/check-existed")
    public ResponseEntity<ResponseMessageDTO> checkExistedBankAccountWUserId(
            @RequestParam(value = "bankAccount") String bankAccount,
            @RequestParam(value = "bankTypeId") String bankTypeId,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "type") String type) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // check existed same user
            List<String> checkExistedSameUser = accountBankReceiveService
                    .checkExistedBankAccountSameUser(bankAccount, bankTypeId, userId);
            System.out.println("type" + type);
            if (checkExistedSameUser == null || checkExistedSameUser.isEmpty()) {
                if (type.equals("ADD")) {
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    // check existed if bank account is authenticated
                    String check = accountBankReceiveService.checkExistedBank(bankAccount, bankTypeId);
                    if (check == null || check.isEmpty()) {
                        result = new ResponseMessageDTO("SUCCESS", "");
                        httpStatus = HttpStatus.OK;
                    } else {
                        result = new ResponseMessageDTO("CHECK", "C03");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                }
            } else {
                result = new ResponseMessageDTO("CHECK", "C06");
                httpStatus = HttpStatus.BAD_REQUEST;
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            logger.error(e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // Button thêm tài khoản (không liên kết)
    @PostMapping("account-bank/unauthenticated")
    public ResponseEntity<ResponseMessageDTO> insertAccountBankWithoutAuthenticate(
            @Valid @RequestBody AccountBankUnauthenticatedDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // insert bankAccount receive
            UUID uuid = UUID.randomUUID();
            String qr = getStaticQR(dto.getBankAccount(), dto.getBankTypeId());
            AccountBankReceiveEntity entity = new AccountBankReceiveEntity();
            entity.setId(uuid.toString());
            entity.setBankTypeId(dto.getBankTypeId());
            entity.setBankAccount(dto.getBankAccount());
            entity.setBankAccountName(dto.getUserBankName());
            entity.setType(0);
            entity.setUserId(dto.getUserId());
            entity.setNationalId("");
            entity.setPhoneAuthenticated("");
            entity.setAuthenticated(false);
            entity.setSync(false);
            entity.setWpSync(false);
            entity.setStatus(true);
            entity.setMmsActive(false);
            entity.setRpaSync(false);
            entity.setUsername("");
            entity.setPassword("");
            entity.setEwalletToken("");
            entity.setTerminalLength(10);
            entity.setValidFeeTo(0L);
            entity.setValidFeeFrom(0L);
            entity.setValidService(false);
            accountBankReceiveService.insertAccountBank(entity);

            // insert account-bank-receive-share
            UUID uuidShare = UUID.randomUUID();
            AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
            accountBankReceiveShareEntity.setId(uuidShare.toString());
            accountBankReceiveShareEntity.setBankId(uuid.toString());
            accountBankReceiveShareEntity.setUserId(dto.getUserId());
            accountBankReceiveShareEntity.setOwner(true);
            accountBankReceiveShareEntity.setTraceTransfer("");
            accountBankReceiveShareEntity.setQrCode("");
            accountBankReceiveShareEntity.setTerminalId("");
            accountBankReceiveShareService.insertAccountBankReceiveShare(accountBankReceiveShareEntity);

            // insert contact
            String checkExistedContact = contactService.checkExistedRecord(dto.getUserId(), qr, 2);
            if (checkExistedContact == null) {
                UUID uuidContact = UUID.randomUUID();
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                ContactEntity contactEntity = new ContactEntity();
                contactEntity.setId(uuidContact.toString());
                contactEntity.setUserId(dto.getUserId());
                contactEntity.setNickname(dto.getUserBankName());
                contactEntity.setValue(qr);
                contactEntity.setAdditionalData("");
                contactEntity.setType(2);
                contactEntity.setStatus(0);
                contactEntity.setTime(time);
                contactEntity.setBankTypeId(dto.getBankTypeId());
                contactEntity.setBankAccount(dto.getBankAccount());
                contactEntity.setImgId("");
                contactEntity.setColorType(0);
                contactEntity.setRelation(0);
                contactService.insertContact(contactEntity);
            }
            //
            LarkUtil larkUtil = new LarkUtil();
            String phoneNo = accountInformationService.getPhoneNoByUserId(dto.getUserId());
            AccountInformationEntity accountInformationEntity = accountInformationService
                    .getAccountInformation(dto.getUserId());
            String fullname = accountInformationEntity.getLastName() + " "
                    + accountInformationEntity.getMiddleName() + " " + accountInformationEntity.getFirstName();
            if (fullname.trim().equals("Undefined")) {
                fullname = dto.getUserBankName();
            }
            String email = "";
            if (accountInformationEntity.getEmail() != null
                    && !accountInformationEntity.getEmail().trim().isEmpty()) {
                email = "\\nEmail " + accountInformationEntity.getEmail();
            }
            String address = "";
            if (accountInformationEntity.getAddress() != null
                    && !accountInformationEntity.getAddress().trim().isEmpty()) {
                address = "\\nĐịa chỉ: " + accountInformationEntity.getAddress();
            }

            BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(dto.getBankTypeId());
            String larkMsg = "💳 Thêm TK mới: " + bankTypeEntity.getBankShortName()
                    + "\\nSố TK: " + dto.getBankAccount()
                    + "\\nChủ Tài khoản: " + dto.getUserBankName()
                    + "\\nTrạng thái: Chưa liên kết"
                    + "\\nSĐT đăng nhập: " + phoneNo
                    + "\\nTên đăng nhập: " + fullname.trim()
                    + email
                    + address;
            SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
            larkUtil.sendMessageToLark(larkMsg, systemSettingEntity.getWebhookUrl());
            result = new ResponseMessageDTO("SUCCESS", uuid.toString() + "*" + qr);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.toString());
            logger.error(e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account-bank/wp")
    public ResponseEntity<List<AccountBankWpDTO>> getAccountBankReceiveWps(
            @RequestHeader("Authorization") String token) {
        List<AccountBankWpDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            String userId = getUserIdFromToken(token);
            result = accountBankReceiveService.getAccountBankReceiveWps(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("getAccountBankReceiveWps: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("account-bank/wp/sync")
    public ResponseEntity<ResponseMessageDTO> updateSyncWp(@RequestBody AccountBankSyncWpDTO dto,
                                                           @RequestHeader("Authorization") String token) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                if (dto.getBankId() != null && !dto.getBankId().trim().isEmpty()) {
                    String userId = getUserIdFromToken(token);
                    // 1. find customer_sync_id by hosting (information)
                    // - if not found => do nothing
                    // - if found => step 2
                    String hosting = getHostingFromToken(token);
                    if (hosting != null && !hosting.trim().isEmpty()) {
                        String customerSyncId = customerSyncService.checkExistedCustomerSyncByInformation(hosting);
                        if (customerSyncId != null && !customerSyncId.isEmpty()) {
                            // 2. Check account_customer_bank is existed by cusomer_sync_id and bank_id
                            // - if not existed => insert account_customer_bank
                            // - if existed => do nothing
                            String checkExistedAccountCustomerBank = accountCustomerBankService
                                    .checkExistedAccountCustomerBank(dto.getBankId(), customerSyncId);
                            if (checkExistedAccountCustomerBank == null
                                    || checkExistedAccountCustomerBank.trim().isEmpty()) {
                                UUID uuid = UUID.randomUUID();
                                AccountCustomerBankEntity entity = new AccountCustomerBankEntity();
                                entity.setId(uuid.toString());
                                entity.setAccountCustomerId("");
                                entity.setBankId(dto.getBankId());
                                String bankAccount = accountBankReceiveService.getBankAccountById(dto.getBankId());
                                entity.setBankAccount(bankAccount);
                                entity.setCustomerSyncId(customerSyncId);
                                accountCustomerBankService.insert(entity);
                            } else {
                                logger.info("updateSyncWp: EXISTED account_customer_bank - id: "
                                        + checkExistedAccountCustomerBank);
                            }
                        } else {
                            logger.info("updateSyncWp: NOT FOUND customer_sync_id - user_id: " + userId);
                        }
                    } else {
                        logger.info("updateSyncWp: NOT FOUND HOSTING - user_id: " + userId);
                    }
                    accountBankReceiveService.updateSyncWp(userId, dto.getBankId());
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("updateSyncWp: ERROR: BankId is Invalid");
                    result = new ResponseMessageDTO("FAILED", "E31");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("updateSyncWp: ERROR: NULL Request Body");
                result = new ResponseMessageDTO("FAILED", "E30");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("updateSyncWp: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private String getUserIdFromToken(String token) {
        String result = "";
        if (token != null && !token.trim().isEmpty()) {
            String secretKey = "mySecretKey";
            String jwtToken = token.substring(7); // remove "Bearer " from the beginning
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
            String userId = (String) claims.get("userId");
            result = userId;
        }
        return result;
    }

    private String getHostingFromToken(String token) {
        String result = "";
        if (token != null && !token.trim().isEmpty()) {
            String secretKey = "mySecretKey";
            String jwtToken = token.substring(7); // remove "Bearer " from the beginning
            Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
            String hosting = (String) claims.get("hosting");
            if (hosting != null && !hosting.trim().isEmpty()) {
                result = hosting;
            }
        }
        return result;
    }

    // button "Liên kết tài khoản - cho việc liên kết sau đó"
    // register authentication
    // for case user created bank before and then register authentication
    @PostMapping("account-bank/register-authentication")
    public ResponseEntity<ResponseMessageDTO> registerAuthentication(
            @Valid @RequestBody RegisterAuthenticationDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // update bank-receive
            String ewalletToken = "";
            if (dto.getEwalletToken() != null) {
                ewalletToken = dto.getEwalletToken();
            }
            String bankCode = accountBankReceiveService.getBankCodeByBankId(dto.getBankId());
            switch (bankCode) {
                case "MB":
                    accountBankReceiveService.updateRegisterAuthenticationBank(dto.getNationalId(), dto.getPhoneAuthenticated(),
                            dto.getBankAccountName(), dto.getBankAccount(),
                            ewalletToken,
                            dto.getBankId());
                    break;
                case "BIDV":
                    accountBankReceiveService.updateRegisterAuthenticationBankBIDV(dto.getNationalId(), dto.getPhoneAuthenticated(),
                            dto.getBankAccountName(), dto.getBankAccount(), dto.getVaNumber().substring(4),
                            ewalletToken,
                            dto.getBankId());
                    AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                            .getAccountBankById(dto.getBankId());
                    // customer va enable
                    UUID customerVaId = UUID.randomUUID();
                    CustomerVaEntity customerVaEntity = new CustomerVaEntity();
                    customerVaEntity.setId(customerVaId.toString());
                    customerVaEntity.setMerchantId(dto.getMerchantId());
                    customerVaEntity.setMerchantName(dto.getMerchantName());
                    customerVaEntity.setBankId(dto.getBankId());
                    customerVaEntity.setUserId(accountBankReceiveEntity.getUserId());
                    customerVaEntity.setCustomerId(dto.getVaNumber().substring(4));
                    customerVaEntity.setBankAccount(dto.getBankAccount());
                    customerVaEntity.setUserBankName(accountBankReceiveEntity.getBankAccountName());
                    customerVaEntity.setNationalId(dto.getNationalId());
                    customerVaEntity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
                    customerVaEntity.setMerchantType("1");
                    customerVaEntity.setVaNumber(dto.getVaNumber());
                    customerVaService.insert(customerVaEntity);
                    break;
            }
            //
            LarkUtil larkUtil = new LarkUtil();
            AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService
                    .getAccountBankById(dto.getBankId());
            String phoneNo = accountInformationService.getPhoneNoByUserId(accountBankReceiveEntity.getUserId());
            AccountInformationEntity accountInformationEntity = accountInformationService
                    .getAccountInformation(accountBankReceiveEntity.getUserId());
            String fullname = accountInformationEntity.getLastName() + " "
                    + accountInformationEntity.getMiddleName() + " " + accountInformationEntity.getFirstName();
            if (fullname.trim().equals("Undefined")) {
                fullname = accountBankReceiveEntity.getBankAccountName();
            }
            String email = "";
            if (accountInformationEntity.getEmail() != null
                    && !accountInformationEntity.getEmail().trim().isEmpty()) {
                email = "\\nEmail " + accountInformationEntity.getEmail();
            }
            String address = "";
            if (accountInformationEntity.getAddress() != null
                    && !accountInformationEntity.getAddress().trim().isEmpty()) {
                address = "\\nĐịa chỉ: " + accountInformationEntity.getAddress();
            }

            String larkMsg = "💳 Liên kết TK: " + "MBBank"
                    + "\\nSố TK: " + dto.getBankAccount()
                    + "\\nChủ Tài khoản: " + accountBankReceiveEntity.getBankAccountName()
                    + "\\nSĐT Xác thực: " + dto.getPhoneAuthenticated()
                    + "\\nTrạng thái: Đã liên kết"
                    + "\\nSĐT đăng nhập: " + phoneNo
                    + "\\nTên đăng nhập: " + fullname.trim()
                    + email
                    + address;
            SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
            larkUtil.sendMessageToLark(larkMsg, systemSettingEntity.getWebhookUrl());
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error(e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // map bank with business-branch (update is linked with business)
    //

    // register bank account with authenticated
    @PostMapping("account-bank")
    public ResponseEntity<ResponseMessageDTO> insertAccountBank(@Valid @RequestBody AccountBankReceiveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (StringUtil.isNullOrEmpty(dto.getBankCode())) {
                dto.setBankCode("MB");
            }
            UUID uuid = UUID.randomUUID();
            String qr = "";
            AccountBankReceiveEntity entity = new AccountBankReceiveEntity();
            switch (dto.getBankCode()) {
                case "MB":
                    qr = getStaticQR(dto.getBankAccount(), dto.getBankTypeId());
                    entity.setId(uuid.toString());
                    entity.setBankTypeId(dto.getBankTypeId());
                    entity.setBankAccount(dto.getBankAccount());
                    entity.setBankAccountName(dto.getUserBankName());
                    entity.setType(0);
                    entity.setUserId(dto.getUserId());
                    entity.setNationalId(dto.getNationalId());
                    entity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
                    entity.setAuthenticated(true);
                    entity.setSync(false);
                    entity.setWpSync(false);
                    entity.setStatus(true);
                    entity.setMmsActive(false);
                    entity.setRpaSync(false);
                    entity.setUsername("");
                    entity.setPassword("");
                    entity.setTerminalLength(10);
                    entity.setValidFeeTo(0L);
                    entity.setValidFeeFrom(0L);
                    entity.setValidService(false);
                    if (dto.getEwalletToken() != null) {
                        entity.setEwalletToken(dto.getEwalletToken());
                        logger.info("insertAccountBank: EWALLET TOKEN: " + dto.getEwalletToken());
                    } else {
                        entity.setEwalletToken("");
                        logger.info("insertAccountBank: EWALLET TOKEN: EMPTY");
                    }
                    accountBankReceiveService.insertAccountBank(entity);
                    break;
                case "BIDV":
                    qr = getStaticQR(dto.getBankAccount(), dto.getBankTypeId());
                    entity.setId(uuid.toString());
                    entity.setBankTypeId(dto.getBankTypeId());
                    entity.setBankAccount(dto.getBankAccount());
                    entity.setBankAccountName(dto.getUserBankName());
                    entity.setType(0);
                    entity.setUserId(dto.getUserId());
                    entity.setNationalId(dto.getNationalId());
                    entity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
                    entity.setAuthenticated(true);
                    entity.setSync(false);
                    entity.setWpSync(false);
                    entity.setStatus(true);
                    entity.setMmsActive(false);
                    entity.setRpaSync(false);
                    entity.setUsername("");
                    entity.setPassword("");
                    entity.setTerminalLength(10);
                    entity.setValidFeeTo(0L);
                    entity.setValidFeeFrom(0L);
                    entity.setValidService(false);
                    entity.setCustomerId(dto.getVaNumber().substring(4));
                    if (dto.getEwalletToken() != null) {
                        entity.setEwalletToken(dto.getEwalletToken());
                        logger.info("insertAccountBank: EWALLET TOKEN: " + dto.getEwalletToken());
                    } else {
                        entity.setEwalletToken("");
                        logger.info("insertAccountBank: EWALLET TOKEN: EMPTY");
                    }

                    // customer va enable
                    UUID customerVaId = UUID.randomUUID();
                    CustomerVaEntity customerVaEntity = new CustomerVaEntity();
                    customerVaEntity.setId(customerVaId.toString());
                    customerVaEntity.setMerchantId(dto.getMerchantId());
                    customerVaEntity.setMerchantName(dto.getMerchantName());
                    customerVaEntity.setBankId(uuid.toString());
                    customerVaEntity.setUserId(dto.getUserId());
                    customerVaEntity.setCustomerId(dto.getVaNumber().substring(4));
                    customerVaEntity.setBankAccount(dto.getBankAccount());
                    customerVaEntity.setUserBankName(dto.getUserBankName());
                    customerVaEntity.setNationalId(dto.getNationalId());
                    customerVaEntity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
                    customerVaEntity.setMerchantType("1");
                    customerVaEntity.setVaNumber(dto.getVaNumber());
                    customerVaService.insert(customerVaEntity);
                    break;
            }
            accountBankReceiveService.insertAccountBank(entity);
            // insert account-bank-receive-share
            UUID uuidShare = UUID.randomUUID();
            AccountBankReceiveShareEntity accountBankReceiveShareEntity = new AccountBankReceiveShareEntity();
            accountBankReceiveShareEntity.setId(uuidShare.toString());
            accountBankReceiveShareEntity.setBankId(uuid.toString());
            accountBankReceiveShareEntity.setUserId(dto.getUserId());
            accountBankReceiveShareEntity.setOwner(true);
            accountBankReceiveShareEntity.setTraceTransfer("");
            accountBankReceiveShareEntity.setQrCode("");
            accountBankReceiveShareEntity.setTerminalId("");
            accountBankReceiveShareService.insertAccountBankReceiveShare(accountBankReceiveShareEntity);
            // insert contact
            String checkExistedContact = contactService.checkExistedRecord(dto.getUserId(), qr, 2);
            if (checkExistedContact == null) {
                UUID uuidContact = UUID.randomUUID();
                LocalDateTime currentDateTime = LocalDateTime.now();
                long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
                ContactEntity contactEntity = new ContactEntity();
                contactEntity.setId(uuidContact.toString());
                contactEntity.setUserId(dto.getUserId());
                contactEntity.setNickname(dto.getUserBankName());
                contactEntity.setValue(qr);
                contactEntity.setAdditionalData("");
                contactEntity.setType(2);
                contactEntity.setStatus(0);
                contactEntity.setTime(time);
                contactEntity.setBankTypeId(dto.getBankTypeId());
                contactEntity.setBankAccount(dto.getBankAccount());
                contactEntity.setImgId("");
                contactEntity.setColorType(0);
                contactEntity.setRelation(0);
                contactService.insertContact(contactEntity);
            }
            //
            LarkUtil larkUtil = new LarkUtil();
            // AccountBankReceiveEntity accountBankReceiveEntity =
            // accountBankService.getAccountBankById(dto.getBankId());
            String phoneNo = accountInformationService.getPhoneNoByUserId(dto.getUserId());
            AccountInformationEntity accountInformationEntity = accountInformationService
                    .getAccountInformation(dto.getUserId());
            String fullname = accountInformationEntity.getLastName() + " "
                    + accountInformationEntity.getMiddleName() + " " + accountInformationEntity.getFirstName();
            if (fullname.trim().equals("Undefined")) {
                fullname = dto.getUserBankName();
            }
            String email = "";
            if (accountInformationEntity.getEmail() != null
                    && !accountInformationEntity.getEmail().trim().isEmpty()) {
                email = "\\nEmail " + accountInformationEntity.getEmail();
            }
            String address = "";
            if (accountInformationEntity.getAddress() != null
                    && !accountInformationEntity.getAddress().trim().isEmpty()) {
                address = "\\nĐịa chỉ: " + accountInformationEntity.getAddress();
            }
            String larkMsg = "💳 Liên kết TK: " + "MBBank"
                    + "\\nSố TK: " + dto.getBankAccount()
                    + "\\nChủ Tài khoản: " + dto.getUserBankName()
                    + "\\nSĐT Xác thực: " + dto.getPhoneAuthenticated()
                    + "\\nTrạng thái: Đã liên kết"
                    + "\\nSĐT đăng nhập: " + phoneNo
                    + "\\nTên đăng nhập: " + fullname.trim()
                    + email
                    + address;
            SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
            larkUtil.sendMessageToLark(larkMsg, systemSettingEntity.getWebhookUrl());
            result = new ResponseMessageDTO("SUCCESS", uuid.toString() + "*" + qr);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Error at insertAccountBank: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @Async
    protected String getStaticQR(String bankAccount, String bankTypeId) {
        String result = "";
        String caiValue = caiBankService.getCaiValue(bankTypeId);
        VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
        vietQRGenerateDTO.setCaiValue(caiValue);
        vietQRGenerateDTO.setBankAccount(bankAccount);
        result = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
        return result;

    }

    @GetMapping("account-bank/detail/web/{bankId}")
    public ResponseEntity<AccountBankReceiveDetailWT> getBankDetailWithoutTransaction(
            @PathVariable("bankId") String bankId) {
        AccountBankReceiveDetailWT result = null;
        HttpStatus httpStatus = null;
        try {
            long currentDateTimeUTCPlus7 = DateTimeUtil.getStartDateUTCPlus7();
            // get
            AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(bankId);
            if (accountBankEntity != null) {
                TransTempCountDTO transTempCountDTO = transReceiveTempService
                        .getTransTempCount(accountBankEntity.getId());
                BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
                // get cai value
                String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
                // generate VietQRGenerateDTO
                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                vietQRGenerateDTO.setCaiValue(caiValue);
                vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                // set values
                result = new AccountBankReceiveDetailWT();
                result.setId(bankId);
                result.setBankAccount(accountBankEntity.getBankAccount());
                result.setUserBankName(accountBankEntity.getBankAccountName());
                result.setBankCode(bankTypeEntity.getBankCode());
                result.setBankName(bankTypeEntity.getBankName());
                result.setImgId(bankTypeEntity.getImgId());
                result.setType(accountBankEntity.getType());
                result.setBankTypeId(bankTypeEntity.getId());
                result.setBankTypeStatus(bankTypeEntity.getStatus());
                result.setUserId(accountBankEntity.getUserId());
                result.setAuthenticated(accountBankEntity.isAuthenticated());
                result.setNationalId(accountBankEntity.getNationalId());
                result.setQrCode(qr);
                result.setEwalletToken(StringUtil.getValueNullChecker(accountBankEntity.getEwalletToken()));
                result.setUnlinkedType(bankTypeEntity.getUnlinkedType());
                result.setPhoneAuthenticated(accountBankEntity.getPhoneAuthenticated());
                result.setIsActiveService(accountBankEntity.isValidService());
                result.setValidFeeFrom(accountBankEntity.getValidFeeFrom());
                result.setValidFeeTo(accountBankEntity.getValidFeeTo());
                if (Objects.nonNull(transTempCountDTO)) {
                    if (transTempCountDTO.getLastTimes() < currentDateTimeUTCPlus7) {
                        result.setTransCount(0);
                    } else {
                        result.setTransCount(transTempCountDTO.getNums());
                    }
                } else {
                    result.setTransCount(0);
                }
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account-bank/detail/{bankId}")
    public ResponseEntity<AccountBankReceiveDetailDTO> getBankDetail(@PathVariable("bankId") String bankId) {
        AccountBankReceiveDetailDTO result = null;
        HttpStatus httpStatus = null;
        try {
            // get
            long currentDateTimeUTCPlus7 = DateTimeUtil.getStartDateUTCPlus7();
            AccountBankReceiveEntity accountBankEntity = accountBankReceiveService.getAccountBankById(bankId);
            if (accountBankEntity != null) {
                TransTempCountDTO transTempCountDTO = transReceiveTempService
                        .getTransTempCount(accountBankEntity.getId());
                BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(accountBankEntity.getBankTypeId());
                // get cai value
                String caiValue = caiBankService.getCaiValue(bankTypeEntity.getId());
                // generate VietQRGenerateDTO
                VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                vietQRGenerateDTO.setCaiValue(caiValue);
                vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                // set values
                result = new AccountBankReceiveDetailDTO();
                result.setId(bankId);
                result.setBankAccount(accountBankEntity.getBankAccount());
                result.setUserBankName(accountBankEntity.getBankAccountName());
                result.setBankCode(bankTypeEntity.getBankCode());
                result.setBankName(bankTypeEntity.getBankName());
                result.setImgId(bankTypeEntity.getImgId());
                result.setType(accountBankEntity.getType());
                result.setBankTypeId(bankTypeEntity.getId());
                result.setBankTypeStatus(bankTypeEntity.getStatus());
                result.setUserId(accountBankEntity.getUserId());
                result.setAuthenticated(accountBankEntity.isAuthenticated());
                result.setNationalId(accountBankEntity.getNationalId());
                result.setQrCode(qr);
                result.setCaiValue(caiValue);
                result.setEwalletToken(accountBankEntity.getEwalletToken());
                result.setUnlinkedType(bankTypeEntity.getUnlinkedType());
                result.setPhoneAuthenticated(accountBankEntity.getPhoneAuthenticated());
                result.setIsActiveService(accountBankEntity.isValidService());
                result.setValidFeeFrom(accountBankEntity.getValidFeeFrom());
                result.setValidFeeTo(accountBankEntity.getValidFeeTo());
                if (Objects.nonNull(transTempCountDTO)) {
                    if (transTempCountDTO.getLastTimes() < currentDateTimeUTCPlus7) {
                        result.setTransCount(0);
                    } else {
                        result.setTransCount(transTempCountDTO.getNums());
                    }
                } else {
                    result.setTransCount(0);
                }
                List<TransactionBankListDTO> transactions = new ArrayList<>();
                result.setTransactions(transactions);
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            logger.error(e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
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

    @GetMapping("account-bank/terminal")
    public ResponseEntity<List<TerminalCodeResponseDTO>> getTerminalsOfBank(
            @Valid @RequestParam String userId,
            @Valid @RequestParam String bankId) {
        List<TerminalCodeResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            //new
            //owner
            List<TerminalCodeResponseDTO> terminalInterOwners = terminalService.getTerminalsByUserIdAndBankIdOwner(userId,
                    bankId);
            // not owner
            List<TerminalCodeResponseDTO> terminalInters = terminalService.getTerminalsByUserIdAndBankId(userId,
                    bankId);
            terminalInterOwners.addAll(terminalInters);
            result = terminalInterOwners;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account-bank/active-key/{userId}")
    public ResponseEntity<List<AccountBankActiveKeyResponseDTO>> getAccountBankBackupAvtiveKey(
            @PathVariable("userId") String userId) {
        List<AccountBankActiveKeyResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // get list banks
            //
            logger.info("getAccountBankBackups: " + userId);
            long currentDateTimeUTCPlus7 = DateTimeUtil.getStartDateUTCPlus7();
            List<AccountBankReceiveShareDTO> banks = accountBankReceiveShareService
                    .getAccountBankReceiveShares(userId);
            List<TransTempCountDTO> transTempCountDTOs = transReceiveTempService
                    .getTransTempCounts(banks.stream().map(AccountBankReceiveShareDTO::getBankId)
                            .distinct().collect(Collectors.toList()));

            Map<String, TransTempCountDTO> transTempCountDTOMap = transTempCountDTOs.stream()
                    .collect(Collectors.toMap(TransTempCountDTO::getBankId, Function.identity()));

            List<CaiValueDTO> caiValues = caiBankService.getCaiValues(banks
                    .stream().map(AccountBankReceiveShareDTO::getBankTypeId)
                    .distinct().collect(Collectors.toList()));

            Map<String, CaiValueDTO> caiValueDTOMap = caiValues.stream()
                    .collect(Collectors.toMap(CaiValueDTO::getBankTypeId, Function.identity()));

            // lấy key trong bảng

            if (!FormatUtil.isListNullOrEmpty(banks)) {
                result = banks.stream().map(item -> {
                    AccountBankActiveKeyResponseDTO dto = new AccountBankActiveKeyResponseDTO();
                    CaiValueDTO valueDTO = caiValueDTOMap.get(item.getBankTypeId());
                    TransTempCountDTO transTempCountDTO = transTempCountDTOMap.get(item.getBankId());
                    if (Objects.nonNull(transTempCountDTO)) {
                        if (transTempCountDTO.getLastTimes() < currentDateTimeUTCPlus7) {
                            dto.setTransCount(0);
                        } else {
                            dto.setTransCount(transTempCountDTO.getNums());
                        }
                    } else {
                        dto.setTransCount(0);
                    }
                    dto.setId(item.getBankId());
                    dto.setBankAccount(item.getBankAccount());
                    dto.setBankShortName(valueDTO.getBankShortName());
                    dto.setUserBankName(item.getUserBankName());
                    dto.setBankCode(valueDTO.getBankCode());
                    dto.setBankName(valueDTO.getBankName());
                    dto.setImgId(valueDTO.getImgId());
                    dto.setType(item.getBankType());
                    dto.setBankTypeId(item.getBankTypeId());
                    dto.setEwalletToken("");
                    dto.setUnlinkedType(valueDTO.getUnlinkedType());
                    dto.setNationalId(item.getNationalId());
                    dto.setAuthenticated(item.getAuthenticated());
                    dto.setUserId(item.getUserId());
                    dto.setIsOwner(item.getIsOwner());
                    dto.setPhoneAuthenticated(item.getPhoneAuthenticated());
                    dto.setBankTypeStatus(valueDTO.getBankTypeStatus());
                    dto.setIsValidService(item.getIsValidService());
                    dto.setValidFeeFrom(item.getValidFeeFrom());
                    dto.setValidFeeTo(item.getValidFeeTo());

                    /// khi user đã active key để lưu lại
                    List<ICheckKeyActiveDTO> bankReceiveActiveHistoryEntity =
                            bankReceiveActiveHistoryService.getBankReceiveActiveByUserIdAndBankIdBackUp(userId, item.getBankId());
                    for (ICheckKeyActiveDTO checkKeyActiveDTO : bankReceiveActiveHistoryEntity) {
                        if (Objects.nonNull(checkKeyActiveDTO)) {
                            dto.setIsActiveKey(true);
                            dto.setTimeActiveKey(checkKeyActiveDTO.getCreateAt());
                            dto.setKeyActive(checkKeyActiveDTO.getKeyActive());
                        } else {
                            dto.setIsActiveKey(false);
                            dto.setTimeActiveKey(0);
                            StringUtil.isNullOrEmpty(checkKeyActiveDTO.getKeyActive());
                        }
                    }

//                        if (checkKeyActiveDTO.getStatusActive() == 1) {
//                            dto.setIsActiveKey(true);
//                            dto.setTimeActiveKey(checkKeyActiveDTO.getCreateAt());
//                            dto.setKeyActive(checkKeyActiveDTO.getKeyActive());
//                        }else if(checkKeyActiveDTO.getStatusActive() == 0) {
//                            dto.setIsActiveKey(false);
//                            dto.setTimeActiveKey(0);
//                            dto.setKeyActive("");
//                        }

                    dto.setCaiValue(valueDTO.getCaiValue());
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(valueDTO.getCaiValue());
                    vietQRGenerateDTO.setBankAccount(item.getBankAccount());
                    String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                    dto.setQrCode(qr);
                    return dto;
                }).collect(Collectors.toList());
            }
            httpStatus = HttpStatus.OK;
        } catch (
                Exception e) {
            logger.info("getAccountBankBackups: ERROR: " + e.getMessage() + " " + userId);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account-bank/{userId}")
    public ResponseEntity<List<AccountBankShareResponseDTO>> getAccountBankBackups(
            @PathVariable("userId") String userId) {
        List<AccountBankShareResponseDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            // get list banks
            //
            logger.info("getAccountBankBackups: " + userId);
            long currentDateTimeUTCPlus7 = DateTimeUtil.getStartDateUTCPlus7();
            List<AccountBankReceiveShareDTO> banks = accountBankReceiveShareService
                    .getAccountBankReceiveShares(userId);
            List<TransTempCountDTO> transTempCountDTOs = transReceiveTempService
                    .getTransTempCounts(banks.stream().map(AccountBankReceiveShareDTO::getBankId)
                            .distinct().collect(Collectors.toList()));

            Map<String, TransTempCountDTO> transTempCountDTOMap = transTempCountDTOs.stream()
                    .collect(Collectors.toMap(TransTempCountDTO::getBankId, Function.identity()));

            List<CaiValueDTO> caiValues = caiBankService.getCaiValues(banks
                    .stream().map(AccountBankReceiveShareDTO::getBankTypeId)
                    .distinct().collect(Collectors.toList()));

            Map<String, CaiValueDTO> caiValueDTOMap = caiValues.stream()
                    .collect(Collectors.toMap(CaiValueDTO::getBankTypeId, Function.identity()));

            if (!FormatUtil.isListNullOrEmpty(banks)) {
                result = banks.stream().map(item -> {
                    AccountBankShareResponseDTO dto = new AccountBankShareResponseDTO();
                    CaiValueDTO valueDTO = caiValueDTOMap.get(item.getBankTypeId());
                    TransTempCountDTO transTempCountDTO = transTempCountDTOMap.get(item.getBankId());
                    if (Objects.nonNull(transTempCountDTO)) {
                        if (transTempCountDTO.getLastTimes() < currentDateTimeUTCPlus7) {
                            dto.setTransCount(0);
                        } else {
                            dto.setTransCount(transTempCountDTO.getNums());
                        }
                    } else {
                        dto.setTransCount(0);
                    }
                    dto.setId(item.getBankId());
                    dto.setBankAccount(item.getBankAccount());
                    dto.setBankShortName(valueDTO.getBankShortName());
                    dto.setUserBankName(item.getUserBankName());
                    dto.setBankCode(valueDTO.getBankCode());
                    dto.setBankName(valueDTO.getBankName());
                    dto.setImgId(valueDTO.getImgId());
                    dto.setType(item.getBankType());
                    dto.setBankTypeId(item.getBankTypeId());
                    dto.setEwalletToken("");
                    dto.setUnlinkedType(valueDTO.getUnlinkedType());
                    dto.setNationalId(item.getNationalId());
                    dto.setAuthenticated(item.getAuthenticated());
                    dto.setUserId(item.getUserId());
                    dto.setIsOwner(item.getIsOwner());
                    dto.setPhoneAuthenticated(item.getPhoneAuthenticated());
                    dto.setBankTypeStatus(valueDTO.getBankTypeStatus());
                    dto.setIsValidService(item.getIsValidService());
                    dto.setValidFeeFrom(item.getValidFeeFrom());
                    dto.setValidFeeTo(item.getValidFeeTo());

                    dto.setCaiValue(valueDTO.getCaiValue());
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    vietQRGenerateDTO.setCaiValue(valueDTO.getCaiValue());
                    vietQRGenerateDTO.setBankAccount(item.getBankAccount());
                    String qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                    dto.setQrCode(qr);
                    return dto;
                }).collect(Collectors.toList());
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.info("getAccountBankBackups: ERROR: " + e.getMessage() + " " + userId);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @DeleteMapping("account-bank")
    public ResponseEntity<ResponseMessageDTO> deleteAccountBank(@Valid @RequestBody BankAccountRemoveDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto.isAuthenticated() == true) {
                result = new ResponseMessageDTO("CHECK", "C04");
                httpStatus = HttpStatus.OK;
            } else {
                // remove account bank receive share
                accountBankReceiveShareService.deleteAccountBankReceiveShareByBankId(dto.getBankId());
                accountBankReceiveService.deleteAccountBank(dto.getBankId());
                result = new ResponseMessageDTO("SUCCESS", "");
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            System.out.println("Error at deleteAccountBank: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "Unexpected Error.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // insert account bank receive with RPA sync
    @PostMapping("account-bank/rpa")
    public ResponseEntity<ResponseMessageDTO> insertAccountBankReceiveRPA(@RequestBody AccountBankReceiveRpaDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String bankTypeId = bankTypeService.getBankTypeIdByBankCode(dto.getBankCode());
            if (bankTypeId != null && !bankTypeId.trim().isEmpty()) {
                String check = accountBankReceiveService.checkExistedBank(dto.getBankAccount(), bankTypeId);
                if (check == null || check.isEmpty()) {
                    UUID uuid = UUID.randomUUID();
                    AccountBankReceiveEntity entity = new AccountBankReceiveEntity();
                    entity.setId(uuid.toString());
                    entity.setBankTypeId(bankTypeId);
                    entity.setBankAccount(dto.getBankAccount());
                    entity.setBankAccountName(dto.getUserBankName());
                    entity.setType(0);
                    entity.setUserId(dto.getUserId());
                    entity.setNationalId("");
                    entity.setPhoneAuthenticated("");
                    entity.setAuthenticated(true);
                    entity.setSync(false);
                    entity.setWpSync(false);
                    entity.setStatus(true);
                    entity.setMmsActive(false);
                    entity.setRpaSync(true);
                    entity.setUsername(dto.getUsername());
                    entity.setPassword(dto.getPassword());
                    entity.setEwalletToken("");
                    entity.setTerminalLength(10);
                    entity.setValidFeeTo(0L);
                    entity.setValidFeeFrom(0L);
                    entity.setValidService(false);
                    accountBankReceiveService.insertAccountBank(entity);
                    // insert account_bank_personal
                    UUID uuidPersonal = UUID.randomUUID();
                    BankReceivePersonalEntity personalEntity = new BankReceivePersonalEntity();
                    personalEntity.setId(uuidPersonal.toString());
                    personalEntity.setBankId(uuid.toString());
                    personalEntity.setUserId(dto.getUserId());
                    accountBankReceivePersonalService.insertAccountBankReceivePersonal(personalEntity);
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    logger.error("insertAccountBankReceiveRPA: EXISTED BANK ACCOUNT");
                    result = new ResponseMessageDTO("FAILED", "E73");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("insertAccountBankReceiveRPA: NOT FOUND BANK TYPE ID");
                result = new ResponseMessageDTO("FAILED", "E51");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("Error at insertAccountBankReceiveRPA: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account-bank/rpa/list")
    public ResponseEntity<List<AccountBankReceiveRPAItemDTO>> getBankAccountRPAs(
            @RequestParam(value = "userId") String userId) {
        List<AccountBankReceiveRPAItemDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = accountBankReceiveService.getBankAccountsRPA(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Error at getBankAccountRPAs: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // For admin get list bank account by customersyncid
    @GetMapping("admin/account-bank/list")
    public ResponseEntity<List<AccountBankReceiveByCusSyncDTO>> getBankAccountsByCusSyncId(
            @RequestParam(value = "customerSyncId") String customerSyncId,
            @RequestParam(value = "offset") int offset) {
        List<AccountBankReceiveByCusSyncDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = accountBankReceiveService.getBankAccountsByCusSyncId(customerSyncId, offset);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Error at getBankAccountsByCusSyncId: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    ///
    ///
    // Transfer bankAccount sync (flow 1 & 2)
    ///
    // 1. check mms_sync (1 or 2) & check is_authenticated
    // => unauthen: 0
    // => authen & flow 1: 1
    // => authen & flow 2: 2
    ///
    // IF 2 to 1:
    // 2. update mms_sync into bank_account_receive
    ///
    // IF 1 to 2:
    // 2. get customer sync info - address
    // 3. get counting bankAccount into customersync
    // 4. Check existing terminal by bankAccount
    // 5. call sync TID MB
    // 6. get list TID
    // 7. insert terminal_bank
    // 8. insert terminal_address
    // 9. update mms_sync, is_sync into bank_account_receive
    @PostMapping("account-bank/flow/switch")
    public ResponseEntity<ResponseMessageDTO> transferBankAccountFlow(
            @RequestBody AccountBankReceiveTransferFlowDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                Boolean isMMSActive = accountBankReceiveService.getMMSActiveByBankId(dto.getBankId());
                if (isMMSActive != null && isMMSActive == true) {
                    // flow 2
                } else if (isMMSActive != null && isMMSActive == false) {
                    // flow 1
                } else {
                    // err
                    System.out.println("transferBankAccountFlow: INVALID MMS ACTIVE/NOT FOUND BANK ACCOUNT");
                    logger.error("transferBankAccountFlow: INVALID MMS ACTIVE/NOT FOUND BANK ACCOUNT");
                    result = new ResponseMessageDTO("FAILED", "E108");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                System.out.println("transferBankAccountFlow: INVALID REQUEST BODY");
                logger.error("transferBankAccountFlow: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            System.out.println("transferBankAccountFlow: ERROR: " + e.toString());
            logger.error("transferBankAccountFlow: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("account-bank/statistics")
    public List<IAccountBankMonthDTO> getBankAccountStatistics() {
        return accountBankReceiveService.getBankAccountStatistics();
    }

    private MerchantBankMapperDTO getMerchantBankMapperDTO(String data) {
        MerchantBankMapperDTO result = new MerchantBankMapperDTO();
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(data, MerchantBankMapperDTO.class);
        } catch (Exception e) {
            logger.error("InvoiceController: ERROR: getMerchantBankMapperDTO: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new MerchantBankMapperDTO();
        }
        return result;
    }

    private TokenMBResponseDTO getToken() throws JsonProcessingException {
        UUID clientMessageId = UUID.randomUUID();

        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl("https://api-private.mbbank.com.vn/private/oauth2/v1/token")
//                .fromHttpUrl("https://kietml.click/getToken.php")
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-private.mbbank.com.vn/private/oauth2/v1/token")
//                .baseUrl("https://kietml.click/getToken.php")
                .build();

        Mono<ClientResponse> responseMono = webClient.post()
                .uri(uriComponents.toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .header("clientMessageId", clientMessageId.toString())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .exchange();

        ClientResponse response = responseMono.block();
        TokenMBResponseDTO tokenResponse = null;

        try {
//            tokenResponse = webClient.post()
//                    .uri(uriComponents.toUri())
//                    .header("clientMessageId", clientMessageId.toString())
//                    .header("Content-Type", "application/x-www-form-urlencoded")
////                    .bodyValue("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
//                    .retrieve()
//                    .bodyToMono(TokenMBResponseDTO.class)
//                    .block();


            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("getToken: RESPONSE: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                tokenResponse = objectMapper.readValue(json, TokenMBResponseDTO.class);
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("getToken: RESPONSE: " + response.statusCode().value() + " - " + json);
            }

            return tokenResponse;

        } catch (WebClientResponseException e) {
            logger.error("Response error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
        return tokenResponse;
    }

    private TerminalResponseFlow2 getTerminals(String token) throws JsonProcessingException {
        UUID clientMessageId = UUID.randomUUID();

        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl("https://api-private.mbbank.com.vn/private/ms/offus/public/account-service/tid/v1.0/list-tid")
//                .fromHttpUrl("https://kietml.click/getTID.php")
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-private.mbbank.com.vn/private/ms/offus/public/account-service/tid/v1.0/list-tid")
//                .baseUrl("https://kietml.click/getTID.php")
                .build();

        Mono<ClientResponse> responseMono = webClient.get()
                .uri(uriComponents.toUri())
//                .contentType(MediaType.APPLICATION_JSON)
                .header("clientMessageId", clientMessageId.toString())
                .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                .header("username", EnvironmentUtil.getUsernameAPI())
                .header("Authorization", "Bearer " + token)
                .exchange();

        ClientResponse response = responseMono.block();
        TerminalResponseFlow2 terminalResponse = null;

        if (response.statusCode().is2xxSuccessful()) {
            String json = response.bodyToMono(String.class).block();
            logger.info("getTerminals: RESPONSE: " + json);
            ObjectMapper objectMapper = new ObjectMapper();
            terminalResponse = objectMapper.readValue(json, TerminalResponseFlow2.class);
        } else {
            String json = response.bodyToMono(String.class).block();
            logger.info("getTerminals: RESPONSE: " + response.statusCode().value() + " - " + json);
        }

        return terminalResponse;
    }


    public TerminalResponseSyncTidDTO syncTerminals(String token) throws JsonProcessingException {

        UUID clientMessageId = UUID.randomUUID();
//        Map<String, Object> data = new HashMap<>();
//        data.put("terminals", terminals);
        TerminalRequestDTO terminalRequestDTO = new TerminalRequestDTO();

        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl("https://api-private.mbbank.com.vn/private/ms/offus/public/account-service/tid/v1.0/synchronize")
//                .fromHttpUrl("https://kietml.click/syncTID.php")
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api-private.mbbank.com.vn/private/ms/offus/public/account-service/tid/v1.0/synchronize")
//                .baseUrl("https://kietml.click/syncTID.php")
                .build();

        Mono<ClientResponse> responseMono = webClient.post()
                .uri(uriComponents.toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .header("clientMessageId", clientMessageId.toString())
                .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                .header("username", EnvironmentUtil.getUsernameAPI())
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(terminalRequestDTO))
                .exchange();

        ClientResponse response = responseMono.block();
        TerminalResponseSyncTidDTO terminalResponse = null;

        if (response.statusCode().is2xxSuccessful()) {
//                String json = response.bodyToMono(String.class).block();
//                logger.info("syncTerminals: RESPONSE: " + json);
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode rootNode = objectMapper.readTree(json);
//                if (rootNode.get("data") != null) {
//                    if (rootNode.get("data").get("qrcode") != null) {
//                        result = rootNode.get("data").get("qrcode").asText();
//                        logger.info("syncTerminals: RESPONSE qrcode: " + result);
//                    } else {
//                        logger.info("syncTerminals: RESPONSE qrcode is null");
//                    }
//                } else {
//                    logger.info("syncTerminals: RESPONSE data is null");
//                }
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("getToken: RESPONSE: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                terminalResponse = objectMapper.readValue(json, TerminalResponseSyncTidDTO.class);
            } else {
                String json = response.bodyToMono(String.class).block();
                logger.info("getToken: RESPONSE: " + response.statusCode().value() + " - " + json);
            }
        } else {
            String json = response.bodyToMono(String.class).block();
            logger.info("syncTerminals: RESPONSE: " + response.statusCode().value() + " - " + json);
        }
        return terminalResponse;
    }
}
