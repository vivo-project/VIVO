<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding terminology annotation
--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Collections" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>
<%@ page import="com.hp.hpl.jena.vocabulary.RDFS" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.DataPropertyComparator" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>

<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.terminologyAnnotation.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
    vreq.setAttribute("defaultNamespace", ""); //empty string triggers default new URI behavior
    
    vreq.setAttribute("stringDatatypeUriJson", MiscWebUtils.escape(XSD.xstring.toString()));
    
%>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="label" value="${rdfs}label" />
<c:set var="foaf" value="http://xmlns.com/foaf/0.1/" />
<c:set var="personClassUri" value="${foaf}Person" />

<%-- Unlike other custom forms, this form does not allow edits of existing authors, so there are no
SPARQL queries for existing values. --%>


<v:jsonset var="n3ForTerminology">
    @prefix core: <${vivoCore}> .   
    ?subject ?predicate ?terminologyContextNode .
    ?terminologyContextNode core:referencedTerm ?referencedTerm .
    ?terminologyContextNode core:entryTerm ?entryTerm .
    ?terminologyContextNode core:termLabel ?termLabel .
    ?terminologyContextNode core:termType ?termType .
</v:jsonset>

<c:set var="returnPathAfterSubmit" value="/edit/editRequestDispatch.jsp?subjectUri=${subjectUri}&predicateUri=${predicateUri}" />
<c:url var="submitSearchUrl" value="/UMLSTermsRetrieval"/>
<c:url var="UMLSCUIURL" value="http://link.informatics.stonybrook.edu/umls/CUI/" />
<c:set var="editjson" scope="request">
{
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "${returnPathAfterSubmit}",

    "subject"   : ["subject", "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["terminologyContextNode", "${objectUriJson}", "URI" ],
    
    "n3required"    : [ "${n3ForTerminology}" ],
    
    "n3optional"    : [  ],                                                
                        
    "newResources"  : { "terminologyContextNode" : "${defaultNamespace}" },

    "urisInScope"    : { },
    "literalsInScope": { },
    "urisOnForm"     : [ "referencedTerm" ],
    "literalsOnForm" : [ "entryTerm", "termLabel", "termType" ],
    "filesOnForm"    : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris" : {  },
    "sparqlForExistingLiterals" : { },
    "sparqlForExistingUris" : { },
    "fields" : {
      "referencedTerm" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED", //UNSURE WHAT TO KEEP HERE
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForTerminology}" ]
      },   
      "entryTerm" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForTerminology}" ]
      },   
      "termLabel" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForTerminology}" ]
      }
      ,"termType" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${n3ForTerminology}" ]
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
    
    //Specific validators can be included here
    
    Model model = (Model) application.getAttribute("jenaOntModel");
    String objectUri = (String) request.getAttribute("objectUri");
    
    //for some reason we are comming from the add new and that is working 
    //but we also come from the edit, and that is not working.
    editConfig.setObject(""); //this will force the edit config to always be an add, never an update
    
    editConfig.prepareForNonUpdate(model); // we're only adding new, not editing existing
    
    String subjectUri = vreq.getParameter("subjectUri");
    String predicateUri = vreq.getParameter("predicateUri");

    String vivoCore = "http://vivoweb.org/ontology/core#";
    
    //Individual infoResource = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);
    Individual subject = ((Individual) request.getAttribute("subject"));
    vreq.setAttribute("subjectName", subject.getName());
    //Get existing terminology annotations
    List<Individual> terminologyAnnotationNodes = subject.getRelatedIndividuals(predicateUri);
    
    
      
    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
                                                                JavaScript.CUSTOM_FORM_UTILS.path(),
                                                                "/js/browserUtils.js",
                                                                "/edit/forms/js/addTerminology.js"
                                                               ));            
    request.setAttribute("customJs", customJs);

    //no custom css we know of yet
    
    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
                                                                 Css.CUSTOM_FORM.path(),
                                                                 "/edit/forms/css/addTerminology.css"                                                               
                                                               ));                                                                                                                                 
    request.setAttribute("customCss", customCss); 
     
   
%>

