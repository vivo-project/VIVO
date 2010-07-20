/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * This is the Value Object equivalent for vivo:AcademicDepartmentOrDivision object type.
 * @author cdtank
 *
 */
public class VivoDepartmentOrDivision extends Individual {

	private Set<VivoCollegeOrSchool> parentColleges = new HashSet<VivoCollegeOrSchool>();

	public VivoDepartmentOrDivision(String departmentURL, VivoCollegeOrSchool parentCollege) {
		super(departmentURL);
		addParentCollege(parentCollege);
	}

	public Set<VivoCollegeOrSchool> getParentCollege() {
		return parentColleges;
	}

	public void addParentCollege(VivoCollegeOrSchool parentCollege) {
		this.parentColleges.add(parentCollege);
	}

	public String getDepartmentURL() {
		return this.getIndividualURL();
	}

	public String getDepartmentLabel() {
		return this.getIndividualLabel();
	}

	public void setDepartmentLabel(String departmentLabel) {
		this.setIndividualLabel(departmentLabel);
	}

}
