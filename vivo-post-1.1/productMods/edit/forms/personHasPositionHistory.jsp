<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal"%>
<%@ page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.StartYearBeforeEndYear"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.PersonHasPositionValidator"%>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.personHasPositionHistory.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    request.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    request.setAttribute("gYearDatatypeUriJson", MiscWebUtils.escape(XSD.gYear.toString()));
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="positionClass" value="${vivoCore}Position" />
<c:set var="orgClass" value="http://xmlns.com/foaf/0.1/Organization" />

<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<c:set var="titlePred" value="${vivoCore}titleOrRole" />
<v:jsonset var="titleExisting" >  
    SELECT ?titleExisting WHERE {
          ?positionUri <${titlePred}> ?titleExisting }
</v:jsonset>

<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="titleAssertion" >      
    ?positionUri <${titlePred}> ?title ;
                 <${label}> ?title. 
</v:jsonset>

<c:set var="startYearPred" value="${vivoCore}startYear" />
<v:jsonset var="startYearExisting" >      
      SELECT ?startYearExisting WHERE {  
          ?positionUri <${startYearPred}> ?startYearExisting }
</v:jsonset>
<v:jsonset var="startYearAssertion" >
      ?positionUri <${startYearPred}> ?startYear .
</v:jsonset>

<c:set var="endYearPred" value="${vivoCore}endYear" />
<v:jsonset var="endYearExisting" >      
      SELECT ?endYearExisting WHERE {  
          ?positionUri <${endYearPred}> ?endYearExisting }
</v:jsonset>
<v:jsonset var="endYearAssertion" >
      ?positionUri <${endYearPred}> ?endYear .
</v:jsonset>

<%--  Note there is really no difference in how things are set up for an object property except
      below in the n3ForEdit section, in whether the ..Existing variable goes in SparqlForExistingLiterals
      or in the SparqlForExistingUris, as well as perhaps in how the options are prepared --%>
<c:set var="positionInOrgPred" value="${vivoCore}positionInOrganization" />
<c:set var="orgForPositionPred" value="${vivoCore}organizationForPosition" />
<v:jsonset var="organizationUriExisting" >      
    SELECT ?existingOrgUri WHERE {
        ?positionUri <${positionInOrgPred}> ?existingOrgUri }
</v:jsonset>
<v:jsonset var="organizationUriAssertion" >      
    ?positionUri <${positionInOrgPred}> ?organizationUri .
    ?organizationUri <${orgForPositionPred}> ?positionUri .
</v:jsonset>

<v:jsonset var="positionTypeExisting">
    SELECT ?existingPositionType WHERE {
        ?positionUri a ?existingPositionType }
</v:jsonset>
<v:jsonset var="positionTypeAssertion">
    ?positionUri a ?positionType .
</v:jsonset>

<v:jsonset var="newOrgNameAssertion">
    ?newOrg <${label}> ?newOrgName .
</v:jsonset>
<v:jsonset var="newOrgTypeAssertion">
    ?newOrg a ?newOrgType .
</v:jsonset>

<v:jsonset var="n3ForStmtToPerson">       
    @prefix core: <${vivoCore}> .     

    ?person      core:personInPosition  ?positionUri .
    
    ?positionUri core:positionForPerson ?person ;
                 a  ?positionType .
</v:jsonset>

<v:jsonset var="n3ForNewOrg">
    ?positionUri <${positionInOrgPred}> ?newOrg .
    
    ?newOrg <${label}> ?newOrgName ;
            a ?newOrgType ;
            <${orgForPositionPred}> ?positionUri .

</v:jsonset>

