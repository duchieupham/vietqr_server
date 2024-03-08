package com.vietqr.org.controller;

import com.vietqr.org.dto.*;
import com.vietqr.org.service.TransactionTerminalTempService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalStatisticController {

    private static final Logger logger = Logger.getLogger(TerminalStatisticController.class);

    @Autowired
    private TransactionTerminalTempService transactionTerminalTempService;

    @GetMapping("terminal-statistic")
    public ResponseEntity<StatisticMerchantDTO> getStatisticTerminal(
            @RequestParam String userId,
            @RequestParam String merchantId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        StatisticMerchantDTO result = null;
        HttpStatus httpStatus = null;
        try {
            IStatisticMerchantDTO dto = transactionTerminalTempService.getStatisticMerchantByDate(userId, fromDate, toDate);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get statistic terminal error", e);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal-statistic/all")
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
                list.add(new StatisticTerminalDTO(dto.getCountTrans(), dto.getSumAmount(), dto.getTime()));
            }, ArrayList::addAll);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get statistic every terminal error", e);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal-statistic/top5")
    public ResponseEntity<List<TopTerminalDTO>> getTop5Terminal(
            @RequestParam String userId,
            @RequestParam String merchantId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<TopTerminalDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            ITopTerminalDTO dto = transactionTerminalTempService.getTop5TerminalByDate(userId, fromDate, toDate);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get top 5 terminal error", e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
