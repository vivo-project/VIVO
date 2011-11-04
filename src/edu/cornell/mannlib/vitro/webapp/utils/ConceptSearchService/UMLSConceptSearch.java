package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.servlet.UMLSTerminologyAnnotation;

public class UMLSConceptSearch extends AbstractConceptSearch{
    private static final String submissionUrl = "http://link.informatics.stonybrook.edu/MeaningLookup/MlServiceServlet?";
    private static final Log log = LogFactory.getLog(UMLSConceptSearch.class);

    public String processResults(String searchEntry) {
    	
    	String dataUrl = submissionUrl + "textToProcess=" + URLEncoder.encode(searchEntry) + "&format=json";
    	String results = null;
     	try{
     			StringWriter sw = new StringWriter();
	    		URL rss = new URL(dataUrl);
	    		
	    		
	    		BufferedReader in = new BufferedReader(new InputStreamReader(rss.openStream()));
	    		String inputLine;
	    		while((inputLine = in.readLine()) != null) {
	    			sw.write(inputLine);
	    		}
	    		in.close();
	    		
	    		results = sw.toString();

	    	} catch(Exception ex) {
	    		log.error("error occurred in servlet", ex);
	    	}
	    	
	    	
	    results = processOutput(results);
    	 return results;
    }

    //Returning string with 
	private String processOutput(String results) {
		String newResults = null;
		try {
			JSONObject json = new JSONObject(results);
			//Return label and CUID of object
			if(json.has("All")) {
				
			}
			
			if(json.has("BestMatch")) {
				JSONArray bestMatchArray = json.getJSONArray("BestMatch");
				int len = bestMatchArray.length();
				int i;
				for(i = 0; i < len; i++) {
					JSONObject o = bestMatchArray.getJSONObject(i);
					String definition = o.getString("definition");
					String label = o.getString("label");
					String CUI = o.getString("CUI");
					String type = o.getString("type");
				}
			}
		} catch(Exception ex) {
			log.error("Error making json object out of output");
		}
		return newResults;
	}
	
}