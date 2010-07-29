<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- 
This form will not allow an edit but will redirect to the publication.
This can be used to skip over an authorship context node.

What does this do on an add?
It shouldn't encounter an add, it will redirect to the subject.  Hide the add with in a policy.

What about the delete link?
The delete link will not go to this form.  You should hide the delete link with the policy.
--%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.VitroRequest" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.web.MiscWebUtils" %>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>

<%! 
	public static Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.jsp.edit.forms.redirectToObject.jsp");
	public static String nodeToPubProp = "http://vivoweb.org/ontology/core#linkedInformationResource";
%>
<%	
    VitroRequest vreq = new VitroRequest(request);
    WebappDaoFactory wdf = vreq.getWebappDaoFactory();    
               
    Individual subject = (Individual) request.getAttribute("subject");
	Individual obj = (Individual) request.getAttribute("object");
	
	if( obj == null ){
    	log.warn("this custom form is intended to redirect to the object but none was found.");
    	%>  
    	<jsp:forward page="/individual">
    		<jsp:param value="${subjectUri}" name="uri"/>
    	</jsp:forward>  
    	<%
    }else{
    	List<ObjectPropertyStatement> stmts =  obj.getObjectPropertyStatements( nodeToPubProp );
    	if( stmts == null || stmts.size() == 0 ){
    		%>  
        	<jsp:forward page="/individual">
        		<jsp:param value="${subjectUri}" name="uri"/>
        	</jsp:forward>  
        	<%	
    	} else {
    		ObjectPropertyStatement ops = stmts.get(0);
    		String pubUri = ops.getObjectURI();
    		if( pubUri != null ){
    			%>  
            	<jsp:forward page="/individual">
            		<jsp:param value="<%= pubUri %>" name="uri"/>
            	</jsp:forward>  
            	<%	
    		} else{
    			%>  
            	<jsp:forward page="/individual">
            		<jsp:param value="${subjectUri}" name="uri"/>
            	</jsp:forward>  
            	<%	}	
    		}
    	}
%>
  