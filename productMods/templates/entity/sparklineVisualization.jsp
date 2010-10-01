<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean"%>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.Individual" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.beans.VClass" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.flags.PortalFlagChoices" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page errorPage="/error.jsp"%>
<%  /***********************************************
    Displays the sparkline visualizations on individual profile pages

    request.attributes:
    an Entity object with the name "entity" 


    request.parameters:
    None, should only work with requestScope attributes for security reasons.

    Consider sticking < % = MiscWebUtils.getReqInfo(request) % > in the html output
    for debugging info.
            
    **********************************************/ 
    Individual entity = (Individual)request.getAttribute("entity");
    boolean displayVisualization = false;

    if (entity == null){
        String e = "sparklineVisuzalition.jsp expects that request attribute 'entity' be set to the Entity object to display.";
        displayVisualization = false;
        throw new JspException(e);
    } else {
        for (VClass currClass : entity.getVClasses()) {
            if ("http://xmlns.com/foaf/0.1/Person".equalsIgnoreCase(currClass.getURI())) {
                displayVisualization = true;
                break;
            }
        }	
    }
    //System.out.println("visualization is supposed to be displayed? > " + displayVisualization);
    if (displayVisualization) {

%>


        <c:set var='portalBean' value='${currentPortal}'/>
        <c:set var="themeDir"><c:out value="${portalBean.themeDir}" /></c:set>
        <c:url var="loadingImageLink" value="/${themeDir}site_icons/visualization/ajax-loader.gif"></c:url>

        <!-- START Visualization Code -->            
        <c:url var="visualizationURL" value="/visualization">
            <c:param name="render_mode" value="dynamic"/>
            <c:param name="container" value="vis_container"/>
            <c:param name="vis" value="person_pub_count"/>
            <c:param name="vis_mode" value="short"/>
            <c:param name="uri" value="${entity.URI}"/>
        </c:url>
	
        <%-- PDF Visualization URL

        For now we have disabled this.
       
        <c:url var="pdfURL" value="/visualization">
            <c:param name="render_mode" value="pdf"/>
            <c:param name="container" value="vis_container"/>
            <c:param name="vis" value="person_pub_count"/>
            <c:param name="vis_mode" value="full"/>
            <c:param name="uri" value="${entity.URI}"/>
        </c:url>

        --%>

        <style type="text/css">
            #vis_container {
                cursor:pointer;
                /*height:36px;
                margin-left:24%;
                margin-top:-2%;
                position:absolute;*/
                /*width:380px;*/
            }
        </style>
	
        <script type="text/javascript">
        <!--

        $(document).ready(function() {

            function renderVisualization(visualizationURL) {
                <%--  
                $("#vis_container").empty().html('<img src="${loadingImageLink}" />');
                --%>
                $.ajax({
                    url: visualizationURL,
                    dataType: "html",
                    success:function(data){
                     $("#vis_container").html(data);

                    }
                });
            }
            
           renderVisualization('${visualizationURL}');

        });

        //-->
        </script>
        
        <div id="vis_container">&nbsp;</div>

        <!--[if lte IE 7]>
        <style type="text/css">

        #vis_container a{
            padding-bottom:5px;
        }

        .vis_link a{
            margin-top: 15px;
            padding:10px;
            display: block;
        }
        </style>
        <![endif]-->

        <%-- 

        For now we have disabled PDF report vis.

        <div id="pdf_url">
            This is the <a href="${pdfURL}">link</a> to PDF report.
        </div>

        --%>

        <!-- END Visualization Code -->

<%

    }

%>