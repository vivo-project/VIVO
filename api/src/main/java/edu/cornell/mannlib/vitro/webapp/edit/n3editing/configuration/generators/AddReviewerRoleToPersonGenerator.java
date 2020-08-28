/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldOptions;
import edu.cornell.mannlib.vitro.webapp.i18n.I18n;

public class AddReviewerRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {

    private static String OBJECT_VCLASS_URI = "http://purl.org/ontology/bibo/Document";

	@Override
	String getTemplate() { return "addReviewerRoleToPerson.ftl"; }

    //The default activityToRolePredicate and roleToActivityPredicates are
	//correct for this subclass so they don't need to be overwritten

/*	@Override
	public String getRoleToActivityPredicate(VitroRequest vreq) {
		return "<http://purl.obolibrary.org/obo/BFO_0000054>";
	}
*/
	//role type will always be set based on particular form
	@Override
	public String getRoleType() {
		//TODO: Get dynamic way of including vivoweb ontology
		return "http://vivoweb.org/ontology/core#ReviewerRole";
	}

	/**
	 *  Each subclass generator will return its own type of option here:
	 *   whether literal hardcoded, based on class group, or subclasses of a specific class
	 */
	@Override
	FieldOptions getRoleActivityFieldOptions(VitroRequest vreq) throws Exception {
	    return GeneratorUtil.buildResourceAndLabelFieldOptions(
	            vreq.getRDFService(), vreq.getWebappDaoFactory(), "", 
	            I18n.bundle(vreq).text("select_type"),
	            "http://purl.org/ontology/bibo/AcademicArticle",
	            "http://purl.org/ontology/bibo/Article",
	            "http://purl.org/ontology/bibo/AudioDocument",
	            "http://purl.org/ontology/bibo/AudioVisualDocument",
	            "http://purl.org/ontology/bibo/Bill",
	            "http://vivoweb.org/ontology/core#Blog",
	            "http://vivoweb.org/ontology/core#BlogPosting",
	            "http://purl.org/ontology/bibo/Book",
	            "http://purl.org/ontology/bibo/BookSection",
	            "http://purl.org/ontology/bibo/Brief",
	            "http://vivoweb.org/ontology/core#CaseStudy",
	            "http://vivoweb.org/ontology/core#Catalog",
	            "http://purl.org/ontology/bibo/Chapter",
	            "http://purl.org/spar/fabio/ClinicalGuideline",
	            "http://purl.org/ontology/bibo/Code",
	            "http://purl.org/ontology/bibo/CollectedDocument",
	            "http://purl.org/spar/fabio/Comment",
	            "http://vivoweb.org/ontology/core#ConferencePaper",
	            "http://vivoweb.org/ontology/core#ConferencePoster",
	            "http://purl.org/ontology/bibo/CourtReporter",
	            "http://vivoweb.org/ontology/core#Database",
	            "http://purl.org/ontology/bibo/LegalDecision",
	            "http://purl.org/ontology/bibo/DocumentPart",
	            "http://purl.org/ontology/bibo/EditedBook",
	            "http://vivoweb.org/ontology/core#EditorialArticle",
	            "http://purl.org/spar/fabio/Erratum",
	            "http://purl.org/ontology/bibo/Excerpt",
	            "http://purl.org/ontology/bibo/Film",
	            "http://purl.org/ontology/bibo/Image",
	            "http://purl.org/ontology/bibo/Issue",
	            "http://purl.org/ontology/bibo/Journal",
	            "http://purl.obolibrary.org/obo/IAO_0000013" /* "Journal Article" */,
	            "http://purl.org/ontology/bibo/LegalCaseDocument",
	            "http://purl.org/ontology/bibo/LegalDocument",
	            "http://purl.org/ontology/bibo/Legislation",
	            "http://purl.org/ontology/bibo/Letter",
	            "http://purl.org/ontology/bibo/Magazine",
	            "http://purl.org/ontology/bibo/Manual",
	            "http://purl.org/ontology/bibo/Manuscript",
	            "http://purl.org/ontology/bibo/Map",
	            "http://vivoweb.org/ontology/core#Newsletter",
	            "http://purl.org/ontology/bibo/Newspaper",
	            "http://vivoweb.org/ontology/core#NewsRelease",
	            "http://purl.org/ontology/bibo/Note",
	            "http://purl.org/ontology/bibo/Patent",
	            "http://purl.org/ontology/bibo/Periodical",
	            "http://purl.org/ontology/bibo/PersonalCommunicationDocument",
	            "http://purl.org/ontology/bibo/Proceedings",
	            "http://purl.obolibrary.org/obo/OBI_0000272" /* Protocol" */,
	            "http://purl.org/ontology/bibo/Quote",
	            "http://purl.org/ontology/bibo/ReferenceSource",
	            "http://purl.org/ontology/bibo/Report",
	            "http://vivoweb.org/ontology/core#ResearchProposal",
	            "http://vivoweb.org/ontology/core#Review",
	            "http://vivoweb.org/ontology/core#Score",
	            "http://vivoweb.org/ontology/core#Screenplay",
	            "http://purl.org/ontology/bibo/Series",
	            "http://purl.org/ontology/bibo/Slide",
	            "http://purl.org/ontology/bibo/Slideshow",
	            "http://vivoweb.org/ontology/core#Speech",
	            "http://purl.org/ontology/bibo/Standard",
	            "http://purl.org/ontology/bibo/Statute",
	            "http://purl.org/ontology/bibo/Thesis",
	            "http://vivoweb.org/ontology/core#Translation",
	            "http://vivoweb.org/ontology/core#Video",
	            "http://purl.org/ontology/bibo/Webpage",
	            "http://purl.org/ontology/bibo/Website",
	            "http://vivoweb.org/ontology/core#WorkingPaper");
	}

	//isShowRoleLabelField remains true for this so doesn't need to be overwritten
	public boolean isShowRoleLabelField() {
		return false;
	}

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
