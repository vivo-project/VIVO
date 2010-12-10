<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.utils.StringUtils" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- 
    This is a custom short view render for educational background.
    The variable individual is the OBJECT of the property statement to be rendered. -- 
    In this JSP that is the  Educational Training object, not the Person, Organization or DegreeType
 --%>

<c:choose>
    <c:when test="${!empty individual}">                
        <c:choose>
            <%-- SUBJECT is a Person  --%>
            <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#educationalTraining'}">         
            
                <%-- Degree type and major --%>               
                <c:set var="degreeLabel" value="" />
                <c:set var="degreeType" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#degreeEarned'].objectPropertyStatements[0].object}"/>
                <c:if test="${ !empty degreeType }">
                    <c:set var="degreeAbbr" value="${degreeType.dataPropertyMap['http://vivoweb.org/ontology/core#abbreviation'].dataPropertyStatements[0].data}"/>
                    <c:set var="degreeLabel" value="${!empty degreeAbbr ? degreeAbbr : degreeType.name }" />               
                </c:if>

                <%-- RY 12/10/10 majorField is no longer required, so make sure we display something reasonable if there is no major field. --%>
                <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>

                <c:choose>
                    <c:when test="${ !empty degreeLabel && !empty degreeMajor }">
                        <c:set var="degreeStr" value="${degreeLabel} in ${degreeMajor}" />
                    </c:when>
                    <c:when test="${ !empty degreeLabel}">
                        <c:set var="degreeStr" value="${degreeLabel}" />
                    </c:when>
                    <c:when test="${ !empty degreeMajor }">
                        <c:set var="degreeStr" value="${degreeMajor}" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="degreeStr" value="" />
                    </c:otherwise>
                </c:choose>

                <%-- Organization granting degree --%>
                <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>              
                <c:if test="${ ! empty selectedOrganization }">                        
                    <c:url var="selectedOrganizationURL" value="/individual">
                        <c:param name="uri" value="${selectedOrganization.URI}"/>
                    </c:url>
                    <c:set var="selectedOrganizationStr" ><a href='${selectedOrganizationURL}'><p:process>${selectedOrganization.name}</p:process></a></c:set>
                </c:if>
                
                <%-- Optional department/school to organization --%>
                <c:set var="degreeDeptOrSchool" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#departmentOrSchool'].dataPropertyStatements[0].data}"/>
                <c:if test="${ ! empty degreeDeptOrSchool }"> 
                </c:if>
                        
                <%-- Year of degree --%>                             
                <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
                
                <%-- Supplemental information --%>
                <c:set var="degreeSupplementalInfo" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#supplementalInformation'].dataPropertyStatements[0].data}"/>        
                
                <%-- Build the output string --%>
                <c:choose>
                    <c:when test="${empty degreeStr && empty selectedOrganizationStr && empty degreeDeptOrSchool}">
                        <a href="${objLink}"><p:process>educational background ${individual.name}</p:process></a>        
                    </c:when>
                    <c:otherwise>                       
                        <%= StringUtils.join(", ", (String) pageContext.getAttribute("degreeStr"),
                                                   (String) pageContext.getAttribute("selectedOrganizationStr"), 
                                                   (String) pageContext.getAttribute("degreeDeptOrSchool"),
                                                   (String) pageContext.getAttribute("year"), 
                                                   (String) pageContext.getAttribute("degreeSupplementalInfo") ) %>                     
                    </c:otherwise>
                </c:choose>
            </c:when>
              
            <%-- SUBJECT is a Degree Type  --%>                     
            <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#degreeOutcomeOf'}">             
                <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
                <c:set var="degreeMajor" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#majorField'].dataPropertyStatements[0].data}"/>
                
                <c:set var="selectedOrganization" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#organizationGrantingDegree'].objectPropertyStatements[0].object}"/>
                <c:set var="selectedOrganizationName" value="${selectedOrganization.name}"/>                                
    
                <c:set var="person" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#educationalTrainingOf'].objectPropertyStatements[0].object}"/>
                <c:set var="personName" value="${person.name}"/>
                <c:url var="personURL" value="/individual"><c:param name="uri" value="${person.URI}"/></c:url>
                <c:set var="personLink" ><a href='${personURL}'>${personName}</a></c:set>
                
                <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
                
                <c:choose>
                    <c:when test="${! empty personName && ! empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">                      
                        ${personLink} in ${degreeMajor}, ${selectedOrganizationName}, ${year}
                    </c:when>
                    <c:when test="${! empty personName && empty year && ! empty degreeMajor && ! empty selectedOrganizationName  }">
                        ${personLink} in ${degreeMajor}, ${selectedOrganizationName}                                            
                    </c:when>
                    <c:when test="${! empty personName && empty year && ! empty degreeMajor && empty selectedOrganizationName  }">
                        ${personLink} in ${degreeMajor}                                          
                    </c:when>
                    <c:when test="${! empty personName && ! empty year && empty degreeMajor && ! empty selectedOrganizationName  }">
                        ${personLink} ${selectedOrganizationName}, ${year}                                              
                    </c:when>
                    <c:otherwise>                       
                        <a href="${objLink}">educational background ${individual.name}</a>                        
                    </c:otherwise>
                </c:choose>
            </c:when>                           
    
        <%-- The predicate was not one of the predicted ones, so create a normal link --%>  
        <c:otherwise>           
            <c:url var="objLink" value="/individual"><c:param name="uri" value="${individual.URI}"/></c:url>
            <a href="${objLink}">${individual.name}</a>
        </c:otherwise>
    </c:choose>         

            
    </c:when>
    
    <%-- This clause is when there is no object individual defined, it should never be reached. --%>
    <c:otherwise>
        <c:out value="Nothing to draw in educationalTrainingShortView"/>
    </c:otherwise>
</c:choose>
