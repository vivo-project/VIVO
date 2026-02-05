package edu.cornell.mannlib.vivo.harvest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HarvestJobExecutor {

    private static final ExecutorService executor =
        Executors.newCachedThreadPool();

    public static void runAsync(String moduleName, String command) {

        executor.submit(() -> {

            HarvestJobRegistry.startJob(moduleName);

            File logFile =
                new File("/tmp/harvest-" + moduleName + ".log");

            try {
                ProcessBuilder pb =
                    new ProcessBuilder("bash", command);

                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (
                    BufferedReader reader =
                        new BufferedReader(
                            new InputStreamReader(
                                process.getInputStream()));
                    FileWriter writer =
                        new FileWriter(logFile, true)
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
