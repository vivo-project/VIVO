<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- 
  Custom two stage form for adding a Role to a Person.  
  
  Stage one is selecting the type of the non-person thing 
  associated with the Role with the intention of reducing the 
  number of Individuals that the user has to select from.
  Stage two is selecting the non-person Individual to associate
  with the Role. 

  This is intended to create a set of statements like:

  ?person  core:hasResearchActivityRole ?newRole.
  ?newRole rdf:type core:ResearchActivityRole ;         
           core:relatedRole ?someActivity .
  ?someActivity rdf:type core:ResearchActivity .
  ?someActivity rdfs:label "activity title" .
  
  Important: This form cannot be directly used as a custom form.  It has parameters that must be set.
  See addClinicalRoleToPerson.jsp for an example.
  
--%>           

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.TitleCase" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.StartYearBeforeEndYear"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty"%>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%@page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement"%><c:set var="vivoOnt" value="http://vivoweb.org/ontology" />

<%!
public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.addRoleToPersonTwoStage.jsp");
%>

<c:set var="vivoCore" value="${vivoOnt}/core#" />

<%--
  These are the parameters that MUST be set of this form:
   role type
   predicate inverse          
   role activity type label (should be singular)
   super type of role types for roleActivityType select list generation 
--%>

<c:set var="roleActivityTypeLabel">${param.roleActivityTypeLabel}</c:set>
<c:set var="buttonLabel" scope="request">${! empty param.buttonLabel ? param.buttonLabel : param.roleActivityTypeLabel}</c:set>
<c:set var="roleType">${param.roleType}</c:set>
<c:set var="roleActivityType_optionsType" >${param.roleActivityType_optionsType}</c:set>
<c:set var="roleActivityType_objectClassUri" >${param.roleActivityType_objectClassUri}</c:set> 
<c:set var="roleActivityType_literalOptions" >${param.roleActivityType_literalOptions}</c:set>
<c:set var="numDateFields">${! empty param.numDateFields ? param.numDateFields : 2 }</c:set>

<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    //vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");    
    String subjectName = ((Individual) request.getAttribute("subject")).getName();
    vreq.setAttribute("subjectUriJson", MiscWebUtils.escape(subjectUri));
    
    vreq.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    
    String intDatatypeUri = XSD.xint.toString();    
    vreq.setAttribute("intDatatypeUri", intDatatypeUri);
    vreq.setAttribute("intDatatypeUriJson", MiscWebUtils.escape(intDatatypeUri));

    vreq.setAttribute("gYearDatatypeUriJson", MiscWebUtils.escape(XSD.gYear.toString()));
    
    vreq.setAttribute("roleActivityTitleCase", TitleCase.toTitleCase(vreq.getParameter("roleActivityTypeLabel")));
    String buttonLabel = (String) vreq.getAttribute("buttonLabel");
    vreq.setAttribute("buttonLabel", TitleCase.toTitleCase(buttonLabel));
    
    ObjectProperty op = wdf.getObjectPropertyDao().getObjectPropertyByURI( predicateUri ); 
    if( op != null &&  op.getURIInverse() != null ){
		%> <c:set var="inversePredicate"><%=op.getURIInverse()%></c:set> <%
    }else{
    	%> <c:set var="inversePredicate"></c:set> <%
    }
%>

<%-- There are 4 modes that this form can be in: 
  1.  Add, there is a subject and a predicate but no role and nothing else. 
        
  2. normal edit where everything should already be filled out.  There is a subject, a object and an individual on
     the other end of the object's core:roleIn stmt. 
  
  3. Repair a bad role node.  There is a subject, prediate and object but there is no individual on the 
     other end of the object's core:roleIn stmt.  This should be similar to an add but the form should be expanded.
     
  4. Really bad node. multiple roleIn statements.
   
