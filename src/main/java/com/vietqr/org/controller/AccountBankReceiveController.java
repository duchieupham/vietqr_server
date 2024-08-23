package com.vietqr.org.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.dto.bidv.RequestCustomerVaDTO;
import com.vietqr.org.entity.*;
import com.vietqr.org.entity.bidv.CustomerVaEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.service.bidv.CustomerVaService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;
import com.vietqr.org.util.bank.mb.MBTokenUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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
import software.amazon.awssdk.thirdparty.jackson.core.type.TypeReference;

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
    CustomerVaService customerVaService;

    @Autowired
    TransactionReceiveService transactionReceiveService;

    @Autowired
    CustomerSyncService customerSyncService;

    @Autowired
    AccountCustomerBankService accountCustomerBankService;

    @Autowired
    KeyActiveBankReceiveService keyActiveBankReceiveService;

    @Autowired
    AccountLoginService accountLoginService;

    @Autowired
    AccountInformationService accountInformationService;

    @Autowired
    SystemSettingService systemSettingService;

    @Autowired
    ContactService contactService;

    @Autowired
    MerchantSyncService merchantSyncService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    EmailVerifyService emailVerifyService;

    @PostMapping("admin/account/update-flow-2")
    public ResponseEntity<Object> updateFlow2(
            @Valid @RequestBody AccountUpdateMMSActiveDTO dto
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            //-Check Tài khoản này đã đăng ký luồng 2 trước đó chưa,
            AccountBankReceiveEntity checkAccount =
                    accountBankReceiveService.getAccountBankReceiveByBankAccountAndBankCode(dto.getBankAccount(), dto.getBankCode());
            //check bank account is_authenticated
            AccountBankReceiveEntity bankReceiveEntity = accountBankReceiveService
                    .getAccountBankReceiveByBankAccountAndBankCode(dto.getBankAccount(), dto.getBankCode());
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
                        // sửa lại cho nhập từ hệ thống khi sync TID
                        String terminalAddressCheckNew = dto.getAddress();
                        List<String> getTerminalNames = terminalBankService.getListTerminalNames();
                        List<String> getTerminalAddresses = terminalBankService.getTerminalAddresses();
                        int i = 0;

                        String checkString = checkAndModifyString(terminalNameGenerate, getTerminalNames);
                        String checkAddress = checkAndModifyStringAddress(terminalAddressCheckNew, getTerminalAddresses);

                        // get token MB
                        // luồng code cũ
                        TokenProductBankDTO tokenBankDTO = MBTokenUtil.getMBBankToken();

//                        TokenMBResponseDTO tokenMBResponseDTO = getToken();
                        // Sync TID MB Bank
                        TerminalRequestDTO.TerminalDTO terminal = new TerminalRequestDTO.TerminalDTO();
                        terminal.setTerminalId(null);
                        terminal.setTerminalName(checkString);
                        terminal.setTerminalAddress(checkAddress);
                        terminal.setProvinceCode("1");
                        terminal.setDistrictCode("6");
                        terminal.setWardsCode("178");
                        terminal.setMccCode("1024");
                        terminal.setFee(0);
                        terminal.setBankCode("311");
                        terminal.setBankCodeBranch("01311038");
                        terminal.setBankAccountNumber(bankAccountNumberEncrypted);
                        terminal.setBankAccountName(bankAccountName.toUpperCase());
                        terminal.setBankCurrencyCode("1");
                        TerminalResponseSyncTidDTO terminalRequestDTO = syncTerminals(terminal, tokenBankDTO.getAccess_token());
                        String terminalIdBySyncTID = terminalRequestDTO.getData().getResult().get(0).getTerminalId();

                        // get TID MB Bank
                        TerminalResponseFlow2 terminalResponseFlow2 = getTerminals(tokenBankDTO.getAccess_token());
                        String getTerminalID = terminalResponseFlow2.getData().getTerminals().get(0).getTerminalId();
                        String getBankAccountNumberNew = terminalResponseFlow2.getData().getTerminals().get(0).getBankAccountNumber();

                        // insert vào 2 bảng terminal_bank, terminal_address
                        TerminalBankEntity terminalBankEntity = new TerminalBankEntity();
                        UUID idTerminalBank = UUID.randomUUID();
                        terminalBankEntity.setId(idTerminalBank.toString());
                        terminalBankEntity.setBankAccountName(dto.getBankAccountName().toUpperCase());
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
                        terminalBankEntity.setMerchantId("b8324764-3f83-4da0-a75f-aa0f13d0f700");
                        terminalBankEntity.setProvinceCode("1");
                        terminalBankEntity.setProvinceName("Hà Nội update");
                        terminalBankEntity.setStatus(1);
                        terminalBankEntity.setTerminalAddress(checkAddress);
                        terminalBankEntity.setTerminalId(getTerminalID); // terminalID lấy từ API syncTID trả về response
                        terminalBankEntity.setTerminalName(checkString);
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
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("account-bank/update-arrangement")
    public ResponseEntity<ResponseMessageDTO> updateAccountBankArrangement(
        @Valid @RequestBody UpdateBankArrangeDTO dto
    ) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (Objects.nonNull(dto) && !dto.getBankArranges().isEmpty()) {
                for (BankArrangeDTO item: dto.getBankArranges()) {
                    accountBankReceiveShareService
                            .updateAccountBankArrangement(item.getBankId(), item.getIndex(), dto.getUserId());
                }
            }
            result = new ResponseMessageDTO("SUCCESS", "");
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("updateAccountBankArrangement ERROR : " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
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
                    accountBankReceiveService.getAccountBankReceiveByBankAccountAndBankCode(dto.getBankAccount(), dto.getBankCode());
            //check bank account is_authenticated
            AccountBankReceiveEntity bankReceiveEntity = accountBankReceiveService
                    .getAccountBankReceiveByBankAccountAndBankCode(dto.getBankAccount(), dto.getBankCode());
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
            entity.setBankAccount(dto.getBankAccount().replaceAll(" ", ""));
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
//            LarkUtil larkUtil = new LarkUtil();
            GoogleChatUtil googleChatUtil = new GoogleChatUtil();
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
                email = "\nEmail " + accountInformationEntity.getEmail();
            }
            String address = "";
            if (accountInformationEntity.getAddress() != null
                    && !accountInformationEntity.getAddress().trim().isEmpty()) {
                address = "\nĐịa chỉ: " + accountInformationEntity.getAddress();
            }

            BankTypeEntity bankTypeEntity = bankTypeService.getBankTypeById(dto.getBankTypeId());
            String larkMsg = "💳 Thêm TK mới: " + bankTypeEntity.getBankShortName()
                    + "\nSố TK: " + dto.getBankAccount()
                    + "\nChủ Tài khoản: " + dto.getUserBankName()
                    + "\nTrạng thái: Chưa liên kết"
                    + "\nSĐT đăng nhập: " + phoneNo
                    + "\nTên đăng nhập: " + fullname.trim()
                    + email
                    + address;
            SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
//            larkUtil.sendMessageToLark(larkMsg, systemSettingEntity.getWebhookUrl());
            googleChatUtil.sendMessageToGoogleChat(larkMsg, systemSettingEntity.getWebhookUrl());
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
                            dto.getBankAccountName(), dto.getBankAccount().replaceAll(" ", ""),
                            ewalletToken,
                            dto.getBankId());
                    break;
                case "BIDV":
                    accountBankReceiveService.updateRegisterAuthenticationBankBIDV(dto.getNationalId(), dto.getPhoneAuthenticated(),
                            dto.getBankAccountName(), dto.getBankAccount().replaceAll(" ", ""), dto.getVaNumber().substring(4),
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
                    customerVaEntity.setBankAccount(dto.getBankAccount().replaceAll(" ", ""));
                    customerVaEntity.setUserBankName(accountBankReceiveEntity.getBankAccountName());
                    customerVaEntity.setNationalId(dto.getNationalId());
                    customerVaEntity.setPhoneAuthenticated(dto.getPhoneAuthenticated());
                    customerVaEntity.setMerchantType("1");
                    customerVaEntity.setVaNumber(dto.getVaNumber());
                    customerVaService.insert(customerVaEntity);
                    break;
            }
            //
