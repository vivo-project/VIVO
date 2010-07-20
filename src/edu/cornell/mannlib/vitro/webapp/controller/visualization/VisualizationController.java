/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization;

/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sparql.resultset.ResultSetFormat;

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * Services a sparql query.  This will return a simple error message and a 501 if
 * there is no jena Model.
 *
 * @author bdc34
 *
 */
public class VisualizationController extends BaseEditController {

	private static final String VIS_TYPE_URL_HANDLE = "vis";
	
	public static final String URL_ENCODING_SCHEME = "UTF-8";

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(VisualizationController.class.getName());

    protected static final Syntax SYNTAX = Syntax.syntaxARQ;

    protected static HashMap<String,ResultSetFormat> formatSymbols = new HashMap<String,ResultSetFormat>();
    static{
        formatSymbols.put( ResultSetFormat.syntaxXML.getSymbol(),     ResultSetFormat.syntaxXML);
        formatSymbols.put( ResultSetFormat.syntaxRDF_XML.getSymbol(), ResultSetFormat.syntaxRDF_XML);
        formatSymbols.put( ResultSetFormat.syntaxRDF_N3.getSymbol(),  ResultSetFormat.syntaxRDF_N3);
        formatSymbols.put( ResultSetFormat.syntaxText.getSymbol() ,   ResultSetFormat.syntaxText);
        formatSymbols.put( ResultSetFormat.syntaxJSON.getSymbol() ,   ResultSetFormat.syntaxJSON);
        formatSymbols.put( "vitro:csv", null);
    }

    protected static HashMap<String,String> rdfFormatSymbols = new HashMap<String,String>();
    static {
    	rdfFormatSymbols.put( "RDF/XML", "application/rdf+xml" );
    	rdfFormatSymbols.put( "RDF/XML-ABBREV", "application/rdf+xml" );
    	rdfFormatSymbols.put( "N3", "text/n3" );
    	rdfFormatSymbols.put( "N-TRIPLE", "text/plain" );
    	rdfFormatSymbols.put( "TTL", "application/x-turtle" );
    }

    protected static HashMap<String, String> mimeTypes = new HashMap<String,String>();
    static{
        mimeTypes.put( ResultSetFormat.syntaxXML.getSymbol() ,         "text/xml" );
        mimeTypes.put( ResultSetFormat.syntaxRDF_XML.getSymbol(),      "application/rdf+xml"  );
        mimeTypes.put( ResultSetFormat.syntaxRDF_N3.getSymbol(),       "text/plain" );
        mimeTypes.put( ResultSetFormat.syntaxText.getSymbol() ,        "text/plain");
        mimeTypes.put( ResultSetFormat.syntaxJSON.getSymbol(),         "application/javascript" );
        mimeTypes.put( "vitro:csv",                                    "text/csv");
    }

    public static final String PERSON_PUBLICATION_COUNT_VIS_URL_VALUE
    								= "person_pub_count";
    
    public static final String PDF_REPORT_VIS_URL_VALUE
									= "pdf_report";
    
    public static final String COLLEGE_PUBLICATION_COUNT_VIS_URL_VALUE
									= "college_pub_count";
    
    public static final String COAUTHORSHIP_VIS_URL_VALUE
									= "coauthorship";
    
    public static final String PERSON_LEVEL_VIS_URL_VALUE
									= "person_level";
    
