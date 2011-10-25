/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.util;

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
		
	private static final String processPropertyURI = "http://vivoweb.org/ontology/core#roleRealizedIn";
	private static final String processPropertyInverseURI = "http://vivoweb.org/ontology/core#realizedRole";
	private static final String nonProcessPropertyURI = "http://vivoweb.org/ontology/core#roleContributesTo";
	private static final String nonProcessPropertyInverseURI = "http://vivoweb.org/ontology/core#ContributingRole";
	
	private static Set<String> processClass = new HashSet<String>();
	static {
		processClass.add("http://vivoweb.org/ontology/core#Process");
		processClass.add("http://purl.org/NET/c4dm/event.owl#Event");
		processClass.add("http://xmlns.com/foaf/0.1/Agent");
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
		Iterator<String> iter = superClassURIs.iterator();
		
		ObjectProperty op = new ObjectProperty();
		boolean isBFOProcess = false;

	    while (iter.hasNext()) {
	    	String superClassURI = iter.next();
	    	
	    	if (processClass.contains(superClassURI)) {
	    		isBFOProcess = true;
	    		break;
	    	}
	    }
		
	    if (isBFOProcess) {
			op.setURI(processPropertyURI);
			op.setURIInverse(processPropertyInverseURI);    	
	    } else {
			op.setURI(nonProcessPropertyURI);
			op.setURIInverse(nonProcessPropertyInverseURI);    		    	
	    }
	    
		return op;
	}	
}
