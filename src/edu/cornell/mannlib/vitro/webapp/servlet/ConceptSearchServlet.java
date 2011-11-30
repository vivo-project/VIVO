/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet; 

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService.BeanToJsonSerializer;
import edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService.ConceptSearchServiceUtils;

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
        	//Json output should be written out
        	List<Concept> results = ConceptSearchServiceUtils.getSearchResults(ctx, vreq);
        	String json = renderJson(results);
        	json = StringUtils.replaceChars(json, "\r\t\n", "");
            PrintWriter writer = resp.getWriter();
            resp.setContentType("application/json");
            writer.write(json);
            writer.close();
        	
        }catch(Exception ex){
            log.warn(ex,ex);            
        }        
    }
    
    protected String renderJson(List<Concept> conceptList) {

        JSONObject jsonObject = null;
        jsonObject = BeanToJsonSerializer.serializeToJsonObject(conceptList);
        log.debug(jsonObject.toString());
        return jsonObject.toString();
    }

}
