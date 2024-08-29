package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.EmailVerifyEntity;
import com.vietqr.org.service.*;
import com.vietqr.org.util.DateTimeUtil;
import com.vietqr.org.util.FormatUtil;
import com.vietqr.org.util.StringUtil;
import com.vietqr.org.util.VietQRUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/status")
public class HeathCheckController {

    @Autowired
    private TransReceiveTempService transReceiveTempService;

    @Autowired
    private AccountBankReceiveShareService accountBankReceiveShareService;

    @Autowired
    private CaiBankService caiBankService;

    @Autowired
    private EmailVerifyService emailVerifyService;

    @Autowired
    private BankReceiveActiveHistoryService bankReceiveActiveHistoryService;
    @GetMapping
    public ResponseEntity<ResponseMessageDTO> heathCheckResponse() {
        ResponseMessageDTO result = new ResponseMessageDTO("SUCCESS", "");
        HttpStatus httpStatus = HttpStatus.OK;
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("/performance")
    public ResponseEntity<ResponseObjectDTO> heathCheckPerformance() {
        ResponseObjectDTO result = null;
        HttpStatus httpStatus = null;
        try {
            String userId = "24889b0e-4adc-4453-b20c-9d5e9946a820";
            long time = System.currentTimeMillis();
            List<AccountBankActiveKeyResponseDTO> data = new ArrayList<>();
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
                data = banks.stream().map(item -> {
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
                        if (!emailVerifyEntity.isVerify()) {
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
            result = new ResponseObjectDTO(System.currentTimeMillis() + "", data);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            result = new ResponseObjectDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
