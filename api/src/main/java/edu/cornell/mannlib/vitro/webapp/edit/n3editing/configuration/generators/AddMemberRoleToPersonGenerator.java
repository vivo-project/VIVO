/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;

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

    @Override
    FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {
                    return new ConstantFieldOptions(
            "","Select type",
            "http://vivoweb.org/ontology/core#AcademicDepartment","Academic Department",
            "http://vivoweb.org/ontology/core#Association","Association",
            "http://vivoweb.org/ontology/core#Center","Center",
            "http://vivoweb.org/ontology/core#ClinicalOrganization","Clinical Organization",
            "http://vivoweb.org/ontology/core#College","College",
            "http://vivoweb.org/ontology/core#Committee","Committee",
            "http://vivoweb.org/ontology/core#Company","Company",
            "http://vivoweb.org/ontology/core#Consortium","Consortium",
            "http://vivoweb.org/ontology/core#CoreLaboratory","Core Laboratory",
            "http://vivoweb.org/ontology/core#Department","Department",
            "http://vivoweb.org/ontology/core#Division","Division",
            "http://vivoweb.org/ontology/core#ExtensionUnit","Extension Unit",
            "http://vivoweb.org/ontology/core#Foundation","Foundation",
            "http://vivoweb.org/ontology/core#FundingOrganization","Funding Organization",
            "http://vivoweb.org/ontology/core#GovernmentAgency","Government Agency",
            "http://xmlns.com/foaf/0.1/Group","Group",
            "http://vivoweb.org/ontology/core#Hospital","Hospital",
            "http://vivoweb.org/ontology/core#Institute","Institute",
            "http://vivoweb.org/ontology/core#Laboratory","Laboratory",
            "http://vivoweb.org/ontology/core#Library","Library",
            "http://purl.obolibrary.org/obo/OBI_0000835","Manufacturer",
            "http://vivoweb.org/ontology/core#Museum","Museum",
            "http://xmlns.com/foaf/0.1/Organization","Organization",
            "http://vivoweb.org/ontology/core#PrivateCompany","Private Company",
            "http://vivoweb.org/ontology/core#Program","Program",
            "http://vivoweb.org/ontology/core#Publisher","Publisher",
            "http://vivoweb.org/ontology/core#ResearchOrganization","Research Organization",
            "http://vivoweb.org/ontology/core#School","School",
            "http://vivoweb.org/ontology/core#Team","Team",
            "http://vivoweb.org/ontology/core#ServiceProvidingLaboratory","Service Providing Lab",
            "http://vivoweb.org/ontology/core#StudentOrganization","Student Organization",
            "http://purl.obolibrary.org/obo/ERO_0000565","Technology Transfer Office",
            "http://vivoweb.org/ontology/core#University","University");
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
