/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;


/**
 * An implementation of FileHarvestJob that can be used for any CSV file harvest.
 */
class CsvFileHarvestJob implements FileHarvestJob {

    /**
     * Contains constant constructor inputs for CsvFileHarvestJob
     * @author mbarbieri
     */
    public enum JobType {
        GRANT("csvGrant", "granttemplate.csv", "CSVtoRDFgrant.sh", "csv-grant-to-vivo.xsl", "Grant", "Imported Grants", "No new grants were imported.", new String[] {"http://vivoweb.org/ontology/core#Grant"}),
        PERSON("csvPerson", "persontemplate.csv", "CSVtoRDFperson.sh", "csv-people-to-vivo.xsl", "Person", "Imported Persons", "No new persons were imported.", new String[] {"http://xmlns.com/foaf/0.1/Person"});

        public final String httpParameterName;
        private final String templateFileName;
        private final String scriptFileName;
        private final String xsltFileName;
        private final String friendlyName;
        private final String linkHeader;
        private final String noNewDataMessage;
        private final String[] rdfTypesForLinks;

        /**
         * Determines if there is a JobType with the specified HTTP parameter name.
         * @param httpParameterName the HTTP parameter name to look for a Job Type for
         * @return true if there is such a JobType, false otherwise
         */
        public static boolean containsTypeWithHttpParameterName(String httpParameterName) {
            return (getByHttpParameterName(httpParameterName) != null);
        }

        /**
         * Returns the JobType with the specified HTTP parameter name.  This is essentially a string identifier for the job type.  This
         * method accepts nulls, returning null in that case.
         * @param httpParameterName the HTTP parameter name to find the job for
         * @return the JobType with the specified HTTP parameter name, or null if there is none or httpParameterName was null
         */
        public static JobType getByHttpParameterName(String httpParameterName) {
            JobType returnValue = null;

            if(httpParameterName != null) {
                JobType[] values = JobType.values();
                for(JobType jobType : values) {
                    if(jobType.httpParameterName.equalsIgnoreCase(httpParameterName)) {
                        returnValue = jobType;
                        break;
                    }
                }
            }
            return returnValue;
        }

        private JobType(String httpParameterName, String templateFileName, String scriptFileName, String xsltFileName, String friendlyName, String linkHeader, String noNewDataMessage, String[] rdfTypesForLinks) {
            this.httpParameterName = httpParameterName;
            this.templateFileName = templateFileName;
            this.scriptFileName = scriptFileName;
            this.xsltFileName = xsltFileName;
            this.friendlyName = friendlyName;
            this.linkHeader = linkHeader;
            this.noNewDataMessage = noNewDataMessage;
            this.rdfTypesForLinks = Arrays.copyOf(rdfTypesForLinks, rdfTypesForLinks.length);
        }

        private CsvFileHarvestJob constructCsvFileHarvestJob(VitroRequest vreq, String namespace) {
            return new CsvFileHarvestJob(vreq, this.templateFileName, this.scriptFileName, this.xsltFileName, namespace, this.friendlyName, this.linkHeader, this.noNewDataMessage, this.rdfTypesForLinks);
        }
    }


    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(CsvFileHarvestJob.class);

    /**
     * The HTTP request.
     */
    private VitroRequest vreq;

    /**
     * The template file against which uploaded CSV files will be validated.
     */
    private File templateFile;

    /**
     * The script which will be run after needed replacements are made.
     */
    private File scriptFile;

    /* *
     * The datamap to convert the JDBCFetch output to RDF/XML.
     */
    /*
    private File xsltFile;
    */

    /**
     * The namespace to be used for the harvest.
     */
    @SuppressWarnings("unused")
    private final String namespace;

    /**
     * A name for the type of data being imported.  For example "Grant" or "Person".
     */
    private final String friendlyName;

    /**
     * A heading to be shown above the area where links to profiles of newly-harvested entities are listed.
     */
    private final String linkHeader;

    /**
     * The message to show to the user if there are no newly-harvested entities to show them.
     */
    private final String noNewDataMessage;

    /**
     * An array of rdf:type values which will be used for links.
     */
    private final String[] rdfTypesForLinks;

    /**
     * The session ID of this user session.
     */
    private final String sessionId;


    public static CsvFileHarvestJob createJob(JobType jobType, VitroRequest vreq, String namespace) {
        return jobType.constructCsvFileHarvestJob(vreq, namespace);
    }

