/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester; 

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.skife.csv.SimpleReader;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.filestorage.backend.FileStorageSetup;
import edu.cornell.mannlib.vitro.webapp.filestorage.uploadrequest.FileUploadServletRequest;

public class TestFileController extends FreemarkerHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(TestFileController.class);
    private static final String TEMPLATE_DEFAULT = "testfile.ftl";

    private static final String PARAMETER_FIRST_UPLOAD = "firstUpload";
    private static final String PARAMETER_UPLOADED_FILE = "uploadedFile";

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
            Map<String, Object> body = new HashMap<String, Object>();
            //body.put("uploadPostback", "false");
            body.put("paramFirstUpload", PARAMETER_FIRST_UPLOAD);
            body.put("paramUploadedFile", PARAMETER_UPLOADED_FILE);
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

    /**
     * Returns the root location of the VIVO Harvester on this machine.
     * @return the root location of the VIVO Harvester on this machine
     */
    public static String getHarvesterPath()
    {
        String harvesterPath = "/usr/share/vivo/harvester/"; //todo: hack
        return harvesterPath;
    }

    /**
     * Returns the base directory used for all File Harvest uploads.
     * @param context the current servlet context
     * @return the base directory for file harvest uploads
     * @throws Exception if the Vitro home directory could not be found
     */
    private String getUploadPathBase(ServletContext context) throws Exception
    {
        String vitroHomeDirectoryName = ConfigurationProperties.getBean(context).getProperty(FileStorageSetup.PROPERTY_VITRO_HOME_DIR);
        if (vitroHomeDirectoryName == null) {
            throw new Exception("Vitro home directory name could not be found.");
        }

        String pathBase = vitroHomeDirectoryName + "/" + FileStorageSetup.FILE_STORAGE_SUBDIRECTORY + "/harvester/";
        return pathBase;
    }

    /**
     * Gets the FileHarvestJob implementation that is needed to handle the specified request.  This
     * will depend on the type of harvest being performed (CSV, RefWorks, etc.)
     * @param request the request from the browser
     * @return the FileHarvestJob that will provide harvest-type-specific services for this request
     */
    private FileHarvestJob getJob(HttpServletRequest request)
    {
        //todo: complete
        return new CsvHarvestJob("persontemplate.csv");
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        JSONObject json = new JSONObject();
        try {

            String path = getUploadPathBase(request.getSession().getServletContext()) + getSessionId(request) + "/";
            File directory = new File(path);


            int maxFileSize = 1024 * 1024;
            FileUploadServletRequest req = FileUploadServletRequest.parseRequest(request, maxFileSize);
            if(req.hasFileUploadException()) {
                Exception e = req.getFileUploadException();
                new ExceptionVisibleToUser(e);
            }

            String firstUpload = req.getParameter(PARAMETER_FIRST_UPLOAD); //clear directory on first upload
            if(firstUpload.toLowerCase().equals("true")) {
                if(directory.exists()) {
                    File[] children = directory.listFiles();
                    for(File child : children) {
                        child.delete();
                    }
                }
            }

            if(!directory.exists())
                directory.mkdirs();

            FileHarvestJob job = getJob(req);

            Map<String, List<FileItem>> fileStreams = req.getFiles();
            if(fileStreams.get(PARAMETER_UPLOADED_FILE) != null && fileStreams.get(PARAMETER_UPLOADED_FILE).size() > 0) {
                FileItem csvStream = fileStreams.get(PARAMETER_UPLOADED_FILE).get(0);
                String name = csvStream.getName();
                name = handleNameCollision(name, directory);
                File file = new File(path + name);
                try {
                    csvStream.write(file);
                } finally {           
                    csvStream.delete();
                }

                String errorMessage = job.validateUpload(file);
                boolean success;
                if(errorMessage != null) {
                    success = false;
                    file.delete();
                } else {
                    success = true;
                    errorMessage = "success";
                }


                try {
                    json.put("success", success);
                    json.put("fileName", name);
                    json.put("errorMessage", errorMessage);
                }
                catch(JSONException e) {
                    log.error(e, e);
                    return;
                }

            } else {
                try {
                    json.put("success", false);
                    json.put("fileName", "(none)");
                    json.put("errorMessage", "No file uploaded");
                } catch(JSONException e) {
                    log.error(e, e);
                    return;
                }

            }
        } catch(ExceptionVisibleToUser e) {
            log.error(e, e);
            try {
                json.put("success", false);
                json.put("filename", "(none)");
                json.put("errorMessage", e.getMessage());
            } catch(JSONException f) {
                log.error(f, f);
                return;
            }
        } catch(Exception e) {
            log.error(e, e);
            return;
        }

        response.getWriter().write(json.toString());
    }


    /**
     * Handles a name conflict in a directory by providing a new name that does not conflict with the
     * name of a file already uploaded.
     * @param filename the name of the file to be added to the directory
     * @param directory the directory where the file should be added, in which to check for files of the
     *                  same name
     * @return a filename that does not conflict with any files in the directory.  If the filename parameter
     *         works, then that is returned.  Otherwise a number is appended in parentheses to the part of
     *         the file name prior to the final "." symbol (if one exists).
     */
    private String handleNameCollision(String filename, File directory) {
        String path = directory.getPath();
        String base = filename;
        String extension = "";
        if(filename.contains(".")) {
            base = filename.substring(0, filename.lastIndexOf("."));
            extension = filename.substring(filename.indexOf("."));
        }

        String renamed = filename;

        for(int i = 1; new File(path + renamed).exists(); i++) {
            renamed = base + " (" + String.valueOf(i) + ")" + extension;
        }

        return renamed;
    }


    /**
     * Returns the ID of the current session between server and browser.
     * @param request the request coming in from the browser
     * @return the session ID
     */
    private String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }






    @SuppressWarnings("unused")
    private void doHarvest()
    {
        /*
        Harvest will entail:
        
        D2RMapFetch
        Transfer to local temp model
        Diffs
        Transfers
        
        If this is being done with a script, then we should probably use a templating system.
        run-csv.sh 
        
         */
    }










    /**
     * Provides a way of throwing an exception whose message it is OK to display unedited to the user. 
     */
    private class ExceptionVisibleToUser extends Exception {
        private static final long serialVersionUID = 1L;
        public ExceptionVisibleToUser(Throwable cause) {
            super(cause);
        }
    }
}


