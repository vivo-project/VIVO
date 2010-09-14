package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

public class Department extends Individual{
	
	BiboDocument publication;
	
	public Department(String departmentURI, String departmentLabel){
		super(departmentURI, departmentLabel);
	}
	
	public void setDepartmentLabel(String departmentURI){
		this.setIndividualLabel(departmentURI);
	}
	
	public String getDepartmentURI(){
		return this.getIndividualURI();
	}
	
	public String getDepartmentLabel(){
		return this.getIndividualLabel();
	}
}
