<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<%-- 
	This is a custom short view render for educational background.
	The variable individual is the OBJECT of the property statement to be rendered. -- 
	In this JSP that is the  Educational Background, not the Person, Organization or DegreeType
 --%>

<c:choose>
	<c:when test="${!empty individual}">		   		
	    <c:choose>
	    	<%-- SUBJECT is a Person  --%>
		    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#educationalBackground'}">		        
		        <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
		        <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>
		        
		        <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>
		        <c:set var="selectedOrganizationName" value="${selectedOrganization.name}"/>
		        
		        <c:set var="degreeType" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#degreeTypeAwarded'].objectPropertyStatements[0].object}"/>
				<c:set var="degreeAbbreviation" value="${degreeType.dataPropertyMap['http://vivoweb.org/ontology/core#degreeAbbreviation'].dataPropertyStatements[0].data}"/>
				<c:if test="${ empty degreeAbbreviation }">
					  <c:set var="degreeAbbreviation" value="${degreeType.name}"/>
				</c:if>
	
				<c:choose>
			    	<c:when test="${! empty year && ! empty degreeMajor && ! empty selectedOrganizationName && ! empty degreeAbbreviation }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${degreeAbbreviation} in ${degreeMajor}</p:process></a> <p:process>${selectedOrganizationName}, ${year}</p:process>
		            </c:when>
		            <c:when test="${! empty year && empty degreeMajor && ! empty selectedOrganizationName && ! empty degreeAbbreviation }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${degreeAbbreviation}</p:process></a> <p:process>${selectedOrganizationName}, ${year}</p:process>
		            </c:when>
		            <c:when test="${ empty year && empty degreeMajor && ! empty selectedOrganizationName && ! empty degreeAbbreviation }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${degreeAbbreviation}</p:process></a> <p:process>${selectedOrganizationName}</p:process>
		            </c:when>
		            <c:when test="${ ! empty year &&  empty degreeMajor && empty selectedOrganizationName && ! empty degreeAbbreviation }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${degreeAbbreviation}</p:process></a> <p:process>${year}</p:process>
		            </c:when>
		            <c:otherwise>
		                <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
	        			<a href="${objLink}"><p:process>${individual.name}</p:process></a>		                 
		            </c:otherwise>
	       		</c:choose>
		    </c:when>
		        
		    <%-- SUBJECT is a Degree Type  --%>			    		
 			<c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#awardedTo'}">		        
		        <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
		        <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>
		        
		        <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>
		        <c:set var="selectedOrganizationName" value="${selectedOrganization.name}"/>		        		        
	
				<c:set var="personName" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#educationalBackgroundOf'].objectPropertyStatements[0].object.name}"/>
				
				<c:choose>
			    	<c:when test="${! empty personName && ! empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${personName} in ${degreeMajor}</p:process></a> <p:process>${selectedOrganizationName}, ${year}</p:process>
		            </c:when>
		            <c:when test="${! empty personName && empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${personName} in ${degreeMajor}</p:process></a> <p:process>${selectedOrganizationName}</p:process>
		            </c:when>
		            <c:when test="${! empty personName && empty year && ! empty degreeMajor && empty selectedOrganizationName  }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${personName} in ${degreeMajor}</p:process></a>
		            </c:when>
		            <c:when test="${! empty personName && ! empty year && empty degreeMajor && ! empty selectedOrganizationName  }">
			            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
		                <a href="<c:out value="${objLink}"/>"><p:process>${personName}</p:process></a> <p:process>${selectedOrganizationName}, ${year}</p:process>
		            </c:when>
		            <c:otherwise>
		                <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
	        			<a href="${objLink}"><p:process>${individual.name}</p:process></a>		                 
		            </c:otherwise>
	       		</c:choose>
		    </c:when>    			    		
	
	    <%-- The predicate was not one of the predicted ones, so create a normal link --%>	
	    <c:otherwise>	        
	        <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
	        <a href="${objLink}"><p:process>${individual.name}</p:process></a>
	    </c:otherwise>
    </c:choose>			

			
	</c:when>
	
	<%-- This clause is when there is no object individual defined, it should never be reached. --%>
	<c:otherwise>
		<c:out value="Nothing to draw in edBackgroundShortView"/>
	</c:otherwise>
</c:choose>
