<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://djpowell.net/tmp/sparql-tag/0.1/" prefix="sparql" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/string-1.1" prefix="str" %>
<%@ page import="java.net.URLDecoder" %>

<div id="content">
<h2>Linkage Information</h2>
<ul>
	<!--
		Author-Resource
	-->
	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="inforauthorships">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			  PREFIX bibo: <http://purl.org/ontology/bibo/>
	          PREFIX core: <http://vivoweb.org/ontology/core#>
	          SELECT (count(?author) as ?counts) WHERE {
	              ?author rdf:type core:Authorship .
				  ?author core:linkedInformationResource ?infor .
				  ?infor rdf:type core:InformationResource .
	          }
	      </sparql:select>
					<c:forEach items="${inforauthorships.rows}" var="inforauthorship" varStatus="counter">
						<li><a href="#">'Person'-'InformationResource' linkages</a> (${inforauthorship.counts.string})</li>
					</c:forEach>
	  </sparql:sparql>
  <sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="inforauthors">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX foaf: <http://xmlns.com/foaf/0.1/>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?author) as ?counts) WHERE {
              ?author core:authorInAuthorship ?obj .
			  ?author rdf:type foaf:Person .
			  ?obj core:linkedInformationResource ?infor .
			  ?infor rdf:type core:InformationResource .
          }
      </sparql:select>
				<c:forEach items="${inforauthors.rows}" var="inforauthor" varStatus="counter">
					<li><a href="#">'Person' entities which published 'InformationResource' entities</a> (${inforauthor.counts.string})</li>
				</c:forEach>
  </sparql:sparql>
<sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="infors">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?infor) as ?counts) WHERE {
              ?subj core:linkedInformationResource ?infor .
			  ?infor rdf:type core:InformationResource .
          }
      </sparql:select>
				<c:forEach items="${infors.rows}" var="infor" varStatus="counter">
					<li><a href="#">'InformationResource' entities</a> (${infor.counts.string})</li>
				</c:forEach>
  </sparql:sparql>

</ul>

<ul>

	<!--
		Author-Conference_Paper
	-->

	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="confauthorships">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			  PREFIX bibo: <http://purl.org/ontology/bibo/>
	          PREFIX core: <http://vivoweb.org/ontology/core#>
	          SELECT (count(?author) as ?counts) WHERE {
	              ?author rdf:type core:Authorship .
				  ?author core:linkedInformationResource ?infor .
				  ?infor rdf:type core:ConferencePaper .
	          }
	      </sparql:select>
					<c:forEach items="${confauthorships.rows}" var="confauthorship" varStatus="counter">
						<li><a href="#">'Person'-'ConferencePaper' linkages</a> (${confauthorship.counts.string})</li>
					</c:forEach>
	  </sparql:sparql>

  <sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="confauthors">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX foaf: <http://xmlns.com/foaf/0.1/>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?author) as ?counts) WHERE {
              ?author core:authorInAuthorship ?obj .
			  ?author rdf:type foaf:Person .
			  ?obj core:linkedInformationResource ?infor .
			  ?infor rdf:type core:ConferencePaper .
          }
      </sparql:select>
				<c:forEach items="${confauthors.rows}" var="confauthor" varStatus="counter">
					<li><a href="#">'Person' entities which published 'ConferencePaper' entities</a> (${confauthor.counts.string})</li>
				</c:forEach>
  </sparql:sparql>

<sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="confs">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?infor) as ?counts) WHERE {
              ?subj core:linkedInformationResource ?infor .
			  ?infor rdf:type core:ConferencePaper .
          }
      </sparql:select>
				<c:forEach items="${confs.rows}" var="conf" varStatus="counter">
					<li><a href="#">'ConferencePaper' entities</a> (${conf.counts.string})</li>
				</c:forEach>
  </sparql:sparql>
</ul>

