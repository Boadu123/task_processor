package com.example.utils;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.time.Instant;
import java.util.concurrent.*;

import static com.example.producer.Producer.logger;
import static com.example.producer.Producer.taskStatusMap;

public class JsonExporter {
    private static final ScheduledExecutorService jsonExportScheduler =
            Executors.newSingleThreadScheduledExecutor();

    public static void startExport() {
        jsonExportScheduler.scheduleAtFixedRate(() -> {
            try (FileWriter writer = new FileWriter("task-status-" + Instant.now().getEpochSecond() + ".json")) {
                Gson gson = new Gson();
                gson.toJson(taskStatusMap, writer);
                logger.info("Exported task status to JSON");
            } catch (Exception e) {
                logger.error(" Failed to export task status", e);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public static void shutdown() {
        jsonExportScheduler.shutdown();
        try {
            if (!jsonExportScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn(" JSON Exporter didn't terminate in time. Forcing shutdown...");
                jsonExportScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warn(" JSON Exporter shutdown interrupted", e);
            jsonExportScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
