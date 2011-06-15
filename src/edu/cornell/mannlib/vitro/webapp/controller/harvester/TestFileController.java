/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.skife.csv.SimpleReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    private static final String PARAMETER_JOB = "job";

    private static final String POST_TO = "/vivo/harvester/harvest";
    
    private static final String JOB_CSV_GRANT = "csvGrant";
    private static final String JOB_CSV_PERSON = "csvPerson";
    
    private static final List<String> knownJobs = Arrays.asList(JOB_CSV_GRANT.toLowerCase(), JOB_CSV_PERSON.toLowerCase());
    
    
    /**
     * Relative path from the VIVO Uploads directory to the root location where user-uploaded files will be stored.  Include
     * final slash.
     */
    private static final String PATH_TO_UPLOADS = "harvester/";

    /**
     * Absolute path on the server of the Harvester root directory.  Include final slash.
     */
    private static final String PATH_TO_HARVESTER = "/home/mbarbieri/workspace/HarvesterDev/";

    /**
     * Relative path from the Harvester root directory to the Additions file containing rdf/xml added to VIVO from Harvest run.
     */
    public static final String PATH_TO_ADDITIONS_FILE = "harvested-data/csv/additions.rdf.xml"; //todo: this is job-specific
    
    /**
     * Relative path from the Harvester root directory to the directory where user-downloadable template files are stored.
     */
    public static final String PATH_TO_TEMPLATE_FILES = "files/";
    
    /**
     * Relative path from the Harvester root directory to the directory containing the script templates.  Include final slash.
     */
    public static final String PATH_TO_HARVESTER_SCRIPTS = "scripts/";
    
    
    
    
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
            String job = vreq.getParameter(PARAMETER_JOB);
            String jobKnown = "false";
            if((job != null) && TestFileController.knownJobs.contains(job.toLowerCase()))
                jobKnown = "true";
            
            Map<String, Object> body = new HashMap<String, Object>();
            //body.put("uploadPostback", "false");
            body.put("paramFirstUpload", PARAMETER_FIRST_UPLOAD);
            body.put("paramUploadedFile", PARAMETER_UPLOADED_FILE);
            body.put("paramIsHarvestClick", PARAMETER_IS_HARVEST_CLICK);
            body.put("paramJob", PARAMETER_JOB);
            body.put("job", job);
            body.put("jobKnown", jobKnown);
            body.put("postTo", POST_TO + "?" + PARAMETER_JOB + "=" + job);
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
        String harvesterPath = PATH_TO_HARVESTER;
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

        String pathBase = vitroHomeDirectoryName + "/" + FileStorageSetup.FILE_STORAGE_SUBDIRECTORY + "/" + PATH_TO_UPLOADS;
        return pathBase;
    }

    /**
     * Gets the FileHarvestJob implementation that is needed to handle the specified request.  This
     * will depend on the type of harvest being performed (CSV, RefWorks, etc.)
     * @param vreq the request from the browser
     * @param jobParameter the POST or GET parameter "job".  Might not be available in vreq at this point,
     *                     thus we are requiring that it be sent in.
     * @return the FileHarvestJob that will provide harvest-type-specific services for this request
     */
    private FileHarvestJob getJob(VitroRequest vreq, String jobParameter)
    {
        String namespace = vreq.getWebappDaoFactory().getDefaultNamespace();
        
        FileHarvestJob job = null; 
        
        //todo: complete
        if(jobParameter == null)
            log.error("No job specified.");
        else if(jobParameter.equalsIgnoreCase(JOB_CSV_GRANT))
            job = new CsvFileHarvestJob(vreq, "granttemplate.csv", "testCSVtoRDFgrant.sh", namespace);
        else if(jobParameter.equalsIgnoreCase(JOB_CSV_PERSON))
            job = new CsvFileHarvestJob(vreq, "persontemplate.csv", "testCSVtoRDFperson.sh", namespace);
        else
            log.error("Invalid job: " + jobParameter);
        
        return job;
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

            //get the job parameter
            String jobParameter = req.getParameter(PARAMETER_JOB);
            
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
            FileHarvestJob job = getJob(vreq, jobParameter);

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
            FileHarvestJob job = getJob(vreq, vreq.getParameter(PARAMETER_JOB));
    
            //String path = getUploadPath(vreq);

            String script = job.getScript();
            String additionsFilePath = job.getAdditionsFilePath();
            log.error("start harvest");
            runScript(getSessionId(request), script, additionsFilePath);
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
                
                VitroRequest vreq = new VitroRequest(request);
                ArrayList<String> newlyAddedUrls = new ArrayList<String>();
                if(finished) {
                    ArrayList<String> newlyAddedUris = sessionIdToNewlyAddedUris.get(sessionId);
                    if(newlyAddedUris != null) {
                        for(String uri : newlyAddedUris) {
                            
                            String namespaceRoot = vreq.getWebappDaoFactory().getDefaultNamespace();
                            
                            String suffix = uri.substring(namespaceRoot.length());
                            String url = "display/" + suffix;
                            
                            newlyAddedUrls.add(uri);
                        }
                    }
                }
                
                JSONObject json = new JSONObject();
                json.put("progressSinceLastCheck", progressSinceLastCheck);
                json.put("finished", finished);
                json.put("newlyAddedUrls", newlyAddedUrls);

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


    private void runScript(String sessionId, String script, String additionsFilePath) {
        
        if(!sessionIdToHarvestThread.containsKey(sessionId)) {
            
            ScriptRunner runner = new ScriptRunner(sessionId, script, additionsFilePath);
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

    private ArrayList<String> extractNewlyAddedUris(File additionsFile) {
        ArrayList<String> newlyAddedUris = new ArrayList<String>();

        log.error(additionsFile.getAbsolutePath());
        
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(additionsFile);
            NodeList descriptionNodes = document.getElementsByTagName("http://www.w3.org/1999/02/22-rdf-syntax-ns#Description");

            int numNodes = descriptionNodes.getLength();
            for(int i = 0; i < numNodes; i++) {
                Node node = descriptionNodes.item(i);
                
                ArrayList<String> types = getRdfTypes(node);
                if(types.contains("http://vivoweb.org/ontology/core#Grant")) { //todo: generalize
                
                    NamedNodeMap attributes = node.getAttributes();
                    Node aboutAttribute = attributes.getNamedItem("http://www.w3.org/1999/02/22-rdf-syntax-ns#about");
                    if(aboutAttribute != null) {
                        String value = aboutAttribute.getNodeValue();
                        newlyAddedUris.add(value);
                    }
                }
            }
            
            

        } catch(Exception e) {
            log.error(e, e);
        }

        return newlyAddedUris;
    }
    
    private ArrayList<String> getRdfTypes(Node descriptionNode) {
        ArrayList<String> rdfTypesList = new ArrayList<String>();
        
        NodeList children = descriptionNode.getChildNodes();
        int numChildren = children.getLength();
        for(int i = 0; i < numChildren; i++) {
            Node child = children.item(i);

            String name = child.getNodeName();
            if(name.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                NamedNodeMap attributes = child.getAttributes();
                Node resourceAttribute = attributes.getNamedItem("http://www.w3.org/1999/02/22-rdf-syntax-ns#resource");
                if(resourceAttribute != null) {
                    String value = resourceAttribute.getNodeValue();
                    rdfTypesList.add(value);
                }
            }
        }

        return rdfTypesList;
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
    private Map<String, ArrayList<String>> sessionIdToNewlyAddedUris = new Hashtable<String, ArrayList<String>>();
    private class ScriptRunner extends Thread {

        private final String sessionId;
        private final String script;
        private final String additionsFilePath;

        public ScriptRunner(String sessionId, String script, String additionsFilePath) {
            this.sessionId = sessionId;
            this.script = script;
            this.additionsFilePath = additionsFilePath;
        }

        @Override
        public void run() {
            try {
                ArrayList<String> unsentLogLines = sessionIdToUnsentLogLines.get(sessionId);
                if(unsentLogLines == null) {
                    unsentLogLines = new ArrayList<String>();
                    sessionIdToUnsentLogLines.put(this.sessionId, unsentLogLines);
                }
                
                File scriptFile = createScriptFile(this.script);

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
                
                File additionsFile = new File(this.additionsFilePath);
                ArrayList<String> newlyAddedUris = extractNewlyAddedUris(additionsFile);
                log.error("newly added URIs size: " + newlyAddedUris.size());
                sessionIdToNewlyAddedUris.put(this.sessionId, newlyAddedUris);
                
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


