package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;

public class GeneratorUtil {
    
    private static final Log log = LogFactory.getLog(GeneratorUtil.class);
	
    /**
     * Build a field options list of resource URIs paired with their labels as
     * retrieved from the supplied RDFService.
     * 
     * @param rdfService       from which to retrieve labels: this should typically
     *                         be a LanguageFilteringRDFService.
     * 
     * @param webappDaoFactory may be null. If non-null, labels for classes will be
     *                         returned from here first before consulting
     *                         rdfService.
     * 
     * @param headerValue      optional value for first value/label pair in the
     *                         options list before appending the URIs with their
     *                         labels. May be null. Example: empty string
     * 
     * @param headerLabel      optional label for first value/label pair in the
     *                         options list before appending the URIs with their
     *                         labels. May be null. Example: "Select type"
     * 
     * @param resourceURIs     variable list of resource URI strings
     * 
     * @return empty ConstantFieldOptions list if resourceURIs is null or empty or
     *         if rdfService is null
     * @throws RDFServiceException from the supplied rdfService
     * @throws Exception           from ConstantFieldOptions constructor
     */
	public static ConstantFieldOptions buildResourceAndLabelFieldOptions(
	        RDFService rdfService, WebappDaoFactory webappDaoFactory, String headerValue, 
	        String headerLabel, String ... resourceURIs) throws Exception {	   
	   if(resourceURIs == null || resourceURIs.length == 0 || rdfService == null) {
	       return new ConstantFieldOptions();
	   }
	   List<String> options = new ArrayList<String>();
	   if(headerValue != null && headerLabel != null) {
	       options.add(headerValue);
	       options.add(headerLabel);
	   }
	   IRIFactory iriFactory = IRIFactory.iriImplementation();	   
	   for(String resourceURI : resourceURIs) {	       
	       IRI iri = iriFactory.create(resourceURI);
	       if(iri.hasViolation(false)) {
	           log.warn("Not adding invalid URI " + resourceURI 
	                   + " to field options list");
	       } else {
	           String label = getLabel(iri, rdfService, webappDaoFactory);
	           if(!StringUtils.isEmpty(label)) {
	               options.add(iri.toString());
	               options.add(label);
	           }
	       }
	   }
	   return new ConstantFieldOptions(options.toArray(
	           new String[options.size()]));
	}
	
	/**
	 * Retrieve label for iri from webappDaoFactory if available and iri is
	 * for a VClass, otherwise retrieve lowest-sorting rdfs:label for iri from 
	 * rdfService  
	 * @param iri may not be null
	 * @param rdfService may not be null
	 */
	private static String getLabel(IRI iri, RDFService rdfService, 
	        WebappDaoFactory webappDaoFactory) throws RDFServiceException {
	    // Try the WebappDaoFactory for class labels that exist only in
	    // "everytime" and do not show up in the RDFService.
	    if(webappDaoFactory != null) {
	        VClass vclass = webappDaoFactory.getVClassDao().getVClassByURI(
	                iri.toString());
	        if(vclass != null) {
	            return vclass.getLabel();
	        }
	    }
	    StringBuilder select = new StringBuilder("SELECT ?label WHERE { \n");
	    select.append("  <" + iri + "> <" + RDFS.label.getURI() + "> ?label \n");
	    select.append("} ORDER BY ?label");
        LabelConsumer labelConsumer = new LabelConsumer();
        rdfService.sparqlSelectQuery(select.toString(), labelConsumer); 
	    return labelConsumer.getLabel();
	}
	
	private static class LabelConsumer extends ResultSetConsumer {

	    private String label;
	    
        @Override
        protected void processQuerySolution(QuerySolution qsoln) {
            if(label != null) {
                return;
                // keep only the first value returned in the result set
            }
            if(qsoln.contains("label") && qsoln.get("label").isLiteral()) {
                label = qsoln.getLiteral("label").getLexicalForm();
            }
        }
	    
        public String getLabel() {
            return label;
        }
        
	}
	
}