--%>
<%
 /* check to see if this is mode 3 */
 int mode = 1;
 Individual obj = (Individual)request.getAttribute("object");
 if( obj != null){
	 List<ObjectPropertyStatement> stmts = obj.getObjectPropertyStatements("http://vivoweb.org/ontology/core#roleIn");
	 if( stmts != null){
		 if( stmts.size() > 1 ){
			 mode = 4; // Multiple roleIn statements, yuck.
		 }else if( stmts.size() == 0 ){
			 mode = 3; // need to repair the role node
		 }else if(stmts.size() == 1 ){
			 mode = 2;
		 }
	 }		 	 
 }
 if( mode == 1 )
	 log.debug("This form will be for an add. Setting mode to " + mode);
 else if(mode == 2){
	 log.debug("This form will be for a normal edit. Setting mode to " + mode);
	 %> <c:set var="editMode" value="edit"/><%
 } else if(mode == 3){
	 log.debug("This form will be for the repair of a bad role node. Setting mode to " + mode);
	 %> <c:set var="editMode" value="repair"/><%
 }else if(mode == 4)
	 log.debug("No form will be shown, since there are multiple core:roleIn statements. Setting mode to " + mode);
%>

<c:set var="vivoOnt" value="http://vivoweb.org/ontology" />
<c:set var="vivoCore" value="${vivoOnt}/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="defaultNamespace" value=""/> <%--blank triggers default URI generation behavior --%>

<%-- label and type required if we are doing an add or a repair, but not an edit --%> 
<c:set var="labelRequired" ><%= (mode == 1 || mode == 3) ?"\"nonempty\"," : "" %></c:set>
<c:set var="typeRequired" ><%= (mode == 1 || mode == 3) ?"\"nonempty\"" : "" %></c:set>

<%-- 
<c:choose>
    <c:when test="${numDateFields == 1}">
        <c:set var="startYearPredicate" value="${vivoCore}year" />
    </c:when>
    <c:otherwise>
        <c:set var="startYearPredicate" value="${vivoCore}startYear" />    
    </c:otherwise>
</c:choose>
--%>
<c:set var="startYearPredicate">
    <c:choose>
        <c:when test="${numDateFields == 1}">${vivoCore}year</c:when>
        <c:otherwise>${vivoCore}startYear</c:otherwise>
    </c:choose>
</c:set>
<v:jsonset var="startYearAssertion" >
    ?role <${startYearPredicate}> ?startYear .
</v:jsonset>

<c:set var="endYearPredicate" value="${vivoCore}endYear" /> 
<v:jsonset var="endYearAssertion" >
    ?role <${endYearPredicate}> ?endYear .
</v:jsonset>

<v:jsonset var="roleLabelAssertion" >
    ?role <${label}> ?roleLabel .
</v:jsonset>

<v:jsonset var="n3ForNewRole">
	@prefix core: <${vivoCore}> .
       
	?person ?rolePredicate ?role.	
	?role   a <${roleType}> .		  
    ?role   core:roleIn ?roleActivity .    
    ?roleActivity  core:relatedRole ?role .    
</v:jsonset>

<v:jsonset var="n3ForActivityType">     
    ?roleActivity a ?roleActivityType .
</v:jsonset>

<v:jsonset var="n3ForRoleToActivity"> 
	@prefix core: <${vivoCore}> .    
    ?role core:roleIn ?roleActivity .
    ?roleActivity  core:relatedRole ?role .   
</v:jsonset>

<v:jsonset var="n3ForActivityLabel">
    ?roleActivity <${label}> ?activityLabel .
</v:jsonset>

<v:jsonset var="n3ForInverse"> 
	?role  ?inverseRolePredicate ?person.
</v:jsonset>

<v:jsonset var="activityLabelQuery">
  PREFIX core: <${vivoCore}>
  PREFIX rdfs: <${rdfs}> 
  SELECT ?existingTitle WHERE {
        ?role  core:roleIn ?existingActivity .
        ?existingActivity rdfs:label ?existingTitle . }
