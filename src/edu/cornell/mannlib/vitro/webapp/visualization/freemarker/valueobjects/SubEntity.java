/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * @author bkoniden
 * Deepak Konidena
 */
public class SubEntity extends Individual {

	Set<Activity> activities = new HashSet<Activity>();
	Map<String, Map<String, String>> personToPositionAndStartYear = new HashMap<String, Map<String, String>>(); 
	
	public SubEntity(String individualURI) {
		super(individualURI);
	}
	
	public Map<String, Map<String, String>> getPersonToPositionAndStartYear() {
		return personToPositionAndStartYear;
	}

	public void setPersonToPositionAndStartYear(
			Map<String, Map<String, String>> personToPositionAndStartYear) {
		this.personToPositionAndStartYear = personToPositionAndStartYear;
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public SubEntity(String individualURI, String individualLabel) {
		super(individualURI, individualLabel);
	}
	
	@Override
	public String toString(){
		return this.getIndividualLabel();
	}
	
	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}

}
