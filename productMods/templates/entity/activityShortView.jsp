<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom short view for ResearchActivity, TeachingActivity, OutreachActivity, and ServiceActivity

    SUBJECT - entity
    PREDICATE - predicateUri
    OBJECT - individual
    
--%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<c:set var="vivoCore" value="http://vivoweb.org/ontology/core#" />
<c:set var="rdfs" value="<%= VitroVocabulary.RDFS %>" />
<c:set var="labelUri" value="${rdfs}label" />

<c:set var="researchActivityUri" value="${vivoCore}hasResearchActivity" />
<c:set var="teachingActivityUri" value="${vivoCore}hasTeachingActivity" />
<c:set var="serviceActivityUri" value="${vivoCore}professionalServiceActivity" />
<c:set var="outreachActivityUri" value="${vivoCore}hasOutreachActivity" />

<c:set var="predicateIsActivity" value="${predicateUri == researchActivityUri || 
                                          predicateUri == teachingActivityUri || 
                                          predicateUri == serviceActivityUri ||
                                          predicateUri == outreachActivityUri}" />

<c:choose>
    <c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced - in this case, the Activity object --%>
        <c:choose>  
            <c:when test="${!empty predicateUri}">
                
                <c:set var="activity" value="${individual}" />
                <%-- Get the Activity label --%>
                <c:set var="activityLabel" value=" ${activity.name}"/>
                <%-- Create an html link element to the activity --%>
                <c:url var="activityUrl" value="/individual"><c:param name="uri" value="${activity.URI}"/></c:url>
                <c:set var="activityLink" ><a href='${activityUrl}'><p:process>${activityLabel}</p:process></a></c:set>
                <c:if test="${!empty activityLink}" >
                    <c:set var="activityLink" value="<strong>${activityLink}</strong> " />
                </c:if> 

                               
                <%-- Get the role of the person in the activity --%>
                <c:set var="role" value="${activity.dataPropertyMap['http://vivoweb.org/ontology/core#role'].dataPropertyStatements[0].data}"/>
                    
                <%-- Get the time span of the service activity --%>
                <c:set var="startYearMonth" value="${activity.dataPropertyMap['http://vivoweb.org/ontology/core#startYearMonth'].dataPropertyStatements[0].data}"/>
                <c:set var="endYearMonth" value="${activity.dataPropertyMap['http://vivoweb.org/ontology/core#endYearMonth'].dataPropertyStatements[0].data}"/>
                <c:set var="timeSpan" value="" />
                <c:if test="${!empty startYearMonth || !empty endYearMonth}" >
                    <c:if test="${!empty startYearMonth}" >
                        <c:set var="start" value="${fn:split(startYearMonth, '-')}" />
                        <c:set var="startDate" value="${start[1]}/${start[0]}" />  
                        <c:set var="timeSpan" value="${startDate}" />                  
                    </c:if>
                    <c:set var="timeSpan" value="${timeSpan} - " />
                    <c:if test="${!empty endYearMonth}">
                        <c:set var="end" value="${fn:split(endYearMonth, '-')}" />
                        <c:set var="endDate" value="${end[1]}/${end[0]}" /> 
                        <c:set var="timeSpan" value="${timeSpan}${endDate}" />                       
                    </c:if>
                </c:if>

                <%-- Construct the final output --%>   
                <p:process>${activityLink} </p:process> 
                
                <%-- We need a join but we have to keep getting variables in and out of JSTL/EL/Java. Do this for now even though it's ugly. --%>
                <c:choose>                
                    <c:when test="${!empty role && !empty timeSpan}">
                        <p:process>${role}, ${timeSpan}</p:process>
                    </c:when>   
                    <c:when test="${!empty role}">
                        <p:process>${role}</p:process>
                    </c:when>  
                    <c:when test="${!empty timeSpan}">
                        <p:process>${timeSpan}</p:process>
                    </c:when>
                </c:choose>         
            </c:when>
            
            <c:otherwise> <%-- no predicate --%>
                <c:out value="No predicate available for custom rendering ..."/>
            </c:otherwise>
        </c:choose>
    </c:when>
    
    <c:otherwise> <%-- no object --%>
        <c:out value="Got nothing to draw here ..."/>
    </c:otherwise>
</c:choose>
