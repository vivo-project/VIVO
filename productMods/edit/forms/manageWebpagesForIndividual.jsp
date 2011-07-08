<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for managing webpages associated with an individual

Object properties: 
core:webpage (range: core:URLLink)
core:webpageOf (domain: core:URLLink) (inverse of core:webpage)

Class: 
core:URLLink - the link to be added to the individual

Data properties of core:URLLink:
core:linkURI
core:linkAnchorText
core:rank

--%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="com.hp.hpl.jena.rdf.model.Literal" %>
<%@ page import="com.hp.hpl.jena.rdf.model.Model" %>
<%@ page import="com.hp.hpl.jena.vocabulary.XSD" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.VClass" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.DataProperty" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.JavaScript" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>
<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.addWebpageToIndividual.jsp");
%>
<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    vreq.setAttribute("defaultNamespace", wdf.getDefaultNamespace());
    
    String propertyUri = (String) request.getAttribute("predicateUri");
    String objectUri = (String) request.getAttribute("objectUri");  
    
    String stringDatatypeUriJson = MiscWebUtils.escape(XSD.xstring.toString());
    String uriDatatypeUriJson = MiscWebUtils.escape(XSD.anyURI.toString());
%>

<c:set var="stringDatatypeUriJson" value="<%= stringDatatypeUriJson %>" />
<c:set var="uriDatatypeUriJson" value="<%= uriDatatypeUriJson %>" />

<c:set var="core" value="http://vivoweb.org/ontology/core#" />
<c:set var="linkClass" value="${core}URLLink" />
<c:set var="webpageProperty" value="${core}webpage" />
<c:set var="inverseProperty" value="${core}webpageOf" />
<c:set var="linkUrl" value="${core}linkURI" />
<c:set var="linkAnchor" value="${core}linkAnchorText" />
<c:set var="rank" value="${core}rank" />

<%--  Enter here any class names to be used for constructing INDIVIDUALS_VIA_VCLASS pick lists
      These are then referenced in the field's ObjectClassUri but not elsewhere.
      NOTE that this class may not exist in the model, in which the only choice of type
      that will show up is "web page", which will insert no new statements and just create
      links of type vitro:Link --%>

<%--  Then enter a SPARQL query for each field, by convention concatenating the field id with "Existing"
      to convey that the expression is used to retrieve any existing value for the field in an existing individual.
      Each of these must then be referenced in the sparqlForExistingLiterals section of the JSON block below
      and in the literalsOnForm --%>
<v:jsonset var="urlQuery" >
      SELECT ?urlExisting
      WHERE { ?link <${linkUrl}>  ?urlExisting }
</v:jsonset>
<%--  Pair the "existing" query with the skeleton of what will be asserted for a new statement involving this field.
      The actual assertion inserted in the model will be created via string substitution into the ? variables.
      NOTE the pattern of punctuation (a period after the prefix URI and after the ?field) --%> 
<v:jsonset var="urlAssertion" >
      ?link <${linkUrl}>  ?url .
</v:jsonset>

<v:jsonset var="anchorQuery" >
      SELECT ?anchorExisting
      WHERE { ?link <${linkAnchor}> ?anchorExisting } 
</v:jsonset>
<v:jsonset var="anchorAssertion" >
      ?link <${linkAnchor}> ?anchor .
</v:jsonset>

<v:jsonset var="rankQuery" >
      SELECT ?rankExisting
      WHERE { ?link <${rank}> ?rankExisting } 
</v:jsonset>
<v:jsonset var="rankAssertion" >
      ?link <${rank}> ?rank .
</v:jsonset>

<%--  When not retrieving a literal via a datatype property, put the SPARQL statement into
      the SparqlForExistingUris --%>

<v:jsonset var="n3ForEdit">
      ?subject <${webpageProperty}>  ?link .
      ?link <${inverseProperty}> ?subject .

      ?link a  <${linkClass}> ;      
            <${linkUrl}>  ?url ;
            <${linkAnchor}> ?anchor ;
            <${rank}> ?rank .
          
</v:jsonset>

<c:set var="returnPathAfterSubmit" value="/edit/editRequestDispatch.jsp?subjectUri=${subjectUri}&predicateUri=${predicateUri}" />

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "${returnPathAfterSubmit}",
    
    "subject"   : ["subject",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["link", "${objectUriJson}", "URI" ],
    
    "n3required"        : [ "${n3ForEdit}" ],
    "n3optional"        : [ ],
    "newResources"      : { "link" : "${defaultNamespace}" },
    "urisInScope"       : { },
    "literalsInScope"   : { },
    "urisOnForm"        : [ ],
    "literalsOnForm"    : [ "url", "anchor", "rank" ],
    "filesOnForm"       : [ ],
    "sparqlForLiterals" : { },
    "sparqlForUris"     : { },
    "sparqlForExistingLiterals" : {
        "url"         : "${urlQuery}",
        "anchor"      : "${anchorQuery}",
        "rank" : "${rankQuery}"
    },
    "sparqlForExistingUris" : { },
    "fields" : {
      "url" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty", "datatype:${uriDatatypeUriJson}" , "httpUrl" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${uriDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${urlAssertion}" ]
      },
      "anchor" : {
         "newResource"      : "false",
         "validators"       : [ "nonempty", "datatype:${stringDatatypeUriJson}" ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${anchorAssertion}" ]
      },
      "rank" : {
         "newResource"      : "false",
         "validators"       : [ ],
         "optionsType"      : "UNDEFINED",
         "literalOptions"   : [ ],
         "predicateUri"     : "",
         "objectClassUri"   : "",
         "rangeDatatypeUri" : "${stringDatatypeUriJson}",
         "rangeLang"        : "",
         "assertions"       : [ "${rankAssertion}" ]      
      }
    }
  }
</c:set>
<%
    log.debug(request.getAttribute("editjson"));
    
    EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
    if( editConfig == null ){
        editConfig = new EditConfiguration((String)request.getAttribute("editjson"));
        EditConfiguration.putConfigInSession(editConfig, session);
    }

    Model model =  (Model)application.getAttribute("jenaOntModel");   
    if( objectUri != null ){        
        editConfig.prepareForObjPropUpdate(model);            
    }else{
        editConfig.prepareForNonUpdate(model);
    }

    /* get some data to make the form more useful */
    String subjectName = ((Individual)request.getAttribute("subject")).getName();

    String submitLabel=""; 
    String title=" <em>webpage</em> for " + subjectName;
    if (objectUri != null) {
        title = "Edit" + title;
        submitLabel = "Save changes";
    } else {
        title = "Create" + title;
        submitLabel = "Create link";
    }
    
    List<String> customJs = new ArrayList<String>(Arrays.asList(JavaScript.JQUERY_UI.path(),
            JavaScript.CUSTOM_FORM_UTILS.path(),
            "/js/browserUtils.js",
            "/edit/forms/js/manageWebpagesForIndividual.js"
           ));            
    request.setAttribute("customJs", customJs);

    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
             Css.CUSTOM_FORM.path()                                              
            )); 
    request.setAttribute("customCss", customCss); 

%>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />

<jsp:include page="${preForm}"/>

<h2><%= title %></h2>
<form class="customForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
    <v:input type="text" label="URL ${requiredHint}" id="url" size="70"/>
    <v:input type="text" label="Webpage name" id="anchor" size="70"/>
    <p><em>If left blank, the URL will be used when displaying a link to this webpage.</em></p>
    <input type="hidden" name="rank" value="-1" />
    <p class="submit"><v:input type="submit" id="submit" value="<%=submitLabel%>" cancel="true"/></p>
</form>

<jsp:include page="${postForm}"/>
