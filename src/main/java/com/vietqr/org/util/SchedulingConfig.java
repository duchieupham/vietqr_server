package com.vietqr.org.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    // create new thread pool to run scheduled task
    @Bean
    public TaskScheduler taskScheduler() {
        TaskScheduler scheduler = new ThreadPoolTaskScheduler();
        ((ThreadPoolTaskScheduler) scheduler).setPoolSize(2);
        ((ThreadPoolTaskScheduler) scheduler).setThreadNamePrefix("scheduled-task-");
        ((ThreadPoolTaskScheduler) scheduler).setDaemon(true);
        return scheduler;
    }
}
