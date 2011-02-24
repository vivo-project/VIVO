/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.freemarker.valueobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author bkoniden
 * Deepak Konidena
 *
 */
public class Entity extends Individual{
	
	Set<Activity> activities = new HashSet<Activity>();
	Set<SubEntity> children = new LinkedHashSet<SubEntity>();
	
	public Entity(String departmentURI, String departmentLabel){
		super(departmentURI, departmentLabel);
	}
	
	public void setDepartmentLabel(String departmentURI){
		this.setIndividualLabel(departmentURI);
	}
	
	public String getEntityURI(){
		return this.getIndividualURI();
	}
	
	public Set<Activity> getActivities() {
		return activities;
	}

	public String getEntityLabel(){
		return this.getIndividualLabel();
	}

	public Set<SubEntity> getSubEntities() {
		return children;
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}

	public void addSubEntity(SubEntity subEntity) {
		this.children.add(subEntity);
		
	}
	
	public void addSubEntitities(Collection<SubEntity> subEntities) {
		this.children.addAll(subEntities);
		
	}

}
