<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Custom short view for ResearchActivity, TeachingActivity, OutreachActivity, and ServiceActivity

    SUBJECT - entity
    PREDICATE - predicateUri
    OBJECT - individual

    Predicates:  on Person page: Person professionalServiceActivity ServiceActivity

--%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"%>

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
                <%-- RY may need another branch in here; predicateIsActivity vs predicate is something else...
                depends if this would display on something other than a person page. --%>
                <c:set var="activity" value="${individual}" />

                <%-- Get the Activity label --%>
                <c:set var="activityLabel" value=" ${activity.name}"/>
                
                <%-- Get the role of the person in the activity --%>
                <c:set var="role" value=" ${activity.dataPropertyMap['http://vivoweb.org/ontology/core#role'].dataPropertyStatements[0].data}"/>
                                               
                <%-- Get the start date of the activity --%>
                <c:set var="startDate" value="${activity.dataPropertyMap['http://vivoweb.org/ontology/core#startYearMonth'].dataPropertyStatements[0].data}"/>
    
                <%-- Create an html link element to the activity --%>
                <c:url var="activityUrl" value="/individual"><c:param name="uri" value="${activity.URI}"/></c:url>
                <c:set var="activityLink" ><a href='${activityUrl}'><p:process>${activityLabel}</p:process></a></c:set>

                <%-- Final output --%>                        
                <strong>${activityLink}</strong> <p:process>${role}, ${startDate}</p:process>
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