    /**
     * Constructor.
     * @param templateFileName just the name of the template file.  The directory is assumed to be standard.
     */
    private CsvFileHarvestJob(VitroRequest vreq, String templateFileName, String scriptFileName, String xsltFileName, String namespace, String friendlyName, String linkHeader, String noNewDataMessage, String[] rdfTypesForLinks) {
        this.vreq = vreq;
        this.templateFile = new File(getTemplateFileDirectory() + templateFileName);
        this.scriptFile = new File(getScriptFileDirectory() + scriptFileName);
//        this.xsltFile = new File(FileHarvestController.getHarvesterPath() + "config/datamaps/" + xsltFileName);
        this.namespace = namespace;
        this.friendlyName = friendlyName;
        this.linkHeader = linkHeader;
        this.noNewDataMessage = noNewDataMessage;
        this.rdfTypesForLinks = Arrays.copyOf(rdfTypesForLinks, rdfTypesForLinks.length);

        this.sessionId = this.vreq.getSession().getId();
    }

    /**
     * Gets the path to the directory containing the template files.
     * @return the path to the directory containing the template files
     */
    private String getTemplateFileDirectory() {
        String harvesterPath = FileHarvestController.getHarvesterPath(vreq);
        String pathToTemplateFiles = harvesterPath + FileHarvestController.PATH_TO_TEMPLATE_FILES;
        return pathToTemplateFiles;
    }

    /**
     * Gets the path to the directory containing the script files.
     * @return the path to the directory containing the script files
     */
    private String getScriptFileDirectory() {
        String harvesterPath = FileHarvestController.getHarvesterPath(vreq);
        String pathToScriptFiles = harvesterPath + FileHarvestController.PATH_TO_HARVESTER_SCRIPTS;
        return pathToScriptFiles;
    }



    private boolean[] getLinesEndingInComma(File file) throws IOException {
        ArrayList<Boolean> linesEndingInCommaList = new ArrayList<Boolean>();

        BufferedReader reader = new BufferedReader(new FileReader(file));

        for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            boolean lineEndsInComma = line.endsWith(",");
            linesEndingInCommaList.add(lineEndsInComma);
        }
        reader.close();

        boolean[] linesEndingInComma = new boolean[linesEndingInCommaList.size()];
        for(int i = 0; i < linesEndingInComma.length; i++) {
            linesEndingInComma[i] = linesEndingInCommaList.get(i);
        }
        return linesEndingInComma;
    }



    @Override
    @SuppressWarnings("rawtypes")
    public String validateUpload(File file) {
        try {
            String message = "No data in file";
            CSVParser cReader = new CSVParser(new FileReader(file), CSVFormat.DEFAULT);
            try {
                int rowNum = 0;
                int numberFields = 0;
                StringBuilder errorMsg = new StringBuilder("File header does not match template");
                for (CSVRecord cRecord : cReader) {
                    rowNum++;
                    if (false) {
                        numberFields = cRecord.size();
                        errorMsg.append("file header items: ");
                        for(int i = 0; i < cRecord.size(); i++) {
                            errorMsg.append(cRecord.get(i)).append(", ");
                        }
                    } else {
                        if (cRecord.size() > 0)  {
                            if(cRecord.size() != numberFields) {
                                if (errorMsg != null) {
                                    errorMsg.append("template items: ");
                                    for(int i = 0; i < cRecord.size(); i++) {
                                        errorMsg.append(cRecord.get(i)).append(", ");
                                    }
                                    return errorMsg.toString();
                                }
                                return "Mismatch in number of entries in row " + rowNum + ": expected " + numberFields + ", found " + cRecord.size();
                            }
                        }
                        message  = null;
                        errorMsg = null;
                    }
                }

                return message;
            } finally {
                cReader.close();
            }
        } catch (IOException e) {
            log.error(e, e);
            return e.getMessage();
        }
    }