<ul>

	<!--
		Author-Academic_Article
	-->
	
	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="acaauthorships">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			  PREFIX bibo: <http://purl.org/ontology/bibo/>
	          PREFIX core: <http://vivoweb.org/ontology/core#>
	          SELECT (count(?author) as ?counts) WHERE {
	              ?author rdf:type core:Authorship .
				  ?author core:linkedInformationResource ?infor .
				  ?infor rdf:type bibo:AcademicArticle .
	          }
	      </sparql:select>
					<c:forEach items="${acaauthorships.rows}" var="acaauthorship" varStatus="counter">
						<li><a href="#">'Person'-'AcademicArticle' linkages</a> (${acaauthorship.counts.string})</li>
					</c:forEach>
	  </sparql:sparql>

  <sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="acaauthors">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX foaf: <http://xmlns.com/foaf/0.1/>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?author) as ?counts) WHERE {
              ?author core:authorInAuthorship ?obj .
			  ?author rdf:type foaf:Person .
			  ?obj core:linkedInformationResource ?infor .
			  ?infor rdf:type bibo:AcademicArticle .
          }
      </sparql:select>
				<c:forEach items="${acaauthors.rows}" var="acaauthor" varStatus="counter">
					<li><a href="#">'Person' entities which published 'AcademicArticle' entities</a> (${acaauthor.counts.string})</li>
				</c:forEach>
  </sparql:sparql>



<sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="acas">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?infor) as ?counts) WHERE {
              ?subj core:linkedInformationResource ?infor .
			  ?infor rdf:type bibo:AcademicArticle .
          }
      </sparql:select>
				<c:forEach items="${acas.rows}" var="aca" varStatus="counter">
					<li><a href="#">'AcademicArticle' entities</a> (${aca.counts.string})</li>
				</c:forEach>
  </sparql:sparql>

</ul>


<ul>
	<!--
		Investigator-Grant
	-->

	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="piships">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			  PREFIX bibo: <http://purl.org/ontology/bibo/>
	          PREFIX core: <http://vivoweb.org/ontology/core#>
	          SELECT (count(*) as ?counts) WHERE {
	              ?grant core:hasInvestigator ?pi .
	          }
	      </sparql:select>
					<c:forEach items="${piships.rows}" var="piship" varStatus="counter">
						<li><a href="#">'Person'-'Grant' linkages</a> (${piship.counts.string})</li>
					</c:forEach>
	  </sparql:sparql>
<sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="pis">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?pi) as ?counts) WHERE {
              ?grant core:hasInvestigator ?pi .
			  ?grant rdf:type core:Grant .
          }
      </sparql:select>
				<c:forEach items="${pis.rows}" var="pi" varStatus="counter">
					<li><a href="#">'Person' entities which are (co-)investigators on 'Grant' entities</a> (${pi.counts.string})</li>
				</c:forEach>
  </sparql:sparql>


<sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="grants">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?grant) as ?counts) WHERE {
			  ?grant rdf:type core:Grant .
          }
      </sparql:select>
				<c:forEach items="${grants.rows}" var="grant" varStatus="counter">
					<li><a href="#">'Grant' entities</a> (${grant.counts.string})</li>
				</c:forEach>
  </sparql:sparql>
</ul>
<ul>

	<!--
		Teacher-Course
	-->

	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="teachings">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			  PREFIX bibo: <http://purl.org/ontology/bibo/>
	          PREFIX core: <http://vivoweb.org/ontology/core#>
	          SELECT (count(*) as ?counts) WHERE {
				  ?teacher core:teaching ?obj .
	          }
	      </sparql:select>
					<c:forEach items="${teachings.rows}" var="teaching" varStatus="counter">
						<li><a href="#">'Person'-'CourseSection' linkages</a> (${teaching.counts.string})</li>
					</c:forEach>
	  </sparql:sparql>

<sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="teachers">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(distinct ?teacher) as ?counts) WHERE {
			  ?teacher core:teaching ?obj .
          }
      </sparql:select>
				<c:forEach items="${teachers.rows}" var="teacher" varStatus="counter">
					<li><a href="#">'Person' entities which teach 'CourseSection' entities</a> (${teacher.counts.string})</li>
				</c:forEach>
  </sparql:sparql>


