<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTION); %>
<vitro:confirmAuthorization />

<h2>Ingest Menu</h2>

<ul class="ingestMenu">
    <li><a href="ingest?action=listModels"title="Manage all available Jena models">Manage Jena Models</a></li>
    <li><a href="ingest?action=subtractModels" title="Subtract one model from another and save difference to new model">Subtract One Model from Another</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="ingest?action=csv2rdf" title="Convert a CSV file to RDF in preparation for ingest">Convert CSV to RDF</a></li>
    <li><a href="jenaXmlFileUpload" title="Convert an XML file to RDF in preparation for ingest">Convert XML to RDF</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="ingest?action=executeSparql" title="Run a SPARQL CONSTRUCT query and apply results to an available model">Execute SPARQL CONSTRUCT</a></li>
    <li><a href="ingest?action=generateTBox" title="Generate TBox from assertions data">Generate TBox</a></li>
    <li><a href="ingest?action=renameBNodes" title="Convert blank nodes to named resources">Name Blank Nodes</a></li>
    <li><a href="ingest?action=smushSingleModel" title="Convert all existing URIs for a resource to a single URI">Smush Resources</a></li>
    <li><a href="ingest?action=mergeResources" title="Merge two resources into one">Merge Resources</a></li>
    <li><a href="ingest?action=renameResource" title="Change the namespace of resources currently in a specified namespace">Change Namespace of Resources</a></li> 
    <li><a href="ingest?action=processStrings" title="Process property value strings">Process Property Value Strings</a></li>
    <li><a href="ingest?action=splitPropertyValues" title="Split property value strings into multiple property values using a regular expression pattern">Split Property Value Strings into Multiple Property Values</a></li>
</ul>

<ul class="ingestMenu">
<!--
    <li><a href="harvester/harvest?job=csvPerson" title="Use the harvester to ingest person data from CSV files">Harvest Person Data from CSV</a></li>
    <li><a href="harvester/harvest?job=csvGrant" title="Use the harvester to ingest grant data from CSV files">Harvest Grant Data from CSV</a></li>
-->
    <li><a href="ingest?action=executeWorkflow" title="Execute an RDF-encoded ingest workflow">Execute Workflow</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="ingest?action=dumpRestore" title="Dump or Restore the knowledge base">Dump or Restore the knowledge base</a></li>
</ul>
