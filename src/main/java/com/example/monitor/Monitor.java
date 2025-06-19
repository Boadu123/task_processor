package com.example.monitor;
import com.example.consumer.Consumer;
import com.example.producer.Producer;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

import static com.example.consumer.Consumer.processingTimestamps;
import static com.example.producer.Producer.*;

public class Monitor {
    public static ScheduledExecutorService startMonitor(ExecutorService consumerPool) {
        ScheduledExecutorService monitorService = Executors.newSingleThreadScheduledExecutor();

        monitorService.scheduleAtFixedRate(() -> {
            logger.info("Queue Size: {}", queue.size());

            if (consumerPool instanceof ThreadPoolExecutor threadPool) {
                logger.info("Active Threads: {}", threadPool.getActiveCount());
                logger.info("Completed Tasks : {}", Consumer.getCompletedCount());
                logger.info("Total Tasks: {}", Producer.getSubmittedCount());
            }

            processingTimestamps.forEach((id, startTime) -> {
                if (Duration.between(startTime, Instant.now()).getSeconds() > 10) {
                    logger.warn("Task {} seems stalled (running for over 10 seconds)", id);
                }
            });
        }, 0, 5, TimeUnit.SECONDS);

        return monitorService;
    }
}