<sparql:sparql>
      <sparql:select model="${applicationScope.jenaOntModel}" var="courses">
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
		  PREFIX bibo: <http://purl.org/ontology/bibo/>
          PREFIX core: <http://vivoweb.org/ontology/core#>
          SELECT (count(?course) as ?counts) WHERE {
			  ?course rdf:type core:CourseSection .
          }
      </sparql:select>
				<c:forEach items="${courses.rows}" var="course" varStatus="counter">
					<li><a href="#">'CourseSection' entities</a> (${course.counts.string})</li>
				</c:forEach>	
  </sparql:sparql>
	
</ul>
<ul>
	<!--
		Co-Author Linkage
	-->
	
	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="coauthors">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
			PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			PREFIX bibo: <http://purl.org/ontology/bibo/>
			PREFIX core: <http://vivoweb.org/ontology/core#>
			SELECT (count(*) as ?counts) WHERE {
			?author1 rdf:type core:Authorship .
			?author2 rdf:type core:Authorship .
			?author1 core:linkedInformationResource ?infor .
			?author2 core:linkedInformationResource ?infor .
			?infor rdf:type core:InformationResource .
			FILTER (str(?author1) < str(?author2)) 
			}
	      </sparql:select>
					<c:forEach items="${coauthors.rows}" var="coauthor" varStatus="counter">
						<li><a href="#">Total co-author linkages</a> (${coauthor.counts.string})</li>
					</c:forEach>
	  </sparql:sparql>
	
	<!--
		Distinct Co-Author Linkage
	-->

	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="discoauthors">
	        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
			PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			PREFIX bibo: <http://purl.org/ontology/bibo/>
			PREFIX core: <http://vivoweb.org/ontology/core#>
			SELECT DISTINCT ?author1 ?author2 WHERE {
			?author1 rdf:type core:Authorship .
			?author2 rdf:type core:Authorship .
			?author1 core:linkedInformationResource ?infor .
			?author2 core:linkedInformationResource ?infor .
			?infor rdf:type core:InformationResource .
			FILTER (str(?author1) < str(?author2)) 
			}
	      </sparql:select>
		<li><a href="#">Unique co-author linkages</a> (${fn:length(discoauthors.rows)})</li>
	  </sparql:sparql>
	</ul>
	<ul>	
	<!--
		Co-Investigator Linkage
	-->
	
	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="copis">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			  PREFIX bibo: <http://purl.org/ontology/bibo/>
	          PREFIX core: <http://vivoweb.org/ontology/core#>
	          SELECT (count(*) as ?counts) WHERE {
	              ?grant core:hasInvestigator ?pi1 .
				  ?grant core:hasInvestigator ?pi2 .
				  FILTER (str(?pi1) < str(?pi2))
	          }
	      </sparql:select>
					<c:forEach items="${copis.rows}" var="copi" varStatus="counter">
						<li><a href="#">Total co-investigator linkages</a> (${copi.counts.string})</li>
					</c:forEach>
	  </sparql:sparql>
	
	<!--
		Distinct Co-Investigator Linkage
	-->

	<sparql:sparql>
	      <sparql:select model="${applicationScope.jenaOntModel}" var="discopis">
	          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
	      	  PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
	          PREFIX akt:  <http://www.aktors.org/ontology/portal#>
			  PREFIX bibo: <http://purl.org/ontology/bibo/>
	          PREFIX core: <http://vivoweb.org/ontology/core#>
	          SELECT DISTINCT ?pi1 ?pi2 WHERE {
	              ?grant core:hasInvestigator ?pi1 .
				  ?grant core:hasInvestigator ?pi2 .
				  FILTER (str(?pi1) < str(?pi2))
	          }
	      </sparql:select>
	<li><a href="#">Unique co-investigator linkages</a> (${fn:length(discopis.rows)})</li>
	  </sparql:sparql>
</ul>
</div>