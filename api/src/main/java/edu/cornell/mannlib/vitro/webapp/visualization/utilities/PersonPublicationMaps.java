/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PersonPublicationMaps {
    public Map<String, Set<String>> personToPublication = new HashMap<String, Set<String>>();
    public Map<String, Set<String>> publicationToPerson = new HashMap<String, Set<String>>();

    public void put(String person, String document) {
        Set<String> documentSet = personToPublication.get(person);
        if (documentSet == null) {
            documentSet = new HashSet<String>();
            documentSet.add(document);
            personToPublication.put(person, documentSet);
        } else {
            documentSet.add(document);
        }

        Set<String> personSet = publicationToPerson.get(document);
        if (personSet == null) {
            personSet = new HashSet<String>();
            personSet.add(person);
            publicationToPerson.put(document, personSet);
        } else {
            personSet.add(person);
        }
    }
}

