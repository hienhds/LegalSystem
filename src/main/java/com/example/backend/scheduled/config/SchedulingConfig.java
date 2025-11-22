package com.example.backend.scheduled.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Cấu hình cho scheduled tasks
 */
@Configuration
public class SchedulingConfig {

    /**
     * Task scheduler với thread pool cho các scheduled jobs
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setRejectedExecutionHandler((runnable, executor) -> {
            // Log rejection và handle gracefully
            System.err.println("Task rejected from scheduler: " + runnable.toString());
        });
        scheduler.initialize();
        return scheduler;
    }
}