/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cornell.mannlib.vitro.webapp.application.ApplicationUtils;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.filestorage.impl.FileStorageImplWrapper;

@WebServlet(name = "FileHarvestController", urlPatterns = {"/harvester/harvest"})
public class FileHarvestController extends FreemarkerHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(FileHarvestController.class);
    private static final String TEMPLATE_DEFAULT = "fileharvest.ftl";

    private static final String NORMAL_TERMINATION_LAST_OUTPUT = "File Harvest completed successfully";

    private static final String PARAMETER_FIRST_UPLOAD = "firstUpload";
    private static final String PARAMETER_UPLOADED_FILE = "uploadedFile";
    private static final String PARAMETER_MODE = "mode";
    private static final String PARAMETER_JOB = "job";

    private static final String POST_TO = UrlBuilder.getUrl("/harvester/harvest");

    private static final String MODE_HARVEST = "harvest";
    private static final String MODE_CHECK_STATUS = "checkStatus";
    private static final String MODE_DOWNLOAD_TEMPLATE = "template";


    /**
     * Stores information about the Harvester thread for a particular user session.
     */
    private Map<String, SessionInfo> sessionIdToSessionInfo = new Hashtable<String, SessionInfo>(); //Hashtable is threadsafe, HashMap is not

    /**
     * A list of known job parameters (that is, "job=" values from the query string which we will accept from the browser).
     * This should be filled in the static initializer and then never written to again.
     */
    private static final List<String> knownJobs = new ArrayList<String>();


    /**
     * Relative path from the VIVO Uploads directory to the root location where user-uploaded files will be stored.  Include
     * final slash.
     */
    private static final String PATH_TO_UPLOADS = "harvester/";

    /**
     * Relative path from the Harvester root directory to the main area reserved for the VIVO File Harvest feature.  Include
     * final slash.
     */
    private static final String PATH_TO_FILE_HARVEST_ROOT = "vivo/";

    /**
     * Relative path from the Harvester root directory to the directory where user-downloadable template files are stored.
     * Include final slash.
     */
    public static final String PATH_TO_TEMPLATE_FILES = PATH_TO_FILE_HARVEST_ROOT + "templates/";

    /**
     * Relative path from the Harvester root directory to the directory containing the script templates.  Include final slash.
     */
    public static final String PATH_TO_HARVESTER_SCRIPTS = PATH_TO_FILE_HARVEST_ROOT + "scripts/";

    /**
     * Relative path from the Harvester root directory to the directory containing the script templates.  Include final slash.
     */
    public static final String PATH_TO_HARVESTED_DATA = PATH_TO_FILE_HARVEST_ROOT + "harvested-data/";


    static {
        fillKnownJobTypesList();
    }

    /**
     * Fill the known job types list.  Any time a new job type is added, we need to make sure this method is adding it to the list.
     * By "new job type" is meant a new "job=" parameter that we understand when we see it in the query string.  This typically means
     * we have also handled seeing this parameter in the getJob() method of this class.
     *
     * The exception to all this is a new CSV job, which is entirely handled by adding a new CsvFileHarvestJob.JobType enum value.  This
     * method as well as this class's getJob() method already handle the rest.
     */
    private static void fillKnownJobTypesList() {

        //fill known CSV job types
        CsvFileHarvestJob.JobType[] csvFileHarvestJobTypes = CsvFileHarvestJob.JobType.values();
        for(CsvFileHarvestJob.JobType csvFileHarvestJobType : csvFileHarvestJobTypes) {
            knownJobs.add(csvFileHarvestJobType.httpParameterName.toLowerCase());
        }
    }


    @Override
	public long maximumMultipartFileSize() {
    	return 1024 * 1024;
	}


	@Override
	public boolean stashFileSizeException() {
		return true;
	}


	@Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
            cleanUpOldSessions();

            String job = vreq.getParameter(PARAMETER_JOB);
            String jobKnown = "false";
            if((job != null) && FileHarvestController.knownJobs.contains(job.toLowerCase()))
                jobKnown = "true";

            FileHarvestJob jobObject = getJob(vreq, job);

            Map<String, Object> body = new HashMap<String, Object>();
            String harvesterPath = getHarvesterPath(vreq);
            //body.put("uploadPostback", "false");
            body.put("paramFirstUpload", PARAMETER_FIRST_UPLOAD);
            body.put("paramUploadedFile", PARAMETER_UPLOADED_FILE);
            body.put("paramMode", PARAMETER_MODE);
            body.put("paramJob", PARAMETER_JOB);
            body.put("modeHarvest", MODE_HARVEST);
            body.put("modeCheckStatus", MODE_CHECK_STATUS);
            body.put("modeDownloadTemplate", MODE_DOWNLOAD_TEMPLATE);
            body.put("job", job);
            body.put("jobKnown", jobKnown);
            body.put("harvesterLocation", harvesterPath);
            body.put("postTo", POST_TO + "?" + PARAMETER_JOB + "=" + job);
            body.put("jobSpecificHeader", (jobObject != null) ? jobObject.getPageHeader() : "");
            body.put("jobSpecificLinkHeader", (jobObject != null) ? jobObject.getLinkHeader() : "");
            body.put("jobSpecificDownloadHelp", (jobObject != null) ? jobObject.getTemplateDownloadHelp() : "");
            body.put("jobSpecificFillInHelp", (jobObject != null) ? jobObject.getTemplateFillInHelp() : "");
            body.put("jobSpecificNoNewDataMessage", (jobObject != null) ? jobObject.getNoNewDataMessage() : "");
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
    public static String getHarvesterPath(HttpServletRequest req)
    {
    	String pathToHarvester = ConfigurationProperties.getBean(req).getProperty("harvester.location");
    	if (pathToHarvester == null) {
    		log.error("The runtime.properties file does not contain a value for 'harvester.location'");
    		return "";
    	}
    	return pathToHarvester;
    }

    /**
     * Returns the path on this machine of the area within Harvester reserved for File Harvest.
     * @return the path on this machine of the area within Harvester reserved for File Harvest
     */
    public static String getFileHarvestRootPath(HttpServletRequest req)
    {
        String fileHarvestRootPath = getHarvesterPath(req) + PATH_TO_FILE_HARVEST_ROOT;
        return fileHarvestRootPath;
    }

    /**
     * Returns the base directory used for all File Harvest uploads.
     * @param context the current servlet context
     * @return the base directory for file harvest uploads
     * @throws Exception if the Vitro home directory could not be found
     */
    private static String getUploadPathBase(ServletContext context) throws Exception
    {
        String vitroHomeDirectoryName = ApplicationUtils.instance().getHomeDirectory().getPath().toString();
        return vitroHomeDirectoryName + "/" + FileStorageImplWrapper.FILE_STORAGE_SUBDIRECTORY + "/" + PATH_TO_UPLOADS;
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

        if(jobParameter == null)
            log.error("No job specified.");
        else if(CsvFileHarvestJob.JobType.containsTypeWithHttpParameterName(jobParameter)) //check if this is a CSV job
            job = CsvFileHarvestJob.createJob(CsvFileHarvestJob.JobType.getByHttpParameterName(jobParameter), vreq, namespace);
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

        try {
            boolean isMultipart = new VitroRequest(request).isMultipart();
            String mode = request.getParameter(PARAMETER_MODE);
            if(isMultipart)
                doFileUploadPost(request, response);
            else if(mode.equals(MODE_HARVEST))
                doHarvestPost(request, response);
            else if(mode.equals(MODE_CHECK_STATUS))
                doCheckHarvestStatusPost(request, response);
            else if(mode.equals(MODE_DOWNLOAD_TEMPLATE))
                doDownloadTemplatePost(request, response);
            else
                throw new Exception("Unrecognized post mode: " + mode);
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

        ObjectNode json = generateJson(false);
        try {
            VitroRequest vreq = new VitroRequest(request);

            //check that the parsing was successful
            if(vreq.hasFileSizeException()) {
                Exception e = vreq.getFileSizeException();
                throw new ExceptionVisibleToUser(e);
            }

            //get the job parameter
            String jobParameter = vreq.getParameter(PARAMETER_JOB);

            //get the location where we want to save the files (it will end in a slash), then create a File object out of it
            String path = getUploadPath(vreq);
            File directory = new File(path);

            //if this is a page refresh, we do not want to save stale files that the user doesn't want anymore, but we
            //  still have the same session ID and therefore the upload directory is unchanged.  Thus we must clear the
            //  upload directory if it exists (a "first upload" parameter, initialized to "true" but which gets set to
            //  "false" once the user starts uploading stuff is used for this).
            String firstUpload = vreq.getParameter(PARAMETER_FIRST_UPLOAD); //clear directory on first upload
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
            Map<String, List<FileItem>> fileStreams = vreq.getFiles();
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
                json.put("success", success);
                json.put("fileName", name);
                json.put("errorMessage", errorMessage);
            } else {
                //if for some reason no file was included with the request, send an error back
                json.put("success", false);
                json.put("fileName", "(none)");
                json.put("errorMessage", "No file uploaded");
            }
        } catch(ExceptionVisibleToUser e) {
            log.error(e, e);

            //handle exceptions whose message is for the user
            json.put("success", false);
            json.put("filename", "(none)");
            json.put("errorMessage", e.getMessage());
        } catch(Exception e) {
            log.error(e, e);
            json = generateJson(true);
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

    	ObjectNode json;
        try {
            VitroRequest vreq = new VitroRequest(request);
            FileHarvestJob job = getJob(vreq, vreq.getParameter(PARAMETER_JOB));

            //String path = getUploadPath(vreq);

            String script = job.getScript();
            String additionsFilePath = job.getAdditionsFilePath();
            String scriptFileLocation = getScriptFileLocation(vreq);
            runScript(getSessionId(request), script, additionsFilePath, scriptFileLocation, job);

            json = generateJson(false);
            json.put("progressSinceLastCheck", "");
            json.put("scriptText", script);
            json.put("finished", false);

        } catch(Exception e) {
        	json = generateJson(true);
            log.error(e, e);
        }

        try {
        	response.getWriter().write(json.toString());
        } catch(IOException e) {
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

        ObjectNode json;
        try {
            String newline = "\n";

            String sessionId = getSessionId(request);
            SessionInfo sessionInfo = sessionIdToSessionInfo.get(sessionId);

            //if we have started a thread, check the status and return it to the user
            if(sessionInfo != null) {

                String[] unsentLogLines;
                ArrayList<String> unsentLogLinesList = sessionInfo.unsentLogLines;

                //don't let the harvester thread add data to the unsent log lines list until we have both copied it and cleared it
                synchronized (unsentLogLinesList) {
                    unsentLogLines = unsentLogLinesList.toArray(new String[unsentLogLinesList.size()]);
                    unsentLogLinesList.clear();
                }

                StringBuilder progressSinceLastCheck = new StringBuilder();
                for (String unsentLogLine : unsentLogLines) {
                    progressSinceLastCheck.append(unsentLogLine).append(newline);
                }

                boolean finished = sessionInfo.isFinished();
                boolean abnormalTermination = false;

                VitroRequest vreq = new VitroRequest(request);
                ArrayNode newlyAddedUrls = JsonNodeFactory.instance.arrayNode();
                ArrayNode newlyAddedUris = JsonNodeFactory.instance.arrayNode();
                if(finished) {
                    if (sessionInfo.newlyAddedUris != null) {
                        for(String uri : sessionInfo.newlyAddedUris) {
                            newlyAddedUris.add(uri);
                            newlyAddedUrls.add(UrlBuilder.getIndividualProfileUrl(uri, vreq));
                        }

                    }

                    //remove all entries in "sessionIdTo..." mappings for this session ID
                    clearSessionInfo(sessionId);

                    if(sessionInfo.getAbnormalTermination())
                    	abnormalTermination = true;
                }

                if(!abnormalTermination) {
	                json = generateJson(false);
	                json.put("progressSinceLastCheck", progressSinceLastCheck.toString());
	                json.put("finished", finished);
	                json.put("newlyAddedUris", newlyAddedUris);
	                json.put("newlyAddedUrls", newlyAddedUrls);
                } else {
                	json = generateJson(true);
                	log.error("File harvest terminated abnormally.");
                }
            } else { //if we have not started a harvest thread, the browser should not have made this request to begin with.  Bad browser, very bad browser.
            	json = generateJson(true);
                log.error("Attempt to check status of a harvest that was never started!  (Session ID " + sessionId + ")");
            }
        } catch(Exception e) {
        	json = generateJson(true);
            log.error(e, e);
        }

        try {
        	response.getWriter().write(json.toString());
        } catch(IOException e) {
        	log.error(e, e);
        }
    }

    private void doDownloadTemplatePost(HttpServletRequest request, HttpServletResponse response) {

        VitroRequest vreq = new VitroRequest(request);
        FileHarvestJob job = getJob(vreq, vreq.getParameter(PARAMETER_JOB));
        File fileToSend = new File(job.getTemplateFilePath());

        response.setContentType("application/octet-stream");
        response.setContentLength((int)(fileToSend.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileToSend.getName() + "\"");

        try {
            byte[] byteBuffer = new byte[(int)(fileToSend.length())];
            DataInputStream inStream = new DataInputStream(new FileInputStream(fileToSend));

            ServletOutputStream outputStream = response.getOutputStream();
            for(int length = inStream.read(byteBuffer); length != -1; length = inStream.read(byteBuffer)) {
                outputStream.write(byteBuffer, 0, length);
            }

            inStream.close();
            outputStream.flush();
            outputStream.close();
        } catch(IOException e) {
            log.error(e, e);
        }
    }

    /**
     * Returns the location in which the ready-to-run scripts, after having template replacements made on them, will be
     * placed.  Final slash included.
     * @return the location in which the ready-to-run scripts will be placed
     */
    private static String getScriptFileLocation(HttpServletRequest req) {
        return getHarvesterPath(req) + PATH_TO_HARVESTER_SCRIPTS + "temp/";
    }



    private File createScriptFile(String scriptFileLocation, String script) throws IOException {
        File scriptDirectory = new File(scriptFileLocation);
        if(!scriptDirectory.exists()) {
            scriptDirectory.mkdirs();
        }

        File tempFile = File.createTempFile("harv", ".sh", scriptDirectory);

        FileWriter writer = new FileWriter(tempFile);
        writer.write(script);
        writer.close();

        return tempFile;
    }


    private void runScript(String sessionId, String script, String additionsFilePath, String scriptFileLocation, FileHarvestJob job) {
        clearSessionInfo(sessionId);

        ScriptRunner runner = new ScriptRunner(sessionId, script, additionsFilePath, scriptFileLocation, job);
        SessionInfo info = new SessionInfo(sessionId, runner);
        sessionIdToSessionInfo.put(sessionId, info);
        runner.start();
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
     * Parse an additions file (RDF/XML) to get the URIs of newly-harvested data, which will be sent to the browser and
     * displayed to the user as links.
     * @param additionsFile the file containing the newly-added RDF/XML
     * @param newlyAddedUris a list in which to place the newly added URIs
     */
    private void extractNewlyAddedUris(File additionsFile, List<String> newlyAddedUris, FileHarvestJob job) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().parse(additionsFile);
            //Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(additionsFile);
            NodeList descriptionNodes = document.getElementsByTagNameNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description");

            int numNodes = descriptionNodes.getLength();
            for(int i = 0; i < numNodes; i++) {
                Node node = descriptionNodes.item(i);

                ArrayList<String> types = getRdfTypes(node);

                boolean match = false;
                String[] validRdfTypesForJob = job.getRdfTypesForLinks();
                for(String rdfType : validRdfTypesForJob) {
                	if(types.contains(rdfType)) {
                        match = true;
                        break;
                    }
                }

                if(match) {

                    NamedNodeMap attributes = node.getAttributes();
                    Node aboutAttribute = attributes.getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about");
                    if(aboutAttribute != null) {
                        String value = aboutAttribute.getNodeValue();
                        newlyAddedUris.add(value);
                    }
                }
            }

        } catch(Exception e) {
            log.error(e, e);
        }
    }

    /**
     * Parse an XML node for all subnodes with qualified name "rdf:type", and return each's "rdf:resource" value in a list.
     * @param descriptionNode the RDF description node
     * @return a list of rdf:types of the given description node
     */
    private ArrayList<String> getRdfTypes(Node descriptionNode) {
        ArrayList<String> rdfTypesList = new ArrayList<String>();

        NodeList children = descriptionNode.getChildNodes();
        int numChildren = children.getLength();
        for(int i = 0; i < numChildren; i++) {
            Node child = children.item(i);

            String namespace = child.getNamespaceURI();
            String name = child.getLocalName();
            String fullName = namespace + name;
            if(fullName.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                NamedNodeMap attributes = child.getAttributes();
                Node resourceAttribute = attributes.getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource");
                if(resourceAttribute != null) {
                    //String attributeNamespace = resourceAttribute.getNamespaceURI();
                    String value = resourceAttribute.getNodeValue();
                    //rdfTypesList.add(attributeNamespace + value);
                    rdfTypesList.add(value);
                }
            }
        }

        return rdfTypesList;
    }

    /**
     * If a session info object exists for this session ID, abort the thread if it is still running and remove the object.
     * @param sessionId the session ID for which to clear info
     */
    private void clearSessionInfo(String sessionId) {
        SessionInfo sessionInfo = this.sessionIdToSessionInfo.get(sessionId);
        if(sessionInfo != null) {
            if(!sessionInfo.isFinished()) {
                if(sessionInfo.harvestThread.isAlive()) {
                    sessionInfo.harvestThread.abortRun();
                }
            }
            this.sessionIdToSessionInfo.remove(sessionId);
        }
    }

    /**
     * If all goes according to plan, clearSessionInfo() should be called once the client gets the last bit of information from the
     * harvest.  However, if the client doesn't request it (because the browser was closed, etc.) then the method will never get called.
     * This method gets called every time the page is initially loaded, to look for session data that is 6 hours old or more, and remove
     * it.
     */
    private void cleanUpOldSessions() {
        int minutesToAllowSession = 360;
        long millisecondsToAllowSession = minutesToAllowSession * 60 * 1000;

        Date now = new Date();
        Set<String> keySet = this.sessionIdToSessionInfo.keySet();
        for(String sessionId : keySet) {
            SessionInfo info = this.sessionIdToSessionInfo.get(sessionId);
            Date startTime = info.createTime;
            long differenceInMilliseconds = now.getTime() - startTime.getTime();
            if(differenceInMilliseconds > millisecondsToAllowSession) {
                log.debug("Removing old session: " + sessionId);
                clearSessionInfo(sessionId);
            }
        }
    }

    /**
     * Create a new JSON object
     * @param fatalError whether the fatal error flag should be set on this object
     * @return the new JSON object
     */
    private ObjectNode generateJson(boolean fatalError) {
    	ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("fatalError", fatalError);
        return json;
    }

    /**
     * Information relating to a particular user session, created just before the harvester thread is starting.
     * @author mbarbieri
     */
    private class SessionInfo {

        /**
         * The session ID for this user session.
         */
        @SuppressWarnings("unused")
        public final String sessionId;

        /**
         * The time this object was created.
         */
        public final Date createTime;

        /**
         * The Harvester thread for his user session.
         */
        public final ScriptRunner harvestThread;

        /**
         * Harvester output that has not yet been sent back to the browser, for this user session.
         */
        public final ArrayList<String> unsentLogLines = new ArrayList<String>();

        /**
         * Flag indicating that the thread has finished.
         */
        private boolean finished = false;

        /**
         * Flag indicating that the thread finished abnormally.
         */
        private boolean abnormalTermination = false;


        /**
         * Newly added entries to VIVO, for this user session.
         */
        public final ArrayList<String> newlyAddedUris = new ArrayList<String>();

        public SessionInfo(String sessionId, ScriptRunner harvestThread) {

            this.createTime = new Date();

            this.sessionId = sessionId;
            this.harvestThread = harvestThread;
        }

        public void setAbnormalTermination() {
        	abnormalTermination = true;
        }
        public boolean getAbnormalTermination() {
        	return abnormalTermination;
        }

        public void finish() {
            finished = true;
        }
        public boolean isFinished() {
            return finished;
        }
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


    private class ScriptRunner extends Thread {

        private final String sessionId;
        private final String script;
        private final String additionsFilePath;
        private final String scriptFileLocation;
        private final FileHarvestJob job;

        private volatile boolean abort = false;

        public ScriptRunner(String sessionId, String script, String additionsFilePath, String scriptFileLocation, FileHarvestJob job) {
            this.sessionId = sessionId;
            this.script = script;
            this.additionsFilePath = additionsFilePath;
            this.scriptFileLocation = scriptFileLocation;
            this.job = job;
        }

        public void abortRun() {
            abort = true;
        }


        @Override
        public void run() {
            SessionInfo sessionInfo = sessionIdToSessionInfo.get(sessionId);
            boolean normalTerminationLineFound = false;
            if(sessionInfo != null) {
                try {
                    ArrayList<String> unsentLogLines = sessionInfo.unsentLogLines;

                    File scriptFile = createScriptFile(this.scriptFileLocation, this.script);

                    String command = "/bin/bash " + this.scriptFileLocation + scriptFile.getName();

                    log.info("Running command: " + command);
                    Process pr = Runtime.getRuntime().exec(command);

                    //try { Thread.sleep(15000); } catch(InterruptedException e) {log.error(e, e);}

                    BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    for(String line = processOutputReader.readLine(); line != null; line = processOutputReader.readLine()) {

                    	normalTerminationLineFound = line.endsWith(NORMAL_TERMINATION_LAST_OUTPUT); //set every read to ensure it's the last line

                        //don't add stuff to this list if the main thread is running a "transaction" of copying out the data to send to client and then clearing the list
                        synchronized(unsentLogLines) {
                            unsentLogLines.add(line);
                        }
                        log.info("Harvester output: " + line);

                        if(this.abort)
                            break;
                    }

                    if(!this.abort){
                        BufferedReader processErrorReader = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                        for(String line = processErrorReader.readLine(); line != null; line = processErrorReader.readLine()) {
                            log.info("Harvester error: " + line);

                            if(this.abort)
                                break;
                        }
                    }

                    if(this.abort) {
                        log.debug("Aborting harvester script for session " + this.sessionId + ".");
                        pr.destroy();
                    } else {
                        int exitVal;

                        try {
                            exitVal = pr.waitFor();
                        }
                        catch(InterruptedException e) {
                            throw new IOException(e.getMessage(), e);
                        }

                        log.debug("Harvester script for session " + this.sessionId + " exited with error code " + exitVal);

                        File additionsFile = new File(this.additionsFilePath);
                        if(additionsFile.exists())
                            extractNewlyAddedUris(additionsFile, sessionInfo.newlyAddedUris, this.job);
                        else
                            log.error("Additions file not found: " + this.additionsFilePath);
                    }

                    log.info("Harvester script execution complete");
                } catch (IOException e) {
                    log.error(e, e);
                } finally {
                    sessionInfo.finish();
                    if(!normalTerminationLineFound)
                    	sessionInfo.setAbnormalTermination();
                }
            }
        }
    }
}