    public static final String UTILITIES_URL_VALUE
									= "utilities";



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        this.doGet(request,response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
    	super.doGet(request, response);

    	VitroRequest vreq = handleLoginAuthentication(request, response);


    	if (PERSON_PUBLICATION_COUNT_VIS_URL_VALUE
    			.equalsIgnoreCase(vreq.getParameter(VIS_TYPE_URL_HANDLE))) {

    		edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.VisualizationRequestHandler visRequestHandler =
    			new edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.VisualizationRequestHandler(vreq, request, response, log);

            String rdfResultFormatParam = "RDF/XML-ABBREV";

            DataSource dataSource = setupJENADataSource(request,
            											response,
            											vreq,
            											rdfResultFormatParam);

            if (dataSource != null) {

            	/*
            	 * This is side-effecting because the visualization content is added
            	 * to the request object.
            	 * */
            	visRequestHandler.generateVisualization(dataSource);

            } else {
            	
            	log.error("ERROR! Data Model Empty");
            }

    	} else if (COLLEGE_PUBLICATION_COUNT_VIS_URL_VALUE
    			.equalsIgnoreCase(vreq.getParameter(VIS_TYPE_URL_HANDLE))) {
    		
    		edu.cornell.mannlib.vitro.webapp.visualization.collegepubcount.VisualizationRequestHandler visRequestHandler =
    			new edu.cornell.mannlib.vitro.webapp.visualization.collegepubcount.VisualizationRequestHandler(vreq, request, response, log);

            String rdfResultFormatParam = "RDF/XML-ABBREV";

            DataSource dataSource = setupJENADataSource(request,
            											response,
            											vreq,
            											rdfResultFormatParam);
            
            if (dataSource != null) {

            	/*
            	 * This is side-effecting because the visualization content is added
            	 * to the request object.
            	 * */
            	visRequestHandler.generateVisualization(dataSource);

            } else {
            	log.error("ERROR! data model empoty");
            }
 
    	} else if (COAUTHORSHIP_VIS_URL_VALUE
    			.equalsIgnoreCase(vreq.getParameter(VIS_TYPE_URL_HANDLE))) {
    		
    		edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisualizationRequestHandler visRequestHandler =
    			new edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.VisualizationRequestHandler(vreq, request, response, log);

            String rdfResultFormatParam = "RDF/XML-ABBREV";

            DataSource dataSource = setupJENADataSource(request,
            											response,
            											vreq,
            											rdfResultFormatParam);

            if (dataSource != null) {

            	/*
            	 * This is side-effecting because the visualization content is added
            	 * to the request object.
            	 * */
            	visRequestHandler.generateVisualization(dataSource);

            } else {
            	log.error("ERROR! data model empoty");
            }
 
    	} else if (PERSON_LEVEL_VIS_URL_VALUE
    			.equalsIgnoreCase(vreq.getParameter(VIS_TYPE_URL_HANDLE))) {
    		
    		edu.cornell.mannlib.vitro.webapp.visualization.personlevel.VisualizationRequestHandler visRequestHandler =
    			new edu.cornell.mannlib.vitro.webapp.visualization.personlevel.VisualizationRequestHandler(vreq, request, response, log);

            String rdfResultFormatParam = "RDF/XML-ABBREV";

            DataSource dataSource = setupJENADataSource(request,
            											response,
            											vreq,
            											rdfResultFormatParam);

            if (dataSource != null) {

            	/*
            	 * This is side-effecting because the visualization content is added
            	 * to the request object.
            	 * */
            	visRequestHandler.generateVisualization(dataSource);

            } else {
            	log.error("ERROR! data model empoty");
            }
 
    	} else if (PDF_REPORT_VIS_URL_VALUE
    			.equalsIgnoreCase(vreq.getParameter(VIS_TYPE_URL_HANDLE))) {
 
    	} else if (UTILITIES_URL_VALUE
    			.equalsIgnoreCase(vreq.getParameter(VIS_TYPE_URL_HANDLE))) {
    		
    		edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationRequestHandler visRequestHandler =
    			new edu.cornell.mannlib.vitro.webapp.visualization.utilities.VisualizationRequestHandler(vreq, request, response, log);

            String rdfResultFormatParam = "RDF/XML-ABBREV";

            DataSource dataSource = setupJENADataSource(request,
            											response,
            											vreq,
            											rdfResultFormatParam);

            if (dataSource != null) {

            	/*
            	 * This is side-effecting because the visualization content is added
            	 * to the request object.
            	 * */
            	visRequestHandler.generateVisualization(dataSource);

            } else {
            	log.error("ERROR! data model empoty");
            }
 
    	} else {
    		
    		log.debug("vis uqery parameter value -> " + vreq.getParameter("vis"));
    		log.debug("uri uqery parameter value -> " + vreq.getParameter("uri"));
    		log.debug("render_mode query parameter value -> " + vreq.getParameter("render_mode"));

    		/*
    		 * This is side-effecting because the error content is directly
    		 * added to the request object. From where it is redirected to
    		 * the error page.
    		 * */
    		handleMalformedParameters("Inappropriate query parameters were submitted. ", request, response);
    	}

        return;
    }

	private VitroRequest handleLoginAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		//        This might not be required
		        /*
		         * why are there multiple places where the login is checked? shud be abtracted into
		         * new methoid?
		         * */
//		        if( !checkLoginStatus(request, response) )
//		        	return null;

		        VitroRequest vreq = new VitroRequest(request);

		        Object obj = vreq.getSession().getAttribute("loginHandler");
		        LoginFormBean loginHandler = null;


		        if( obj != null && obj instanceof LoginFormBean )
		            loginHandler = ((LoginFormBean)obj);

		        /*
		         * what is the speciality of 5 in the conditions?
		         *
		        if( loginHandler == null ||
		            ! "authenticated".equalsIgnoreCase(loginHandler.getLoginStatus()) ||
		             Integer.parseInt(loginHandler.getLoginRole()) <= 5 ){
		            HttpSession session = request.getSession(true);

		            session.setAttribute("postLoginRequest",
		                    vreq.getRequestURI()+( vreq.getQueryString()!=null?('?' + vreq.getQueryString()):"" ));
		            String redirectURL = request.getContextPath() + Controllers.SITE_ADMIN + "?login=block";
		            response.sendRedirect(redirectURL);
		            return null;
		        }
		        */
		return vreq;
	}

	private DataSource setupJENADataSource(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq,
			String rdfResultFormatParam) {

		Model model = vreq.getJenaOntModel(); // getModel()
        if( model == null ){
            doNoModelInContext(request,response);
            return null;
        }

        log.debug("rdfResultFormat was: " + rdfResultFormatParam);

        DataSource dataSource = DatasetFactory.create() ;
        ModelMaker maker = (ModelMaker) getServletContext().getAttribute("vitroJenaModelMaker");

        	dataSource.setDefaultModel(model) ;

        return dataSource;
	}

    private void doNoModelInContext(HttpServletRequest request, HttpServletResponse res){
        try {
            res.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            ServletOutputStream sos = res.getOutputStream();
            sos.println("<html><body>this service is not supporeted by the current " +
                    "webapp configuration. A jena model is required in the servlet context.</body></html>" );
        } catch (IOException e) {
            log.error("Could not write to ServletOutputStream");
        }
    }


    private void handleMalformedParameters(String errorMessage, HttpServletRequest request,
    					HttpServletResponse response)
    	throws ServletException, IOException {

        VitroRequest vreq = new VitroRequest(request);
        Portal portal = vreq.getPortal();

        request.setAttribute("error", errorMessage);

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp", "/templates/visualization/visualization_error.jsp");
        request.setAttribute("portalBean", portal);
        request.setAttribute("title", "Visualization Query Error");

        try {
            requestDispatcher.forward(request, response);
        } catch (Exception e) {
            log.error("EntityEditController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }
    }

}

