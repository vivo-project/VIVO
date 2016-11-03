/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class OrgUtils {
    public static Map<String, String> getParentURIsToLabel(String org, Map<String, Set<String>> subOrgMap, Map<String, String> orgLabelMap) {
        Map<String, String> parentURIsToLabel = new TreeMap<>();

        if (!StringUtils.isEmpty(org)) {
            for (Map.Entry<String, Set<String>> orgMapEntry : subOrgMap.entrySet()) {
                Set<String> subOrgs = orgMapEntry.getValue();
                if (subOrgs != null && subOrgs.contains(org) && orgLabelMap.containsKey(orgMapEntry.getKey())) {
                    parentURIsToLabel.put(orgMapEntry.getKey(), orgLabelMap.get(orgMapEntry.getKey()));
                }
            }
        }

        return parentURIsToLabel;
    }

    public static void getObjectMappingsForOrgAndSubOrgs(
            String orgUri,
            Set<String> orgObjects,
            Set<String> orgObjectsIncludesPeople,
            Map<String, Set<String>> subOrgObjectMap,
            Map<String, Set<String>> subOrgMap,
            Map<String, Set<String>> organisationToPeopleMap,
            Map<String, Set<String>> personToObjectMap
    ) {
        if (subOrgMap.containsKey(orgUri)) {
            for (String topSubOrg : subOrgMap.get(orgUri)) {
                Set<String> subOrgPublications       = new HashSet<String>();
                Set<String> subOrgPublicationsPeople = new HashSet<String>();

                Set<String> fullSubOrgs  = OrgUtils.orgAndAllSubOrgs(new HashSet<String>(), topSubOrg, subOrgMap);

                for (String subOrg : fullSubOrgs) {
                    Set<String> peopleInSubOrg = organisationToPeopleMap.get(subOrg);
                    if (peopleInSubOrg != null) {
                        for (String person : peopleInSubOrg) {
                            if (personToObjectMap.containsKey(person)) {
                                if (subOrgPublicationsPeople.add(person)) {
                                    subOrgPublications.addAll(personToObjectMap.get(person));

                                    if (orgObjectsIncludesPeople.add(person)) {
                                        orgObjects.addAll(personToObjectMap.get(person));
                                    }
                                }
                            }
                        }
                    }
                }

                subOrgObjectMap.put(topSubOrg, subOrgPublications);
            }
        }

        Set<String> people = organisationToPeopleMap.get(orgUri);
        if (people != null) {
            for (String person : people) {
                if (orgObjectsIncludesPeople.add(person)) {
                    if (personToObjectMap.containsKey(person)) {
                        orgObjects.addAll(personToObjectMap.get(person));
                    }
                }
            }
        }
    }

    private static Set<String> orgAndAllSubOrgs(Set<String> allSubOrgs, String org, Map<String, Set<String>> subOrgMap) {
        if (allSubOrgs.add(org)) {
            if (subOrgMap.containsKey(org)) {
                for (String subOrg : subOrgMap.get(org)) {
                    orgAndAllSubOrgs(allSubOrgs, subOrg, subOrgMap);
                }
            }
        }

        return allSubOrgs;
    }
}
