<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseAdvancedDataToolsPages" %>
<% request.setAttribute("requestedActions", new UseAdvancedDataToolsPages()); %>
<vitro:confirmAuthorization />

<h2>Ingest Menu</h2>

<ul class="ingestMenu">
    <li><a href="ingest?action=connectDB">Connect DB</a></li>
    <li><a href="ingest?action=listModels">Manage Jena Models</a></li>
    <li><a href="ingest?action=subtractModels">Subtract One Model from Another</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="ingest?action=csv2rdf">Convert CSV to RDF</a></li>
    <li><a href="jenaXmlFileUpload">Convert XML to RDF</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="ingest?action=executeSparql">Execute SPARQL CONSTRUCT</a></li>
    <li><a href="ingest?action=generateTBox">Generate TBox</a></li>
    <li><a href="ingest?action=renameBNodes">Name Blank Nodes</a></li>
    <li><a href="ingest?action=smushSingleModel">Smush Resources</a></li>
    <li><a href="ingest?action=mergeIndividuals">Merge Individuals</a></li>
    <li><a href="ingest?action=renameResource">Change Namespace of Resources</a></li> 
    <li><a href="ingest?action=processStrings">Process Property Value Strings</a></li>
    <li><a href="ingest?action=splitPropertyValues">Split Property Value Strings into Multiple Property Values</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="harvester/harvest?job=csvPerson">Harvest Person Data from CSV</a></li>
    <li><a href="harvester/harvest?job=csvGrant">Harvest Grant Data from CSV</a></li>
    <li><a href="ingest?action=executeWorkflow">Execute Workflow</a></li>
</ul>