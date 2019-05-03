/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Adds static Strings that may be useful for forms that are part of VIVO.
 *
 * @author bdc34
 *
 */
public abstract class VivoBaseGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {

    final static String vivoCore ="http://vivoweb.org/ontology/core#" ;
    final static String rdfs =VitroVocabulary.RDFS ;
    final static String foaf = "http://xmlns.com/foaf/0.1/";
    final static String type =VitroVocabulary.RDF_TYPE ;
    final static String label =rdfs+"label" ;
    final static String bibo = "http://purl.org/ontology/bibo/";

    final static String edProcessClass = vivoCore+"EducationalProcess" ;
    final static String degreeTypeClass =vivoCore+"AcademicDegree" ;
    final static String majorFieldPred =vivoCore+"majorField" ;
    final static String deptPred =vivoCore+"departmentOrSchool" ;
    final static String infoPred =vivoCore+"supplementalInformation" ;
    final static String authorRankPredicate = vivoCore + "authorRank";
    final static String linkedAuthorPredicate = vivoCore + "relates";

    final static String dateTimeValue =vivoCore+"dateTime";
    final static String dateTimeValueType =vivoCore+"DateTimeValue";
    final static String dateTimePrecision =vivoCore+"dateTimePrecision";

    final static String toInterval =vivoCore+"dateTimeInterval";
    final static String intervalType =vivoCore+"DateTimeInterval";
    final static String intervalToStart =vivoCore+"start";
    final static String intervalToEnd =vivoCore+"end";

    final static String orgClass ="http://xmlns.com/foaf/0.1/Organization" ;
    final static String personClass = foaf + "Person";

}
