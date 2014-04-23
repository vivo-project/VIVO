/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class determines what n3 should be returned for a particular data getter and can be overwritten or extended in VIVO. 
 */
public class ProcessDataGetterN3Map {
    private static final Log log = LogFactory.getLog(ProcessDataGetterN3Map.class);
    public  static HashMap<String, String> getDataGetterTypeToProcessorMap() {
    	 HashMap<String, String> map = new HashMap<String, String>();
    	 map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SparqlQueryDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessSparqlDataGetterN3");
    	 map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessClassGroupDataGetterN3");
    	 map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessInternalClassDataGetterN3");
    	 map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.FixedHTMLDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessFixedHTMLN3");
    	 map.put("edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SearchIndividualsDataGetter", "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessSearchIndividualsDataGetterN3");

    	 return map;
    }
}