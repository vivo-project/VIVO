<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib prefix="form" uri="http://vitro.mannlib.cornell.edu/edit/tags" %>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Portal" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>


<%@page import="edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.PelletListener"%>
<jsp:useBean id="loginHandler" class="edu.cornell.mannlib.vedit.beans.LoginFormBean" scope="session" />
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %><%/* this odd thing points to something in web.xml */ %>
<%@ page errorPage="/error.jsp"%>
<%

    Portal portal = (Portal) request.getAttribute("portalBean");
    final String DEFAULT_SEARCH_METHOD = "fulltext"; /* options are fulltext/termlike */
    String loginD = (loginD = request.getParameter("login")) == null ? "block" : loginD.equals("null") || loginD.equals("") ? "block" : loginD;
%>

<html>
<!--<meta http-equiv="Refresh" content="10;url=uf_login_process.jsp">-->
<head>
<style type="text/css"><!--
#LoadingDiv{
    margin: 0px 0px 0px 0px;
    position: fixed;
    height: 100%;
    z-index: 9999;
    padding-top: 300px;
    padding-left: 50px;
    width: 100%;
    clear: none;
    text-align: center;
    font-weight: bolder;
    font-size: 18px;
    background: url('images/transbg50.png');
}
#LoadingDivWhite {
    margin: 0 auto;   
    width: 250px;
    color:#305882;
    height: 50px;
    border: 5px solid #305882;
    background-color: #f3f3f3;
    padding: 10px;
    font-size:10pt;
}
--></style>

</head>


<script>
<!--
    function submitUFform()
    {
        // this is needed for the the loading display/double click prevention
        var ldiv = document.getElementById('LoadingDiv');
        ldiv.style.display = 'block';
        
        document.login.submit();
    }


//-->
</script>

<body onLoad="submitUFform()">





<c:set var='themeDir' ><c:out value='<%=portal.getThemeDir()%>' default='themes/default/'/></c:set>

<script type="text/javascript">
  // Give initial focus to the password or username field 
  $(document).ready(function(){
    if ($("em.passwordError").length > 0) {
      $("input#password").focus();
    } else {
      $("input#username").focus();
    }
  });
</script>


<div id="LoadingDiv" style="display:none;">
<div class="LoadingDivWhite">
    <em>Loading.....</em><br />
    <img src='images/ajax-loader.gif' />
</div>
</div>



<div id="content">
<!-- ############################################################# start left block ########################################################### -->
<%  if (loginHandler.getLoginStatus().equals("authenticated")) { %>
<div class="column span-6 loggedIn">
<%  } else { %>
<div class="column span-6">
<%  } %>
    <div onclick="switchGroupDisplay('loginarea','loginSw','${themeDir}site_icons')" title="click to toggle login fields on or off" class="headerlink" onmouseover="onMouseOverHeading(this)" onmouseout="onMouseOutHeading(this)">
      
    </div>
<%  if (loginHandler.getLoginStatus().equals("authenticated")) { %>
       <div id="loginarea" class="pageGroupBody" style="display:block">
<%  } else { %>
        <div id="loginarea" class="pageGroupBody" style="display:<%=loginD%>">
<%  } %>