//            LarkUtil larkUtil = new LarkUtil();
            GoogleChatUtil googleChatUtil = new GoogleChatUtil();
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
                email = "\nEmail " + accountInformationEntity.getEmail();
            }
            String address = "";
            if (accountInformationEntity.getAddress() != null
                    && !accountInformationEntity.getAddress().trim().isEmpty()) {
                address = "\nĐịa chỉ: " + accountInformationEntity.getAddress();
            }

            String larkMsg = "💳 Liên kết TK: " + "MBBank"
                    + "\nSố TK: " + dto.getBankAccount()
                    + "\nChủ Tài khoản: " + accountBankReceiveEntity.getBankAccountName()
                    + "\nSĐT Xác thực: " + dto.getPhoneAuthenticated()
                    + "\nTrạng thái: Đã liên kết"
                    + "\nSĐT đăng nhập: " + phoneNo
                    + "\nTên đăng nhập: " + fullname.trim()
                    + email
                    + address;
            SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
//            larkUtil.sendMessageToLark(larkMsg, systemSettingEntity.getWebhookUrl());
            googleChatUtil.sendMessageToGoogleChat(larkMsg, systemSettingEntity.getWebhookUrl());
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
//            LarkUtil larkUtil = new LarkUtil();
            GoogleChatUtil googleChatUtil = new GoogleChatUtil();
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
                email = "\nEmail " + accountInformationEntity.getEmail();
            }
            String address = "";
            if (accountInformationEntity.getAddress() != null
                    && !accountInformationEntity.getAddress().trim().isEmpty()) {
                address = "\nĐịa chỉ: " + accountInformationEntity.getAddress();
            }
            String larkMsg = "💳 Liên kết TK: " + "MBBank"
                    + "\nSố TK: " + dto.getBankAccount()
                    + "\nChủ Tài khoản: " + dto.getUserBankName()
                    + "\nSĐT Xác thực: " + dto.getPhoneAuthenticated()
                    + "\nTrạng thái: Đã liên kết"
                    + "\nSĐT đăng nhập: " + phoneNo
                    + "\nTên đăng nhập: " + fullname.trim()
                    + email
                    + address;
            SystemSettingEntity systemSettingEntity = systemSettingService.getSystemSetting();
            googleChatUtil.sendMessageToGoogleChat(larkMsg, systemSettingEntity.getWebhookUrl());
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
                switch (bankTypeEntity.getBankCode()) {
                    case "BIDV":
                        if (accountBankEntity.isAuthenticated()) {
                            String vaNumber = customerVaService.getVaNumberByBankId(accountBankEntity.getId());
                            if (!StringUtil.isNullOrEmpty(vaNumber)) {
                                vietQRGenerateDTO.setBankAccount(vaNumber);
                            } else {
                                vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                            }
                        } else {
                            vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                        }
                        break;
                    default:
                        vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                        break;
                }
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
                switch (bankTypeEntity.getBankCode()) {
                    case "BIDV":
                        if (accountBankEntity.isAuthenticated()) {
                            String vaNumber = customerVaService.getVaNumberByBankId(accountBankEntity.getId());
                            if (!StringUtil.isNullOrEmpty(vaNumber)) {
                                vietQRGenerateDTO.setBankAccount(vaNumber);
                            } else {
                                vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                            }
                        } else {
                            vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                        }
                        break;
                    default:
                        vietQRGenerateDTO.setBankAccount(accountBankEntity.getBankAccount());
                        break;
                }
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
                            // fix
                            dto.setTransCount(Objects.nonNull(transTempCountDTO.getNums()) ? transTempCountDTO.getNums() : 0);
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

                    // khi user đã active key để lưu lại
                    List<ICheckKeyActiveDTO> bankReceiveActiveHistoryEntity =
                            bankReceiveActiveHistoryService.getBankReceiveActiveByUserIdAndBankIdBackUp(userId, item.getBankId());
                    for (ICheckKeyActiveDTO checkKeyActiveDTO : bankReceiveActiveHistoryEntity) {
                        if (Objects.nonNull(checkKeyActiveDTO)) {
                            dto.setTimeActiveKey(checkKeyActiveDTO.getCreateAt());
                            dto.setKeyActive(checkKeyActiveDTO.getKeyActive());
                        } else {
                            dto.setTimeActiveKey(0);
                            dto.setKeyActive("");
                        }
                    }

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
    public ResponseEntity<List<AccountBankActiveKeyResponseDTO>> getAccountBankBackups(
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

            if (!FormatUtil.isListNullOrEmpty(banks)) {
                result = banks.stream().map(item -> {
                    AccountBankActiveKeyResponseDTO dto = new AccountBankActiveKeyResponseDTO();
                    CaiValueDTO valueDTO = caiValueDTOMap.get(item.getBankTypeId());
                    TransTempCountDTO transTempCountDTO = transTempCountDTOMap.get(item.getBankId());
                    if (Objects.nonNull(transTempCountDTO)) {
                        if (transTempCountDTO.getLastTimes() < currentDateTimeUTCPlus7) {
                            dto.setTransCount(0);
                        } else {
//                            dto.setTransCount(transTempCountDTO.getNums());
                            dto.setTransCount(Objects.nonNull(transTempCountDTO.getNums()) ? transTempCountDTO.getNums() : 0);
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
                    dto.setMmsActive(item.getMmsActive());

                    /// khi user đã active key để lưu lại
                    List<ICheckKeyActiveDTO> bankReceiveActiveHistoryEntity =
                            bankReceiveActiveHistoryService.getBankReceiveActiveByUserIdAndBankIdBackUp(userId, item.getBankId());
                    for (ICheckKeyActiveDTO checkKeyActiveDTO : bankReceiveActiveHistoryEntity) {
                        if (Objects.nonNull(checkKeyActiveDTO)) {
                            dto.setTimeActiveKey(checkKeyActiveDTO.getCreateAt());
                            dto.setKeyActive(StringUtil.getValueNullChecker(checkKeyActiveDTO.getKeyActive()));
                        } else {
                            dto.setTimeActiveKey(0);
                            dto.setKeyActive("");
                        }
                    }
                    // set thêm field để biết verify email hay chưa
                    List<EmailVerifyEntity> emailVerify = emailVerifyService.getEmailVerifyByUserId(dto.getUserId());
                    for (EmailVerifyEntity emailVerifyEntity : emailVerify) {
                        if (emailVerifyEntity.isVerify() == false) {
                            dto.setEmailVerified(false);
                            break;
                        }
                        dto.setEmailVerified(true);
                        break;
                    }
                    Thread thread = new Thread(() -> {
                        List<String> IdBankReceiveActiveHistory = bankReceiveActiveHistoryService.getIdBankReceiveActiveByUserIdAndBankId(dto.getUserId(), item.getBankId());
                        for (String id : IdBankReceiveActiveHistory) {
                            if (id != null) {
                                dto.setActiveKey(true);
                            } else {
                                dto.setActiveKey(false);
                            }
                        }
                    });
                    thread.start();

                    dto.setCaiValue(valueDTO.getCaiValue());
                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO();
                    String qr = "";
                    switch (valueDTO.getBankCode()) {
                        case "BIDV":
                            if (item.getAuthenticated()) {
                                vietQRGenerateDTO.setCaiValue(valueDTO.getCaiValue());
                                vietQRGenerateDTO.setBankAccount(item.getVaNumber());
                                qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                            } else {
                                vietQRGenerateDTO.setCaiValue(valueDTO.getCaiValue());
                                vietQRGenerateDTO.setBankAccount(item.getBankAccount());
                                qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                            }
                            break;
                        default:
                            vietQRGenerateDTO.setCaiValue(valueDTO.getCaiValue());
                            vietQRGenerateDTO.setBankAccount(item.getBankAccount());
                            qr = VietQRUtil.generateStaticQR(vietQRGenerateDTO);
                            break;
                    }
                    dto.setQrCode(qr);
                    if (dto.getKeyActive() == null) {
                        dto.setKeyActive("");
                    }
                    return dto;
                }).collect(Collectors.toList());
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.info("getAccountBankBackups: ERROR: " + e.getMessage() + " " + userId);
            System.out.println(e.getMessage());
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
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .header("clientMessageId", clientMessageId.toString())
                .header("Content-Type", "application/x-www-form-urlencoded")
//                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .exchange();

        ClientResponse response = responseMono.block();
        TokenMBResponseDTO tokenResponse = null;

        try {

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
                .fromHttpUrl(EnvironmentUtil.getBankUrl() + "ms/offus/public/account-service/tid/v1.0/list-tid")
//                .fromHttpUrl("https://kietml.click/getTID.php")
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl(EnvironmentUtil.getBankUrl() + "ms/offus/public/account-service/tid/v1.0/list-tid")
//                .baseUrl("https://kietml.click/getTID.php")
                .build();

        Mono<ClientResponse> responseMono = webClient.get()
                .uri(uriComponents.toUri())
//                .contentType(MediaType.APPLICATION_JSON)
                .header("clientMessageId", clientMessageId.toString())
                .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                .header("username", EnvironmentUtil.getUsernameAPI())
                .header("page", "1")
                .header("size", "10")
                .header("Authorization", "Bearer " + token)
                .exchange();

        ClientResponse response = responseMono.block();
        TerminalResponseFlow2 terminalResponse = null;

        String json = response.bodyToMono(String.class).block();

        if (response.statusCode().is2xxSuccessful()) {
            logger.info("getTerminals: RESPONSE: " + json);
            ObjectMapper objectMapper = new ObjectMapper();

            // Configure ObjectMapper to ignore unknown properties
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//            JsonNode rootNode = objectMapper.readTree(json);
//
//            if (rootNode.get("data") != null &&
//                    rootNode.get("data").get("terminals") != null) {
//                String data = rootNode.get("data").get("terminals").asText();
//                try {
//                    List<TerminalMbDTO> terminals = objectMapper.convertValue(
//                            data,
//                            new TypeReference<List<TerminalMbDTO>>() {}
//                    );
//                } catch (JsonProcessingException e) {
//                }
//            }

            try {
                terminalResponse = objectMapper.readValue(json, TerminalResponseFlow2.class);
            } catch (Exception e) {
                logger.error("Error parsing JSON to TerminalResponseFlow2", e);
            }

            System.out.println(terminalResponse);
        } else {
            logger.info("getTerminals: RESPONSE: " + response.statusCode().value() + " - " + json);
        }

        return terminalResponse;
    }


    public TerminalResponseSyncTidDTO syncTerminals(TerminalRequestDTO.TerminalDTO terminalRequestDTO, String token) throws JsonProcessingException {

        UUID clientMessageId = UUID.randomUUID();
        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        Map<String, Object> data2 = new HashMap<>();
        data2.put("terminalId", null);
        data2.put("terminalName", terminalRequestDTO.getTerminalName());
        data2.put("terminalAddress", terminalRequestDTO.getTerminalAddress());
        data2.put("provinceCode", terminalRequestDTO.getProvinceCode());
        data2.put("districtCode", terminalRequestDTO.getDistrictCode());
        data2.put("wardsCode", terminalRequestDTO.getWardsCode());
        data2.put("mccCode", terminalRequestDTO.getMccCode());
        data2.put("fee", terminalRequestDTO.getFee());
        data2.put("bankCode", terminalRequestDTO.getBankCode());
        data2.put("bankCodeBranch", terminalRequestDTO.getBankCodeBranch());
        data2.put("bankAccountNumber", terminalRequestDTO.getBankAccountNumber());
        data2.put("bankAccountName", terminalRequestDTO.getBankAccountName());
        data2.put("bankCurrencyCode", terminalRequestDTO.getBankCurrencyCode());
        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(data2);
        data.put("terminals", mapList);
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(EnvironmentUtil.getBankUrl() + "ms/offus/public/account-service/tid/v1.0/synchronize")
//                .fromHttpUrl("https://kietml.click/syncTID.php")
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl(EnvironmentUtil.getBankUrl() + "ms/offus/public/account-service/tid/v1.0/synchronize")
//                .baseUrl("https://kietml.click/syncTID.php")
                .build();

        Mono<ClientResponse> responseMono = webClient.post()
                .uri(uriComponents.toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .header("clientMessageId", clientMessageId.toString())
                .header("secretKey", EnvironmentUtil.getSecretKeyAPI())
                .header("username", EnvironmentUtil.getUsernameAPI())
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(data))
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

    public String checkAndModifyString(String target, List<String> stringList) {
        String modifiedString = target;
        int counter = 2;

        while (stringList.contains(modifiedString)) {
            modifiedString = target + counter;
            counter++;
        }

        return modifiedString;
    }

    public String checkAndModifyStringAddress(String target, List<String> stringList) {
        String modifiedString = target;
        int counter = 2;

        while (stringList.contains(modifiedString)) {
            modifiedString = target + " - " + String.format("%02d", counter);
            counter++;
        }

        return modifiedString;
    }

    @PostMapping("check-log/request_otp_bank")
    public ResponseEntity<ResponseMessageDTO> requestOTPCheckLog(@Valid @RequestBody RequestLinkedBankDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            if (dto != null) {
                if (dto.getBankCode().trim().equals("MB")) {
                    RequestBankDTO requestBankDTO = new RequestBankDTO();
                    requestBankDTO.setNationalId(dto.getNationalId());
                    requestBankDTO.setAccountNumber(dto.getAccountNumber());
                    requestBankDTO.setAccountName(dto.getAccountName());
                    requestBankDTO.setPhoneNumber(dto.getPhoneNumber());
                    requestBankDTO.setApplicationType(dto.getApplicationType());
                    ResponseMessageDTO responseMessageDTO = requestLinkedMBOTPV2(requestBankDTO);
                    result = responseMessageDTO;
                    if (result.getStatus().equals("SUCCESS")) {
                        httpStatus = HttpStatus.OK;
                    } else {
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else if (dto.getBankCode().trim().equals("BIDV")) {
                    Long customerVaLength = customerVaService.getCustomerVaLength() + 1;
                    String merchantName = "";
                    String rawMerchantName = dto.getAccountName().replaceAll(" ", "");
                    if (!StringUtil.isNullOrEmpty(rawMerchantName)) {
                        // GENERATE MERCHANT NAME
                        int size = (rawMerchantName.length() / 3) * 2;
                        merchantName = (rawMerchantName.substring(size) + RandomCodeUtil.generateOTP(3)).toUpperCase();
                    } else {
                        merchantName = rawMerchantName.toUpperCase();
                    }
                    String merchantId = CustomerVaUtil.generateMerchantId(merchantName, customerVaLength);
                    String checkExistedMerchantId = customerVaService.checkExistedMerchantId(merchantId);
                    RequestCustomerVaDTO requestCustomerVaDTO = new RequestCustomerVaDTO();
                    requestCustomerVaDTO.setBankCode(dto.getBankCode());
                    requestCustomerVaDTO.setBankAccount(dto.getAccountNumber());
                    requestCustomerVaDTO.setMerchantName(merchantName);
                    requestCustomerVaDTO.setUserBankName(dto.getAccountName());
                    requestCustomerVaDTO.setNationalId(dto.getNationalId());
                    requestCustomerVaDTO.setPhoneAuthenticated(dto.getPhoneNumber());
                    result = CustomerVaUtil.requestCustomerVaV2(requestCustomerVaDTO, merchantId, "1",
                            customerVaLength, checkExistedMerchantId);
//					result = requestLinkedBIDVOTP(dto);
                    if (result.getStatus().equals("SUCCESS")) {
                        httpStatus = HttpStatus.OK;
                    } else {
                        httpStatus = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    logger.error("requestLinkedBankOTP: INVALID BANK CODE");
                    System.out.println("requestLinkedBankOTP: INVALID BANK CODE");
                    result = new ResponseMessageDTO("FAILED", "E109");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } else {
                logger.error("requestLinkedBankOTP: INVALID REQUEST BODY");
                System.out.println("requestLinkedBankOTP: INVALID REQUEST BODY");
                result = new ResponseMessageDTO("FAILED", "E46");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("Error at requestOTP: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    private ResponseMessageDTO requestLinkedMBOTPV2(RequestBankDTO dto) {
        ResponseMessageDTO result = null;
        try {
            UUID clientMessageId = UUID.randomUUID();
            Map<String, Object> data = new HashMap<>();
            data.put("nationalId", dto.getNationalId());
            data.put("accountNumber", dto.getAccountNumber());
            data.put("accountName", dto.getAccountName());
            data.put("phoneNumber", dto.getPhoneNumber());
            data.put("authenType", "SMS");
            data.put("applicationType", dto.getApplicationType());
            data.put("transType", "DC");
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(EnvironmentUtil.getBankUrl()
                            + "ms/push-mesages-partner/v1.0/bdsd/subscribe/request")
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(
                            EnvironmentUtil.getBankUrl()
                                    + "ms/push-mesages-partner/v1.0/bdsd/subscribe/request")
                    .build();
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("clientMessageId", clientMessageId.toString())
                    .header("transactionId", RandomCodeUtil.generateRandomUUID())
                    .header("Authorization", "Bearer " + getMBBankToken().getAccess_token())
                    .body(BodyInserters.fromValue(data))
                    .exchange();
            ClientResponse response = responseMono.block();
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                logger.info("Response requestOTP: " + json);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                String requestId = rootNode.get("data").get("requestId").asText();
                result = new ResponseMessageDTO("SUCCESS",
                        requestId);
            } else {
                ConfirmRequestFailedBankDTO confirmRequestBankDTO = response.bodyToMono(
                                ConfirmRequestFailedBankDTO.class)
                        .block();
                LocalDateTime currentDateTime = LocalDateTime.now();
                logger.info("Response requestOTP error: Request Body: " + dto.toString() + " at: " + System.currentTimeMillis());
                logger.error("Response requestOTP error: client msg id: " + clientMessageId.toString() + " - "
                        + confirmRequestBankDTO.getSoaErrorCode() + "-"
                        + confirmRequestBankDTO.getSoaErrorDesc() + " at "
                        + currentDateTime.toEpochSecond(ZoneOffset.UTC));
                String status = "FAILED";
                String message = confirmRequestBankDTO.toString();
                result = new ResponseMessageDTO(status, message);
            }
        } catch (Exception e) {
            logger.error("requestLinkedMBOTP: ERROR: " + e.toString());
            result = new ResponseMessageDTO("FAILED", "E05 - " + e.getMessage());
        }
        return result;
    }

    // get token MB Bank
    private TokenProductBankDTO getMBBankToken() {
        TokenProductBankDTO result = null;
        try {
            String key = EnvironmentUtil.getUserBankAccess() + ":" + EnvironmentUtil.getPasswordBankAccess();
            String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());
            UriComponents uriComponents = UriComponentsBuilder
                    .fromHttpUrl(EnvironmentUtil.getBankUrl() + "oauth2/v1/token")
                    .buildAndExpand(/* add url parameter here */);
            WebClient webClient = WebClient.builder()
                    .baseUrl(EnvironmentUtil.getBankUrl()
                            + "oauth2/v1/token")
                    .build();
            // Call POST API
            TokenProductBankDTO response = webClient.method(HttpMethod.POST)
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "Basic " + encodedKey)
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                    .exchange()
                    .flatMap(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return clientResponse.bodyToMono(TokenProductBankDTO.class);
                        } else {
                            clientResponse.body((clientHttpResponse, context) -> {
                                logger.info(clientHttpResponse.getBody().collectList().block().toString());
                                return clientHttpResponse.getBody();
                            });
                            return null;
                        }
                    })
                    .block();
            result = response;
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }

    @GetMapping("/list-account-bank")
    public ResponseEntity<Object> getListBankAccount(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String value,
            @RequestParam int page,
            @RequestParam int size) {
        Object result;
        HttpStatus httpStatus;
        PageResDTO pageResDTO = new PageResDTO();

        try {
            int totalElement;
            int offset = (page - 1) * size;
            List<BankAccountResponseDTO> data;
            // List<IBankAccountResponseDTO> data;

            if (type == null || value == null || value.isEmpty() || type == 9) {
                data = accountBankReceiveService.getAllBankAccount(offset, size);
                totalElement = accountBankReceiveService.countAllBankAccounts();
            } else {
                switch (type) {
                    case 1:
                        data = accountBankReceiveService.getBankAccountsByAccounts(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByAccount(value);
                        break;
                    case 2:
                        data = accountBankReceiveService.getBankAccountsByAccountNames(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByAccountName(value);
                        break;
                    case 3:
                        data = accountBankReceiveService.getBankAccountsByPhoneAuthenticated(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByPhoneAuthenticated(value);
                        break;
                    case 4:
                        data = accountBankReceiveService.getBankAccountsByNationalIds(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByNationalId(value);
                        break;
                    default:
                        data = new ArrayList<>();
                        totalElement = 0;
                        break;
                }
            }

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
            logger.error("BankAccountController: ERROR: getListBankAccount: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/new-list-bank-account")
    public ResponseEntity<Object> getNewListBankAccount(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String value,
            @RequestParam(required = false) Integer searchType,  // Thêm searchType để biết tìm kiếm theo tiêu chí gì
            @RequestParam int page,
            @RequestParam int size) {
        Object result;
        HttpStatus httpStatus;
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        AdminExtraBankDTO extraBankDTO = new AdminExtraBankDTO();
        DataDTO dataDTO = new DataDTO(extraBankDTO);

        try {
            int totalElement;
            int offset = (page - 1) * size;
            List<BankAccountResponseDTO> data;
            IAdminExtraBankDTO extraBankDTO1 = null;

            // Xác định tìm kiếm theo tiêu chí nào
            if (type == 1) { // Lọc theo thời gian kích hoạt DV
                data = accountBankReceiveService.getBankAccountsByValidFeeToAndIsValidServiceWithSearch(searchType, value, offset, size);
                totalElement = accountBankReceiveService.countBankAccountsByValidFeeToAndIsValidServiceWithSearch(searchType, value);
            } else if (type == 2) { // Lọc theo thời gian thêm gần đây
                data = accountBankReceiveService.getBankAccountsByTimeCreateWithSearch(searchType, value, offset, size);
                totalElement = accountBankReceiveService.countBankAccountsByTimeCreateWithSearch(searchType, value);
            } else { // Tìm kiếm trực tiếp theo các trường khác
                switch (type) {
                    case 3: // Tìm kiếm theo TKNH
                        data = accountBankReceiveService.getBankAccountsByAccounts(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByAccount(value);
                        break;
                    case 4: // Tìm kiếm theo Chủ TK
                        data = accountBankReceiveService.getBankAccountsByAccountNames(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByAccountName(value);
                        break;
                    case 5: // Tìm kiếm theo SĐT
                        data = accountBankReceiveService.getBankAccountsByPhoneAuthenticated(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByPhoneAuthenticated(value);
                        break;
                    case 6: // Tìm kiếm theo CMND
                        data = accountBankReceiveService.getBankAccountsByNationalIds(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByNationalId(value);
                        break;
                    default: // Nếu không có bộ lọc, mặc định tìm kiếm theo TKNH
                        data = accountBankReceiveService.getBankAccountsByAccounts(value, offset, size);
                        totalElement = accountBankReceiveService.countBankAccountsByAccount(value);
                        break;
                }
            }

            // Thống kê nhanh (extraData)
            extraBankDTO1 = accountBankReceiveService.getExtraBankDataForAllTime();
            if (extraBankDTO1 != null) {
                extraBankDTO.setOverdueCount(extraBankDTO1.getOverdueCount());
                extraBankDTO.setNearlyExpireCount(extraBankDTO1.getNearlyExpireCount());
                extraBankDTO.setValidCount(extraBankDTO1.getValidCount());
                extraBankDTO.setNotRegisteredCount(extraBankDTO1.getNotRegisteredCount());
            }

            PageDTO pageDTO = new PageDTO();
            pageDTO.setTotalPage(StringUtil.getTotalPage(totalElement, size));
            pageDTO.setTotalElement(totalElement);
            pageDTO.setSize(size);
            pageDTO.setPage(page);
            pageResponseDTO.setMetadata(pageDTO);
            dataDTO.setItems(data);
            dataDTO.setExtraData(extraBankDTO);
            pageResponseDTO.setData(dataDTO);
            result = pageResponseDTO;
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            logger.error("BankAccountController: ERROR: getListBankAccount: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(result, httpStatus);
    }


}
