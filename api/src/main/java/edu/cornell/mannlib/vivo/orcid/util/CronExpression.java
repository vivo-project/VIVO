package edu.cornell.mannlib.vivo.orcid.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class CronExpression {

    private final Set<Integer> seconds;

    private final Set<Integer> minutes;

    private final Set<Integer> hours;

    private final Set<Integer> daysOfMonth;

    private final Set<Integer> months;

    private final Set<Integer> daysOfWeek;


    public CronExpression(String expr) {
        String[] parts = expr.trim().split("\\s+");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Cron must have 6 parts (sec min hour day month dow)");
        }

        seconds = parseField(parts[0], 0, 59);
        minutes = parseField(parts[1], 0, 59);
        hours = parseField(parts[2], 0, 23);
        daysOfMonth = parseField(parts[3], 1, 31);
        months = parseField(parts[4], 1, 12);
        daysOfWeek = parseDayOfWeek(parts[5]);
    }

    public Date getNextValidTimeAfter(Date afterTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(afterTime);

        // Start at next second
        cal.add(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);

        // Try up to 4 years ahead, prevents infinite loop
        for (int i = 0; i < 4 * 366 * 24 * 60 * 60; i++) {
            if (matches(cal)) {
                return cal.getTime();
            }
            increment(cal);
        }
        return null;
    }

    private boolean matches(Calendar cal) {
        int sec = cal.get(Calendar.SECOND);
        int min = cal.get(Calendar.MINUTE);
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int dom = cal.get(Calendar.DAY_OF_MONTH);
        int mon = cal.get(Calendar.MONTH) + 1;

        int dow = cal.get(Calendar.DAY_OF_WEEK) - 1;  // Sun=0
        if (dow == 0) {
            dow = 7; // Make Sunday = 7 to match cron
        }

        return seconds.contains(sec)
            && minutes.contains(min)
            && hours.contains(hr)
            && months.contains(mon)
            && daysMatch(dom, dow);
    }

    // Cron "day of month" vs "day of week" OR / AND logic (Quartz-like)
    private boolean daysMatch(int dom, int dow) {
        boolean domMatch = daysOfMonth.contains(dom);
        boolean dowMatch = daysOfWeek.contains(dow) || daysOfWeek.contains(0); // 0 or 7 = Sunday

        // Standard cron: both fields are ANDed unless one is "?"
        boolean domWildcard = daysOfMonth.contains(-1);
        boolean dowWildcard = daysOfWeek.contains(-1);

        if (!domWildcard && !dowWildcard) {  // both active
            return domMatch && dowMatch;
        }
        if (!domWildcard) {
            return domMatch;
        }
        if (!dowWildcard) {
            return dowMatch;
        }

        return true;
    }

    private void increment(Calendar cal) {
        // If second not allowed then jump to next valid second
        int s = cal.get(Calendar.SECOND);
        Integer next = nextOrReset(seconds, s, 59);
        if (next != null) {
            cal.set(Calendar.SECOND, next);
            return;
        }
        cal.set(Calendar.SECOND, seconds.iterator().next());

        // Increment minute
        int m = cal.get(Calendar.MINUTE);
        next = nextOrReset(minutes, m, 59);
        if (next != null) {
            cal.set(Calendar.MINUTE, next);
            return;
        }
        cal.set(Calendar.MINUTE, minutes.iterator().next());

        // Increment hour
        int h = cal.get(Calendar.HOUR_OF_DAY);
        next = nextOrReset(hours, h, 23);
        if (next != null) {
            cal.set(Calendar.HOUR_OF_DAY, next);
            return;
        }
        cal.set(Calendar.HOUR_OF_DAY, hours.iterator().next());

        // Increment day
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, hours.iterator().next());
        cal.set(Calendar.MINUTE, minutes.iterator().next());
        cal.set(Calendar.SECOND, seconds.iterator().next());

        // Adjust month if needed
        while (!months.contains(cal.get(Calendar.MONTH) + 1)) {
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private Integer nextOrReset(Set<Integer> allowed, int current, int max) {
        for (int v : allowed) {
            if (v > current) {
                return v;
            }
        }
        return null; // wrap needed
    }

    private Set<Integer> parseField(String field, int min, int max) {
        if ("?".equals(field)) {
            HashSet<Integer> result = new HashSet<>();
            result.add(-1);
            return result;
        }

        Set<Integer> result = new TreeSet<>();

        if ("*".equals(field)) {
            for (int i = min; i <= max; i++) {
                result.add(i);
            }
            return result;
        }

        for (String part : field.split(",")) {
            parsePart(part, min, max, result);
        }

        return result;
    }

    private void parsePart(String part, int min, int max, Set<Integer> result) {
        if (part.contains("/")) {
            String[] sp = part.split("/");
            int step = Integer.parseInt(sp[1]);
            if ("*".equals(sp[0])) {
                for (int i = min; i <= max; i += step) {
                    result.add(i);
                }
            } else {
                addRange(sp[0], step, min, max, result);
            }
        } else if (part.contains("-")) {
            addRange(part, 1, min, max, result);
        } else {
            int v = Integer.parseInt(part);
            if (v < min || v > max) {
                throw new IllegalArgumentException("Value out of range: " + v);
            }
            result.add(v);
        }
    }

    private void addRange(String range, int step, int min, int max, Set<Integer> result) {
        String[] r = range.split("-");
        int start = Integer.parseInt(r[0]);
        int end = Integer.parseInt(r[1]);
        if (start > end) {
            throw new IllegalArgumentException("Invalid range: " + range);
        }
        for (int i = start; i <= end; i += step) {
            if (i < min || i > max) {
                continue;
            }
            result.add(i);
        }
    }

    // Day of week supports MON-SUN
    private Set<Integer> parseDayOfWeek(String field) {
        field = field.toUpperCase();

        field = field.replace("SUN", "7")
            .replace("MON", "1")
            .replace("TUE", "2")
            .replace("WED", "3")
            .replace("THU", "4")
            .replace("FRI", "5")
            .replace("SAT", "6");

        return parseField(field, 0, 7); // 0 or 7 = Sunday
    }
}
