/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CounterUtils {
    public static final List<List<Integer>> getObjectCountByYear(Set<String> objects, Map<String, String> objectToYearMap) {
        List<List<Integer>> yearCounts = new ArrayList<>();
        if (objects != null) {
            int[] counts = new int[Calendar.getInstance().get(Calendar.YEAR) + 1000];
            for (String publication : objects) {
                int year = 0;
                try {
                    year = Integer.parseInt(objectToYearMap.get(publication), 10);
                } catch (Throwable t) {
                }

                if (year > counts.length - 1) {
                    year = 0;
                }

                counts[year]++;
            }

            for (int i = 1; i < counts.length; i++) {
                if (counts[i] > 0) {
                    List<Integer> currentYear = new ArrayList<Integer>();
                    currentYear.add(i);
                    currentYear.add(counts[i]);
                    yearCounts.add(currentYear);
                }
            }

            if (counts[0] > 0) {
                List<Integer> currentYear = new ArrayList<Integer>();
                currentYear.add(-1);
                currentYear.add(counts[0]);
                yearCounts.add(currentYear);
            }
        }

        return yearCounts;
    }
}
