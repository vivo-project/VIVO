<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>
<jsp:useBean id="now" class="org.joda.time.DateTime"/>

<c:choose>
	<c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced -- the Position, not the Person or Organization --%>
	
        <c:set var="startYear" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#startYear'].dataPropertyStatements[0].data}"/>
        <c:set var="endYear" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#endYear'].dataPropertyStatements[0].data}"/>
        <c:choose>
	        <c:when test="${! empty startYear}">
		        <c:choose>
		            <c:when test="${! empty endYear}">
		                <c:set var="timeSpan" value=", ${startYear} - ${endYear}"/>
		            </c:when>
		            <c:otherwise>
		                <c:set var="timeSpan" value=", ${startYear} - "/>
		            </c:otherwise>
		        </c:choose>
			</c:when>
			<c:when test="${! empty endYear}">
                <c:set var="timeSpan" value=", - ${endYear}" />
			</c:when>
		</c:choose>
        	
        <c:choose><%-- use working title in preference to HR title --%>
            <c:when test="${!empty individual.dataPropertyMap['http://vivoweb.org/ontology/core#titleOrRole'].dataPropertyStatements[0].data}">
                <c:set var="title" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#titleOrRole'].dataPropertyStatements[0].data}"/>
            </c:when>
            <c:when test="${!empty individual.dataPropertyMap['http://vivoweb.org/ontology/core#hrJobTitle'].dataPropertyStatements[0].data}">
                <c:set var="title" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#hrJobTitle'].dataPropertyStatements[0].data}"/>
            </c:when>
            <c:otherwise>
                <c:set var="title" value="${individual.name}"/>
            </c:otherwise>
        </c:choose>           

		<c:choose>
			<c:when test="${!empty predicateUri}">
 			    <c:choose>
				    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#personInPosition'}"><%-- SUBJECT is a Person, so get info from Position about related Organization --%>
					    <c:choose>
                             <c:when test="${!empty individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionInOrganization']}"><%-- this Position is linked to an Organization --%>
					            <c:set var="objName" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionInOrganization'].objectPropertyStatements[0].object.name}"/>
                                <c:choose>
                                    <c:when test="${!empty title}">
                                        <c:set var="objLabel" value="${title}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="objLabel" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionInOrganization'].objectPropertyStatements[0].object.moniker}"/>
                                    </c:otherwise>
                                </c:choose>
					            <c:set var="objUri" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionInOrganization'].objectPropertyStatements[0].object.URI}"/>
				            </c:when>
				            <c:otherwise><%-- this Position is not linked to an Organization --%>
                                <c:choose>
                                    <c:when test="${!empty individual.dataPropertyMap['http://vivoweb.org/ontology/core#involvedOrganizationName'].dataPropertyStatements[0].data}"><%-- an Organization name has been specified --%>
                                        <c:set var="objName" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#involvedOrganizationName'].dataPropertyStatements[0].data}"/>
                                        <c:set var="objLabel" value="${title}"/>
                                    </c:when>
                                    <c:otherwise><%-- not even an organization name, so just display the title in place of the name --%>
                                        <c:set var="objName" value="${title}"/>
                                        <c:set var="title" value=""/><%-- don't show title twice --%>
                                    </c:otherwise>
                                </c:choose>
				            </c:otherwise>
				        </c:choose>
				        
				        <%-- Hack to control extra spaces...should be done differently though. --%>
                        <c:if test="${!empty objName && !empty objLabel}" >
                            <c:set var="objLabel" value=" ${objLabel}" />
                        </c:if>
                        
						<c:choose>
					    	<c:when test="${!empty objUri}">
					            <c:url var="objLink" value="/entity"><c:param name="uri" value="${objUri}"/></c:url>
				                <a href="<c:out value="${objLink}"/>"><p:process>${objName}</p:process></a><p:process>${objLabel}${timeSpan}</p:process>
				            </c:when>
				            <c:otherwise>
				                <p:process><strong>${objName}</strong>${objLabel}${timeSpan}</p:process> 
				            </c:otherwise>
		        		</c:choose>
				    </c:when>
			    			    
					<c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#organizationForPosition'}"><%-- SUBJECT is an Organization, so get info from Position about the related Person --%>
				    	<c:choose>
				    	   
				    	   <c:when test="${not empty endYear && now.year > endYear}" >
				    	   	<%-- don't show because the position is not current --%>
				    	   </c:when> 
				    	   
				    		<c:when test="${!empty individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionForPerson']}"><%-- there is a related Person --%>
					    		<c:set var="objName" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionForPerson'].objectPropertyStatements[0].object.name}"/>
					    		<c:choose>
					    		    <c:when test="${!empty title}">
					    		        <c:set var="objLabel" value="${title}"/>
					    		    </c:when>
					    		    <c:otherwise>
					    		        <c:set var="objLabel" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionForPerson'].objectPropertyStatements[0].object.moniker}"/>
					    		    </c:otherwise>
					    		</c:choose>
					    		<c:set var="objUri" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#positionForPerson'].objectPropertyStatements[0].object.URI}"/>
								
								<c:choose>
							    	<c:when test="${!empty objUri}">
							            <c:url var="objLink" value="/entity"><c:param name="uri" value="${objUri}"/></c:url>
						                <a href="<c:out value="${objLink}"/>"><p:process>${objName}</p:process></a> <p:process>${objLabel} ${timeSpan}</p:process>
						            </c:when>
						            <c:otherwise>
						                <p:process><strong>${objName}</strong> ${objLabel} ${timeSpan}</p:process> 
						            </c:otherwise>
				        		</c:choose>				        									
					    	</c:when>
					    	
					    	<c:otherwise><%-- no related Person, which should not happen --%>
					    		<c:set var="objName" value="${individual.name}"/>
					    		<c:set var="objLabel" value="${title}"/>
					        </c:otherwise>
					    </c:choose>
					</c:when>
					
				    <c:otherwise>
				        <c:set var="objName" value="unknown predicate"/>
				        <c:set var="objUri" value="${predicateUri}"/>
				    </c:otherwise>
			    </c:choose>			
			
			</c:when>
			<c:otherwise>
				<c:out value="No predicate available for custom rendering ..."/>
			</c:otherwise>
        </c:choose>
	</c:when>
	<c:otherwise>
		<c:out value="Got nothing to draw here ..."/>
	</c:otherwise>
</c:choose>
