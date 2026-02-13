package edu.cornell.mannlib.vivo.harvest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HarvestJobExecutor {

    private static final Log log = LogFactory.getLog(HarvestJobExecutor.class);

    private static final ExecutorService executor =
        new ThreadPoolExecutor(
            10,
            10,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            r -> {
                Thread t = new Thread(r);
                t.setName("harvest-executor-" + t.getId());
                t.setDaemon(false);
                return t;
            }
        );


    public static void runAsync(String moduleName, List<String> command, Path modulePath,
                                @Nullable String scheduledTaskName) {
        if (HarvestJobRegistry.isRunning(moduleName)) {
            return;
        }

        boolean isScheduled = Objects.nonNull(scheduledTaskName);

        Future<?> future = executor.submit(() -> {
            HarvestJobRegistry.startJob(moduleName);

            File logFile =
                new File(
                    "/tmp/harvest-" + moduleName +
                        (isScheduled ? ("-" + scheduledTaskName.toLowerCase().replaceAll("\\s", "_")) : "") +
                        (isScheduled ? ("-" + LocalDateTime.now()) : "") +
                        ".log"
                );

            try {
                List<String> fullCommand = new ArrayList<>();

                fullCommand.add("/usr/bin/setsid");
                fullCommand.addAll(command);

                ProcessBuilder pb =
                    new ProcessBuilder(fullCommand);

                pb.directory(modulePath.toFile());
                pb.redirectErrorStream(true);
                pb.environment().clear();
                pb.environment().put("PATH", "/usr/bin:/bin");

                Process process = pb.start();

                long pid = process.pid();

                HarvestJobRegistry
                    .registerProcess(moduleName, process);

                HarvestJobRegistry
                    .registerPid(moduleName, pid);

                try (
                    BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                    FileWriter writer = new FileWriter(logFile, false)) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (Thread.currentThread().isInterrupted()) {
                            process.destroyForcibly();
                            break;
                        }

                        writer.write(line + "\n");
                        writer.flush();
                    }
                }

                process.waitFor();
            } catch (Exception e) {
                log.error("Unexpected error occurred when trying to run harvest job: " + e.getMessage());
            } finally {
                HarvestJobRegistry.finishJob(moduleName);
            }
        });

        HarvestJobRegistry
            .registerFuture(moduleName, future);
    }

    public static void shutdown() {
        log.info("Shutting down HarvestJobExecutor...");

        try {
            HarvestJobRegistry.stopAllJobs();
        } catch (Exception e) {
            log.error("Error stopping running harvest jobs", e);
        }

        executor.shutdownNow();
    }
}
