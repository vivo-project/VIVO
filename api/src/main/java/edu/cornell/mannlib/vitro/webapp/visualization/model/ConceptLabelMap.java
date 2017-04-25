/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConceptLabelMap {
    public final Map<String, String> conceptToLabel = new HashMap<String, String>();
    public final Map<String, Set<String>> lowerLabelToConcepts = new HashMap<String, Set<String>>();
}
