/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester; 

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.TemplateProcessingHelper.TemplateProcessingException;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.JenaModelUtils;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;
import edu.cornell.mannlib.vitro.webapp.filestorage.uploadrequest.FileUploadServletRequest;
import freemarker.template.Configuration;

public class TestFileController extends FreemarkerHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(TestFileController.class);
    private static final String TEMPLATE_DEFAULT = "testfile.ftl";

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
            Map<String, Object> body = new HashMap<String, Object>();
            //body.put("uploadPostback", "false");
            body.put("processRequestTest", "hi.");
            return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
        } catch (Throwable e) {
            log.error(e, e);
            return new ExceptionResponseValues(e);
        }
    }

    @Override
    protected String getTitle(String siteName, VitroRequest vreq) {
        return "VIVO Harvester Test";
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if(1 == 1)
        {
            //Map<String, Object> body = new HashMap<String, Object>();
            //body.put("uploadPostback", "true");
            //body.put("testvalue", "hello!");
            //ResponseValues responseValues = new TemplateResponseValues(TEMPLATE_DEFAULT, body);
            //ResponseValues responseValues = new Re

            //response.getWriter().write("hello!");

            //try {doResponse(new VitroRequest(request), response, responseValues);} catch(Exception e) {log.error(e, e);}
            //return;
        }

        int maxFileSize = 1024 * 1024;

        FileUploadServletRequest req = FileUploadServletRequest.parseRequest(request, maxFileSize);
        if(req.hasFileUploadException()) {
            //todo: complete
        }

        Map<String, List<FileItem>> fileStreams = req.getFiles();

        VitroRequest vreq = new VitroRequest(req);       
        //if (!checkLoginStatus(request,response) ){
            //todo: complete
        //}

        //LoginStatusBean loginBean = LoginStatusBean.getBean(vreq);

        //try {
        //    super.doGet(vreq,response);
        //} catch (Exception e) {
            //todo: complete
        //}

        JSONObject json = new JSONObject();

        String path = "/home/mbarbieri/tempfileupload/"; //todo: complete
        if(fileStreams.get("csvFile") != null && fileStreams.get("csvFile").size() > 0) {
            FileItem csvStream = fileStreams.get("csvFile").get(0);
            String name = csvStream.getName();
            File file = new File(path + name);
            //FileWriter writer = new FileWriter(file);
            try {
                /*
                InputStream inputStream = csvStream.getInputStream();

                for(int nextByte = inputStream.read(); nextByte != -1; nextByte = inputStream.read())
                {
                    writer.write(nextByte);
                }
                */
                csvStream.write(file);
            } catch (Exception e) {                               
                //todo: handle
            } finally {           
                csvStream.delete();
            }

            String errorMessage = validateCsvFile(file);
            boolean success;
            if(errorMessage != null) {
                success = false;
                //todo: handle
            } else {
                success = true;
                errorMessage = "success";
            }


            try {
                //todo: complete
                json.put("success", success);
                json.put("fileName", name);
                json.put("errorMessage", errorMessage);
            }
            catch(JSONException e) {
                //todo: handle
            }

        } else {
            try {
                json.put("success", false);
                json.put("errorMessage", "No file uploaded");
            } catch(JSONException e) {
                //todo: handle
            }

        }

        response.getWriter().write(json.toString());

        /*
         * Ajax request: filename, data
         * Process: validate
         * Ajax response: success or error message
         * 
         */
    }

    private String validateCsvFile(File file)
    {
        return null;
    }

}
