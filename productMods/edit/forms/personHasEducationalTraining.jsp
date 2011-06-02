<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding an educational attainment to an individual

Classes: 
core:EducationalTraining - primary new individual being created
foaf:Person - existing individual
foaf:Organization - new or existing individual
core:AcademicDegree - existing individual

Data properties of EducationalTraining:
core:majorField
core:departmentOrSchool
core:supplementalInformation

Object properties (domain : range)

core:educationalTraining (Person : EducationalTraining) - inverse of core:educationalTrainingOf
core:educationalTrainingOf (EducationalTraining : Person) - inverse of core:educationalTraining

core:degreeEarned (EducationalTraining : AcademicDegree) - inverse of core:degreeOutcomeOf
core:degreeOutcomeOf (AcademicDegree : EducationalTraining) - inverse of core:degreeEarned

core:organizationGrantingDegree (EducationalTraining : Organization) - no inverse

Future version
--------------
Classes:
core:DateTimeValue
core:DateTimeValuePrecision
Object properties:
core:dateTimeValue (EducationalTraining : DateTimeValue)
core:dateTimePrecision (DateTimeValue : DateTimeValuePrecision)
--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal"%>
<%@ page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary.Precision"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.Field"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.elements.DateTimeWithPrecision"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.DateTimeIntervalValidation"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.personHasEducationalBackground.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    request.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    request.setAttribute("gYearDatatypeUriJson", MiscWebUtils.escape(XSD.gYear.toString()));
    
    /*
    There are 4 modes that this form can be in: 
     1.  Add, there is a subject and a predicate but no position and nothing else. 
           
     2. normal edit where everything should already be filled out.  There is a subject, a object and an individual on
        the other end of the object's core:trainingAtOrganization stmt. 
     
     3. Repair a bad role node.  There is a subject, prediate and object but there is no individual on the 
        other end of the object's core:trainingAtOrganization stmt.  This should be similar to an add but the form should be expanded.
        
     4. Really bad node. multiple core:trainingAtOrganization statements.   
   */

    EditMode mode = FrontEndEditingUtils.getEditMode(request, "http://vivoweb.org/ontology/core#trainingAtOrganization");

    if( mode == EditMode.ADD ) {
       %> <c:set var="editMode" value="add"/><%
    } else if(mode == EditMode.EDIT){
        %> <c:set var="editMode" value="edit"/><%
    } else if(mode == EditMode.REPAIR){
        %> <c:set var="editMode" value="repair"/><%
    }
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="type" value="<%= VitroVocabulary.RDF_TYPE %>" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="orgClass" value="http://xmlns.com/foaf/0.1/Organization" />
<c:set var="degreeClass" value="${vivoCore}AcademicDegree" />

<%-- Define predicates used in n3 assertions and sparql queries --%>
<c:set var="majorFieldPred" value="${vivoCore}majorField" />
<c:set var="deptPred" value="${vivoCore}departmentOrSchool" />
<c:set var="infoPred" value="${vivoCore}supplementalInformation" />
<c:set var="degreeEarned" value="${vivoCore}degreeEarned" />
<c:set var="degreeOutcomeOf" value="${vivoCore}degreeOutcomeOf" />
<c:set var="trainingAtOrg" value="${vivoCore}trainingAtOrganization" />

<c:set var="dateTimeValue" value="${vivoCore}dateTime"/>
<c:set var="dateTimeValueType" value="${vivoCore}DateTimeValue"/>
<c:set var="dateTimePrecision" value="${vivoCore}dateTimePrecision"/>

<c:set var="ToInterval" value="${vivoCore}dateTimeInterval"/>
<c:set var="intervalType" value="${vivoCore}DateTimeInterval"/>
<c:set var="intervalToStart" value="${vivoCore}start"/>
<c:set var="intervalToEnd" value="${vivoCore}end"/>

<%-- Assertions for adding a new educational training entry --%>
<v:jsonset var="orgTypeAssertion">
    ?org a ?orgType .
</v:jsonset>

<v:jsonset var="orgLabelAssertion">
    ?org <${label}> ?orgLabel .
</v:jsonset>

<v:jsonset var="degreeAssertion" >      
    ?edTraining <${degreeEarned}> ?degree .
    ?degree <${degreeOutcomeOf}> ?edTraining .
</v:jsonset>

<v:jsonset var="majorFieldAssertion" >      
    ?edTraining <${majorFieldPred}> ?majorField .
</v:jsonset>

