/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.personpubcount;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.AuthorizationRequest;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.i18n.I18n;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.query.Dataset;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CumulativeCountRequestHandler implements VisualizationRequestHandler {
    private static final Log log = LogFactory.getLog(CumulativeCountRequestHandler.class);

    // Flag to say whether we have attempted to load column definitions from the runtime.properties
    private static boolean loadedColumnDefinitions = false;

    // Default configuration for pub lication groups and the types they contain
    private static String[] pubsGroups = { "Articles", "Books" };

    // Default types for each publications group
    private static Map<String, String[]> groupTypeMap = Stream.of(
        new SimpleEntry<String, String[]>(
                    "Articles", new String[] {
                            "http://purl.org/ontology/bibo/AcademicArticle",
                            "http://purl.org/ontology/bibo/Article"
                        }
                ),
        new SimpleEntry<String, String[]>(
                    "Books", new String[] {
                            "http://purl.org/ontology/bibo/Book",
                            "http://purl.org/ontology/bibo/BookSection",
                            "http://purl.org/ontology/bibo/Chapter",
                            "http://purl.org/ontology/bibo/EditedBook"
                        }
                )
    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

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
        // Ensure that we have loaded any configured definitions
        loadColumnDefintions(vitroRequest);

        String personURI = vitroRequest.getParameter("uri");

        QueryRunner<Collection<Activity>> queryManager = new PersonPublicationCountQueryRunner(
                personURI,
                vitroRequest.getRDFService(),
                log);

        Collection<Activity> authorDocuments = queryManager.getQueryResult();

        Map<Integer, int[]> yearToTypeCount = new TreeMap<Integer, int[]>();
        for (Activity currentActivity : authorDocuments) {
            String activityYearStr = currentActivity.getParsedActivityYear();
            Integer activityYear;
            try {
                activityYear = Integer.parseInt(activityYearStr, 10);
            } catch (NumberFormatException nfe) {
                activityYear = 0;
            }

            // Get an array for the type counts
            int[] typeCounts;
            if (yearToTypeCount.containsKey(activityYear)) {
                typeCounts = yearToTypeCount.get(activityYear);
            } else {
                // Note that we want an array that is one bigger than the number of groups defined (for "other")
                typeCounts = new int[pubsGroups.length + 1];
                yearToTypeCount.put(activityYear, typeCounts);
            }

            // Match the activity types to one of the configured groups
            int groupIndex = getGroupIndex(currentActivity.getActivityTypes());

            if (groupIndex < 0) {
                // Use the first element to count "other"
                typeCounts[0]++;
            } else {
                // Add one to the group index (reserve '0' as the "other" count), and increment the relevant count
                typeCounts[groupIndex + 1]++;
            }
        }

        StringBuilder csv = new StringBuilder();

        csv.append("Year,Previous,Other");
        // Add the configured groups to CSV headings
        for (String groupName : pubsGroups) {
            // Retrieve a label from the i18n file
            String label = I18n.text(vitroRequest, "histogram_label_for_" + groupName);
            if (!StringUtils.isEmpty(label)) {
                csv.append(",").append(label);
            } else {
                // No label, so use the group name
                csv.append(",").append(groupName);
            }
        }
        csv.append("\n");

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
                    // Too early a year - just get the type counts and add everything to the overall total
                    int[] typeCounts = yearToTypeCount.get(year);
                    if (typeCounts != null) {
                        for (int count : typeCounts) {
                            publicationCount += count;
                        }
                    }
                }
            } else {
                // Get the type counts for the given year
                int[] typeCounts = yearToTypeCount.get(year);
                if (typeCounts == null) {
                    // No type counts, so create an empty array (must contain one element more than configured groups)
                    typeCounts = new int[pubsGroups.length + 1];
                }

                // Add the year and overall previous total to the CSV
                csv.append(year).append(",").append(publicationCount);

                // Add all of the type counts to the CSV, and the overall count
                for (int count : typeCounts) {
                    csv.append(",").append(count);
                    publicationCount += count;
                }
                csv.append("\n");
            }
        }

        return csv.toString();
    }

    @Override
    public Map<String, String> generateDataVisualization(VitroRequest vitroRequest, Log log, Dataset dataset) throws MalformedQueryParametersException {
        return null;
    }

    /**
     * Find an appropriate group index for the type URIs passed.
     * It treats the configured group order as the priority, so it will return the group
     * where a type first matches, if there is more than one type passed.
     *
     * @param types
     * @return
     */
    private int getGroupIndex(Set<String> types) {
        int index = 0;

        if (types != null) {
            for (String column : pubsGroups) {
                if (groupTypeMap.containsKey(column)) {
                    for (String mappedType : groupTypeMap.get(column)) {
                        if (types.contains(mappedType)) {
                            return index;
                        }
                    }
                }

                index++;
            }
        }

        return -1;
    }

    /**
     * Load column definition from the runtime.properties
     *
     * @param vitroRequest
     */
    private synchronized void loadColumnDefintions(VitroRequest vitroRequest) {
        // Only do this if we haven't already loaded a configuration
        if (!loadedColumnDefinitions) {
            // Flag that we don't need to do this again
            loadedColumnDefinitions = true;

            ConfigurationProperties properties = ConfigurationProperties.getBean(vitroRequest);

            if (properties != null) {
                // Get the configurated group names (e.g. Articles, Books, etc.)
                String groupsDefsStr = properties.getProperty("histogram.groups");
                if (!StringUtils.isEmpty(groupsDefsStr)) {
                    // Create a temporary map for the loaded group configuration
                    Map<String, String[]> loadedGroupsTypeMap = new HashMap<>();

                    String[] loadedGroups = groupsDefsStr.split("\\s*,\\s*");

                    // For each group
                    for (String group : loadedGroups) {
                        // Validate that there is a name
                        if (StringUtils.isEmpty(group)) {
                            log.error("Error in groups definition for publications count: " + groupsDefsStr);
                            return;
                        }

                        // Get the comma separated list of URIs for this group
                        String typeStr = properties.getProperty("histogram.types.for." + group);
                        if (StringUtils.isEmpty(typeStr)) {
                            log.error("Error in type definition for publication count: histogram.types.for." + group);
                            return;
                        }

                        // Split the type URIs into an array
                        // And add it to the group-types map
                        loadedGroupsTypeMap.put(group, typeStr.split("\\s*,\\s*"));
                    }

                    // Finished parsing the config, replace the static fields
                    pubsGroups = loadedGroups;
                    groupTypeMap = loadedGroupsTypeMap;
                }
            }
        }
    }
}
