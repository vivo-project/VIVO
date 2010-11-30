/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author bkoniden
 * Deepak Konidena
 *
 */
public class Entity extends Individual{
	
	Set<BiboDocument> publications = new HashSet<BiboDocument>();
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
	
	public Set<BiboDocument> getPublications() {
		return publications;
	}

	public String getEntityLabel(){
		return this.getIndividualLabel();
	}

	public Set<SubEntity> getSubEntities() {
		return children;
	}

	public void addPublications(BiboDocument biboDocument) {
		this.publications.add(biboDocument);
	}

	public void addSubEntity(SubEntity subEntity) {
		this.children.add(subEntity);
		
	}

}
