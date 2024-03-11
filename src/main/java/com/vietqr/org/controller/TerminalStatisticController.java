package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.service.TerminalService;
import com.vietqr.org.service.TransactionTerminalTempService;
import com.vietqr.org.util.DateTimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalStatisticController {

    private static final Logger logger = Logger.getLogger(TerminalStatisticController.class);

    @Autowired
    private TransactionTerminalTempService transactionTerminalTempService;

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
        try {
            IStatisticMerchantDTO dto = transactionTerminalTempService.getStatisticMerchantByDate(userId, fromDate, toDate);
            if (dto != null) {
                result = new StatisticMerchantDTO();
                result.setDate(dto.getDate());
                result.setTotalTrans(dto.getTotalTrans());
                result.setTotalAmount(dto.getTotalAmount());
                result.setMerchantId("");
                result.setDate(DateTimeUtil.removeTimeInDateTimeString(fromDate));
                result.setMerchantName("");
                result.setVsoCode("");
                int countTerminal = terminalService.countNumberOfTerminalByUserId(userId);
                result.setTotalTerminal(countTerminal);

                RevenueTerminalDTO revenueTerminalDTOPrevDate = transactionTerminalTempService
                        .getTotalTranByUserIdAndTimeBetweenWithCurrentTime(
                                userId, DateTimeUtil.getPrevDateAsString(),
                                DateTimeUtil.getCurrentDateTimeAsNumber() - 86400);
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
                                userId, DateTimeUtil.getPrevMonthAsString(), DateTimeUtil.getPrevMonthAsString());

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
            List<IStatisticTerminalOverViewDTO> dtos = transactionTerminalTempService
                    .getStatisticMerchantByDateEveryTerminal(userId, fromDate, toDate, offset);
            if (dtos != null && !dtos.isEmpty()) {
                result = dtos.stream().map(item -> {
                            StatisticTerminalOverViewDTO dto = new StatisticTerminalOverViewDTO();
                            dto.setTerminalId(item.getTerminalId());
                            dto.setTerminalName(item.getTerminalName());
                            dto.setTerminalCode(item.getTerminalCode());
                            dto.setTerminalAddress(item.getTerminalAddress());
                            dto.setTotalTrans(item.getTotalTrans());
                            dto.setTotalAmount(item.getTotalAmount());
                            RevenueTerminalDTO revGrowthPrevDate = transactionTerminalTempService
                                    .getTotalTranByTerminalCodeAndTimeBetweenWithCurrentTime(
                                            dto.getTerminalCode(), DateTimeUtil.getPrevDateAsString(),
                                            DateTimeUtil.getCurrentDateTimeAsNumber() - 86400);
                            if (revGrowthPrevDate != null && revGrowthPrevDate.getTotalAmount() != 0 && revGrowthPrevDate.getTotalTrans() != 0) {
                                double revGrowthPrevDateNum = revGrowthPrevDate.getTotalAmount() == 0 ? 0 :
                                        (double) (dto.getTotalAmount() - revGrowthPrevDate.getTotalAmount())
                                                / revGrowthPrevDate.getTotalAmount();
                                dto.setRatePreviousDate((int) (revGrowthPrevDateNum * 100));
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
