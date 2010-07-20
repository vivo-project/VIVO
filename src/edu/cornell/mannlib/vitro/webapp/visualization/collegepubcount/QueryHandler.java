/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.collegepubcount;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
import com.hp.hpl.jena.iri.Violation;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants.EmployeeType;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.VivoCollegeOrSchool;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.VivoDepartmentOrDivision;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.VivoEmployee;



/**
 * @author cdtank
 *
 */
public class QueryHandler {

	protected static final Syntax SYNTAX = Syntax.syntaxARQ;

	private String collegeURIParam, resultFormatParam, rdfResultFormatParam;
	private Map<String, VivoCollegeOrSchool> collegeURLToVO = new HashMap<String, VivoCollegeOrSchool>();
	private DataSource dataSource;

	private Log log;

	public QueryHandler(String collegeURIParam,
			String resultFormatParam, String rdfResultFormatParam,
			DataSource dataSource, Log log) {

		this.collegeURIParam = collegeURIParam;
		this.resultFormatParam = resultFormatParam;
		this.rdfResultFormatParam = rdfResultFormatParam;
		this.dataSource = dataSource;
		this.log = log;

	}

	private Set<VivoEmployee> createJavaValueObjects(ResultSet resultSet) {
		
		Set<VivoEmployee> collegeAcademicEmployees = new HashSet<VivoEmployee>();
		
		Map<String, VivoDepartmentOrDivision> departmentURLToVO = new HashMap<String, VivoDepartmentOrDivision>();
		Map<String, VivoEmployee> employeeURLToVO = new HashMap<String, VivoEmployee>();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();

			String collegeURL = solution.get(QueryFieldLabels.COLLEGE_URL).toString();
			
			VivoCollegeOrSchool currentCollege;
			
			if (collegeURLToVO.containsKey(collegeURL)) {
				currentCollege = collegeURLToVO.get(collegeURL);
			} else {
				currentCollege = new VivoCollegeOrSchool(collegeURL);
				collegeURLToVO.put(collegeURL, currentCollege);
				
				RDFNode collegeLabelNode = solution.get(QueryFieldLabels.COLLEGE_LABEL);
				if (collegeLabelNode != null) {
					currentCollege.setCollegeLabel(collegeLabelNode.toString());
				}
			}

			String departmentURL = solution.get(QueryFieldLabels.DEPARTMENT_URL).toString();
			
			VivoDepartmentOrDivision currentDepartment;
			
			if (departmentURLToVO.containsKey(departmentURL)) {
				currentDepartment = departmentURLToVO.get(departmentURL);
				currentDepartment.addParentCollege(currentCollege);
			} else {
				currentDepartment = new VivoDepartmentOrDivision(departmentURL, currentCollege);
				departmentURLToVO.put(departmentURL, currentDepartment);

				RDFNode departmentLabelNode = solution.get(QueryFieldLabels.DEPARTMENT_LABEL);
				if (departmentLabelNode != null) {
					currentDepartment.setDepartmentLabel(departmentLabelNode.toString());
				}
			}

			currentCollege.addDepartment(currentDepartment);
			
			RDFNode employeeNode = solution.get(QueryFieldLabels.ACADEMIC_FACULTY_EMPLOYEE_URL);
			EmployeeType currentEmployeeType;
			VivoEmployee currentEmployee;
			
			if (employeeNode != null) {
				currentEmployeeType = EmployeeType.ACADEMIC_FACULTY_EMPLOYEE;
			} else {
				currentEmployeeType = EmployeeType.ACADEMIC_STAFF_EMPLOYEE;
				employeeNode = solution.get(QueryFieldLabels.ACADEMIC_STAFF_EMPLOYEE_URL);
			}
			
			if (employeeURLToVO.containsKey(employeeNode.toString())) {
				currentEmployee = employeeURLToVO.get(employeeNode.toString());
				currentEmployee.addParentDepartment(currentDepartment);
			} else {
				currentEmployee = new VivoEmployee(employeeNode.toString(), currentEmployeeType, currentDepartment);
				RDFNode authorLabelNode = solution.get(QueryFieldLabels.AUTHOR_LABEL);
				if (authorLabelNode != null) {
					currentEmployee.setIndividualLabel(authorLabelNode.toString());
				}
				employeeURLToVO.put(employeeNode.toString(), currentEmployee);
			}
			
			RDFNode documentNode = solution.get(QueryFieldLabels.DOCUMENT_URL);
			if (documentNode != null) {
				
				/*
				 * A document can have multiple authors but if the same author serves in multiple departments
				 * then we need to account for "An Authored Document" only once. This check will make sure that
				 * a document by same author is not added twice just because that author serves in 2 distinct 
				 * department.
				 * */
				boolean isNewDocumentAlreadyAdded = testForDuplicateEntryOfDocument(
															currentEmployee, 
															documentNode);
				
				if (!isNewDocumentAlreadyAdded) {
					currentEmployee.addAuthorDocument(createAuthorDocumentsVO(solution, documentNode.toString()));
				}
				
			}
			
			collegeAcademicEmployees.add(currentEmployee);
		}
		
		
/*		System.out.println(collegeURLToVO);
		System.out.println("------------------------------------------------------------");
		System.out.println(departmentURLToVO);
		System.out.println("------------------------------------------------------------");
		System.out.println(employeeURLToVO);
		System.out.println("------------------------------------------------------------");
*/		
		return collegeAcademicEmployees;
	}

	private boolean testForDuplicateEntryOfDocument(
			VivoEmployee currentEmployee, RDFNode documentNode) {
		boolean isNewDocumentAlreadyAdded = false;
		
		for (BiboDocument currentAuthoredDocument : currentEmployee.getAuthorDocuments()) {
			if (currentAuthoredDocument.getDocumentURL().equalsIgnoreCase(documentNode.toString())) {
				isNewDocumentAlreadyAdded = true;
				break;
			}
		}
		return isNewDocumentAlreadyAdded;
	}

	public Map<String, VivoCollegeOrSchool> getCollegeURLToVO() {
		return collegeURLToVO;
	}

	private BiboDocument createAuthorDocumentsVO(QuerySolution solution, String documentURI) {

			BiboDocument biboDocument = new BiboDocument(documentURI);

			RDFNode documentLabelNode = solution.get(QueryFieldLabels.DOCUMENT_LABEL);
			if (documentLabelNode != null) {
				biboDocument.setDocumentLabel(documentLabelNode.toString());
			}

			RDFNode documentBlurbNode = solution.get(QueryFieldLabels.DOCUMENT_BLURB);
			if (documentBlurbNode != null) {
				biboDocument.setDocumentBlurb(documentBlurbNode.toString());
			}

			RDFNode documentMonikerNode = solution.get(QueryFieldLabels.DOCUMENT_MONIKER);
			if (documentMonikerNode != null) {
				biboDocument.setDocumentMoniker(documentMonikerNode.toString());
			}

			RDFNode documentDescriptionNode = solution.get(QueryFieldLabels.DOCUMENT_DESCRIPTION);
			if (documentDescriptionNode != null) {
				biboDocument.setDocumentDescription(documentDescriptionNode.toString());
			}

			RDFNode publicationYearNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR);
			if (publicationYearNode != null) {
				biboDocument.setPublicationYear(publicationYearNode.toString());
			}
			
			RDFNode publicationYearMonthNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR_MONTH);
			if (publicationYearMonthNode != null) {
				biboDocument.setPublicationYearMonth(publicationYearMonthNode.toString());
			}
			
			RDFNode publicationDateNode = solution.get(QueryFieldLabels.DOCUMENT_PUBLICATION_DATE);
			if (publicationDateNode != null) {
				biboDocument.setPublicationDate(publicationDateNode.toString());
			}
			
			return biboDocument;
	}
	
	private ResultSet executeQuery(String queryText,
								   String resultFormatParam, 
								   String rdfResultFormatParam, 
								   DataSource dataSource) {

        QueryExecution queryExecution = null;
        try{
            Query query = QueryFactory.create(queryText, SYNTAX);

//            QuerySolutionMap qs = new QuerySolutionMap();
//            qs.add("authPerson", queryParam); // bind resource to s
            
            queryExecution = QueryExecutionFactory.create(query, dataSource);
            

            //remocve this if loop after knowing what is describe & construct sparql stuff.
            if (query.isSelectType()){
                return queryExecution.execSelect();
            }
        } finally {
            if(queryExecution != null) {
            	queryExecution.close();
            }

        }
		return null;
    }

	private String generateCollegeEmployeeSparqlQuery(String queryURI) {
//		Resource uri1 = ResourceFactory.createResource(queryURI);

		String sparqlQuery = QueryConstants.getSparqlPrefixQuery()
							+ "SELECT (str(?collegeLabel) as ?" + QueryFieldLabels.COLLEGE_LABEL + ") " 
							+ "		(str(?department) as ?" + QueryFieldLabels.DEPARTMENT_URL + ") " 
							+ "		(str(?departmentLabel) as ?" + QueryFieldLabels.DEPARTMENT_LABEL + ") " 
							+ "		(str(?academicFacultyEmployee) as ?" + QueryFieldLabels.ACADEMIC_FACULTY_EMPLOYEE_URL + ") " 
							+ "		(str(?academicStaffEmployee) as ?" + QueryFieldLabels.ACADEMIC_STAFF_EMPLOYEE_URL + ") "
							+ "		(str(<" + queryURI + ">) as ?" + QueryFieldLabels.COLLEGE_URL + ") "
							+ "		(str(?authorLabel) as ?" + QueryFieldLabels.AUTHOR_LABEL + ") "
							+ "		(str(?document) as ?" + QueryFieldLabels.DOCUMENT_URL + ") "
							+ "		(str(?documentMoniker) as ?" + QueryFieldLabels.DOCUMENT_MONIKER + ") "
							+ "		(str(?documentLabel) as ?" + QueryFieldLabels.DOCUMENT_LABEL + ") "
							+ "		(str(?documentBlurb) as ?" + QueryFieldLabels.DOCUMENT_BLURB + ") "
							+ "		(str(?documentDescription) as ?" + QueryFieldLabels.DOCUMENT_DESCRIPTION + ") "
							+ "		(str(?publicationYear) as ?" + QueryFieldLabels.DOCUMENT_PUBLICATION_YEAR + ") "
							+ "WHERE { "
							+ "<" + queryURI + "> rdf:type vivo:CollegeOrSchoolWithinUniversity; " +
									"rdfs:label ?collegeLabel; " +
									"vivo:hasAcademicDepartmentOrDivision ?department . "
							+ "?department rdfs:label ?departmentLabel ."
							+ "{ "
							+ getAcademicEmployeePublicationsSparqlQuery("academicFacultyEmployee", "vivo:hasEmployeeAcademicFacultyMember")
							+ " UNION " 
							+ getAcademicEmployeePublicationsSparqlQuery("academicStaffEmployee", "vivo:hasEmployeeAcademicStaffMember")
							+ "}"
							+ "}";

//		System.out.println(sparqlQuery);
		
		return sparqlQuery;
	}

	
	private String getAcademicEmployeePublicationsSparqlQuery(String employeeHandle, 
															  String ontologyHandle) {
		
		String sparqlQuery = " {?department " + ontologyHandle + " ?" + employeeHandle + " . " 
							+ "?" + employeeHandle + " rdf:type foaf:Person; rdfs:label ?authorLabel. " 
							+ "OPTIONAL { ?" + employeeHandle + " vivo:authorOf ?document ." +
										" ?document rdf:type bibo:Document ." +
										" ?document rdfs:label ?documentLabel ." +
										" OPTIONAL { ?document vitro:moniker ?documentMoniker } ." +
										" OPTIONAL { ?document vitro:blurb ?documentBlurb } ." +
										" OPTIONAL { ?document vitro:description ?documentDescription } ." +
										" OPTIONAL { ?document vivo:publicationYear ?publicationYear } ." +
										"}" +
								"} ";
		
		return sparqlQuery;
		
	}
	
	public Set<VivoEmployee> getVisualizationJavaValueObjects()
		throws MalformedQueryParametersException {

        if (this.collegeURIParam == null || "".equals(collegeURIParam)) {
        	throw new MalformedQueryParametersException("URI parameter is either null or empty.");
        } else {

        	/*
        	 * To test for the validity of the URI submitted.
        	 * */
        	IRIFactory iRIFactory = IRIFactory.jenaImplementation();
    		IRI iri = iRIFactory.create(this.collegeURIParam);
            if (iri.hasViolation(false)) {
                String errorMsg = ((Violation)iri.violations(false).next()).getShortMessage()+" ";
                log.error("Pub Count Vis Query " + errorMsg);
                throw new MalformedQueryParametersException("URI provided for an individual is malformed.");
            }
        }

		ResultSet resultSet	= executeQuery(generateCollegeEmployeeSparqlQuery(this.collegeURIParam),
										   this.resultFormatParam,
										   this.rdfResultFormatParam,
										   this.dataSource);

		return createJavaValueObjects(resultSet);
	}

}
