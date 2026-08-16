package edu.cornell.mannlib.vivo.scheduler;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SchedulerManager {

    private static final Log log = LogFactory.getLog(SchedulerManager.class);

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
        String cron = scheduled.cron();
        if (isPropertyPlaceholder(cron)) {
            String propertyKey = getPropertyKey(cron);
            String cronFromProperties = ConfigurationProperties.getInstance().getProperty(propertyKey);

            if (cronFromProperties == null || cronFromProperties.trim().isEmpty()) {
                log.info("Not scheduling " + describe(bean, method) + ", runtime.properties does not define "
                    + propertyKey + ".");
                return;
            }

            scheduleWithCron(task, cronFromProperties, bean, method);
        } else if (!cron.isEmpty()) {
            scheduleWithCron(task, cron, bean, method);
        } else if (!scheduled.fixedRate().isEmpty()) {
            long rate = Long.parseLong(scheduled.fixedRate());
            scheduler.scheduleAtFixedRate(task, 0, rate, TimeUnit.MILLISECONDS);
        } else if (!scheduled.fixedDelay().isEmpty()) {
            long delay = Long.parseLong(scheduled.fixedDelay());
            scheduler.scheduleWithFixedDelay(task, 0, delay, TimeUnit.MILLISECONDS);
        }
    }

    private static boolean isPropertyPlaceholder(String cron) {
        return cron.startsWith("${") && cron.endsWith("}");
    }

    private static String getPropertyKey(String cron) {
        return cron.substring(2, cron.length() - 1);
    }

    private static String describe(Object bean, Method method) {
        return bean.getClass().getSimpleName() + "." + method.getName() + "()";
    }

    private static void scheduleWithCron(Runnable task, String cronExpression, Object bean, Method method) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            scheduleNextRun(task, cron);
        } catch (IllegalArgumentException e) {
            log.error("Not scheduling " + describe(bean, method) + ", '" + cronExpression
                + "' is not a valid cron expression.", e);
        }
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
