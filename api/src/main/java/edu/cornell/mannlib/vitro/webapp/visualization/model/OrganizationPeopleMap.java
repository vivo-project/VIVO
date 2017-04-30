/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OrganizationPeopleMap {
    public final Map<String, Set<String>> organizationToPeople = new HashMap<String, Set<String>>();
    public final Map<String, Set<String>> personToOrganizations = new HashMap<String, Set<String>>();
}
