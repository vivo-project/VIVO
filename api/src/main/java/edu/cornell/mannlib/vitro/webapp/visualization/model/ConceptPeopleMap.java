/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConceptPeopleMap {
    public final Map<String, Set<String>> conceptToPeople = new HashMap<String, Set<String>>();
    public final Map<String, Set<String>> personToConcepts = new HashMap<String, Set<String>>();
}
