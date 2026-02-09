package edu.cornell.mannlib.vivo.harvest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import edu.cornell.mannlib.vivo.harvest.contextmodel.ModuleRuntimeContext;
import edu.cornell.mannlib.vivo.harvest.controller.WorkflowOutputLogController;

public class HarvestJobRegistry {

    private static final Map<String, ModuleRuntimeContext> jobs =
        new ConcurrentHashMap<>();


    public static void startJob(String module) {
        jobs.put(module, new ModuleRuntimeContext(module));
    }

    public static void finishJob(String module) {
        jobs.remove(module);
        WorkflowOutputLogController.cleanupLogPosition(module);
    }

    public static boolean isRunning(String module) {
        return jobs.containsKey(module);
    }

    public static void registerFuture(String module, Future<?> future) {
        if (!isRunning(module)) {
            return;
        }

        jobs.get(module).setFuture(future);
    }

    public static void registerProcess(String module, Process process) {
        if (!isRunning(module)) {
            return;
        }

        jobs.get(module).setProcessHandle(process);
    }

    public static void registerPid(String module, long pid) {
        if (!isRunning(module)) {
            return;
        }

        jobs.get(module).setPid(pid);
    }

    public static void stopJob(String module) {
        ModuleRuntimeContext ctx = jobs.getOrDefault(module, null);

        if (ctx == null) {
            return;
        }

        Long pid = ctx.getPid();

        if (pid != null) {
            try {
                new ProcessBuilder(
                    "pkill",
                    "-KILL",
                    "-P",
                    String.valueOf(pid)
                ).start().waitFor();

                new ProcessBuilder(
                    "kill",
                    "-KILL",
                    String.valueOf(pid)
                ).start().waitFor();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Future<?> future = ctx.getFuture();
        if (future != null) {
            future.cancel(true);
        }

        finishJob(module);
    }
}
