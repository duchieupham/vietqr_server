package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.service.TerminalBankReceiveService;
import com.vietqr.org.service.TerminalService;
import com.vietqr.org.service.TransactionTerminalTempService;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalStatisticController {

    private static final Logger logger = Logger.getLogger(TerminalStatisticController.class);

    @Autowired
    private TransactionTerminalTempService transactionTerminalTempService;

    @Autowired
    private TerminalBankReceiveService terminalBankReceiveService;

    @Autowired
    private TerminalService terminalService;

    @GetMapping("merchant/overview")
    public ResponseEntity<StatisticMerchantDTO> getStatisticTerminal(
            @RequestParam String userId,
            @RequestParam String merchantId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        StatisticMerchantDTO result = null;
        HttpStatus httpStatus = null;
        List<String> listTerminalCode = new ArrayList<>();
        try {
            // old code
//            tempCode = terminalService.getAllCodeByUserId(userId);
//            listTerminalCode.addAll(tempCode);
//            Set<String> uniqueCodes = new HashSet<>(listTerminalCode);
//            listTerminalCode = new ArrayList<>(uniqueCodes);

            // new code
            if (merchantId != null && !merchantId.isEmpty()) {
                listTerminalCode = terminalService.getAllCodeByMerchantId(merchantId, userId);
            } else {
                listTerminalCode = terminalService.getAllCodeByUserIdOwner(userId);
            }
            Set<String> uniqueCodes = new HashSet<>(listTerminalCode);
            listTerminalCode = new ArrayList<>(uniqueCodes);


            if (!listTerminalCode.isEmpty()) {
                List<String> listCode = terminalBankReceiveService.getTerminalCodeByMainTerminalCodeList(listTerminalCode);
                listCode.addAll(listTerminalCode);
                IStatisticMerchantDTO dto = transactionTerminalTempService.getStatisticMerchantByDate(listCode, fromDate, toDate);
                if (dto != null) {
                    result = new StatisticMerchantDTO();
                    result.setDate(dto.getDate());
                    result.setTotalTrans(dto.getTotalTrans());
                    result.setTotalAmount(dto.getTotalAmount());
                    result.setMerchantId("");
                    result.setDate(DateTimeUtil.removeTimeInDateTimeString(fromDate));
                    result.setMerchantName("");
                    result.setVsoCode("");
                    int countTerminal = listTerminalCode.size();
                    result.setTotalTerminal(countTerminal);
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
                                (double) (dto.getTotalAmount() - revenueTerminalDTOPrevDate.getTotalAmount())
                                        / revenueTerminalDTOPrevDate.getTotalAmount();
                        result.setratePreviousDate((int) (revGrowthPrevDate * 100));
                    } else {
                        result.setratePreviousDate(0);
                    }
                    RevenueTerminalDTO revenueTerminalDTOPrevMonth = transactionTerminalTempService
                            .getTotalTranByUserIdAndTimeBetween(
                                    listCode, DateTimeUtil.getPrevMonthAsString(), DateTimeUtil.getPrevMonthAsString());

                    if (revenueTerminalDTOPrevMonth != null && revenueTerminalDTOPrevMonth.getTotalTrans() != 0 && revenueTerminalDTOPrevMonth.getTotalAmount() != 0) {
                        double revGrowthPrevMonth = revenueTerminalDTOPrevMonth.getTotalAmount() == 0 ? 0 :
                                (double) ((dto.getTotalAmount() - revenueTerminalDTOPrevMonth.getTotalAmount())
                                        / revenueTerminalDTOPrevMonth.getTotalAmount());
                        result.setRatePreviousMonth((int) (revGrowthPrevMonth * 100));
                    } else {
                        result.setRatePreviousMonth(0);
                    }
                }
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.OK;
            }

        } catch (Exception e) {
            logger.error("Get statistic terminal error", e);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant/statistic")
    public ResponseEntity<List<StatisticTerminalOverViewDTO>> getStatisticEveryTerminalByUserId(
            @RequestParam String userId,
            @RequestParam String merchantId,
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam int offset) {
        List<StatisticTerminalOverViewDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<IStatisticTerminalOverViewDTO> dtos = new ArrayList<>();
            if (merchantId != null && !merchantId.isEmpty()) {
                dtos = terminalService
                        .getListTerminalByUserId(userId, offset);
            } else {
                dtos = terminalService
                        .getListTerminalByMerchantId(merchantId, userId, offset);
            }

//            if (dtos != null && dtos.size() < 10) {
//                int totalTerminalOwner = terminalService.countNumberOfTerminalByUserIdOwner(userId);
//                List<IStatisticTerminalOverViewDTO> dtos1 = terminalService
//                        .getListTerminalByUserIdNotOwner(userId, Math.abs(offset - totalTerminalOwner) % 10, 10 - dtos.size());
//                dtos.addAll(dtos1);
//            }
            if (dtos != null && !dtos.isEmpty()) {
                result = dtos.stream().map(item -> {
                            StatisticTerminalOverViewDTO dto = new StatisticTerminalOverViewDTO();
                            dto.setTerminalId(item.getTerminalId());
                            dto.setTerminalName(item.getTerminalName());
                            dto.setTerminalCode(item.getTerminalCode());
                            dto.setTerminalAddress(item.getTerminalAddress());
                            List<String> listCode = new ArrayList<>();
                            listCode = terminalBankReceiveService.getSubTerminalCodeByTerminalCode(item.getTerminalCode());
                            listCode.add(item.getTerminalCode());
                            RevenueTerminalDTO revGrowthToday = transactionTerminalTempService.getTotalTranByTerminalCodeAndTimeBetween(
                                    listCode, DateTimeUtil.removeTimeInDateTimeString(fromDate), DateTimeUtil.removeTimeInDateTimeString(toDate));
                            dto.setTotalTrans(revGrowthToday.getTotalTrans());
                            dto.setTotalAmount(revGrowthToday.getTotalAmount());
                            LocalDateTime now = LocalDateTime.now();
                            long time = now.toEpochSecond(ZoneOffset.UTC);
                            // + 7 xem đã qua ngày chưa;
                            time += DateTimeUtil.GMT_PLUS_7_OFFSET;
                            // đổi sang DateTime - đây là thời gian hiện tại
                            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
                            // đây là thời gian bắt ầu ngày hiện tại
                            LocalDateTime startOfDay = localDateTime.toLocalDate().atStartOfDay();
                            RevenueTerminalDTO revGrowthPrevDate = transactionTerminalTempService
                                    .getTotalTranByTerminalCodeAndTimeBetweenWithCurrentTime(
                                            listCode, startOfDay.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND,
                                            localDateTime.toEpochSecond(ZoneOffset.UTC) - DateTimeUtil.A_DAY_TO_SECOND);
                            if (revGrowthPrevDate != null && revGrowthPrevDate.getTotalAmount() != 0 && revGrowthPrevDate.getTotalTrans() != 0) {
                                double revGrowthPrevDateNum = revGrowthPrevDate.getTotalAmount() == 0 ? 0 :
                                        (double) (dto.getTotalAmount() - revGrowthPrevDate.getTotalAmount())
                                                / revGrowthPrevDate.getTotalAmount();
                                dto.setRatePreviousDate((int) (revGrowthPrevDateNum * 100));
                            } else if (revGrowthPrevDate != null && revGrowthPrevDate.getTotalAmount() == 0 && dto.getTotalAmount() != 0) {
                                dto.setRatePreviousDate(100);
                            } else {
                                dto.setRatePreviousDate(0);
                            }
                            return dto;
                        }
                ).collect(Collectors.toList());
            }
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get statistic every terminal error", e);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant-statistic/all")
    public ResponseEntity<List<StatisticTerminalDTO>> getStatisticEveryTerminal(
            @RequestParam String userId,
            @RequestParam String merchantId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<StatisticTerminalDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<IStatisticTerminalDTO> dtos = transactionTerminalTempService
                    .getStatisticMerchantByDateEveryHour(userId, fromDate, toDate);
            result = dtos.stream().collect(ArrayList::new, (list, dto) -> {
                list.add(new StatisticTerminalDTO(dto.getTotalTrans(), dto.getTotalAmount(), dto.getTimeDate()));
            }, ArrayList::addAll);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get statistic every terminal error", e);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("merchant-statistic/top-terminal")
    public ResponseEntity<List<TopTerminalDTO>> getTop5Terminal(
            @RequestParam String userId,
            @RequestParam String merchantId,
            @RequestParam int pageSize,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<TopTerminalDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            List<ITopTerminalDTO> dtos = transactionTerminalTempService
                    .getTopTerminalByDate(userId, fromDate, toDate, pageSize);
            result = dtos.stream().map(item -> {
                        TopTerminalDTO dto = new TopTerminalDTO();
                        dto.setTerminalId(item.getTerminalId());
                        dto.setTerminalName(item.getTerminalName());
                        dto.setTerminalCode(item.getTerminalCode());
                        dto.setTerminalAddress(item.getTerminalAddress());
                        dto.setTotalAmount(item.getTotalAmount());
                        dto.setDate(DateTimeUtil.removeTimeInDateTimeString(fromDate));
                        return dto;
                    }
            ).collect(Collectors.toList());
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get top terminal error: ", e);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
