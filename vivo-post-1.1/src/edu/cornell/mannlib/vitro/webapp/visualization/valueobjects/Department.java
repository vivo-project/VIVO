package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Set;

public class Department extends Individual{
	
	Set<BiboDocument> publication;
	Set<Person> person;
	
	public Department(String departmentURI, String departmentLabel){
		super(departmentURI, departmentLabel);
	}
	
	public void setDepartmentLabel(String departmentURI){
		this.setIndividualLabel(departmentURI);
	}
	
	public String getDepartmentURI(){
		return this.getIndividualURI();
	}
	
	public Set<BiboDocument> getPublication() {
		return publication;
	}

	public String getDepartmentLabel(){
		return this.getIndividualLabel();
	}

	public Set<Person> getPerson() {
		return person;
	}

	public void addPublication(BiboDocument biboDocument) {
		this.publication.add(biboDocument);
	}

	public void addPersons(Person person) {
		this.person.add(person);
		
	}

	public void addPerson(Person person) {
		this.person.add(person);
		
	}

}
