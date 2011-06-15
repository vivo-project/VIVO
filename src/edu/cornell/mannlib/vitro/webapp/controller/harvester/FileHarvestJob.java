/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.harvester; 

import java.io.File;


/**
 * Handles specifics of a file harvest.  
 * @author mbarbieri
 *
 */
interface FileHarvestJob {

    /**
     * Checks to make sure the uploaded file can be handled by this job (for instance, are we looking for a CSV file with specific columns?)
     * @param file the uploaded file to check
     * @return null if success, message to be returned to the user if failure
     */
    String validateUpload(File file);
    
    /**
     * Gets the console script which can be used to run the harvest job. 
     * @return the console script which can be used to run the harvest job
     */
    String getScript();
    
    /**
     * The path to the file containing the RDF/XML triples that get added to VIVO.
     * @return the path to the file containing the RDF/XML triples that get added to VIVO
     */
    String getAdditionsFilePath();
}

