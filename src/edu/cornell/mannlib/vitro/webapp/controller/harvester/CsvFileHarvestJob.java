/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skife.csv.SimpleReader;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;



/**
 * An implementation of FileHarvestJob that can be used for any CSV file harvest.
 */
class CsvFileHarvestJob implements FileHarvestJob {

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

    /**
     * The namespace to be used for the harvest.
     */
    private final String namespace;

    /**
     * Constructor.
     * @param templateFileName just the name of the template file.  The directory is assumed to be standard.
     */
    public CsvFileHarvestJob(VitroRequest vreq, String templateFileName, String scriptFileName, String namespace) {
        this.vreq = vreq;
        this.templateFile = new File(getTemplateFileDirectory() + templateFileName);
        this.scriptFile = new File(getScriptFileDirectory() + scriptFileName);
        log.error(getTemplateFileDirectory() + templateFileName);
        this.namespace = namespace;
    }

    /**
     * Gets the path to the directory containing the template files.
     * @return the path to the directory containing the template files
     */
    private String getTemplateFileDirectory() {
        String harvesterPath = TestFileController.getHarvesterPath();
        String pathToTemplateFiles = harvesterPath + TestFileController.PATH_TO_TEMPLATE_FILES;
        return pathToTemplateFiles;
    }

    /**
     * Gets the path to the directory containing the script files.
     * @return the path to the directory containing the script files
     */
    private String getScriptFileDirectory() {
        String harvesterPath = TestFileController.getHarvesterPath();
        String pathToScriptFiles = harvesterPath + TestFileController.PATH_TO_HARVESTER_SCRIPTS;
        return pathToScriptFiles;
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
        String errorMessage = "File header does not match template";
        if(line.length != templateFirstLine.length) {
            //return errorMessage + ": " + "file header columns = " + line.length + ", template columns = " + templateFirstLine.length;
            String errorMsg = "";
            errorMsg += "file header items: ";
            for(int i = 0; i < line.length; i++) {
                errorMsg += line[i] + ", ";
            }
            errorMsg += "template items: ";
            for(int i = 0; i < templateFirstLine.length; i++) {
                errorMsg += templateFirstLine[i] + ", ";
            }
            return errorMsg;
        }
        for(int i = 0; i < line.length; i++)
        {
            if(!line[i].equals(templateFirstLine[i]))
                return errorMessage + ": file header column " + (i + 1) + " = " + line[i] + ", template column " + (i + 1) + " = " + templateFirstLine[i];
        }
        return null;
    }

    @Override
    public String getScript()
    {
        File scriptTemplate = this.scriptFile;

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
    public String getAdditionsFilePath() {

        return TestFileController.getHarvesterPath() + TestFileController.PATH_TO_ADDITIONS_FILE;
    }
    
    

}


