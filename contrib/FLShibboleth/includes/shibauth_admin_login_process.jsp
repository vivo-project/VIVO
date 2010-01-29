<%@ page isThreadSafe="false" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>

<%    final int DEFAULT_PORTAL_ID=1;
    String portalIdStr=(portalIdStr=(String)request.getAttribute("home"))==null ?
        ((portalIdStr=request.getParameter("home"))==null?String.valueOf(DEFAULT_PORTAL_ID):portalIdStr):portalIdStr;
    //int incomingPortalId=Integer.parseInt(portalIdStr);     %>

<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session">
<jsp:setProperty name="loginHandler" property="*"/>
</jsp:useBean>
<% 
    // Get shib_idp and glid from the shib returned header information
    
    //////////////////////////////////////////////////////////////////////
    // Change both variables to empty string when shib works
    //////////////////////////////////////////////////////////////////////
    String shib_idp = "";
    String glid = ""; 
    
    Enumeration headerNames = request.getHeaderNames();
    while(headerNames.hasMoreElements()) {
         String headerName = (String)headerNames.nextElement();
      
          if (headerName.equals("shib-identity-provider")) {
              shib_idp = request.getHeader(headerName);
          }
          
           if (headerName.equals("glid")) {
              glid = request.getHeader(headerName);
          }
          
          //out.println("<br>" + headerName + ":" + request.getHeader(headerName));
    }
    
    //out.println("<br>shib_idp:" + shib_idp);
    //out.println("<br>glid:" + glid);
    if (shib_idp.equals("https://login.ufl.edu/idp/shibboleth")) {
        //out.println("<br>portalIdStr:" + portalIdStr);
%>

<% 
    String submitModeStr = request.getParameter("loginSubmitMode");
    
    //out.println("<br>loginSubmitMode:" + submitModeStr);
    if ( submitModeStr == null )
        submitModeStr = "Log In";
    
    //out.println("<br>loginSubmitMode:" + submitModeStr);    
    
    if ( submitModeStr == null )
        submitModeStr = "unknown";
    if ( submitModeStr.equals("Log Out")) {
%>
        <jsp:forward page="/logout" >
            <jsp:param name="home" value="<%=portalIdStr%>" />
        </jsp:forward>
<% 
    } else if ( submitModeStr.equals("Log In")) {
        //out.println("<br>Log In 1");
        //String loginNameStr = request.getParameter("loginName");
        String loginNameStr = glid;
        String loginPasswordStr = "sia#ia*9"; //request.getParameter("loginPassword"); %>
        <jsp:setProperty name="loginHandler" property="loginName" value="<%=loginNameStr%>" />
        <jsp:setProperty name="loginHandler" property="loginPassword" value="<%=loginPasswordStr%>" />
        <jsp:setProperty name="loginHandler" property="loginRemoteAddr" value="<%=request.getRemoteAddr()%>" />
<%     
        //if ( loginHandler.validateLoginForm() ) {
        if ( loginNameStr != "" ) {
            //out.println("<br>Log In 2");
            
%>
            <jsp:forward page="/shibauthenticate" >
                <jsp:param name="home" value="<%=portalIdStr%>" />
            </jsp:forward>
<%        
            
        } else {
            //out.println("<br>Log In 3");
            
            String redirectURL="/vitro/about?home="+portalIdStr+"&amp;login=block";
            response.sendRedirect(redirectURL);
}
        }
%>

<%
    } else {
        out.println("<br>Wrong IDP<br>");
    }
%>
