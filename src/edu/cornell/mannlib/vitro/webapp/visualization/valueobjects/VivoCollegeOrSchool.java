/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * This is the Value Object equivalent for vivo:CollegeOrSchoolWithinUniversity object type.
 * @author cdtank
 *
 */
public class VivoCollegeOrSchool extends Individual {

	private Set<VivoDepartmentOrDivision> departments = new HashSet<VivoDepartmentOrDivision>();

	public VivoCollegeOrSchool(String collegeURL) {
		super(collegeURL);
	}
	
	public Set<VivoDepartmentOrDivision> getDepartments() {
		return departments;
	}

	public void addDepartment(VivoDepartmentOrDivision department) {
		this.departments.add(department);
	}

	public String getCollegeURL() {
		return this.getIndividualURL();
	}

	public String getCollegeLabel() {
		return this.getIndividualLabel();
	}

	public void setCollegeLabel(String collegeLabel) {
		this.setIndividualLabel(collegeLabel);
	}

}
