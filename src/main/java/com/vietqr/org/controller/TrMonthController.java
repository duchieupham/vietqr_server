package com.vietqr.org.controller;

import com.vietqr.org.dto.TrMonthDTO;
import com.vietqr.org.dto.TransactionReceiveAdminListDTO;
import com.vietqr.org.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@RestController
//@CrossOrigin
@RequestMapping("/api")
public class TrMonthController {
    private static final Logger logger = Logger.getLogger(TrMonthController.class);

    @Autowired
    TrMonthService trMonthService;

//    @Scheduled(fixedRate = 5000)
//    @Scheduled(cron = "0 0 0 * * *")
    @GetMapping("admin/trmonth/{time}")
//    @Scheduled(fixedRate = 5000)
//    @Scheduled(cron = "0 0 0 * * *")
    public ResponseEntity<List<TrMonthDTO>> getTrMonth(@PathVariable String time) {
        List<TrMonthDTO> result = new ArrayList<>();
        HttpStatus httpStatus = null;
        try {
            result = trMonthService.getTrMonthByMonth(time);
            httpStatus = HttpStatus.OK;
            LOGGER.info("Bắt đầu đồng bộ data từ Tr_Month mỗi ngày vào lúc 00h00");
        } catch (Exception e) {
            logger.error("getDataTrMonth: ERROR: " + e.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(result, httpStatus);
    }



//    @GetMapping("admin/trmonths/{time}")
//    @Scheduled(fixedRate = 86400000)    //Start synchronizing data after deploying
//    @Scheduled(cron = "0 0 0 * * *")    //Start synchronizing data 00h00s every day
//    public ResponseEntity<List<TrMonthDTO>> getTrMonth(@PathVariable String time) {
//        List<TrMonthDTO> result = new ArrayList<>();
//        HttpStatus httpStatus = null;
//        LOGGER.info("Bắt đầu đồng bộ data từ tr-month mỗi ngày vào lúc 00h00");
//        try {
//            result = trMonthService.getTrMonthByMonths(time);
//            httpStatus = HttpStatus.OK;
//        } catch (Exception e) {
//            logger.error("getDataTrMonth: ERROR: " + e.toString());
//            httpStatus = HttpStatus.BAD_REQUEST;
//        }
//        return new ResponseEntity<>(result, httpStatus);
//    }

}
