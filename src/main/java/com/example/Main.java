package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.consumer.Consumer;
import com.example.utils.JsonExporter;
import com.example.producer.Producer;
import com.example.core.Task;
import com.example.monitor.Monitor;

import static com.example.producer.Producer.logger;
import static com.example.producer.Producer.queue;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Producer producer = new Producer("Washer", 2, true);
        Thread t1 = new Thread(producer);

        Producer producer1 = new Producer("Machine", 1, false);
        Thread t2 = new Thread(producer1);

        ExecutorService consumerPool = Executors.newFixedThreadPool(2);

        consumerPool.submit(new Consumer());
        consumerPool.submit(new Consumer());

        ScheduledExecutorService monitorService = Monitor.startMonitor(consumerPool);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        JsonExporter.startExport();

        queue.put(new Task.Builder().name("POISON").priority(Integer.MAX_VALUE).build());
        queue.put(new Task.Builder().name("POISON").priority(Integer.MAX_VALUE).build());
        consumerPool.shutdown();
        if (!consumerPool.awaitTermination(30, TimeUnit.SECONDS)) {
            logger.warn("Consumers didn't shut down in time.");
            consumerPool.shutdownNow();
        }

        monitorService.shutdown();
        if (!monitorService.awaitTermination(10, TimeUnit.SECONDS)) {
            logger.warn("Monitor didn't shut down in time.");
            monitorService.shutdownNow();
        }

        JsonExporter.shutdown();

        logger.info("All services stopped. Exiting.");

    }
}