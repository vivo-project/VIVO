<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<%-- 
	This is a custom short view render for educational background.
	The variable individual is the OBJECT of the property statement to be rendered. -- 
	In this JSP that is the  Educational Background, not the Person, Organization or DegreeType
 --%>

<c:if test="${sessionScope.loginHandler != null &&
              sessionScope.loginHandler.loginStatus == 'authenticated' &&
              sessionScope.loginHandler.loginRole >= sessionScope.loginHandler.dba }">
              <c:set var="showEdBackgroundContextNode" value="true"/>
</c:if>

<c:choose>
	<c:when test="${!empty individual}">		   		
	    <c:choose>
	    	<%-- SUBJECT is a Person  --%>
		    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#educationalBackground'}">		        
		        <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
		        <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>
		        
		        <c:set var="degreeSupplementalInfo" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#supplementalInformation'].dataPropertyStatements[0].data}"/>
				<c:if test="${ not empty degreeSupplementalInfo }">
					<c:set var="degreeSupplementalInfo" value=", ${degreeSupplementalInfo}"/> 
				</c:if>		        
				
		        <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>		       
		        <c:url var="selectedOrganizationURL" value="/individual">
		        	<c:param name="uri" value="${selectedOrganization.URI}"/>
				</c:url>
		        <c:set var="selectedOrganizationStr" >, <a href='${selectedOrganizationURL}'><p:process>${selectedOrganization.name}</p:process></a></c:set>
		        
		        <c:set var="degreeDeptOrSchool" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#departmentOrSchool'].dataPropertyStatements[0].data}"/>
		        <c:if test="${ not empty degreeDeptOrSchool }">
					<c:set var="selectedOrganizationStr">${selectedOrganizationStr}, <p:process>${degreeDeptOrSchool}</p:process></c:set> 
				</c:if>
		        
		        
		        <c:set var="degreeType" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#degreeTypeAwarded'].objectPropertyStatements[0].object}"/>
				<c:set var="degreeAbbreviation" value="${degreeType.dataPropertyMap['http://vivoweb.org/ontology/core#degreeAbbreviation'].dataPropertyStatements[0].data}"/>
				<c:if test="${ empty degreeAbbreviation }">
					  <c:set var="degreeAbbreviation" value="${degreeType.name}"/>
				</c:if>
	
				<c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
				<c:if test="${showEdBackgroundContextNode}">
					<c:set var="contextNodeURL" value="<a href='${objLink}'> context node</a>"/>
				</c:if>
				
				<c:choose>
				   <%-- degreeMajor, year, org and abbreviation are all required --%>
			    	<c:when test="${!empty degreeAbbreviation && ! empty year && ! empty degreeMajor && ! empty selectedOrganizationStr }">			            
						<p:process>${degreeAbbreviation} in ${degreeMajor}</p:process> ${selectedOrganizationStr},<p:process> ${year} ${degreeSupplementalInfo}</p:process> ${contextNodeURL }
		            </c:when>		            
		            <c:when test="${!empty degreeAbbreviation && ! empty year && empty degreeMajor && ! empty selectedOrganizationStr  }">
		                <p:process>${degreeAbbreviation}</p:process> ${selectedOrganizationStr},<p:process> ${year} ${degreeDeptOrSchool} ${degreeSupplementalInfo}</p:process> ${contextNodeURL }
		            </c:when>
		            <c:when test="${!empty degreeAbbreviation &&  empty year && empty degreeMajor && ! empty selectedOrganizationStr  }">
		                <p:process>${degreeAbbreviation}</p:process> {selectedOrganizationStr} <p:process> ${degreeDeptOrSchool} ${degreeSupplementalInfo}</p:process> ${contextNodeURL }
		            </c:when>
		            <c:when test="${!empty degreeAbbreviation &&  ! empty year &&  empty degreeMajor && empty selectedOrganizationStr }">
		                <p:process>${degreeAbbreviation} ${year} ${degreeDeptOrSchool} ${degreeSupplementalInfo}</p:process> ${contextNodeURL }
		            </c:when>
		            <c:otherwise>
	        			<a href="${objLink}"><p:process>educational background ${individual.name}</p:process></a>		                 
		            </c:otherwise>
	       		</c:choose>
		    </c:when>
		        
		    <%-- SUBJECT is a Degree Type  --%>			    		
 			<c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#awardedTo'}">		        
		        <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
		        <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>
		        
		        <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>
		        <c:set var="selectedOrganizationName" value="${selectedOrganization.name}"/>		        		        
	
				<c:set var="person" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#educationalBackgroundOf'].objectPropertyStatements[0].object}"/>
				<c:set var="personName" value="${person.name}"/>
				<c:url var="personURL" value="/individual"><c:param name="uri" value="${person.URI}"/></c:url>
				<c:set var="personLink" ><a href='${personURL}'><p:process>${personName}</p:process></a></c:set>
				
				<c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
				
				<c:choose>
			    	<c:when test="${! empty personName && ! empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">			            
		                ${personLink} <p:process> in ${degreeMajor}, ${selectedOrganizationName}, ${year}</p:process>
		            </c:when>
		            <c:when test="${! empty personName && empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">
		            	${personLink} <p:process> in ${degreeMajor}, ${selectedOrganizationName}</p:process>			            		                
		            </c:when>
		            <c:when test="${! empty personName && empty year && ! empty degreeMajor && empty selectedOrganizationName  }">
		            	${personLink} <p:process> in ${degreeMajor}</p:process>			            		                
		            </c:when>
		            <c:when test="${! empty personName && ! empty year && empty degreeMajor && ! empty selectedOrganizationName  }">
		            	${personLink} <p:process> ${selectedOrganizationName}, ${year}</p:process>			            		                
		            </c:when>
		            <c:otherwise>		                
	        			<a href="${objLink}"><p:process>educational background ${individual.name}</p:process></a>		                 
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
