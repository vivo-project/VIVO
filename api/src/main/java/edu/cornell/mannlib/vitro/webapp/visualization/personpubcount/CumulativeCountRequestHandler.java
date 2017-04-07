/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.query.Dataset;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CumulativeCountRequestHandler implements VisualizationRequestHandler {
    @Override
    public AuthorizationRequest getRequiredPrivileges() {
        return null;
    }

    @Override
    public ResponseValues generateStandardVisualization(VitroRequest vitroRequest, Log log, Dataset dataSource) throws MalformedQueryParametersException {
        return null;
    }

    @Override
    public ResponseValues generateVisualizationForShortURLRequests(Map<String, String> parameters, VitroRequest vitroRequest, Log log, Dataset dataSource) throws MalformedQueryParametersException {
        return null;
    }

    @Override
    public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log, Dataset dataSource) throws MalformedQueryParametersException {
        String personURI = vitroRequest.getParameter("uri");

        QueryRunner<Set<Activity>> queryManager = new PersonPublicationCountQueryRunner(
                personURI,
                vitroRequest.getRDFService(),
                log);

        Set<Activity> authorDocuments = queryManager.getQueryResult();

        Map<Integer, Map<String, Integer>> yearToTypeCount = new TreeMap<Integer, Map<String, Integer>>();
        for (Activity currentActivity : authorDocuments) {
            String activityYearStr = currentActivity.getParsedActivityYear();
            Integer activityYear;
            try {
                activityYear = Integer.parseInt(activityYearStr, 10);
            } catch (NumberFormatException nfe) {
                activityYear = 0;
            }

            Map<String, Integer> typeCounts;
            if (yearToTypeCount.containsKey(activityYear)) {
                typeCounts = yearToTypeCount.get(activityYear);
            } else {
                typeCounts = new TreeMap<String, Integer>();
                yearToTypeCount.put(activityYear, typeCounts);
            }

            String activityType = currentActivity.getActivityType();
            if (StringUtils.isEmpty(activityType)) {
                activityType = "http://purl.org/ontology/bibo/Document";
            }

            if (typeCounts.containsKey(activityType)) {
                typeCounts.put(activityType, typeCounts.get(activityType) + 1);

            } else {
                typeCounts.put(activityType, 1);
            }
        }

        StringBuilder csv = new StringBuilder();

        csv.append("Year,Previous,Other,Books,Articles\n");

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        List<Integer> years = new ArrayList<Integer>(yearToTypeCount.keySet());
        for (int year = currentYear - 9; year < currentYear + 1; year++) {
            if (!years.contains(year)) {
                years.add(year);
            }
        }

        Collections.sort(years);
        int publicationCount = 0;
        for (Integer year : years) {
            if (year < currentYear - 9) {
                if (yearToTypeCount.containsKey(year)) {
                    Map<String, Integer> typeCounts = yearToTypeCount.get(year);
                    for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
                        publicationCount += entry.getValue();
                    }
                }
            } else {
                int articleCount = 0;
                int bookCount = 0;
                int otherCount = 0;

                if (yearToTypeCount.containsKey(year)) {
                    Map<String, Integer> typeCounts = yearToTypeCount.get(year);
                    for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
                        if ("http://purl.org/ontology/bibo/AcademicArticle".equalsIgnoreCase(entry.getKey()) ||
                                "http://purl.org/ontology/bibo/Article".equalsIgnoreCase(entry.getKey()) ) {
                            articleCount += entry.getValue();
                        } else if ("http://purl.org/ontology/bibo/Book".equalsIgnoreCase(entry.getKey()) ||
                                "http://purl.org/ontology/bibo/BookSection".equalsIgnoreCase(entry.getKey()) ||
                                "http://purl.org/ontology/bibo/Chapter".equalsIgnoreCase(entry.getKey()) ||
                                "http://purl.org/ontology/bibo/EditedBook".equalsIgnoreCase(entry.getKey()) ) {
                            bookCount += entry.getValue();
                        } else {
                            otherCount += entry.getValue();
                        }
                    }
                }

                csv.append(year).append(",")
                        .append(publicationCount).append(",")
                        .append(otherCount).append(",")
                        .append(bookCount).append(",")
                        .append(articleCount).append("\n");

                publicationCount += articleCount + bookCount + otherCount;
            }
        }

        return csv.toString();
    }

    @Override
    public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log, Dataset dataset) throws MalformedQueryParametersException {
        return null;
    }
}