<c:set var="title" value="<em>${subjectName}</em>"/>

<jsp:include page="${preForm}" />
<h1>JSP form, must be removed for the 1.4!</h1>


<%-- DO NOT CHANGE IDS, CLASSES, OR HTML STRUCTURE ON THIS PAGE WITHOUT UNDERSTANDING THE IMPACT ON THE JAVASCRIPT! --%>
<h2>${title}</h2>

<%@ include file="unsupportedBrowserMessage.jsp" %>

<div class="noIE67">
<h3>Manage Terminology Annotations</h3>

<ul id="existingTerms" >
<%
   
    int existingTermsCount = terminologyAnnotationNodes.size();  

%>        
    <script type="text/javascript">
        var existingTermsData = [];
    </script>
    
<%    
	String termLabelUri = vivoCore + "termLabel";
	String termTypeUri = vivoCore + "termType";
    for ( Individual termNode : terminologyAnnotationNodes ) {
        request.setAttribute("termNodeUri", termNode.getURI());
		//Get label and type only as mirroring authorship where labels but no links for individuals incoldued
		DataPropertyStatement termLabelStatement = termNode.getDataPropertyStatement(RDFS.label.getURI());
		String termLabel = termLabelStatement.getData();
        //request.setAttribute("termLabel", termLabel); 
       	//DataPropertyStatement termTypeStatement = termNode.getDataPropertyStatement(termTypeUri);
		//String termType = termTypeStatement.getData();
		request.setAttribute("termType", "fake");
%> 
        <li class="existingTerm">
            <%-- span.author will be used in the next phase, when we display a message that the author has been
            removed. That text will replace the a.authorName, which will be removed. --%>    
            <span class="term">
                <%-- This span is here to assign a width to. We can't assign directly to the a.authorName,
                for the case when it's followed by an em tag - we want the width to apply to the whole thing. --%>
                <span class="termWrapper">
                   <span class="termLabel">
                   ${termLabel} (${termType})</span> 
                </span>
                <c:url var="deleteTermHref" value="/edit/primitiveDelete" />
                <a href="${deleteTermHref}" class="remove">Remove</a>
            </span>
        </li>    
        
        <script type="text/javascript">
            existingTermsData.push({
                "termNodeUri": "${termNodeUri}",
                "termLabel": "${termLabel}"      
            });
        </script>         
<%         
    }
%>
    
</ul>

<%  if (existingTermsCount == 0) { %>   
        <p>There are currently no terms specified.</p>
<%  } %>

<div id="showAddForm">
    <v:input type="submit" value="Add Term" id="showAddFormButton" name="showAddFormButton" cancel="true" cancelLabel="Return" cancelUrl="/individual" />
</div> 

<form id="addTerminologyForm" class="customForm" action="<c:url value="/edit/processTerminologyAnnotation"/>" >
    <p class="inline"><v:input type="text" id="searchTerm" label="Search UMLS Terms" cssClass="acSelector" size="35" />
    <input type="button" id="searchButton" name="searchButton" value="Search"/>
    </p>
   	<input type="hidden" id="entryTerm" name="entryTerm" value="" />  <!-- Field value populated by JavaScript -->
    <input type="hidden" id="referencedTerm" name="referencedTerm" value=""/> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="termLabel" name="termLabel" value="" />  <!-- Field value populated by JavaScript -->
    <input type="hidden" id="termType" name="termType" value="" />  <!-- Field value populated by JavaScript -->
    
    
   
    <div id="selectedTerm" name="selectedTerm" class="acSelection">
        <%-- RY maybe make this a label and input field. See what looks best. --%>
        <p class="inline">
        
        </p>
       <!-- Field value populated by JavaScript -->
    	
    </div>
	<div id="errors" name="errors"></div>
    
    <p class="submit"><v:input type="submit" id="submit" name="submit" value="Add Term" cancel="true" /></p>

</form>
</div>


<script type="text/javascript">
var customFormData = {
    dataServiceUrl: '${submitSearchUrl}',
    UMLSCUIURL: '${UMLSCUIURL}'
};
</script>

<jsp:include page="${postForm}"/>

