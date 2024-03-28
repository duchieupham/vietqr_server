package com.vietqr.org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.service.*;
import com.vietqr.org.util.EnvironmentUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class MerchantTransReceiveRequestController {
    private static final Logger logger = Logger.getLogger(MerchantTransReceiveRequestController.class);

    @Autowired
    private TransactionTerminalTempService transactionTerminalTempService;

    @Autowired
    private TransactionReceiveHistoryService transactionReceiveHistoryService;

    @Autowired
    private TransactionReceiveService transactionReceiveService;

    @Autowired
    private MerchantMemberRoleService merchantMemberRoleService;

    @Autowired
    private TransReceiveRequestMappingService transReceiveRequestMappingService;

    @PostMapping("transaction-request")
    public ResponseEntity<ResponseMessageDTO> mapTransactionRequestToTerminal(@RequestBody @Valid MapRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> rolesAccept = new ArrayList<>();
            rolesAccept.add(EnvironmentUtil.getRequestReceiveMerchantRoleId());
            rolesAccept.add(EnvironmentUtil.getRequestReceiveTerminalRoleId());
            rolesAccept.add(EnvironmentUtil.getAdminRoleId());
            String roles = String.join("|", rolesAccept);
            String isExist = merchantMemberRoleService.checkMemberHaveRole(dto.getUserId(),
                    roles);
            if (isExist == null || isExist.trim().isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E113");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                        .getTransactionReceiveById(dto.getTransactionId());
                if (transactionReceiveEntity != null) {
                    TransReceiveRequestMappingEntity entity = new TransReceiveRequestMappingEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setMerchantId(dto.getMerchantId());
                    entity.setTerminalId(dto.getTerminalId());
                    entity.setTransactionReceiveId(dto.getTransactionId());
                    entity.setUserId(dto.getUserId());
                    entity.setRequestType(dto.getRequestType());
                    entity.setRequestValue(dto.getRequestValue());
                    entity.setTimeCreated(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                    entity.setTimeApproved(0);
                    entity.setStatus(0);

                    transactionReceiveEntity.setTransStatus(1);

                    transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                    transReceiveRequestMappingService.insert(entity);
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                } else {
                    result = new ResponseMessageDTO("FAILED", "E114");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }

        } catch (Exception e) {
            logger.error("mapTransactionRequestToTerminal: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction-request")
    public ResponseEntity<Object> getListTransactionRequest(@RequestParam(value = "page") int page,
                                                            @RequestParam(value = "size") int size,
                                                            @RequestParam(value = "type") int type,
                                                            @RequestParam(value = "value") String value,
                                                            @RequestParam(value = "fromDate") String fromDate,
                                                            @RequestParam(value = "toDate") String toDate,
                                                            @RequestParam(value = "bankId") String bankId,
                                                            @RequestParam(value = "userId") String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            PageResultDTO dto = new PageResultDTO();
            int totalPage = 1;
            List<String> rolesAccept = new ArrayList<>();
            rolesAccept.add(EnvironmentUtil.getRequestReceiveMerchantRoleId());
            rolesAccept.add(EnvironmentUtil.getRequestReceiveTerminalRoleId());
            rolesAccept.add(EnvironmentUtil.getAdminRoleId());
            String roles = String.join("|", rolesAccept);
            String isExist = merchantMemberRoleService.checkMemberHaveRole(userId,
                    roles);
            if (isExist != null && !isExist.trim().isEmpty()) {

                List<TransactionReceiveAdminListDTO> listTrans = transactionReceiveService
                        .getTransactionReceiveWithRequest(bankId, fromDate, toDate, (page - 1) * size, size);
                int total = transactionReceiveService.countTransactionReceiveWithRequest(bankId, fromDate, toDate);
                if (type == 7) {
                    listTrans = transactionReceiveService
                            .getTransactionReceiveWithRequestById(bankId, fromDate, toDate, (page - 1) * size, size, value);
                    total = 1;
                }
                List<String> listTransId = listTrans.stream().map(TransactionReceiveAdminListDTO::getId)
                        .collect(Collectors.toList());
                List<TransRequestDTO> listRequests = transReceiveRequestMappingService
                        .getTransactionReceiveRequest(listTransId);

                Map<String, List<TransRequestDTO>> terminalBanksMap = listRequests.stream()
                        .collect(Collectors.groupingBy(TransRequestDTO::getTransactionId));
                List<TransactionRelatedRequestDTO> listTransRequest = listTrans.stream().map(item -> {
                    TransactionRelatedRequestDTO trans = new TransactionRelatedRequestDTO();
                    trans.setId(item.getId());
                    trans.setBankAccount(item.getBankAccount());
                    trans.setAmount(item.getAmount());
                    trans.setBankId(item.getBankId());
                    trans.setContent(item.getContent());
                    trans.setOrderId(item.getOrderId());
                    trans.setReferenceNumber(item.getReferenceNumber());
                    trans.setStatus(item.getStatus());
                    trans.setTimeCreated(item.getTimeCreated());
                    trans.setTimePaid(item.getTimePaid());
                    trans.setTransType(item.getTransType());
                    trans.setType(item.getType());
                    trans.setUserBankName(item.getUserBankName());
                    trans.setBankShortName(item.getBankShortName());
                    trans.setTerminalCode(item.getTerminalCode());
                    trans.setNote(item.getNote());
                    trans.setRequests(terminalBanksMap
                            .getOrDefault(item.getId(), new ArrayList<>()));
                    trans.setTotalRequest(trans.getRequests().size());
                    return trans;
                }).collect(Collectors.toList());
                dto.setItems(listTransRequest);
                dto.setTotalElement(total);
                if (total % size == 0) {
                    totalPage = total / size;
                } else {
                    totalPage = total / size + 1;
                }
                dto.setPage(page);
                dto.setSize(size);
                dto.setTotalPage(totalPage);
                result = dto;
                httpStatus = HttpStatus.OK;
            } else {
                result = new ResponseMessageDTO("FAILED", "E113");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            logger.error("getListTransactionRequest: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("transaction-request/total")
    public ResponseEntity<Object> getTotalRequest(@RequestParam(value = "type") int type,
                                                  @RequestParam(value = "value") String value,
                                                  @RequestParam(value = "fromDate") String fromDate,
                                                  @RequestParam(value = "toDate") String toDate,
                                                  @RequestParam(value = "bankId") String bankId,
                                                  @RequestParam(value = "userId") String userId) {
        Object result = null;
        HttpStatus httpStatus = null;
        try {
            int total = transactionReceiveService.countTransactionReceiveWithRequest(bankId, fromDate, toDate);
            result = new TotalResquestDTO(total);
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            logger.error("getListTransactionRequest: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    // approve
    @PostMapping("transaction-approve")
    public ResponseEntity<ResponseMessageDTO> requestMapTransactionDetail(@RequestBody @Valid ApproveTransRequestDTO dto) {
        ResponseMessageDTO result = null;
        HttpStatus httpStatus = null;
        try {
            List<String> rolesAccept = new ArrayList<>();
            rolesAccept.add(EnvironmentUtil.getRequestReceiveMerchantRoleId());
            rolesAccept.add(EnvironmentUtil.getRequestReceiveTerminalRoleId());
            rolesAccept.add(EnvironmentUtil.getAdminRoleId());
            String roles = String.join("|", rolesAccept);
            String isExist = merchantMemberRoleService.checkMemberHaveRole(dto.getUserId(),
                    roles);
            if (isExist == null || isExist.trim().isEmpty()) {
                result = new ResponseMessageDTO("FAILED", "E115");
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                ObjectMapper mapper = new ObjectMapper();
                LocalDateTime now = LocalDateTime.now();
                long currentTime = now.toEpochSecond(ZoneOffset.UTC);

                TransactionUpdateTerminalDTO data1 = new TransactionUpdateTerminalDTO();
                TransactionUpdateTerminalDTO data2 = new TransactionUpdateTerminalDTO();
                TransactionReceiveEntity transactionReceiveEntity = transactionReceiveService
                        .getTransactionReceiveById(dto.getTransactionId());
                TransReceiveRequestMappingEntity transReceiveRequestMappingEntity = transReceiveRequestMappingService
                        .findById(dto.getRequestId());
                if (dto.getStatus() == 1) {
                    transReceiveRequestMappingEntity.setStatus(1);
                    if (transactionReceiveEntity == null) {
                        result = new ResponseMessageDTO("FAILED", "E46");
                        httpStatus = HttpStatus.BAD_REQUEST;
                    } else {
                        if (transactionReceiveEntity.getType() == 0) {
                            //update terminal code
                            transactionReceiveService.updateTransactionReceiveTerminal(dto.getTransactionId(),
                                    transReceiveRequestMappingEntity.getRequestValue(), 0);
                            data1.setTransactionId(dto.getTransactionId());
                            data1.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                            data1.setType(transactionReceiveEntity.getType());
                            data1.setTransStatus(transactionReceiveEntity.getTransStatus());
                            data2.setTransactionId(dto.getTransactionId());
                            data2.setTerminalCode(transReceiveRequestMappingEntity.getRequestValue());
                            data2.setType(transactionReceiveEntity.getType());
                            data2.setTransStatus(2);
                        } else {
                            // update terminal code
                            transactionReceiveService.updateTransactionReceiveTerminal(dto.getTransactionId(),
                                    transReceiveRequestMappingEntity.getRequestValue(), 1);
                            data1.setTransactionId(dto.getTransactionId());
                            data1.setTerminalCode(transactionReceiveEntity.getTerminalCode());
                            data1.setType(transactionReceiveEntity.getType());
                            data1.setTransStatus(transactionReceiveEntity.getTransStatus());
                            data2.setTransactionId(dto.getTransactionId());
                            data2.setTerminalCode(transReceiveRequestMappingEntity.getRequestValue());
                            data2.setType(1);
                            data2.setTransStatus(2);
                        }

                        // insert for statistic
                        TransactionTerminalTempEntity transactionTerminalTemp = transactionTerminalTempService
                                .getTempByTransactionId(dto.getTransactionId());
                        if (transactionTerminalTemp != null) {
                            transactionTerminalTemp
                                    .setTerminalCode(transReceiveRequestMappingEntity.getRequestValue());
                        } else {
                            transactionTerminalTemp = new TransactionTerminalTempEntity();
                            transactionTerminalTemp.setId(UUID.randomUUID().toString());
                            transactionTerminalTemp.setTransactionId(dto.getTransactionId());
                            transactionTerminalTemp.setTerminalCode(transReceiveRequestMappingEntity.getRequestValue());
                            transactionTerminalTemp.setTime(transactionReceiveEntity.getTime());
                            transactionTerminalTemp.setAmount(transactionReceiveEntity.getAmount());
                        }

                        TransactionReceiveHistoryEntity transactionReceiveHistoryEntity = new TransactionReceiveHistoryEntity();
                        transactionReceiveHistoryEntity.setId(UUID.randomUUID().toString());
                        transactionReceiveHistoryEntity.setTransactionReceiveId(dto.getTransactionId());
                        transactionReceiveHistoryEntity.setUserId(dto.getUserId());
                        transactionReceiveHistoryEntity.setTimeUpdated(currentTime);
                        transactionReceiveHistoryEntity.setType(2);
                        transactionReceiveHistoryEntity.setData3(dto.getRequestId());
                        transactionReceiveHistoryEntity.setData2(mapper.writeValueAsString(data2));
                        transactionReceiveHistoryEntity.setData1(mapper.writeValueAsString(data1));
                        // update request already approve:
                        transReceiveRequestMappingEntity.setTimeApproved(currentTime);
                        // update transaction
                        transactionReceiveEntity.setTransStatus(2);

                        transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                        transactionTerminalTempService.insertTransactionTerminal(transactionTerminalTemp);
                        transactionReceiveHistoryService.insertTransactionReceiveHistory(transactionReceiveHistoryEntity);
                        httpStatus = HttpStatus.OK;
                    }
                } else {
                    transReceiveRequestMappingEntity.setStatus(2);  // rejected
                    List<String> strings = new ArrayList<>();
                    strings.add(dto.getTransactionId());
                    List<TransRequestDTO> listRequests = transReceiveRequestMappingService
                            .getTransactionReceiveRequest(strings);
                    if (transactionReceiveEntity != null && listRequests.size() <= 1) {
                        transactionReceiveEntity.setTransStatus(0);
                        transactionReceiveService.insertTransactionReceive(transactionReceiveEntity);
                    }
                    transReceiveRequestMappingService.insert(transReceiveRequestMappingEntity);
                    result = new ResponseMessageDTO("SUCCESS", "");
                    httpStatus = HttpStatus.OK;
                }
            }

        } catch (Exception e) {
            logger.error("requestMapTransactionDetail: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
            result = new ResponseMessageDTO("FAILED", "E05");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
