/* $This file is distributed under the terms of the license in LICENSE$ */

package org.vivoweb.webapp.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;


public class ModelUtils {

	private static final Log log = LogFactory.getLog(ModelUtils.class.getName());

    private static final String processPropertyURI = "http://purl.obolibrary.org/obo/BFO_0000054";
    private static final String processPropertyInverseURI = "http://purl.obolibrary.org/obo/BFO_0000055";
    private static final String nonProcessPropertyURI = "http://vivoweb.org/ontology/core#roleContributesTo";
	private static final String nonProcessPropertyInverseURI = "http://vivoweb.org/ontology/core#contributingRole";
    private static final String grantPropertyURI = "http://vivoweb.org/ontology/core#relatedBy";
	private static final String grantPropertyInverseURI = "http://vivoweb.org/ontology/core#relates";

	private static Set<String> processClass = new HashSet<String>();
	static {
		processClass.add("http://vivoweb.org/ontology/core#Project");
		processClass.add("http://purl.org/NET/c4dm/event.owl#Event");
		processClass.add("http://vivoweb.org/ontology/core#EventSeries");
	}

	private static Set<String> grantClass = new HashSet<String>();
	static {
		grantClass.add("http://vivoweb.org/ontology/core#Grant");
	}
	/*
	 * Given a class URI that represents the type of entity that a Role
	 * is in (in, in any of its senses) this method returns the URIs of
	 * the properties that should use used to relate the Role to the entity.
	 *
	 * Note: we may want to change the implementation of this method
	 * to check whether the target class has both a parent that is a
	 * BFO Process and a parent that is not a BFO Process and issue
	 * a warning if so.
	 */
	public static ObjectProperty getPropertyForRoleInClass(String classURI, WebappDaoFactory wadf) {

		if (classURI == null) {
			log.error("input classURI is null");
			return null;
		}

		if (wadf == null) {
			log.error("input WebappDaoFactory is null");
			return null;
		}

		VClassDao vcd = wadf.getVClassDao();
		List<String> superClassURIs = vcd.getSuperClassURIs(classURI, false);
		superClassURIs.add(classURI);
		Iterator<String> iter = superClassURIs.iterator();

		ObjectProperty op = new ObjectProperty();
		boolean isBFOProcess = false;
		boolean isGrantClass = false;

	    while (iter.hasNext()) {
	    	String superClassURI = iter.next();

	    	if (processClass.contains(superClassURI)) {
	    		isBFOProcess = true;
	    		break;
	    	}
	    	if (grantClass.contains(superClassURI)) {
	    		isGrantClass = true;
	    		break;
	    	}
	    }

	    if (isBFOProcess) {
			op.setURI(processPropertyURI);
			op.setURIInverse(processPropertyInverseURI);
	    }
	    else if (isGrantClass){
			op.setURI(grantPropertyURI);
			op.setURIInverse(grantPropertyInverseURI);
	    }
	    else {
			op.setURI(nonProcessPropertyURI);
			op.setURIInverse(nonProcessPropertyInverseURI);
	    }

		return op;
	}

	//Return list of all possible predicates
	public static List<String> getPossiblePropertiesForRole() {
		List<String> properties = new ArrayList<String>();
		properties.add(processPropertyURI);
		properties.add(nonProcessPropertyURI);
		properties.add(grantPropertyURI);
		return properties;
	}

	public static List<String> getPossibleInversePropertiesForRole() {
		List<String> properties = new ArrayList<String>();
		properties.add(processPropertyInverseURI);
		properties.add(nonProcessPropertyInverseURI);
		properties.add(grantPropertyInverseURI);
		return properties;
	}
}
