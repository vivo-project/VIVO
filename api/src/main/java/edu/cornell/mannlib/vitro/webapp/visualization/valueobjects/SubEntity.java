/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;

/**
 * @author bkoniden (Deepak Konidena)
 * modified by @author cdtank (Chintan Tank)
 * last modified at Mar 21, 2011 2:57:20 PM
 */
public class SubEntity extends Individual {

	private Set<Activity> activities = new HashSet<Activity>();
	private Set<String> entityTypes = new HashSet<String>();
	private VOConstants.EntityClassType entityClass;
	private String lastCachedAtDateTime = null;

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

	public void setEntityClass(VOConstants.EntityClassType entityClass) {
		this.entityClass = entityClass;
	}

	public VOConstants.EntityClassType getEntityClass() {
		return entityClass;
	}

	public void setLastCachedAtDateTime(String lastCachedAtDateTime) {
		this.lastCachedAtDateTime = lastCachedAtDateTime;
	}

	public String getLastCachedAtDateTime() {
		return lastCachedAtDateTime;
	}

}