<v:jsonset var="n3ForStart">
    ?edTraining      <${ToInterval}> ?intervalNode .    
    ?intervalNode  <${type}> <${intervalType}> .
    ?intervalNode <${intervalToStart}> ?startNode .    
    ?startNode  <${type}> <${dateTimeValueType}> .
    ?startNode  <${dateTimeValue}> ?startField-value .
    ?startNode  <${dateTimePrecision}> ?startField-precision .
</v:jsonset>

<v:jsonset var="n3ForEnd">
    ?edTraining      <${ToInterval}> ?intervalNode .    
    ?intervalNode  <${type}> <${intervalType}> .
    ?intervalNode <${intervalToEnd}> ?endNode .
    ?endNode  <${type}> <${dateTimeValueType}> .
    ?endNode  <${dateTimeValue}> ?endField-value .
    ?endNode  <${dateTimePrecision}> ?endField-precision .
</v:jsonset>

<v:jsonset var="deptAssertion" >      
    ?edTraining <${deptPred}> ?dept .
</v:jsonset>

<v:jsonset var="infoAssertion" >      
    ?edTraining <${infoPred}> ?info .
</v:jsonset>

<v:jsonset var="n3ForNewEdTraining">       
    @prefix core: <${vivoCore}> .     

    ?person core:educationalTraining  ?edTraining .
    
    ?edTraining  a core:EducationalTraining ;
                 core:educationalTrainingOf ?person ;
                 <${trainingAtOrg}> ?org .
</v:jsonset>

<%-- This property has no inverse --%>
<v:jsonset var="n3ForEdTrainingToOrg" >      
    ?edTraining <${trainingAtOrg}> ?org .
</v:jsonset>

<%-- Queries for editing an existing educational training entry --%>

<v:jsonset var="orgQuery" >      
    SELECT ?existingOrg WHERE {
        ?edTraining <${trainingAtOrg}> ?existingOrg . }
</v:jsonset>

<v:jsonset var="orgLabelQuery" >      
    SELECT ?existingOrgLabel WHERE {
        ?edTraining <${trainingAtOrg}> ?existingOrg .
        ?existingOrg <${label}> ?existingOrgLabel .
    }
</v:jsonset>

<%-- Limit type to subclasses of foaf:Organization. Otherwise, sometimes owl:Thing or another
type is returned and we don't get a match to the select element options. --%>
<v:jsonset var="orgTypeQuery" >      
    PREFIX rdfs: <${rdfs}>   
    SELECT ?existingOrgType WHERE {
        ?edTraining <${trainingAtOrg}> ?existingOrg .
        ?existingOrg a ?existingOrgType .
        ?existingOrgType rdfs:subClassOf <${orgClass}> .
    }
</v:jsonset>

<v:jsonset var="degreeQuery" >      
    SELECT ?existingDegree WHERE {
        ?edTraining <${degreeEarned}> ?existingDegree . }
</v:jsonset>

<v:jsonset var="majorFieldQuery" >  
    SELECT ?existingMajorField WHERE {
          ?edTraining <${majorFieldPred}> ?existingMajorField . }
</v:jsonset>

<v:jsonset var="deptQuery" >  
    SELECT ?existingDept WHERE {
          ?edTraining <${deptPred}> ?existingDept . }
</v:jsonset>

<v:jsonset var="infoQuery" >  
    SELECT ?existingInfo WHERE {
          ?edTraining <${infoPred}> ?existingInfo . }
</v:jsonset>


 <v:jsonset var="existingIntervalNodeQuery" >  
    SELECT ?existingIntervalNode WHERE {
          ?edTraining <${ToInterval}> ?existingIntervalNode .
          ?existingIntervalNode <${type}> <${intervalType}> . }
</v:jsonset>
 
 <v:jsonset var="existingStartNodeQuery" >  
    SELECT ?existingStartNode WHERE {
      ?edTraining <${ToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToStart}> ?existingStartNode . 
      ?existingStartNode <${type}> <${dateTimeValueType}> .}              
</v:jsonset>

<v:jsonset var="existingStartDateQuery" >  
    SELECT ?existingDateStart WHERE {
     ?edTraining <${ToInterval}> ?intervalNode .
     ?intervalNode <${type}> <${intervalType}> .
     ?intervalNode <${intervalToStart}> ?startNode .
     ?startNode <${type}> <${dateTimeValueType}> .
     ?startNode <${dateTimeValue}> ?existingDateStart . }
</v:jsonset>

<v:jsonset var="existingStartPrecisionQuery" >  
    SELECT ?existingStartPrecision WHERE {
      ?edTraining <${ToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToStart}> ?startNode .
      ?startNode <${type}> <${dateTimeValueType}> .          
      ?startNode <${dateTimePrecision}> ?existingStartPrecision . }
