/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.bo.ConceptInfo;
import edu.cornell.mannlib.semservices.bo.SemanticServicesError;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService.ConceptSearchServiceUtils;

@WebServlet(name = "ConceptSearchService", urlPatterns = {"/conceptSearchService"} )
public class ConceptSearchServlet extends VitroHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ConceptSearchServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        VitroRequest vreq = new VitroRequest(req);

        try{
        	ServletContext ctx = vreq.getSession().getServletContext();
        	//Captures both concept list and any errors if they exist
        	ConceptInfo conceptInfo = new ConceptInfo();
    		conceptInfo.setSemanticServicesError(null);

        	//Json output should be written out
        	List<Concept> results =  null;
        	try {
        		results = ConceptSearchServiceUtils.getSearchResults(ctx, vreq);
        	}
        	catch (Exception ex) {
        		 SemanticServicesError semanticServicesError = new SemanticServicesError(
        	               "Exception encountered ", ex.getMessage(), "fatal");
        		 log.error("An error occurred retrieving search results", ex);
        		 conceptInfo.setSemanticServicesError(semanticServicesError);
        	}
        	conceptInfo.setConceptList(results);

        	String json = renderJson(conceptInfo);

        	json = StringUtils.replaceChars(json, "\r\t\n", "");
            PrintWriter writer = resp.getWriter();
            resp.setContentType("application/json");
            writer.write(json);
            writer.close();

        }catch(Exception ex){
            log.warn(ex,ex);
        }
    }


    protected String renderJson(ConceptInfo conceptInfo) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(conceptInfo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error("An error occurred in rendering conceptInfo as json ", e);
			return null;
		}
	}


}
