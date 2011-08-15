/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualController;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.DefaultObjectWrapper;

public class UMLSTerminologyAnnotation extends VitroHttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(UMLSTerminologyAnnotation.class);
    private static final String submissionUrl = "http://link.informatics.stonybrook.edu/MeaningLookup/MlServiceServlet?";
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        VitroRequest vreq = new VitroRequest(req);

        try{
          //Get parameter
        	String entryText = vreq.getParameter("searchTerm");
        	System.out.println("Entry text is " + entryText);
        	//TODO: Find a non-deprecated mechanism to do so
        	String dataUrl = submissionUrl + "textToProcess=" + URLEncoder.encode(entryText) + "&format=json";
        	try{
	            ServletOutputStream sos = resp.getOutputStream();

	    		resp.setCharacterEncoding("UTF-8");
	            resp.setContentType("application/json;charset=UTF-8");
	    		URL rss = new URL(dataUrl);
	    		
	    		BufferedReader in = new BufferedReader(new InputStreamReader(rss.openStream()));
	    		String inputLine;
	    		while((inputLine = in.readLine()) != null) {
	    			sos.println(inputLine);
	    		}
	    		in.close();

	    	} catch(Exception ex) {
	    		log.error("error occurred in servlet", ex);
	    	}
        	
        }catch(Exception ex){
            log.warn(ex,ex);            
        }        
    }

}
