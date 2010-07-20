/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.HashSet;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants.EmployeeType;

/**
 * 
 * This is the Value Object equivalent for vivo's Employee object type.
 * @author cdtank
 *
 */
public class VivoEmployee extends Individual {

	private EmployeeType employeeType; 
	private Set<VivoDepartmentOrDivision> parentDepartments = new HashSet<VivoDepartmentOrDivision>();
	private Set<BiboDocument> authorDocuments = new HashSet<BiboDocument>();

	public VivoEmployee(String employeeURL, 
						EmployeeType employeeType, 
						VivoDepartmentOrDivision parentDepartment) {
		super(employeeURL);
		addParentDepartment(parentDepartment);
	}

	public String getEmployeeURL() {
		return this.getIndividualURL();
	}

	public String getEmployeeName() {
		return this.getIndividualLabel();
	}
	
	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(EmployeeType employeeType) {
		this.employeeType = employeeType;
	}

	public Set<VivoDepartmentOrDivision> getParentDepartments() {
		return parentDepartments;
	}

	public void addParentDepartment(VivoDepartmentOrDivision parentDepartment) {
		this.parentDepartments.add(parentDepartment);
	}

	public Set<BiboDocument> getAuthorDocuments() {
		return authorDocuments;
	}

	public void addAuthorDocument(BiboDocument authorDocument) {
		this.authorDocuments.add(authorDocument);
	}


}
