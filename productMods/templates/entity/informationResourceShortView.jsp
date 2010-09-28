<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- rjy7 NIHVIVO-1158 Under core:authorInAuthorship on a person's page, we are now displaying the object property statements
for the related property core:linkedInformationResource, so that we can collate by publication subclass. The subject is the
Authorship, and the object is the InformationResource, so the authorship short view defined on Authorships no longer applies.
We thus define an information resource short view to display the publications. --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced -- the InformationResource individual --%>
        <c:choose>
            <c:when test="${!empty predicateUri}">
                <c:choose>
                    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#authorInAuthorship'}">      
                        <c:set var="name"  value="${individual.name}"/>      
                        <c:set var="uri" value="${individual.URI}"/>
                        <c:set var="year" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="name" value="unknown predicate - please contact your VIVO support team"/>
                        <c:set var="uri" value="${predicateUri}"/>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${!empty uri}">
                        <c:url var="olink" value="/entity"><c:param name="uri" value="${uri}"/></c:url>
                        <a href="<c:out value="${olink}"/>">${name}</a> ${year}
                    </c:when>
                    <c:otherwise>
                        <strong>${name}</strong> ${year}
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