</v:jsonset>


 <v:jsonset var="existingEndNodeQuery" >  
    SELECT ?existingEndNode WHERE {
      ?edTraining <${ToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToEnd}> ?existingEndNode . 
      ?existingEndNode <${type}> <${dateTimeValueType}> .}              
</v:jsonset>

<v:jsonset var="existingEndDateQuery" >  
    SELECT ?existingEndDate WHERE {
     ?edTraining <${ToInterval}> ?intervalNode .
     ?intervalNode <${type}> <${intervalType}> .
     ?intervalNode <${intervalToEnd}> ?endNode .
     ?endNode <${type}> <${dateTimeValueType}> .
     ?endNode <${dateTimeValue}> ?existingEndDate . }
</v:jsonset>

<v:jsonset var="existingEndPrecisionQuery" >  
    SELECT ?existingEndPrecision WHERE {
      ?edTraining <${ToInterval}> ?intervalNode .
      ?intervalNode <${type}> <${intervalType}> .
      ?intervalNode <${intervalToEnd}> ?endNode .
      ?endNode <${type}> <${dateTimeValueType}> .          
      ?endNode <${dateTimePrecision}> ?existingEndPrecision . }
</v:jsonset>

<v:jsonset var="orgClassUriJson">${orgClass}</v:jsonset>
<v:jsonset var="degreeClassUriJson">${degreeClass}</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["edTraining", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForNewEdTraining}", "${orgLabelAssertion}", "${orgTypeAssertion}" ],
    
    "n3optional"    : [ "${n3ForEdTrainingToOrg}", "${majorFieldAssertion}",                                          
                        "${degreeAssertion}", "${deptAssertion}", "${infoAssertion}" , "${n3ForStart}", "${n3ForEnd}"],
                        
    "newResources"  : { "edTraining" : "${defaultNamespace}",
                        "org" : "${defaultNamespace}" ,
                        "intervalNode" : "${defaultNamespace}",
                        "startNode" : "${defaultNamespace}",
                        "endNode" : "${defaultNamespace}"  },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "org", "orgType", "degree" ],
    "literalsOnForm" : [ "orgLabel", "majorField", "dept", "info" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "orgLabel"           : "${orgLabelQuery}",
        "majorField"         : "${majorFieldQuery}",
        "dept"               : "${deptQuery}",
        "info"               : "${infoQuery}",                
        "startField-value"   : "${existingStartDateQuery}",
        "endField-value"     : "${existingEndDateQuery}"               
    },
    "sparqlForExistingUris" : {
        "org"            : "${orgQuery}",
        "orgType"        : "${orgTypeQuery}",
        "degree"         : "${degreeQuery}",        
        "intervalNode"      : "${existingIntervalNodeQuery}", 
        "startNode"         : "${existingStartNodeQuery}",
        "endNode"           : "${existingEndNodeQuery}",
        "startField-precision": "${existingStartPrecisionQuery}",
        "endField-precision"  : "${existingEndPrecisionQuery}"
    },
    "fields" : {
      "degree" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${degreeClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${degreeAssertion}" ]
      },   
      "majorField" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${majorFieldAssertion}" ]
      },       
      "startField" : {
            "newResource"       : "false",
            "validators"        : [  ],
            "optionsType"       : "UNDEFINED",
            "literalOptions"    : [ ],
            "predicateUri"      : "",
            "objectClassUri"    : "",
            "rangeDatatypeUri"  : "",
            "rangeLang"         : "",
            "assertions"        : [ "${n3ForStart}" ]
        },
      "endField" : {
            "newResource"       : "false",
            "validators"        : [  ],
            "optionsType"       : "UNDEFINED",
            "literalOptions"    : [ ],
            "predicateUri"      : "",
            "objectClassUri"    : "",
            "rangeDatatypeUri"  : "",
            "rangeLang"         : "",
            "assertions"        : [ "${n3ForEnd}" ]
        },
     "org" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForEdTrainingToOrg}" ]
      },      
      "orgLabel" : {
         "newResource"      : "false",
         "validators"       : [  "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : [ "${orgLabelAssertion}" ]
      },
     "orgType" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "CHILD_VCLASSES",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${orgTypeAssertion}" ]
      },      
      "dept" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${deptAssertion}"]
      },
      "info" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${infoAssertion}"]
      }    
  }
}
</c:set>
<%
    log.debug(request.getAttribute("editjson"));

    EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
    if (editConfig == null) {
        editConfig = new EditConfiguration((String) request.getAttribute("editjson"));  
                
        //setup date time edit elements
        Field startField = editConfig.getField("startField");
        // arguments for DateTimeWithPrecision are (fieldName, minimumPrecision, [requiredLevel])
        startField.setEditElement(new DateTimeWithPrecision(startField, VitroVocabulary.Precision.YEAR.uri(), VitroVocabulary.Precision.NONE.uri()));        
        Field endField = editConfig.getField("endField");
        endField.setEditElement(new DateTimeWithPrecision(endField, VitroVocabulary.Precision.YEAR.uri(), VitroVocabulary.Precision.NONE.uri()));

        editConfig.addValidator(new DateTimeIntervalValidation("startField","endField") ); 
        
        EditConfiguration.putConfigInSession(editConfig,session);
    }
        
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    if (objectUri != null) { // editing existing
        editConfig.prepareForObjPropUpdate(model);
    } else { // adding new
        editConfig.prepareForNonUpdate(model);
    }
    
    editConfig.setTemplate("personHasEducationalTraining.ftl"); 
    editConfig.setSubmitToUrl("/edit/processRdfForm2.jsp");
        
    String subjectName = ((Individual) request.getAttribute("subject")).getName();

    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
                                                                JavaScript.CUSTOM_FORM_UTILS.path(),
                                                                "/edit/forms/js/customFormWithAutocomplete.js"                                                    
                                                               ));            
    request.setAttribute("customJs", customJs);

    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
                                                   Css.CUSTOM_FORM.path(),
                                                   "/edit/forms/css/customFormWithAutocomplete.css"
                                                  ));                                                                                                                                   
    request.setAttribute("customCss", customCss); 