</v:jsonset>

<v:jsonset var="startYearQuery">
  SELECT ?existingStartYear WHERE { ?role  <${startYearPredicate}> ?existingStartYear .}       
</v:jsonset>

<v:jsonset var="endYearQuery">
  SELECT ?existingStartYear WHERE { ?role  <${endYearPredicate}> ?existingStartYear .}
</v:jsonset>

<v:jsonset var="activityQuery">
  PREFIX core: <${vivoCore}>  
  SELECT ?existingActivity WHERE { ?role  core:roleIn ?existingActivity . }
</v:jsonset>

<v:jsonset var="roleLabelQuery">
  SELECT ?existingRoleLabel WHERE { ?role  <${label}> ?existingRoleLabel . }
</v:jsonset>

<v:jsonset var="activityTypeQuery">
  PREFIX core: <${vivoCore}>
  SELECT ?existingActivityType WHERE { 
      ?role core:roleIn ?existingActivity .
      ?existingActivity a ?existingActivityType . 
  }
</v:jsonset>

<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "/individual",

    "subject"   : ["person", "${subjectUriJson}" ],
    "predicate" : ["rolePredicate", "${predicateUriJson}" ],
    "object"    : ["role", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForNewRole}", "${startYearAssertion}", "${roleLabelAssertion}" ],        
    "n3optional"    : [ "${n3ForActivityLabel}", "${n3ForActivityType}", "${n3ForInverse}", "${endYearAssertion}" ],        
                                                                                        
    "newResources"  : { "role" : "${defaultNamespace}",
                        "roleActivity" : "${defaultNamespace}" },

    "urisInScope"    : { "inverseRolePredicate" : "${inversePredicate}" },
    "literalsInScope": { },
    "urisOnForm"     : [ "roleActivity", "roleActivityType" ],
    "literalsOnForm" : [ "activityLabel", "roleLabel", "startYear", "endYear" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : { "activityLabel":"${activityLabelQuery}", "roleLabel":"${roleLabelQuery}", "startYear":"${startYearQuery}", "endYear":"${endYearQuery}" },
    "sparqlForExistingUris" : { "roleActivity":"${activityQuery}" , "roleActivityType":"${activityTypeQuery}" },
    "fields" : {
      "activityLabel" : {
         "newResource"      : "false",
         "validators"       : [ ${labelRequired} "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : ["${n3ForActivityLabel}" ]
      },   
      "roleActivityType" : {
         "newResource"      : "true",
         "validators"       : [ ${typeRequired} ],
         "optionsType"      : "${roleActivityType_optionsType}",
         "literalOptions"   : [ ${roleActivityType_literalOptions } ],
         "predicateUri"     : "",
         "objectClassUri"   : "${roleActivityType_objectClassUri}",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : ["${n3ForActivityType}" ]
      },               
      "roleActivity" : {
         "newResource"      : "true",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",         
         "assertions"       : [ "${n3ForRoleToActivity}" ]
      },
      "roleLabel" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty","datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : ["${roleLabelAssertion}" ]
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
    EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
    if (editConfig == null) {
        editConfig = new EditConfiguration((String) request.getAttribute("editjson"));     
        EditConfiguration.putConfigInSession(editConfig,session);
    }
    
    editConfig.addValidator(new StartYearBeforeEndYear("startYear","endYear") ); 

    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    if (objectUri != null) { 
        editConfig.prepareForObjPropUpdate(model);
        // Return browser to person individual after editing an existing role.
    } else { 
        editConfig.prepareForNonUpdate(model);
        // NIHVIVO-1014 Return browser to person individual after editing an existing role.
        // Return the browser to the new activity entity after adding a new role.
        // editConfig.setEntityToReturnTo("?roleActivity");
    }        

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

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />
<c:set var="yearHint" value="<span class='hint'>(YYYY)</span>" />

<c:choose>
    <%-- Includes edit AND repair mode --%>
    <c:when test="<%= request.getAttribute(\"objectUri\")!=null %>">     	
        <c:set var="titleVerb" value="Edit" />        
        <c:set var="submitButtonText" value="Edit ${buttonLabel}" />
        <c:set var="disabledVal">${editMode == "repair" ? "" : "disabled" }</c:set>
    </c:when>
    <c:otherwise>
        <c:set var="titleVerb" value="Create" />
        <c:set var="editMode" value="add" />
        <c:set var="submitButtonText" value="${buttonLabel}" />
        <c:set var="disabledVal" value="" />
    </c:otherwise>
</c:choose>

<jsp:include page="${preForm}" />

<% if( mode == 4 ){ %>
 <div>This form is unable to handle the editing of this role because it is associated with 
      multiple ${param.roleActivityTypeLabel} individuals.</div>      
<% }else{ %>
	
	<h2>${titleVerb}&nbsp;${roleActivityTypeLabel} entry for <%= subjectName %></h2>
	<%-- DO NOT CHANGE IDS, CLASSES, OR HTML STRUCTURE IN THIS FORM WITHOUT UNDERSTANDING THE IMPACT ON THE JAVASCRIPT! --%>
	
	<form id="addRoleForm" class="customForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
	
	    <p class="inline"><v:input type="select" label="${roleActivityTitleCase} Type ${requiredHint}" name="roleActivityType" disabled="${disabledVal}" id="typeSelector" /></p>
	    
	    <div class="fullViewOnly">
	        
		    <p><v:input type="text" id="relatedIndLabel" name="activityLabel" label="### Name ${requiredHint}" cssClass="acSelector" disabled="${disabledVal}" size="50"  /></p>
	
	        <%-- Store these values in hidden fields, because the displayed fields are disabled and don't submit. This ensures that when
	        returning from a validation error, we retain the values. --%>
	        <c:if test="${editMode == 'edit'}">
	           <v:input type="hidden" id="roleActivityType" />
	           <v:input type="hidden" id="activityLabel" />
	        </c:if>
	        
		    <div class="acSelection">
		        <%-- RY maybe make this a label and input field. See what looks best. --%>
		        <p class="inline"><label></label><span class="acSelectionInfo"></span> <a href="<c:url value="/individual?uri=" />" class="verifyMatch">(Verify this match)</a></p>
		        <v:input type="hidden" id="roleActivityUri" name="roleActivity" cssClass="acUriReceiver" /> <!-- Field value populated by JavaScript -->
		    </div>
	
	        <p><v:input type="text" id="roleLabel" label="Role in ### ${requiredHint}" size="50" /></p>
	        
	        <c:choose>
	            <c:when test="${numDateFields == 1}">
	                <v:input type="text" label="Year ${requiredHint} ${yearHint}" id="startYear" size="7"/>            
	            </c:when>
	            <c:otherwise>
	                <h4 class="label">Years of Participation in ###</h4>    
	                <v:input type="text" label="Start Year ${requiredHint} ${yearHint}" id="startYear" size="7"/>   
	                <v:input type="text" label="End Year ${yearHint}" id="endYear" size="7"/>             
	            </c:otherwise>
	        </c:choose>
	 
	    </div>   
	     
	    <p class="submit"><v:input type="submit" id="submit" value="${submitButtonText}" cancel="true" /></p>
	    
	    <p id="requiredLegend" class="requiredHint">* required fields</p>
	</form>
	
	<c:url var="acUrl" value="/autocomplete?tokenize=true&stem=true" />
	
	<script type="text/javascript">
	var customFormData  = {
	    acUrl: '${acUrl}',
	    editMode: '${editMode}',
	    submitButtonTextType: 'compound',
	    defaultTypeName: 'activity' // used in repair mode, to generate button text and org name field label
	};
	</script>
<% } %>

<jsp:include page="${postForm}"/>