<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>
 
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<%-- 
This short view is intended to be called to handle short views for roles
 
The following vars should be set by the jsp that is calling this short view 
    personToRolePredicate: URI of the person to role predicate.
    roleToPersonPredicate: URI of the role to person predicate. 
    roleActivityToRolePredicate: URI of the activity to role predicate.

    roleActivityLabel: human readable label of activity used for error messages   
 
Optional vars:
    roleLabelForPerson: human readable label for person when viewing from non-person side of role. Most short views 
        don't specify this value because the role name is displayed instead. Grant-related short views specify
        this value because there is no specific role name. 	
    startYearPredicate: defaults to http://vivoweb.org/ontology/core#startYear if not specified
 --%>

<c:set var="startYearPredicate">${! empty param.startYearPredicate ? param.startYearPredicate : 'http://vivoweb.org/ontology/core#startYear'}</c:set>
<c:set var="showDateRange">${ startYearPredicate == 'http://vivoweb.org/ontology/core#startYear' }</c:set>

<c:set var="errorMsg" value=""/>
<c:choose>
	<c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced -- the Role individual, not the Person or grant --%>
 		<c:choose>
			<c:when test="${!empty predicateUri}">
											
			    <%-- get years off role --%>
				<c:set var="startYear" value="${individual.dataPropertyMap[startYearPredicate].dataPropertyStatements[0].data}"/>
                <c:if test="${! empty startYear}">
                	<c:set var="endYear" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#endYear'].dataPropertyStatements[0].data}"/>
                    <c:if test="${startYear == endYear}">
                        <c:set var="showDateRange" value="false" />
                    </c:if>
                	<c:if test="${showDateRange}">                       
                        <c:set var="endYearVal" value = " -" />
                        <c:if test="${! empty endYear}">
                            <c:set var="endYearVal" value="${endYearVal} ${endYear}" />
                        </c:if>
                    </c:if>
                </c:if>
                
 			    <c:choose>
 			    	<%-- SUBJECT is a Person, so get info from other part of the role --%>
				    <c:when test="${predicateUri == param.personToRolePredicate}">
					    <c:choose>
                            <c:when test="${ ! empty individual.objectPropertyMap['http://vivoweb.org/ontology/core#roleIn'] }">
					            <c:set var="roleActivity" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#roleIn'].objectPropertyStatements[0].object}" />
					            <c:set var="name"  value="${roleActivity.name}"/> 					            
					            <%-- On the person page, it's redundant to display the role label in this case, since the object property
					            label contains the same information. --%>
					            <c:set var="label" value=""/>
					            <c:if test="${ empty param.roleLabelForPerson }" >
					            	<c:set var="label" value="${ ! empty individual.rdfsLabel ? individual.rdfsLabel : ''}" />
					            </c:if>
					             					            					           
                                <c:set var="uri" value="${roleActivity.URI}"/>                                                                
                            </c:when>
 				            <c:otherwise>
 				            <%-- This Role is not linked to anything yet; use name as a placeholder and 
 				                 add link to the Role so user can add more information. --%> 				                 				                     				                    
								<c:set var="name" value="unknown"/>
 				                <c:set var="errorMsg" value="&nbsp;(unidentified activity - please edit)"/>
 				                <c:set var="label" value="${ ! empty individual.rdfsLabel ? individual.rdfsLabel : 'unlabeled role' }"/>
                                <c:set var="uri" value=""/>
				            </c:otherwise>
				        </c:choose>
				    </c:when>
				    
				    <%-- SUBJECT is an activity of some sort, so get info from the Role about the related Person --%>
				    <c:when test="${predicateUri == param.roleActivityToRolePredicate}">				    				   
				    	<c:choose>
				    	
				    	    <%-- there is a related Person --%>
				    		<c:when test="${!empty individual.objectPropertyMap[ param.roleToPersonPredicate ]}">				    		
				    		    <c:set var="person" value="${individual.objectPropertyMap[ param.roleToPersonPredicate ].objectPropertyStatements[0].object}" />
					    		<c:set var="name" value="${person.name}"/>
					    		
					    		<c:set var="label" value="${individual.rdfsLabel}"/>
					    		<c:if test="${ empty individual.rdfsLabel }">
					    		   <c:set var="label" value="${ ! empty param.roleLabelForPerson ? param.roleLabelForPerson : '' }"/>
					    		</c:if>
					            					    							    		
                                <c:set var="uri" value="${person.URI}"/>
					    	</c:when>					    						    	
					    	
					    	<%-- this is a Role with out a Person (likely from before custom form available) --%>
					    	<c:otherwise>					    	
                                <c:choose>
                                    <c:when test="${!empty individual.name}"><c:set var="name" value="${individual.name}"/></c:when>
                                    <c:otherwise><c:set var="name" value="unlabeled ${param.roleActivityLabel} to person relation"/></c:otherwise>
                               </c:choose>
                               <c:set var="name" value="unknown person"/>
                               <c:set var="label" value="${ ! empty individual.rdfsLabel ? individual.rdfsLabel : 'unlabeled param.roleActivityLabel to person relation' }"/>                   
                               <c:set var="errorMsg" value="&nbsp;(unidentified person - please edit)"/>
                               <c:set var="uri" value=""/>
					        </c:otherwise>
					    </c:choose>
					</c:when>
					
				    <c:otherwise>				    				
				        <c:set var="name" value="unknown predicate"/>
				        <c:set var="label" value="please contact your VIVO support team"/>
				        <c:set var="uri" value="${predicateUri}"/>
				    </c:otherwise>
			    </c:choose>
			    
			    <%-- only show error messages if logged in --%>
			    <c:if test="${ ! showSelfEdits}">
			       <c:set var="errorMsg" value=""/>
			    </c:if>
			    
			    <%-- output the actual html --%>
			    <c:choose>
			    	<c:when test="${!empty uri}">
			            <c:url var="olink" value="/entity"><c:param name="uri" value="${uri}"/></c:url>
		                <a href="<c:out value="${olink}"/>">${name}</a>&nbsp;${label}&nbsp;${startYear}${endYearVal} ${errorMsg}
		            </c:when>
		            <c:otherwise>
		                <p:process><strong>${name}</strong> ${label}</p:process> ${errorMsg}
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
