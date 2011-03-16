/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author bkoniden
 * Deepak Konidena
 */
public class SubEntity extends Individual {

	private Set<Activity> activities = new HashSet<Activity>();
	private Set<String> entityTypes = new HashSet<String>();
	
	public SubEntity(String individualURI) {
		super(individualURI);
	}
	
	public SubEntity(String individualURI, String individualLabel) {
		super(individualURI, individualLabel);
	}
	
	@Override
	public String toString() {
		return this.getIndividualLabel();
	}
	
	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}
	
	public void addActivities(Collection<Activity> activities) {
		this.activities.addAll(activities);
	}
	
	public Set<Activity> getActivities() {
		return activities;
	}
	
	public void addEntityTypeLabel(String typeLabel) {
		this.entityTypes.add(typeLabel);
	}
	
	public Set<String> getEntityTypeLabels() {
		return entityTypes;
	}

}
