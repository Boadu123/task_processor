package com.example.consumer;

import com.example.enums.TaskStatus;
import com.example.core.Task;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.producer.Producer.*;

public class Consumer implements Runnable{

    public static ConcurrentHashMap<UUID, Instant> processingTimestamps = new ConcurrentHashMap<>();
    private static final AtomicInteger completedCount = new AtomicInteger(0);

    public static int getCompletedCount() {
        return completedCount.get();
    }
    @Override
    public void run() {
        while(true){
            Task task =null;
            try {

                task = queue.take();

                if (task.isPoisonPill()) {
                    logger.info("{} received poison pill. Shutting down.", Thread.currentThread().getName());
                    break;
                }

                taskStatusMap.put(task.getId(), TaskStatus.PROCESSING);
                logger.info("{} started processing: {}", Thread.currentThread().getName(), task);
                logger.info("Current taskStatusMap in the process: {}", taskStatusMap);

                Thread.sleep(1000);
                taskStatusMap.put(task.getId(), TaskStatus.COMPLETED);
                completedCount.incrementAndGet();
                processingTimestamps.put(task.getId(), Instant.now());
                logger.info("{} completed task: {} at {}", Thread.currentThread().getName(), task.getName(), java.time.Instant.now());
                logger.info("Current taskStatusMap after completion: {}", taskStatusMap);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                if (task.getRetryCount() < 3) {
                    task.incrementRetryCount();
                    logger.warn("Retrying task {} (Attempt {})", task.getId(), task.getRetryCount());
                    try {
                        queue.put(task); // might throw InterruptedException
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        logger.error("Retry failed for task {} due to interruption", task.getId(), ex);
                    }                } else {
                    taskStatusMap.put(task.getId(), TaskStatus.FAILED);
                    logger.error("Task {} failed after 3 retries", task.getId());
                }


                logger.warn("{} was interrupted", Thread.currentThread().getName(), e);
                logger.info("Current taskStatusMap after failure: {}", taskStatusMap);
                break;
            }
        }
    }
}