<v:jsonset var="positionClassUriJson">${positionClass}</v:jsonset>
<v:jsonset var="orgClassUriJson">${orgClass}</v:jsonset>

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/entity",

    "subject"   : ["person",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["positionUri", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForStmtToPerson}", "${titleAssertion}", "${startYearAssertion}" ],
    
    "n3optional"    : [ "${organizationUriAssertion}",                         
                        "${n3ForNewOrg}", "${newOrgNameAssertion}", "${newOrgTypeAssertion}",                       
                        "${endYearAssertion}"],
                        
    "newResources"  : { "positionUri" : "${defaultNamespace}",
                        "newOrg" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "organizationUri", "newOrgType", "positionType" ],
    "literalsOnForm" :  [ "title", "newOrgName", "startYear", "endYear" ],                          
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : {
        "title"              : "${titleExisting}",
        "startYear"          : "${startYearExisting}",
        "endYear"            : "${endYearExisting}"
    },
    "sparqlForExistingUris" : {
        "organizationUri"   : "${organizationUriExisting}",
        "positionType"      : "${positionTypeExisting}"
    },
    "fields" : {
      "title" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${titleAssertion}" ]
      },
     "positionType" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "CHILD_VCLASSES_WITH_PARENT",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${positionClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${positionTypeAssertion}" ]
      },         
     "organizationUri" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "INDIVIDUALS_VIA_VCLASS",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${organizationUriAssertion}" ]
      },      
      "newOrgName" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : [ "${n3ForNewOrg}" ]
      },
     "newOrgType" : {
         "newResource"      : "false",
         "validators"       : [  ],
         "optionsType"      : "CHILD_VCLASSES",
         "literalOptions"   : [ "Select one" ],
         "predicateUri"     : "",
         "objectClassUri"   : "${orgClassUriJson}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${newOrgTypeAssertion}" ]
      },      
      "startYear" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty", "datatype:${gYearDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${gYearDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${startYearAssertion}"]
      },
      "endYear" : {
         "newResource"      : "false",
         "validators"       : [ "datatype:${gYearDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${gYearDatatypeUriJson}",
         "rangeLang"        : "",         
         "assertions"       : ["${endYearAssertion}"]
      }
  }
}
</c:set>
<%
    log.debug(request.getAttribute("editjson"));

    EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
    if (editConfig == null) {
        editConfig = new EditConfiguration((String) request.getAttribute("editjson"));     
        EditConfiguration.putConfigInSession(editConfig,session);
    }
    
    editConfig.addValidator(new PersonHasPositionValidator() );
    editConfig.addValidator(new StartYearBeforeEndYear("startYear","endYear") ); 
    		
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    if (objectUri != null) { // editing existing
        editConfig.prepareForObjPropUpdate(model);
    } else { // adding new
        editConfig.prepareForNonUpdate(model);
    }
    
    String subjectName = ((Individual) request.getAttribute("subject")).getName();
%> 

    <c:set var="subjectName" value="<%= subjectName %>" />
<%
    if (objectUri != null) { // editing existing entry
%>
        <c:set var="editType" value="edit" />
        <c:set var="formSteps" value="1" />
        <c:set var="title" value="Edit position entry for ${subjectName}" />
        <%-- NB This will be the button text when Javascript is disabled. --%>
        <c:set var="submitLabel" value="Save changes" />
<% 
    } else { // adding new entry
%>
        <c:set var="editType" value="add" />
        <c:set var="formSteps" value="2" />
        <c:set var="title" value="Create position entry for ${subjectName}" />
        <%-- NB This will be the button text when Javascript is disabled. --%>
        <c:set var="submitLabel" value="Create position" />
<%  } 
    
    List<String> customJs = new ArrayList<String>(Arrays.asList("/js/utils.js",            
                                                                "/js/customFormUtils.js",           
                                                                "/edit/forms/js/customForm.js"
                                                                //, "/edit/forms/js/customFormTwoStep.js"
                                                                ));
    request.setAttribute("customJs", customJs);
    
    List<String> customCss = new ArrayList<String>(Arrays.asList("/edit/forms/css/customForm.css"
                                                                 , "/edit/forms/css/personHasPositionHistory.css"
                                                                 ));
    request.setAttribute("customCss", customCss);   
%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="view" value='<%= vreq.getAttribute("view") %>' />

<jsp:include page="${preForm}" />

<h2>${title}</h2>

<form class="${editType}" action="<c:url value="/edit/processRdfForm2.jsp"/>" >

    <div class="relatedIndividual">
        <div class="existing">
            <v:input type="select" label="Select Existing Organization ${requiredHint}" id="organizationUri"  /><span class="existingOrNew">or</span>
        </div>
    
        <div class="addNewLink">
            If your organization is not listed, please <a href="#">add a new organization</a>.    
        </div>
      
        <div class="new">
            <h6>Add a New Organization</h6>
            <v:input type="text" label="Organization Name ${requiredHint}" id="newOrgName" size="30" />
            <v:input type="select" label="Select Organization Type ${requiredHint}" id="newOrgType" />
        </div>   
    </div>   
    
    <div class="entry"> 
        <v:input type="text" label="Position Title ${requiredHint}" id="title" size="30" />
        <v:input type="select" label="Position Type ${requiredHint}" id="positionType" />

        <p class="inline year"><v:input type="text" label="Start Year ${requiredHint} <span class='hint'>(YYYY)</span>" id="startYear" size="4" /></p>    
        <p class="inline year"><v:input type="text" label="End Year <span id='endYearHint' class='hint'>(YYYY)</span>" id="endYear" size="4" /></p>
    </div>
    
    <!-- Processing information for Javascript -->
    <input type="hidden" name="editType" value="${editType}" />
    <input type="hidden" name="entryType" value="position" /> 
    <input type="hidden" name="secondaryType" value="organization" />
    <%-- RY If set steps to 1 when editType == 'edit', may be able to combine the
    step 1 and edit cases in the Javascript.  --%>
    <input type="hidden" name="steps" value="${formSteps}" />
    <input type="hidden" name="view" value="${view}" />
       
    <p class="submit"><v:input type="submit" id="submit" value="${submitLabel}" cancel="true"/></p>
    
    <p id="requiredLegend" class="requiredHint">* required fields</p>
</form>

<jsp:include page="${postForm}"/>

