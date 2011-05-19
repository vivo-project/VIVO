/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
    private static final String PARAMETER_IS_HARVEST_CLICK = "isHarvestClick";

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
            Map<String, Object> body = new HashMap<String, Object>();
            //body.put("uploadPostback", "false");
            body.put("paramFirstUpload", PARAMETER_FIRST_UPLOAD);
            body.put("paramUploadedFile", PARAMETER_UPLOADED_FILE);
            body.put("paramIsHarvestClick", PARAMETER_IS_HARVEST_CLICK);
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
        //String harvesterPath = "/usr/share/vivo/harvester/"; //todo: hack
        String harvesterPath = "/home/mbarbieri/workspace/HarvesterDevTomcat2/";
        return harvesterPath;
    }

    /**
     * Returns the base directory used for all File Harvest uploads.
     * @param context the current servlet context
     * @return the base directory for file harvest uploads
     * @throws Exception if the Vitro home directory could not be found
     */
    private static String getUploadPathBase(ServletContext context) throws Exception
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
     * @param vreq the request from the browser
     * @return the FileHarvestJob that will provide harvest-type-specific services for this request
     */
    private FileHarvestJob getJob(VitroRequest vreq)
    {
        String namespace = vreq.getWebappDaoFactory().getDefaultNamespace();

        //todo: complete
        return new CsvHarvestJob(vreq, "granttemplate.csv", namespace);
    }

    /**
     * Gets the location where we want to save uploaded files.  This location is in the VIVO uploads directory under
     * "harvester", and then in a directory named by the user's session ID as retrieved from the request.  The path
     * returned by this method will end in a slash (/).
     * @param vreq the request from which to get the session ID
     * @return the path to the location where uploaded files will be saved.  This path will end in a slash (/)
     */
    public static String getUploadPath(VitroRequest vreq) {
        try {
            String path = getUploadPathBase(vreq.getSession().getServletContext()) + getSessionId(vreq) + "/";
            return path;
        } catch(Exception e) {
            log.error(e, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        log.error("this is a post.");
        
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if(isMultipart)
                doFileUploadPost(request, response);
            else if(request.getParameter(PARAMETER_IS_HARVEST_CLICK).toLowerCase().equals("true"))
                doHarvestPost(request, response);
            else
                doCheckHarvestStatusPost(request, response);
        } catch(Exception e) {
            log.error(e, e);
        }
    }

    /**
     * This is for when the user clicks the "Upload" button on the form, sending a file to the server.  An HTTP post is
     * redirected here when it is determined that the request was multipart (as this will identify the post as a file
     * upload click).
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if an IO error occurs
     * @throws ServletException if a servlet error occurs
     */
    private void doFileUploadPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        log.error("file upload post.");
        JSONObject json = new JSONObject();
        try {
            VitroRequest vreq = new VitroRequest(request);

            //parse request for uploaded file
            int maxFileSize = 1024 * 1024;
            FileUploadServletRequest req = FileUploadServletRequest.parseRequest(vreq, maxFileSize);
            if(req.hasFileUploadException()) {
                Exception e = req.getFileUploadException();
                new ExceptionVisibleToUser(e);
            }
            
            //get the location where we want to save the files (it will end in a slash), then create a File object out of it 
            String path = getUploadPath(vreq);
            File directory = new File(path);

            //if this is a page refresh, we do not want to save stale files that the user doesn't want anymore, but we
            //  still have the same session ID and therefore the upload directory is unchanged.  Thus we must clear the
            //  upload directory if it exists (a "first upload" parameter, initialized to "true" but which gets set to
            //  "false" once the user starts uploading stuff is used for this).
            String firstUpload = req.getParameter(PARAMETER_FIRST_UPLOAD); //clear directory on first upload
            log.error(firstUpload);
            if(firstUpload.toLowerCase().equals("true")) {
                if(directory.exists()) {
                    File[] children = directory.listFiles();
                    for(File child : children) {
                        child.delete();
                    }
                }
            }

            //if the upload directory does not exist then create it
            if(!directory.exists())
                directory.mkdirs();

            //get the file harvest job for this request (this will determine what type of harvest is run)
            FileHarvestJob job = getJob(vreq);

            //get the files out of the parsed request (there should only be one)
            Map<String, List<FileItem>> fileStreams = req.getFiles();
            if(fileStreams.get(PARAMETER_UPLOADED_FILE) != null && fileStreams.get(PARAMETER_UPLOADED_FILE).size() > 0) {
                
                //get the individual file data from the request
                FileItem csvStream = fileStreams.get(PARAMETER_UPLOADED_FILE).get(0);
                String name = csvStream.getName();
                
                //if another uploaded file exists with the same name, alter the name so that it is unique
                name = handleNameCollision(name, directory);
                
                //write the file from the request to the upload directory
                File file = new File(path + name);
                try {
                    csvStream.write(file);
                } finally {
                    csvStream.delete();
                }

                //ask the file harvest job to validate that it's okay with what was uploaded; if not delete the file
                String errorMessage = job.validateUpload(file);
                boolean success;
                if(errorMessage != null) {
                    success = false;
                    file.delete();
                } else {
                    success = true;
                    errorMessage = "success";
                }

                //prepare the results which will be sent back to the browser for display
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
                
                //if for some reason no file was included with the request, send an error back
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
            
            //handle exceptions whose message is for the user
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

        //write the prepared response
        response.getWriter().write(json.toString());
    }

    /**
     * This is for when the user clicks the "Harvest" button on the form, sending a file to the server.  An HTTP post is
     * redirected here when an isHarvestClick parameter is contained in the post data and set to "true".
     * @param request the HTTP request
     * @param response the HTTP response
     */
    private void doHarvestPost(HttpServletRequest request, HttpServletResponse response) {

        log.error("harvest post.");
        try {
            VitroRequest vreq = new VitroRequest(request);
            FileHarvestJob job = getJob(vreq);
    
            //String path = getUploadPath(vreq);

            String script = job.getScript();
            log.error("start harvest");
            runScript(getSessionId(request), script);
            log.error("end harvest");

            JSONObject json = new JSONObject();
            json.put("progressSinceLastCheck", "");
            json.put("finished", false);

            response.getWriter().write(json.toString());

        } catch(Exception e) {
            log.error(e, e);
        }
    }
    
    /**
     * This is for posts automatically sent by the client during the harvest, to check on the status of the harvest and
     * return updated log data and whether the harvest is complete or still running.  An HTTP post is redirected here
     * when an isHarvestClick parameter is contained in the post data and set to "false".
     * @param request the HTTP request
     * @param response the HTTP response
     */
    private void doCheckHarvestStatusPost(HttpServletRequest request, HttpServletResponse response) {

        log.error("check harvest status post.");
        
        try {
            String newline = "\n";
            
            String sessionId = getSessionId(request);
            
            ArrayList<String> unsentLogLinesList = sessionIdToUnsentLogLines.get(sessionId);
            String[] unsentLogLines;
            if(unsentLogLinesList != null) {
                synchronized (unsentLogLinesList) {
                    unsentLogLines = unsentLogLinesList.toArray(new String[unsentLogLinesList.size()]);
                    unsentLogLinesList.clear();
                }
                
                String progressSinceLastCheck = "";
                for(int i = 0; i < unsentLogLines.length; i++) {
                    progressSinceLastCheck += unsentLogLines[i] + newline;
                }
                
                boolean finished = !sessionIdToHarvestThread.containsKey(sessionId);
                
                JSONObject json = new JSONObject();
                json.put("progressSinceLastCheck", progressSinceLastCheck);
                json.put("finished", finished);

                response.getWriter().write(json.toString());
            }
        } catch(Exception e) {
            log.error(e, e);
        }
    }
    
    

    private File createScriptFile(String script) throws IOException {
        File scriptDirectory = new File(getHarvesterPath() + "scripts/temp");
        if(!scriptDirectory.exists()) {
            scriptDirectory.mkdirs();
        }

        File tempFile = File.createTempFile("harv", ".sh", scriptDirectory);

        FileWriter writer = new FileWriter(tempFile);
        writer.write(script);
        writer.close();

        return tempFile;
    }


    private void runScript(String sessionId, String script) {
        
        if(!sessionIdToHarvestThread.containsKey(sessionId)) {
            
            ScriptRunner runner = new ScriptRunner(sessionId, script);
            sessionIdToHarvestThread.put(sessionId, runner);
            runner.start();
        }
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
        String base = filename;
        String extension = "";
        if(filename.contains(".")) {
            base = filename.substring(0, filename.lastIndexOf("."));
            extension = filename.substring(filename.indexOf("."));
        }

        String renamed = filename;

        for(int i = 1; new File(directory, renamed).exists(); i++) {
            renamed = base + " (" + String.valueOf(i) + ")" + extension;
        }

        return renamed;
    }


    /**
     * Returns the ID of the current session between server and browser.
     * @param request the request coming in from the browser
     * @return the session ID
     */
    private static String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
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
    
    
    private Map<String, ScriptRunner> sessionIdToHarvestThread = new Hashtable<String, ScriptRunner>(); //Hashtable is threadsafe, HashMap is not
    private Map<String, ArrayList<String>> sessionIdToUnsentLogLines = new Hashtable<String, ArrayList<String>>(); //Hashtable is threadsafe, HashMap is not
    private class ScriptRunner extends Thread {

        private final String sessionId;
        private final String script;

        public ScriptRunner(String sessionId, String script) {
            this.sessionId = sessionId;
            this.script = script;
        }

        @Override
        public void run() {
            try {
                ArrayList<String> unsentLogLines = sessionIdToUnsentLogLines.get(sessionId);
                if(unsentLogLines == null) {
                    unsentLogLines = new ArrayList<String>();
                    sessionIdToUnsentLogLines.put(sessionId, unsentLogLines);
                }
                
                File scriptFile = createScriptFile(script);

                String command = "/bin/bash " + getHarvesterPath() + "scripts/temp/" + scriptFile.getName();

                log.info("Running command: " + command);
                Process pr = Runtime.getRuntime().exec(command);
                
                //try { Thread.sleep(15000); } catch(InterruptedException e) {log.error(e, e);}

                BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                for(String line = processOutputReader.readLine(); line != null; line = processOutputReader.readLine()) {
                    synchronized(unsentLogLines) {
                        unsentLogLines.add(line);
                    }
                    log.info("Harvester output: " + line);
                }

                BufferedReader processErrorReader = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                for(String line = processErrorReader.readLine(); line != null; line = processErrorReader.readLine()) {
                    log.info("Harvester error: " + line);
                }

                int exitVal;
        
                try {
                    exitVal = pr.waitFor();
                }
                catch(InterruptedException e) {
                    throw new IOException(e.getMessage(), e);
                }
                log.debug("Harvester script exited with error code " + exitVal);
                log.info("Harvester script execution complete");
            } catch (IOException e) {
                log.error(e, e);
            } finally {
                if(sessionIdToHarvestThread.containsKey(sessionId)) {
                    sessionIdToHarvestThread.remove(sessionId);
                }
            }
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
     * The HTTP request.
     */
    private VitroRequest vreq;

    /**
     * The template file against which uploaded CSV files will be validated.
     */
    private File templateFile;

    /**
     * The namespace to be used for the harvest.
     */
    private final String namespace;

    /**
     * Constructor.
     * @param templateFileName just the name of the template file.  The directory is assumed to be standard.
     */
    public CsvHarvestJob(VitroRequest vreq, String templateFileName, String namespace) {
        this.vreq = vreq;
        this.templateFile = new File(getTemplateFileDirectory() + templateFileName);
        this.namespace = namespace;
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
                    if(line.length != templateFirstLine.length) {
                        String retval = "Mismatch in number of entries in row " + i + ": expected , " + templateFirstLine.length + ", found " + line.length + "  ";
                        for(int j = 0; j < line.length; j++) {
                            retval += "\"" + line[j] + "\", ";
                        }
                        //return retval;
                        return "Mismatch in number of entries in row " + i + ": expected , " + templateFirstLine.length + ", found " + line.length;
                    }
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

    @Override
    public String getScript()
    {
        String path = TestFileController.getHarvesterPath() + "scripts/" + "testCSVtoRDFgrant.sh"; //todo: complete
        File scriptTemplate = new File(path);

        String scriptTemplateContents = readScriptTemplate(scriptTemplate);
        String replacements = performScriptTemplateReplacements(scriptTemplateContents);
        return replacements;
    }


    private String performScriptTemplateReplacements(String scriptTemplateContents) {
        String replacements = scriptTemplateContents;

        String fileDirectory = TestFileController.getUploadPath(vreq);

        replacements = replacements.replace("${UPLOADS_FOLDER}", fileDirectory);

        /*
         * What needs to be replaced?
         *
         * task directory name
         */
        //todo: complete
        return replacements;
    }


    private String readScriptTemplate(File scriptTemplate) {
        String scriptTemplateContents = null;
        BufferedReader reader = null;
        try {
            int fileSize = (int)(scriptTemplate.length());
            char[] buffer = new char[fileSize];
            reader = new BufferedReader(new FileReader(scriptTemplate), fileSize);
            reader.read(buffer);
            scriptTemplateContents = new String(buffer);
        } catch (IOException e) {
            log.error(e, e);
        } finally {
            try {
                if(reader != null)
                    reader.close();
            } catch(IOException e) {
                log.error(e, e);
            }
        }

        return scriptTemplateContents;
    }


    @Override
    public void performHarvest(File directory) {
        
    }
    
    

}


