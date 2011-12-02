/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.service;

import java.util.List;

import edu.cornell.mannlib.semservices.bo.Concept;

public interface ExternalConceptService {
   // this is the only method that needs to be exposed
   List<Concept> processResults(String term) throws Exception;

}
