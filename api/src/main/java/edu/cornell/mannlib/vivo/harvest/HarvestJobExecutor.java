package edu.cornell.mannlib.vivo.harvest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HarvestJobExecutor {

    private static final ExecutorService executor =
        Executors.newCachedThreadPool();

    public static void runAsync(String moduleName, List<String> command, Path modulePath) {
        executor.submit(() -> {
            HarvestJobRegistry.startJob(moduleName);

            File logFile =
                new File("/tmp/harvest-" + moduleName + ".log");

            try {
                ProcessBuilder pb = new ProcessBuilder(command);

                pb.directory(new File(modulePath.toString()));

                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (
                    BufferedReader reader =
                        new BufferedReader(
                            new InputStreamReader(
                                process.getInputStream()));
                    FileWriter writer =
                        new FileWriter(logFile, false)
                ) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                }

                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                HarvestJobRegistry.finishJob(moduleName);
            }
        });
    }
}
