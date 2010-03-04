<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<%-- Custom short view for ServiceActivity 

    SUBJECT - entity
    PREDICATE - predicateUri
    OBJECT - individual

--%>

<c:choose>
    <c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced - in this case, a ServiceActivity object --%>
        <c:choose>  
            <c:when test="${!empty predicateUri}">
                <c:set var="serviceActivity" value="${individual}" />

                <%-- Get the ServiceActivity title --%>
                <c:set var="title" value=" ${serviceActivity.dataPropertyMap['http://vivoweb.org/ontology/core#titleOrRole'].dataPropertyStatements[0].data}"/>
                                
                <%-- Get the time span of the service activity --%>
                <%-- RY FIX the display --%>
                <c:set var="startYearMonth" value="${serviceActivity.dataPropertyMap['http://vivoweb.org/ontology/core#startYearMonth'].dataPropertyStatements[0].data}"/>
                <c:set var="endYearMonth" value="${serviceActivity.dataPropertyMap['http://vivoweb.org/ontology/core#endYearMonth'].dataPropertyStatements[0].data}"/>
                <c:if test="${!empty startYearMonth}">
                    <c:set var="timeSpan" value=", ${startYearMonth} - "/>
                    <c:if test="${!empty endYearMonth}">
                        <c:set var="timeSpan" value="${timeSpan}${endYearMonth}"/>
                    </c:if>
                </c:if>

                <c:choose>
                    <%-- CASE 1: SUBJECT is Person, OBJECT is ServiceActivity --%>           
                    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#professionalServiceActivity'}">
                        <%-- The head object of the short view is the Organization object's name or data property organization name. Get it from the service activity. --%>
                        <c:choose>
                            <%-- This ServiceActivity object is linked to an Organization object --%>
                            <c:when test="${!empty serviceActivity.objectPropertyMap['http://vivoweb.org/ontology/core#activityRelatedOrganization']}">
                                <c:set var="obj" value="${serviceActivity.objectPropertyMap['http://vivoweb.org/ontology/core#activityRelatedOrganization'].objectPropertyStatements[0].object}" />
                                <c:set var="objName" value="${obj.name}"/>
                                <c:set var="objUri" value="${obj.URI}"/>                      
                            </c:when>
                            
                            <%-- The ServiceActivity object has an organization name data property --%>
                            <c:when test="${!empty serviceActivity.dataPropertyMap['http://vivoweb.org/ontology/core#involvedOrganizationName'].dataPropertyStatements[0].data}">
                                <c:set var="objName" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#involvedOrganizationName'].dataPropertyStatements[0].data}"/>
                            </c:when>
                            
                            <c:otherwise><%-- There's not even an organization name, so just display the title in place of the name --%>
                               <c:set var="objName" value="${title}"/>
                               <c:set var="title" value="" /><%-- don't output title twice --%>
                            </c:otherwise>
                        </c:choose>                          
                    </c:when>

                    <%-- CASE 2: SUBJECT is Organization, OBJECT is ServiceActivity --%>
                    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#organizationRelatedActivity'}">
                        <c:set var="obj" value="${serviceActivity.objectPropertyMap['http://vivoweb.org/ontology/core#professionalServiceActivityBy'].objectPropertyStatements[0].object}" />
                        <c:set var="objName" value="${obj.name}"/>
                        <c:set var="objUri" value="${obj.URI}"/>                                          
                    </c:when>
                          
                    <%-- CASE 3: Other predicate --%>    
                    <c:otherwise>
                        <c:set var="objName" value="unknown object"/>
                    </c:otherwise>             
                </c:choose>

                <%-- Create an html link element to the objUri --%>
                <c:if test="${!empty objUri}">
                    <c:url var="orgLink" value="/entity"><c:param name="uri" value="${objUri}"/></c:url>
                    <c:set var="openLink" value="<a href='${orgLink}'>" />
                    <c:set var="closeLink" value="</a>" />
                </c:if>  

                <%-- Final output --%>                        
                ${openLink}<p:process><strong>${objName}</strong></p:process>${closeLink}<p:process>${title}${timeSpan}</p:process>
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
