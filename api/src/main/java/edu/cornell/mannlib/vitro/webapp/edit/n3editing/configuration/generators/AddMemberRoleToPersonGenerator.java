/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;
import edu.cornell.mannlib.vitro.webapp.i18n.I18n;
import edu.cornell.mannlib.vitro.webapp.i18n.I18nBundle;
import edu.cornell.mannlib.vitro.webapp.i18n.selection.SelectedLocale;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;

public class AddMemberRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {

	private static String template = "addMemberRoleToPerson.ftl";
	private static String VCLASS_URI = "http://xmlns.com/foaf/0.1/Organization";
	@Override
	String getTemplate() {
		return template;
	}

	@Override
	String getRoleType() {
		return "http://vivoweb.org/ontology/core#MemberRole";
	}


	FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {
	    return GeneratorUtil.buildResourceAndLabelFieldOptions(
	            vreq.getRDFService(), vreq.getWebappDaoFactory(), "", 
	            I18n.bundle(vreq).text("select_type"),
	            "http://vivoweb.org/ontology/core#AcademicDepartment",
	            "http://vivoweb.org/ontology/core#Association",
	            "http://vivoweb.org/ontology/core#Center",
	            "http://vivoweb.org/ontology/core#ClinicalOrganization",
	            "http://vivoweb.org/ontology/core#College",
	            "http://vivoweb.org/ontology/core#Committee",
	            "http://vivoweb.org/ontology/core#Company",
	            "http://vivoweb.org/ontology/core#Consortium",
	            "http://vivoweb.org/ontology/core#CoreLaboratory",
	            "http://vivoweb.org/ontology/core#Department",
	            "http://vivoweb.org/ontology/core#Division",
	            "http://vivoweb.org/ontology/core#ExtensionUnit",
	            "http://vivoweb.org/ontology/core#Foundation",
	            "http://vivoweb.org/ontology/core#FundingOrganization",
	            "http://vivoweb.org/ontology/core#GovernmentAgency",
	            "http://xmlns.com/foaf/0.1/Group",
	            "http://vivoweb.org/ontology/core#Hospital",
	            "http://vivoweb.org/ontology/core#Institute",
	            "http://vivoweb.org/ontology/core#Laboratory",
	            "http://vivoweb.org/ontology/core#Library",
	            "http://purl.obolibrary.org/obo/OBI_0000835" /* Manufacturer */,
	            "http://vivoweb.org/ontology/core#Museum",
	            "http://xmlns.com/foaf/0.1/Organization",
	            "http://vivoweb.org/ontology/core#PrivateCompany",
	            "http://vivoweb.org/ontology/core#Program",
	            "http://vivoweb.org/ontology/core#Publisher",
	            "http://vivoweb.org/ontology/core#ResearchOrganization",
	            "http://vivoweb.org/ontology/core#School",
	            "http://vivoweb.org/ontology/core#Team",
	            "http://vivoweb.org/ontology/core#ServiceProvidingLaboratory",
	            "http://vivoweb.org/ontology/core#StudentOrganization",
	            "http://purl.obolibrary.org/obo/ERO_0000565" /* Technology Transfer Office */,
	            "http://vivoweb.org/ontology/core#University");
	}

	@Override
	boolean isShowRoleLabelField(){return true;}

   /*
    * Use the methods below to change the date/time precision in the
    * custom form associated with this generator. When not used, the
    * precision will be YEAR. The other precisons are MONTH, DAY, HOUR,
    * MINUTE, TIME and NONE.
    */
/*
    public String getStartDatePrecision() {
        String precision = VitroVocabulary.Precision.MONTH.uri();
	    return precision;
    }

    public String getEndDatePrecision() {
        String precision = VitroVocabulary.Precision.DAY.uri();
	    return precision;
    }
*/
}
