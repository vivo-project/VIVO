<?xml version="1.0" encoding="UTF-8"?>
<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:edLnk="http://vitro.mannlib.cornell.edu/vitro/tags/PropertyEditLink" version="2.0">

<jsp:directive.page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"/>
<jsp:directive.page import="edu.cornell.mannlib.vitro.webapp.beans.User"/>

<div class="editingForm">

<jsp:include page="/templates/edit/fetch/vertical.jsp"/>

<div align="center">
<table class="form-background" border="0" cellpadding="2" cellspacing="2">
<tr align="center">
    <td valign="bottom">
        <form action="listUsers" method="get">
        <input type="hidden" name="home" value="${portalBean.portalId}" />
            <input type="submit" class="form-button" value="See All User Accounts"/>
        </form>
    </td>
    <td valign="bottom" align="center">
        <form action="editForm" method="get">
            <input name="home" type="hidden" value="${portalBean.portalId}" />
            <input name="uri" type = "hidden" value="${user.URI}" />
            <input type="submit" class="form-button" value="Edit User Account"/>
        <input type="hidden" name="controller" value="User"/>
        </form>
         <form action="editForm" method="get">
            <input name="home" type="hidden" value="${portalBean.portalId}" />
            <input name="uri" type = "hidden" value="${user.URI}" />
            <input name="Md5password" type="hidden" value=""/>
            <input name="OldPassword" type="hidden" value=""/>
            <input type="submit" class="form-button" value="Reset Password"/>
            <input type="hidden" name="controller" value="User"/>
        </form>
    </td>
    <td valign="bottom">
        <form action="editForm" method="get">
            <input name="home" type="hidden" value="${portalBean.portalId}" />
        <input type="hidden" name="controller" value="User"/>
            <input type="submit" class="form-button" value="Add New User Account"/>
        </form>
    </td>            
</tr>
</table>

<!-- Commenting out account association (to an individual of class foaf:Person) feature to reduce confusion in release 1.1

<c:if test="true">

<h3 class="associate">Associate user account with a person</h3>
<table class="form-background" border="0" cellpadding="2" cellspacing="2">  
  <c:if test="${requestScope.mayEditAsStmts != null }">          
    <c:forEach items="${requestScope.mayEditAsStmts }" var="stmt">
      <c:url var="deleteUrl" value="/edit/editRequestDispatch.jsp">
        <c:param name="subjectUri">${user.URI}</c:param>
        <c:param name="predicateUri">${stmt.propertyURI}</c:param>
        <c:param name="objectUri">${stmt.objectURI}</c:param>
        <c:param name="editform">admin/mayEditAs.jsp</c:param>                
      </c:url>
      <tr>
        <td>
        	<c:if test="${stmt.object == null or empty stmt.object.name }">
        		<c:set var="associatedIndividual" value="${stmt.objectURI}" />
        	</c:if>
        	<c:if test="${stmt.object != null and !empty stmt.object.name }">
        	  <c:set var="associatedIndividual" value="${stmt.object.name}" />
        	</c:if>            
          ${associatedIndividual} - <a href="${deleteUrl}">Change or Remove Association</a>
        </td>            
      </tr>
    </c:forEach> 
    <tr><td><em class="note">Note: <c:if test="${requestScope.user.roleURI == 1 }">This association allows the user to edit this person and be redirected to the person's profile when logging in.</c:if><c:if test="${requestScope.user.roleURI != 1 }">This association will result in the user being redirected to the person's profile when logging in.</c:if></em></td></tr>             
  </c:if>
  
  <c:if test="${requestScope.mayEditAsStmts == null  }">
    <tr>
      <td>
        <c:url var="addUrl" value="/edit/editRequestDispatch.jsp">
        <c:param name="subjectUri">${user.URI}</c:param>
        <c:param name="editform">admin/mayEditAs.jsp</c:param>
        </c:url>
        This user account is not associated with a person - <a href="${addUrl}">Select a person</a>
      </td>
    </tr>
    <tr>
      <td><em class="note">Note: <c:if test="${requestScope.user.roleURI == 1 }">Until an association is made, the self editor has no permissions to edit. Associating this user account to a person allows the user to edit this person and be redirected to the person's profile when logging in.</c:if><c:if test="${requestScope.user.roleURI != 1 }">Associating this user account to a person will result in the user being redirected to the person's profile when logging in.</c:if></em></td>
    </tr>  
  </c:if>
    
</table>

</c:if>
-->
</div>
</div>
</jsp:root>