/**
 * An implementation of FileHarvestJob that can be used for any CSV file harvest. 
 */
class CsvHarvestJob implements FileHarvestJob {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(CsvHarvestJob.class);
    
    /**
     * The template file against which uploaded CSV files will be validated.
     */
    private File templateFile;
    
    /**
     * Constructor.
     * @param templateFileName just the name of the template file.  The directory is assumed to be standard.
     */
    public CsvHarvestJob(String templateFileName) {
        templateFile = new File(getTemplateFileDirectory() + templateFileName);
    }

    /**
     * Gets the path to the directory containing the template files.
     * @return the path to the directory containing the template files
     */
    private String getTemplateFileDirectory() {
        String harvesterPath = TestFileController.getHarvesterPath();
        String pathToTemplateFiles = harvesterPath + "files/";
        return pathToTemplateFiles;
    }


    @Override
    @SuppressWarnings("rawtypes")
    public String validateUpload(File file) {
        try {
            SimpleReader reader = new SimpleReader();
            
            List templateCsv = reader.parse(this.templateFile);
            String[] templateFirstLine = (String[])templateCsv.get(0);

            List csv = reader.parse(file);
            
            int length = csv.size();

            if(length == 0)
                return "No data in file";
            
            for(int i = 0; i < length; i++) {
                String[] line = (String[])csv.get(i);
                if(i == 0) {
                    String errorMessage = validateCsvFirstLine(templateFirstLine, line);
                    if(errorMessage != null)
                        return errorMessage;
                }
                else if(line.length != 0) {
                    if(line.length != templateFirstLine.length)
                        return "Mismatch in number of entries in row " + i;
                }
            }

        } catch (IOException e) {
            log.error(e, e);
            return e.getMessage();
        }
        return null;
    }

    /**
     * Makes sure that the first line of the CSV file is identical to the first line of the template file.  This is
     * assuming we are expecting all user CSV files to contain an initial header line.  If this is not the case, then
     * this method is unnecessary.
     * @param templateFirstLine the parsed-out contents of the first line of the template file
     * @param line the parsed-out contents of the first line of the input file
     * @return an error message if the two lines don't match, or null if they do
     */
    private String validateCsvFirstLine(String[] templateFirstLine, String[] line) {
        String errorMessage = "File header does not match specification";
        if(line.length != templateFirstLine.length)
            return errorMessage;
        for(int i = 0; i < line.length; i++)
        {
            if(!line[i].equals(templateFirstLine[i]))
                    return errorMessage;
        }
        return null;
    }
}


