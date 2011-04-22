/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester; 

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.filestorage.uploadrequest.FileUploadServletRequest;

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

        int maxFileSize = 1024 * 1024;

        FileUploadServletRequest req = FileUploadServletRequest.parseRequest(request, maxFileSize);
        if(req.hasFileUploadException()) {
            //todo: complete
        }

        Map<String, List<FileItem>> fileStreams = req.getFiles();

        JSONObject json = new JSONObject();

        String path = "/home/mbarbieri/tempfileupload/"; //todo: complete
        if(fileStreams.get("csvFile") != null && fileStreams.get("csvFile").size() > 0) {
            FileItem csvStream = fileStreams.get("csvFile").get(0);
            String name = csvStream.getName();
            File file = new File(path + name);
            try {
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
    }

    private String validateCsvFile(File file)
    {
        return null;
    }

}
