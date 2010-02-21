<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://vitro.mannlib.cornell.edu/vitro/tags/StringProcessorTag" prefix="p" %>

<c:choose>
	<c:when test="${!empty individual}"><%-- individual is the OBJECT of the property referenced -- the Authorship individual, not the Person or Publication --%>
        <%-- c:set var="authorRank" value="${individual.dataPropertyMap['http://vivoweb.org/ontology/core#authorRank'].dataPropertyStatements[0].data}"/ --%>
 		<c:choose>
			<c:when test="${!empty predicateUri}">
 			    <c:choose>
				    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#authorInAuthorship'}"><%-- SUBJECT is a Person, so get info from Authorship about related Publication --%>
					    <c:choose>
                            <c:when test="${!empty individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource']}"><%-- this Position is linked to an Organization --%>
					            <c:set var="name"    value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.name}"/>
                                <c:set var="moniker" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.moniker}"/>
                                <c:set var="year"    value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.dataPropertyMap['http://vivoweb.org/ontology/core#year'].dataPropertyStatements[0].data}"/>
                                <c:set var="label" value="${moniker} ${year}"/>
                                <c:set var="uri"     value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedInformationResource'].objectPropertyStatements[0].object.URI}"/>
                            </c:when>
 				            <c:otherwise><%-- this Position is not linked to a Publication yet; use Authorship name as a placeholder and add link to the Authorship so user can add more information --%>
 				                <c:choose>
 				                    <c:when test="${!empty individual.name}">
 				                        <c:set var="name" value="${individual.name}"/>
 				                    </c:when>
 				                    <c:otherwise>
                                        <c:set var="name" value="unlabeled authorship"/>
                                    </c:otherwise>
 				                </c:choose>
                                <c:set var="label" value="(no publication linked yet)"/>
                                <c:set var="uri" value="${individual.URI}"/>
				            </c:otherwise>
				        </c:choose>
				    </c:when>
				    <c:when test="${predicateUri == 'http://vivoweb.org/ontology/core#informationResourceInAuthorship'}"><%-- SUBJECT is a Publication, so get info from Authorship about the related Person --%>
				    	<c:choose>
				    		<c:when test="${!empty individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor']}"><%-- there is a related Person --%>
					    		<c:set var="name"  value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor'].objectPropertyStatements[0].object.name}"/>
                                <c:set var="label" value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor'].objectPropertyStatements[0].object.moniker}"/>
                                <c:set var="uri"   value="${individual.objectPropertyMap['http://vivoweb.org/ontology/core#linkedAuthor'].objectPropertyStatements[0].object.URI}"/>
					    	</c:when>
					    	<c:when test="${!empty individual.dataPropertyMap['http://vivoweb.org/ontology/core#authorNameAsListed'].dataPropertyStatements[0].data}"><%-- only an author name has been specified --%>
                                <c:set var="name" value="<strong>${individual.dataPropertyMap['http://vivoweb.org/ontology/core#authorNameAsListed'].dataPropertyStatements[0].data}</strong>"/>
					    	</c:when>
					    	<c:otherwise><%-- no related Person yet (likely from before custom form available) --%>
                                <c:choose>
                                    <c:when test="${!empty individual.name}"><c:set var="name" value="${individual.name}"/></c:when>
                                    <c:otherwise><c:set var="name" value="unlabeled authorship"/></c:otherwise>
                               </c:choose>
                               <c:set var="label" value="(no author linked yet)"/>
                               <c:set var="uri" value="${individual.URI}"/>
					        </c:otherwise>
					    </c:choose>
					</c:when>
				    <c:otherwise>
				        <c:set var="name" value="unknown predicate"/>
				        <c:set var="label" value="please contact your VIVO support team"/>
				        <c:set var="uri" value="${predicateUri}"/>
				    </c:otherwise>
			    </c:choose>
			    <c:choose>
			    	<c:when test="${!empty uri}">
			            <c:url var="olink" value="/entity"><c:param name="uri" value="${uri}"/></c:url>
		                <a href="<c:out value="${olink}"/>"><p:process>${name}</p:process></a> <p:process>${label}</p:process>
		            </c:when>
		            <c:otherwise>
		                <p:process><strong>${name}</strong> ${label}</p:process> 
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
