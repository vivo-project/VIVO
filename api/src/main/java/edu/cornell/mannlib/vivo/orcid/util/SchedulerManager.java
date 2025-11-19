package edu.cornell.mannlib.vivo.orcid.util;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;

public class SchedulerManager {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);


    public static void scheduleTasks(Object... beans) {
        for (Object bean : beans) {
            scheduleMethodsInBean(bean);
        }
    }

    private static void scheduleMethodsInBean(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Scheduled.class)) {
                Scheduled scheduled = method.getAnnotation(Scheduled.class);
                scheduleMethod(bean, method, scheduled);
            }
        }
    }

    private static void scheduleMethod(Object bean, Method method, Scheduled scheduled) {
        method.setAccessible(true);

        Runnable task = () -> {
            try {
                method.invoke(bean);
            } catch (Exception e) {
                System.err.println("Error executing scheduled task: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // Priority: 1. CRON from properties, 2. Direct cron, 3. fixedRate, 4. fixedDelay
        String cronFromProperties = getCronFromProperties(scheduled);
        if (cronFromProperties != null) {
            scheduleWithCron(task, cronFromProperties);
        } else if (!scheduled.cron().isEmpty()) {
            scheduleWithCron(task, scheduled.cron());
        } else if (!scheduled.fixedRate().isEmpty()) {
            long rate = Long.parseLong(scheduled.fixedRate());
            scheduler.scheduleAtFixedRate(task, 0, rate, TimeUnit.MILLISECONDS);
        } else if (!scheduled.fixedDelay().isEmpty()) {
            long delay = Long.parseLong(scheduled.fixedDelay());
            scheduler.scheduleWithFixedDelay(task, 0, delay, TimeUnit.MILLISECONDS);
        }
    }

    private static String getCronFromProperties(Scheduled scheduled) {
        String cron = scheduled.cron();
        if (cron.startsWith("${") && cron.endsWith("}")) {
            String propertyKey = cron.substring(2, cron.length() - 1);
            return ConfigurationProperties.getInstance().getProperty(propertyKey);
        }
        return null;
    }

    private static void scheduleWithCron(Runnable task, String cronExpression) {
        CronExpression cron = new CronExpression(cronExpression);
        scheduleNextRun(task, cron);
    }

    private static void scheduleNextRun(Runnable task, CronExpression cron) {
        Date now = new Date();
        Date nextRun = cron.getNextValidTimeAfter(now);

        if (nextRun != null) {
            long delay = nextRun.getTime() - now.getTime();
            scheduler.schedule(() -> {
                task.run();
                scheduleNextRun(task, cron);
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    public static void shutdown() {
        scheduler.shutdown();
    }
}
