/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;
import edu.cornell.mannlib.vitro.webapp.i18n.I18n;
import edu.cornell.mannlib.vitro.webapp.i18n.I18nBundle;

public class AddHeadOfRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {

	private static String template = "addHeadOfRoleToPerson.ftl";
	private static String OPTION_CLASS_URI = "http://xmlns.com/foaf/0.1/Organization";

    //Should this be overridden
	@Override
	String getTemplate() {
		return template;
	}

	@Override
	String getRoleType() {
		return "http://vivoweb.org/ontology/core#LeaderRole";
	}

	/** Head Of role involves hard-coded options for the "right side" of the role or activity */
    @Override
    FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {
    	// UQAM-Linguistic-Management Taking into account the linguistic context
		I18nBundle i18n = I18n.bundle(vreq);
		String i18nSelectType = i18n.text("select_type");
		String selectType = (i18nSelectType == null || i18nSelectType.isEmpty()) ? "Select type" : i18nSelectType ;

        return new
        ChildVClassesOptions(OPTION_CLASS_URI)
            .setDefaultOptionLabel(selectType);
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