<%  if ( loginHandler.getLoginStatus().equals("authenticated")) {
        /* test if session is still valid */
        String currentSessionId = session.getId();
        String storedSessionId = loginHandler.getSessionId();
        if ( currentSessionId.equals( storedSessionId ) ) {
            String currentRemoteAddrStr = request.getRemoteAddr();
            String storedRemoteAddr = loginHandler.getLoginRemoteAddr();
            int securityLevel = Integer.parseInt( loginHandler.getLoginRole() );
            if ( currentRemoteAddrStr.equals( storedRemoteAddr ) ) {%>
            <em>Logged in as:</em> <strong><jsp:getProperty name="loginHandler" property="loginName" /></strong>
              <form class="old-global-form" name="logout" action="login_process.jsp" method="post">
                  <input type="hidden" name="home" value="<%=portal.getPortalId()%>"/>
                  <input type="submit" name="loginSubmitMode" value="Log Out" class="logout-button button" />
              </form>
              
                (<em>${languageModeStr}</em>)
            <%
                    Object plObj = getServletContext().getAttribute("pelletListener");
                    if ( (plObj != null) && (plObj instanceof PelletListener) ) {
                        PelletListener pelletListener = (PelletListener) plObj;
                        if (!pelletListener.isConsistent()) {
                            %>
                                <p class="notice">
                                    INCONSISTENT ONTOLOGY: reasoning halted.
                                </p>
                                <p class="notice">
                                    Cause: <%=pelletListener.getExplanation()%>
                                </p>
                            <% 
                        }
                    }
                %>
                  <ul class="adminLinks">
                <li><a href="listTabs?home=<%=portal.getPortalId()%>">Tabs</a></li>
                <li><a href="listGroups?home=<%=portal.getPortalId()%>">Class groups</a></li>
                <li><a href="listPropertyGroups?home=<%=portal.getPortalId()%>">Property groups</a></li>
                <li><a href="showClassHierarchy?home=<%=portal.getPortalId()%>">Root classes</a></li>
                <li><a href="showObjectPropertyHierarchy?home=${portalBean.portalId}&amp;iffRoot=true">Root object properties</a></li>
                <li><a href="showDataPropertyHierarchy?home=<%=portal.getPortalId()%>">Root data properties</a></li>
                <li><a href="listOntologies?home=<%=portal.getPortalId()%>">Ontologies</a></li>
                <li>
                  <form class="old-global-form" action="editForm" method="get">
                      <select id="VClassURI" name="VClassURI" class="form-item span-23">
                          <form:option name="VClassId"/>
                      </select>
                      <input type="submit" id="submit" value="Add Individual of This Type"/>
                      <input type="hidden" name="home" value="<%=portal.getPortalId()%>" />
                      <input type="hidden" name="controller" value="Entity"/>
                  </form>
                </li>
<%              if (securityLevel>=4) { %>
                <li><a href="editForm?home=<%=portal.getPortalId()%>&amp;controller=Portal&amp;id=<%=portal.getPortalId()%>">Edit Current Portal</a></li>
                          <li><a href="listPortals?home=<%=portal.getPortalId()%>">All Portals</a></li>
<%              }
                if (securityLevel>=5) { %>
                    <li><a href="listUsers?home=<%=portal.getPortalId()%>">Administer User Accounts</a></li>
                    <c:if test="${verbosePropertyListing == true}">
                        <li><a href="about?verbose=false">Turn off Verbose Property Display</a></li>
                    </c:if>
                    <c:if test="${empty verbosePropertyListing || verbosePropertyListing == false}">
                        <li><a href="about?verbose=true">Turn on Verbose Property Display</a></li>
                    </c:if>                    
<%              }       
                if (securityLevel>=50) { %>
                    <li><a href="uploadRDFForm?home=<%=portal.getPortalId()%>">Add/Remove RDF Data</a></li>
                    <li><a href="export?home=<%=portal.getPortalId()%>">Export to RDF</a></li>
                    <%-- <li><a href="refactorOp?home=<%=portal.getPortalId()%>&amp;modeStr=fixDataTypes">Realign Datatype Literals</a></li> --%> 
                    <li><a href="admin/sparqlquery">SPARQL Query</a></li>
                    <li><a href="ingest">Ingest Tools</a></li>
                </ul>
<%              } %>
               
<%          } else { %>
                
                <em>(IP address has changed)</em><br>
<%              loginHandler.setLoginStatus("logged out");
            }
        } else {
            loginHandler.setLoginStatus("logged out"); %>
            
            <em>(session has expired)</em><br/>
            <form class="old-global-form" name="login" action="login_process.jsp" method="post" onsubmit="return isValidLogin(this) ">
            <input type="hidden" name="home" value="<%=portal.getPortalId()%>" />
            Username:<input type="text" name="loginName" size="10" class="form-item"  /><br />
            Password:<input type="password" name="loginPassword" size="10" class="form-item" /><br />
            <input type="submit" name="loginSubmitMode" value="Log In" class="form-item button" />
            </form>
<%      }
    } else { /* not thrown out by coming from different IP address or expired session; check login status returned by authenticate.java */ %>

<%      if ( loginHandler.getLoginStatus().equals("logged out")) { %>
            <em class="noticeText">(currently logged out)</em>
<%      } else if ( loginHandler.getLoginStatus().equals("bad_password")) { %>
            <em class="errorText">(password incorrect)</em><br/>
<%      } else if ( loginHandler.getLoginStatus().equals("first_login_no_password")) { %>
            <em class="noticeText">(1st login; need to request initial password below)</em>
<%      } else if ( loginHandler.getLoginStatus().equals("first_login_mistyped")) { %>
            <em class="noticeText">(1st login; initial password entered incorrectly)</em>
<%      } else if ( loginHandler.getLoginStatus().equals("first_login_changing_password")) { %>
            <em class="noticeText">(1st login; changing to new private password)</em>
<%      } else if ( loginHandler.getLoginStatus().equals("changing_password_repeated_old")) { %>
            <em class="noticeText">(changing to a different password)</em>
<%      } else if ( loginHandler.getLoginStatus().equals("changing_password")) { %>
            <em class="noticeText">(changing to new password)</em>
<%      } else if ( loginHandler.getLoginStatus().equals("none")) { %>
            <!--<em class="noticeText">(new session)</em><br/>-->
<%      } else { %>
            <em class="errorText">status unrecognized: <%=loginHandler.getLoginStatus()%></em><br/>
<%      } %>
        <form id="ufform" class="old-global-form" name="login" action="shibauth_admin_login_process.jsp" method="post" onsubmit="return isValidLogin(this) ">
        <input type="hidden" name="home" value="<%=portal.getPortalId()%>" />
        <!--<label for="loginName">Username:</label>-->
<%      String status= loginHandler.getLoginStatus();
        if ( status.equals("bad_password") || status.equals("first_login_no_password")
            || status.equals("first_login_mistyped") || status.equals("first_login_changing_password")
            || status.equals("changing_password_repeated_old") || status.equals("changing_password") ) { %>
            <input id="username" type="text" name="loginName" value='<%=loginHandler.getLoginName()%>' size="10" class="form-item" /><br />
<%      } else { %>
            <!--<input id="username" type="text" name="loginName" size="10" class="form-item" /><br />-->
            <input type="hidden" name="loginName" value="" />
<%      } %>
        
<!--
        <label for="loginPassword">Password:</label>
        <input id="password" type="password" name="loginPassword" size="10" class="form-item" /><br />
        -->
        <input type="hidden" name="password" value="" />

        <%  String passwordError=loginHandler.getErrorMsg("loginPassword");
            if (passwordError!=null && !passwordError.equals("")) {%>
            <em class="errorText passwordError"><%=passwordError%></em>
        <%  } %>
            <!--<input type="submit" name="loginSubmitMode" value="Log In" class="form-item button" />-->
            </form>
<%      } %>
        </div>
        
</div><%-- span-6 --%>

<div class="column span-17">
<%                  String aboutText=portal.getAboutText();
                    if (aboutText!=null && !aboutText.equals("")) {%>
                        <div class="pageGroupBody"><%=aboutText%></div>
<%                  }%>

<%                  String ackText=portal.getAcknowledgeText();
                    if (ackText!=null && !ackText.equals("")) {%>
                        <div class="pageGroupBody"><%=ackText%></div>
<%                  }%>
</div><%-- span-17 --%>

<!--<hr class="clear" />-->
</div> <!-- content -->



</body>
</html>
