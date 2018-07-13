/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.utils;

import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import org.apache.jena.ontology.OntModel;

import java.util.ArrayList;
import java.util.List;

public abstract class RelationshipCheckerRegistry {
    private final static List<RelationshipChecker> allCheckers = new ArrayList<>();

    public static void registerRelationshipChecker(RelationshipChecker rc) {
        boolean registered = false;
        for (RelationshipChecker checker : allCheckers) {
            if (checker.getClass().equals(rc.getClass())) {
                registered = true;
            }
        }

        if (!registered) {
            allCheckers.add(rc);
        }
    }

    public static boolean anyRelated(OntModel ontModel, List<String> fromUris, List<String> toUris) {
        for (RelationshipChecker rc : allCheckers) {
            if (rc.isRelated(ontModel, fromUris, toUris)) {
                return true;
            }
        }

        return false;
    }
}