/*
    private void prepareWorkspaceDirectory() {
        String path = FileHarvestController.getFileHarvestRootPath() + "workspaces/" + this.sessionId;
        File directory = new File(path);
        if(!directory.exists())
            directory.mkdirs();

        File scriptTemplate = this.scriptFile;
        String scriptTemplateContents = readFromFile(scriptTemplate);
        String scriptTemplateReplacements = performScriptTemplateReplacements(scriptTemplateContents);
        File outputScriptFile = new File(path + "/" + scriptTemplate.getName());
        writeToFile(outputScriptFile, scriptTemplateReplacements);

        File xsltTemplate = this.xsltFile;
        String xsltTemplateContents = readFromFile(xsltTemplate);
        String xsltTemplateReplacements = performXsltTemplateReplacements(xsltTemplateContents);
        File outputXsltFile = new File(path + "/" + xsltTemplate.getName());
        writeToFile(outputXsltFile, xsltTemplateReplacements);

    }
*/




    @Override
    public String getScript()
    {
        File scriptTemplate = this.scriptFile;

        String scriptTemplateContents = readFromFile(scriptTemplate);
        String replacements = performScriptTemplateReplacements(scriptTemplateContents);
        return replacements;
    }


    private String performScriptTemplateReplacements(String scriptTemplateContents) {
        String replacements = scriptTemplateContents;

        String workingDirectory = FileHarvestController.getHarvesterPath(vreq);
        String fileDirectory = FileHarvestController.getUploadPath(vreq);
        String harvestedDataPath = getHarvestedDataPath();
        String globalHarvestedDataRelativePath = FileHarvestController.PATH_TO_HARVESTED_DATA;

        if(harvestedDataPath.endsWith("/"))
            harvestedDataPath = harvestedDataPath.substring(0, harvestedDataPath.length() - 1);

        replacements = replacements.replace("${WORKING_DIRECTORY}", workingDirectory);
        replacements = replacements.replace("${UPLOADS_FOLDER}", fileDirectory);
        replacements = replacements.replace("${HARVESTED_DATA_PATH}", harvestedDataPath);
        replacements = replacements.replace("${GLOBAL_HARVESTED_DATA_RELATIVE_PATH}", globalHarvestedDataRelativePath);

        return replacements;
    }

/*
    private String performXsltTemplateReplacements(String xsltTemplateContents) {
        String replacements = xsltTemplateContents;

        replacements = replacements.replace("", "");

        return replacements;
    }


    private void writeToFile(File file, String contents) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.print(contents);
        } catch(IOException e) {
            log.error(e, e);
        } finally {
            if(writer != null)
                writer.close();
        }
    }
*/

    private String readFromFile(File file) {
        String contents = null;
        BufferedReader reader = null;
        try {
            int fileSize = (int)(file.length());
            char[] buffer = new char[fileSize];
            reader = new BufferedReader(new FileReader(file), fileSize);
            reader.read(buffer);
            contents = new String(buffer);
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

        return contents;
    }

    private String getHarvestedDataPath() {
        return FileHarvestController.getHarvesterPath(vreq) + FileHarvestController.PATH_TO_HARVESTED_DATA + "csv/" + this.sessionId + "/";
    }

    @Override
    public String getAdditionsFilePath() {
        return getHarvestedDataPath() + "additions.rdf.xml";
    }

    @Override
    public String getPageHeader() {
        return "Harvest " + this.friendlyName + " data from CSV file(s)";
    }

    @Override
    public String getLinkHeader() {
        return this.linkHeader;
    }

    @Override
    public String getTemplateFilePath() {
        return this.templateFile.getPath();
    }

    @Override
    public String[] getRdfTypesForLinks() {
        return Arrays.copyOf(this.rdfTypesForLinks, this.rdfTypesForLinks.length);
    }

    @Override
    public String getTemplateDownloadHelp() {
        return "Click here to download a template file to assist you with harvesting the data.";
    }

    @Override
    public String getTemplateFillInHelp() {
        String newline = "\n";
        String help = "";
        help += "<p>A CSV, or <strong>C</strong>omma-<strong>S</strong>eparated <strong>V</strong>alues file, is a method of storing tabular data in plain text.  The first line of a CSV file contains header information, while each subsequent line contains a data record.</p>" + newline;
        help += "<p>The template we provide contains only the header, which you will then fill in accordingly.  For example, if the template contains the text \"firstName,lastName\", then you might add two more lines, \"John,Doe\" and \"Jane,Public\".</p>" + newline;
        help += "<p>People in the harvest are grouped by the \"PersonID\" field and matched with existing data using the \"Email\" and/or \"FullName\" fields.</p>" + newline;
        help += "<p>In the grant harvest grants, people, and departments are grouped by \"GrantID\",\"PIID\" and \"CoPIID\", \"AdminDepartmentID\" fields respectively and matched with existing data using the name fields.</p>" + newline;
        return help;
    }

    @Override
    public String getNoNewDataMessage() {
        return this.noNewDataMessage;
    }

}


