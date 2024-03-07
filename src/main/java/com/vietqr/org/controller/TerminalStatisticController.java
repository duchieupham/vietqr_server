package com.vietqr.org.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class TerminalStatisticController {

    private static final Logger logger = Logger.getLogger(TerminalStatisticController.class);

    @Autowired
    private StatisticTerminalRedisService statisticTerminalRedisService;

    @GetMapping("terminal-statistic")
    public ResponseEntity<StatisticAllTerminalEntity> getStatisticTerminal(
            @RequestParam String userId) {
        StatisticAllTerminalEntity result = null;
        HttpStatus httpStatus = null;
        try {
            result = statisticTerminalRedisService.getStatisticAllTerminalToDay(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get statistic terminal error", e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal-statistic/all")
    public ResponseEntity<List<StatisticTerminalDTO>> getStatisticEveryTerminal(
            @RequestParam String userId) {
        List<StatisticTerminalDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = statisticTerminalRedisService.getStatisticTerminalToDay(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get statistic every terminal error", e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @GetMapping("terminal-statistic/top5")
    public ResponseEntity<List<TopTerminalDTO>> getTop5Terminal(
            @RequestParam String userId) {
        List<TopTerminalDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = statisticTerminalRedisService.getTop5TerminalToDay(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logger.error("Get top 5 terminal error", e);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(result, httpStatus);
    }
}