%>

<c:set var="subjectName" value="<%= subjectName %>" />
    
<%-- Configure add vs. edit --%> 
<c:choose>
    <c:when test='${editMode == "add"}'>
        <c:set var="titleVerb" value="Create" />
        <c:set var="submitButtonText" value="Education and Training" />
        <c:set var="disabledVal" value="" />
    </c:when>
    <c:otherwise>
        <c:set var="titleVerb" value="Edit" />
        <c:set var="submitButtonText" value="Edit Education and Training" />
        <c:set var="disabledVal">${editMode == "repair" ? "" : "disabled" }</c:set>    
    </c:otherwise>
</c:choose>

<%-- 
This goes to an experimental FM based form: 
<jsp:forward page="/N3EditForm"/> 
--%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="yearHint" value="<span class='hint'>(YYYY)</span>" />

<jsp:include page="${preForm}" />

<% if( mode == EditMode.ERROR ){ %>
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<% }else{ %>

<h2>${titleVerb} education and training entry for ${subjectName}</h2>

<form class="customForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <v:input type="select" label="Degree" id="degree"  />  
    
    <v:input type="text" label="Major Field of Degree" id="majorField" size="30" />   
              
    <v:input id="startField"  label="Start Year <span class='hint'>(YYYY)</span>" />
    <v:input id="endField" label="End Year <span class='hint'>(YYYY)</span>" />                             
    
    <p class="inline"><v:input type="select" label="Organization Type ${requiredHint}" name="orgType" disabled="${disabledVal}" id="typeSelector" /></p>
           
    <p><v:input type="text" id="relatedIndLabel" name="orgLabel" label="### Name ${requiredHint}" cssClass="acSelector" disabled="${disabledVal}" size="50"  /></p>

    <%-- Store these values in hidden fields, because the displayed fields are disabled and don't submit. This ensures that when
    returning from a validation error, we retain the values. --%>
    <c:if test="${editMode == 'edit'}">
       <v:input type="hidden" id="orgType" />
       <v:input type="hidden" id="orgLabel" />
    </c:if>

    <div class="acSelection">

        <p class="inline"><label></label><span class="acSelectionInfo"></span> <a href="<c:url value="/individual?uri=" />" class="verifyMatch">(Verify this match)</a></p>
        <v:input type="hidden" id="org" cssClass="acUriReceiver" /> <!-- Field value populated by JavaScript -->
    </div>

    <v:input type="text" label="Department or School Name within the ###" id="dept" size="50" />
    
    <v:input type="text" label="Supplemental Information" id="info" size="50" />
    <p>e.g., <em>Postdoctoral training</em> or <em>Transferred</em></p>    
    
    <p class="submit"><v:input type="submit" id="submit" value="${submitButtonText}" cancel="true"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<c:url var="acUrl" value="/autocomplete?tokenize=true" />

<script type="text/javascript">
var customFormData  = {
    acUrl: '${acUrl}',
    editMode: '${editMode}',
    submitButtonTextType: 'compound',
    defaultTypeName: 'organization'
};
</script>

<% } %>

<jsp:include page="${postForm}"/>
