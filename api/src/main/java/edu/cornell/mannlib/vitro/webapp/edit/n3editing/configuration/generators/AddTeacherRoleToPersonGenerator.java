/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;

public class AddTeacherRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {

	private static String template = "addTeacherRoleToPerson.ftl";

	@Override
	String getTemplate() {
		return template;
	}

	@Override
	String getRoleType() {
		return "http://vivoweb.org/ontology/core#TeacherRole";
	}


	/** Teacher role involves hard-coded options for the "right side"
	 * of the role or activity. */
	@Override
    FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {
		return new ConstantFieldOptions(
          "", "Select one",
          "http://purl.org/ontology/bibo/Conference", "Conference",
          "http://vivoweb.org/ontology/core#Course",  "Course",
          "http://purl.org/ontology/bibo/Workshop",   "Workshop");
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
