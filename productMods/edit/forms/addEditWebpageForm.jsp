<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom form for adding or editing a webpage associated with an individual. The primary page,
manageWebpagesForIndividual.jsp, forwards to this page if: (a) we are adding a new page, or 
(b) an edit link in the Manage Webpages view has been clicked. 

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
<%@ page import="com.hp.hpl.jena.query.ResultSet" %>
<%@ page import="com.hp.hpl.jena.rdf.model.RDFNode" %>
<%@ page import="com.hp.hpl.jena.query.QuerySolution" %>
<%@ page import="com.hp.hpl.jena.query.Dataset" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Css" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils" %>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="v" uri="http://vitro.mannlib.cornell.edu/vitro/tags" %>
<%! 
    public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.addEditWebpageForm.jsp");

%>

<%
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    vreq.setAttribute("defaultNamespace", wdf.getDefaultNamespace());
    
    String subjectUri = (String) request.getAttribute("subjectUri");
    String propertyUri = (String) request.getAttribute("predicateUri");
    String objectUri = (String) request.getAttribute("objectUri");  

    String stringDatatypeUriJson = MiscWebUtils.escape(XSD.xstring.toString());
    String uriDatatypeUriJson = MiscWebUtils.escape(XSD.anyURI.toString());
    String intDatatypeUriJson = MiscWebUtils.escape(XSD.xint.toString());
%>

<c:set var="stringDatatypeUriJson" value="<%= stringDatatypeUriJson %>" />
<c:set var="uriDatatypeUriJson" value="<%= uriDatatypeUriJson %>" />
<c:set var="intDatatypeUriJson" value="<%= intDatatypeUriJson %>" />

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
            <${linkUrl}>  ?url .
          
</v:jsonset>

<c:set var="returnPathAfterSubmit" value="/edit/editRequestDispatch.jsp?subjectUri=${subjectUri}&predicateUri=${predicateUri}&view=manage" />

<c:set var="editjson" scope="request">
  {
    "formUrl" : "${formUrl}",
    "editKey" : "${editKey}",
    "urlPatternToReturnTo" : "${returnPathAfterSubmit}",
    
    "subject"   : ["subject",    "${subjectUriJson}" ],
    "predicate" : ["predicate", "${predicateUriJson}" ],
    "object"    : ["link", "${objectUriJson}", "URI" ],
    
    "n3required"        : [ "${n3ForEdit}" ],
    "n3optional"        : [ "${rankAssertion}", "${anchorAssertion}" ],
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
        "rank"        : "${rankQuery}"
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
         "validators"       : [ "datatype:${stringDatatypeUriJson}" ],
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
         "rangeDatatypeUri" : "${intDatatypeUriJson}",
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
    } else {
        editConfig.prepareForNonUpdate(model);
    }

    List<String> customCss = new ArrayList<String>(Arrays.asList(Css.JQUERY_UI.path(),
             Css.CUSTOM_FORM.path()                                              
            )); 
    request.setAttribute("customCss", customCss); 
    
    String subjectName = ((Individual)request.getAttribute("subject")).getName();
    
    // Get largest existing rank value to compute hidden rank field value
    int newRank = getMaxRank(objectUri, subjectUri, vreq) + 1;

%>

<c:choose>
    <c:when test="${ ! empty objectUri }">  
        <c:set var="editMode" value="edit" />
        <c:set var="title" value="Edit webpage of" />        
        <c:set var="submitButtonText" value="Save changes" />
        <c:set var="cancelUrl" value="" />
    </c:when>
    <c:otherwise>
        <c:set var="editMode" value="add" />
        <c:set var="title" value="Add a webpage for" />        
        <c:set var="submitButtonText" value="Add webpage" />
        <%-- Cancel to where ever we came from: either directly from the profile page, or from
             the Manage Web Pages screen. The latter has added a url param to signal itself. --%>
        <c:set var="cancelUrl" value="${ param.cancelTo == 'manage' ? '' : '/individual' }" />
    </c:otherwise>
</c:choose>

<c:set var="requiredHint" value="<span class='requiredHint'> *</span>" />

<jsp:include page="${preForm}"/>

<h2>${title}&nbsp;<%= subjectName %></h2>

<form class="customForm" action="<c:url value="/edit/processRdfForm2.jsp"/>" >
    <v:input type="text" label="URL ${requiredHint}" id="url" size="70"/>
    <v:input type="text" label="Webpage Name" id="anchor" size="70"/>
    <p><em>If left blank, the URL will be used when displaying a link to this webpage.</em></p>
    <c:if test="${editMode == 'add'}">
        <input type="hidden" name="rank" value="<%= newRank %>" />
    </c:if>
    <p class="submit">
        <v:input type="submit" id="submit" value="${submitButtonText}" cancel="true" cancelUrl="${cancelUrl}" />
    </p>
</form>

<jsp:include page="${postForm}"/>

<%!

/* Note on ordering by rank in sparql: if there is a non-integer value on a link, that will be returned,
 * since it's ranked highest. Preventing that would require getting all the ranks and sorting in Java,
 * throwing out non-int values. 
 */
private static String RANK_QUERY = ""
    + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
    + "SELECT DISTINCT ?rank WHERE { \n"
    + "    ?subject core:webpage ?link . \n"
    + "    ?link core:rank ?rank .\n"
    + "} ORDER BY DESC(?rank) LIMIT 1";
    
private int getMaxRank(String objectUri, String subjectUri, VitroRequest vreq) {

    int maxRank = 0; // default value 
    if (objectUri == null) { // adding new webpage   
        String queryStr = QueryUtils.subUriForQueryVar(RANK_QUERY, "subject", subjectUri);
        log.debug("Query string is: " + queryStr);
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            if (results != null && results.hasNext()) { // there is at most one result
                QuerySolution soln = results.next(); 
                RDFNode node = soln.get("rank");
                if (node != null && node.isLiteral()) {
                    // node.asLiteral().getInt() won't return an xsd:string that 
                    // can be parsed as an int.
                    int rank = Integer.parseInt(node.asLiteral().getLexicalForm());
                    if (rank > maxRank) {  
                        log.debug("setting maxRank to " + rank);
                        maxRank = rank;
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.error("Invalid rank returned from query: not an integer value.");
        } catch (Exception e) {
            log.error(e, e);
        }
    }
    return maxRank;
}

%>
