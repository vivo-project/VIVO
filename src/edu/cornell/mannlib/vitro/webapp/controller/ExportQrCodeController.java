/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller; 

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalysisContextImpl;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalyzer;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestInfo;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.DefaultObjectWrapper;

public class ExportQrCodeController extends FreemarkerHttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ExportQrCodeController.class);
    private static final String TEMPLATE_DEFAULT = "foaf-person--exportQrCode.ftl";

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
        	Individual individual = getIndividualFromRequest(vreq);
            
            DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
            wrapper.setExposureLevel(BeansWrapper.EXPOSE_SAFE);
            
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("individual", wrapper.wrap(new IndividualTemplateModel(individual, vreq)));
            
            return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
        } catch (Throwable e) {
            log.error(e, e);
            return new ExceptionResponseValues(e);
        }
    }

	private Individual getIndividualFromRequest(VitroRequest vreq) {
		IndividualRequestInfo requestInfo = new IndividualRequestAnalyzer(vreq,
				new IndividualRequestAnalysisContextImpl(vreq)).analyze();
		return requestInfo.getIndividual();
	}

	@Override
    protected String getTitle(String siteName, VitroRequest vreq) {
        try {
            return "Export QR Code for " + getIndividualFromRequest(vreq).getRdfsLabel();
        } catch (Throwable e) {
            log.error(e, e);
            return "There was an error in the system. The individual could not be found";
        }
    }

}
