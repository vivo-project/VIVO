<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%-- Included in siteAdmin_body.jsp to handle login/logout form and processing --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>

<c:url var="loginJSP" value="<%= Controllers.LOGIN_JSP %>" />
<c:set var="loginFormTitle" value="<h3>Please log in</h3>" />


    
<% 
    int securityLevel = loginHandler.ANYBODY;
    String loginStatus = loginHandler.getLoginStatus(); 
    if ( loginStatus.equals("authenticated")) {
%>
        <div id="logoutPanel">
<%
    } else {
%>
        <div id="loginPanel" class="pageBodyGroup">
<%
    }
    if ( loginStatus.equals("authenticated")) {
    	
    // test whether session is still valid 
    String currentSessionId = session.getId();
    String storedSessionId = loginHandler.getSessionId();
    
        if ( currentSessionId.equals( storedSessionId ) ) {
            String currentRemoteAddrStr = request.getRemoteAddr();
            String storedRemoteAddr = loginHandler.getLoginRemoteAddr();
            securityLevel = Integer.parseInt( loginHandler.getLoginRole() );

            if ( currentRemoteAddrStr.equals( storedRemoteAddr ) ) { 
%>              
               <form class="logout" name="logout" action="${loginJSP}" method="post">
                   <input type="hidden" name="home" value="<%=portal.getPortalId()%>"/>
                   <em>Logged in as</em> <strong><jsp:getProperty name="loginHandler" property="loginName" /></strong>
                   <input type="submit" name="loginSubmitMode" value="Log out" class="logout-button button" />
               </form>
             
<%          
            } else { 
%>
               ${loginFormTitle}
               <em>(IP address has changed)</em><br />
<%              
               loginHandler.setLoginStatus("logged out");
            }
        
        } else {
            loginHandler.setLoginStatus("logged out"); 
%>
            ${loginFormTitle}
            <em>(session has expired)</em><br/>
            <form class="login" name="login" action="${loginJSP}" method="post" onsubmit="return isValidLogin(this) ">
                <input type="hidden" name="home" value="<%=portal.getPortalId()%>" />
                Username: <input type="text" name="loginName" size="10" class="form-item"  /><br />
                Password: <input type="password" name="loginPassword" size="10" class="form-item" /><br />
                <input type="submit" name="loginSubmitMode" value="Log in" class="form-item button" />
            </form>
<%      
        }
    
    } else { /* not thrown out by coming from different IP address or expired session; check login status returned by authenticate.java */ 
%>
        <h3>Please log in</strong></h3>
<%      
        if ( loginStatus.equals("logged out")) { %>
            <em class="noticeText">(currently logged out)</em>
<%      } else if ( loginStatus.equals("bad_password")) { %>
            <em class="errorText">(password incorrect)</em><br/>
<%      } else if ( loginStatus.equals("unknown_username")) { %>
            <em class="errorText">(unknown username)</em><br/>
<%      } else if ( loginStatus.equals("first_login_no_password")) { %>
            <em class="noticeText">(1st login; need to request initial password below)</em>
<%      } else if ( loginStatus.equals("first_login_mistyped")) { %>
            <em class="noticeText">(1st login; initial password entered incorrectly)</em>
<%      } else if ( loginStatus.equals("first_login_changing_password")) { %>
            <em class="noticeText">(1st login; changing to new private password)</em>
<%      } else if ( loginStatus.equals("changing_password_repeated_old")) { %>
            <em class="noticeText">(changing to a different password)</em>
<%      } else if ( loginStatus.equals("changing_password")) { %>
            <em class="noticeText">(changing to new password)</em>
<%      } else if ( loginStatus.equals("none")) { %>
            <em class="noticeText">(new session)</em><br/>
<%      } else { %>
            <em class="errorText">Status unrecognized: <%=loginStatus.replace("_", " ")%></em><br/>
<%      } %>
        
        <!--<form class="old-global-form" name="login" action="${loginJSP}" method="post" onsubmit="return isValidLogin(this) ">-->
        <form class="old-global-form" name="login" action="shibauth_admin_login.jsp" method="post" onsubmit="return isValidLogin(this) ">
            <input type="hidden" name="home" value="<%=portal.getPortalId()%>" />
            <label for="loginName">Username:</label>
<%      
            if ( loginStatus.equals("bad_password") || loginStatus.equals("first_login_no_password")
                || loginStatus.equals("first_login_mistyped") || loginStatus.equals("first_login_changing_password")
                || loginStatus.equals("changing_password_repeated_old") || loginStatus.equals("changing_password") ) { %>
                <input id="username" type="text" name="loginName" value='<%=loginHandler.getLoginName()%>' size="10" class="form-item" /><br />
<%          } else { %>
                <input id="username" type="text" name="loginName" size="10" class="form-item" /><br />
<%              if ( loginStatus.equals("unknown_username") )  { %>
                    <em class="errorText usernameError">Unknown username</em>          
<%              }  
            } 
%>
            <label for="loginPassword">Password:</label>
            <input id="password" type="password" name="loginPassword" size="10" class="form-item" /><br />

<%          String passwordError=loginHandler.getErrorMsg("loginPassword");
            if (passwordError!=null && !passwordError.equals("")) {%>
                <em class="errorText passwordError"><%=passwordError%></em>
<%          } %>

            <input type="submit" name="loginSubmitMode" value="Log in" class="form-item button" />
            <br></br>
            <a href="https://vivotest.ctrip.ufl.edu/Shibboleth.sso/Login?target=<%=response.encodeURL("https://vivotest.ctrip.ufl.edu/vitro/shibauth_admin_login.jsp")%>">UF Login &raquo;</a>
            <br><br>
            <a href="shibauth_admin_login.jsp">Test UF Login &raquo;</a>
        </form>
<%  } %>

</div> <!--  end loginPanel -->
