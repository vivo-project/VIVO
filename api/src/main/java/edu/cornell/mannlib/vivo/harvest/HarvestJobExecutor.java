package edu.cornell.mannlib.vivo.harvest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HarvestJobExecutor {

    private static final ExecutorService executor =
        Executors.newFixedThreadPool(10);


    public static void runAsync(String moduleName, List<String> command, Path modulePath) {
        if (HarvestJobRegistry.isRunning(moduleName)) {
            return;
        }

        Future<?> future = executor.submit(() -> {
            HarvestJobRegistry.startJob(moduleName);

            File logFile =
                new File("/tmp/harvest-" + moduleName + ".log");

            try {
                List<String> fullCommand = new ArrayList<>();

                fullCommand.add("setsid");
                fullCommand.addAll(command);

                ProcessBuilder pb =
                    new ProcessBuilder(fullCommand);

                pb.directory(modulePath.toFile());
                pb.redirectErrorStream(true);

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
                e.printStackTrace();
            } finally {
                HarvestJobRegistry.finishJob(moduleName);
            }
        });

        HarvestJobRegistry
            .registerFuture(moduleName, future);
    }
}
