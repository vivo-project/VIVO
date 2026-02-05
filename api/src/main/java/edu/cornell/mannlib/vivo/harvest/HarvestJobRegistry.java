package edu.cornell.mannlib.vivo.harvest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class HarvestJobRegistry {

    private static final Map<String, AtomicBoolean> runningJobs =
        new ConcurrentHashMap<>();


    public static void startJob(String moduleName) {
        runningJobs.put(moduleName, new AtomicBoolean(true));
    }

    public static void finishJob(String moduleName) {
        runningJobs.computeIfPresent(moduleName,
            (k, v) -> {
                v.set(false);
                return v;
            });
    }

    public static boolean isRunning(String moduleName) {
        return runningJobs.getOrDefault(
            moduleName,
            new AtomicBoolean(false)
        ).get();
    }
}
