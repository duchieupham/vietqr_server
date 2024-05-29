package com.vietqr.org.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@Component
public class ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    //@Scheduled(fixedRate = 86400000)    // Time interval between method runs
    public void scheduleDataWithFixedRate() {
        // Call the function to statistical data from PROD STATISTIC by day


        LOGGER.info("Đồng bộ data từ PROD STATISTIC theo ngày");
    }

    //@Scheduled(fixedRate = 86400000 , initialDelay = 3600000)  // Delay for first time run method
    public void scheduleDataWithInitialDelay() {
        // Call the function to statistical data from PROD STATISTIC by month

        LOGGER.info("Đồng bộ data từ PROD STATISTIC theo tháng sau khi Deploy thành công 1 khoảng tgian");
    }

    //@Scheduled(cron = "0 0 0 * * *") //
    public void scheduleTaskWithCronExpression() {
        LOGGER.info("Đồng bộ data từ PROD STATISTIC vào lúc 00h00 theo ngày");
    }


}
