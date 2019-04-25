/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesWithParent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
/**

Custom form for adding or editing a webpage associated with an individual. The primary page,
ManageWebpagesForIndividual, should forward to this page if: (a) we are adding a new page, or
(b) an edit link in the Manage Webpages view has been clicked. But right now (a) is not implemented.


*/
public class AddEditWebpageFormGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {
    public static Log log = LogFactory.getLog( AddEditWebpageFormGenerator.class );
    private static String formTemplate = "addEditWebpageForm.ftl";
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) throws Exception {
        EditConfigurationVTwo config = setupConfig(vreq, session);

        config.setUrlPatternToReturnTo(getUrlPatternToReturnTo(vreq));
        prepare(vreq, config);
        return config;
    }

    //Have broken this method down into two portions to allow for overriding of edit configuration
    //without having to copy the entire method and before prepare is called

    protected EditConfigurationVTwo setupConfig(VitroRequest vreq, HttpSession session) throws Exception{

    	EditConfigurationVTwo config = new EditConfigurationVTwo();

	    config.setTemplate(this.getTemplate());

	    initBasics(config, vreq);
	    initPropertyParameters(vreq, session, config);
	    initObjectPropForm(config, vreq);
	    String linkUri = getLinkUri(vreq);
	    String domainUri = vreq.getParameter("domainUri");
	    String vcardIndividualType = "http://www.w3.org/2006/vcard/ns#Kind";


	    config.setVarNameForSubject("subject");
	    config.setVarNameForObject("vcard");

	    config.addNewResource("vcard", DEFAULT_NS_FOR_NEW_RESOURCE);
	    config.addNewResource("link", DEFAULT_NS_FOR_NEW_RESOURCE);

	    config.setN3Required(list( this.getN3ForWebpage(), N3_FOR_URLTYPE ));
	    config.setN3Optional(list( N3_FOR_ANCHOR, N3_FOR_RANK));

	    config.addUrisInScope("webpageProperty",     list( "http://purl.obolibrary.org/obo/ARG_2000028" ));
	    config.addUrisInScope("inverseProperty",     list( "http://purl.obolibrary.org/obo/ARG_2000029" ));
	    config.addUrisInScope("linkUrlPredicate",             list( "http://www.w3.org/2006/vcard/ns#url" ));
	    config.addUrisInScope("linkLabelPredicate",  list( "http://www.w3.org/2000/01/rdf-schema#label" ));
	    config.addUrisInScope("rankPredicate",       list( core + "rank"));
	    config.addUrisInScope("vcardType",       list( vcardIndividualType ));


	    if ( config.isUpdate() ) {
	        config.addUrisInScope("link",  list( linkUri ));
	    }
	    else {
	        if ( domainUri.equals("http://xmlns.com/foaf/0.1/Person") ) {
	        vcardIndividualType = "http://www.w3.org/2006/vcard/ns#Individual";
	        }
	        else if ( domainUri.equals("http://xmlns.com/foaf/0.1/Organization") ) {
	            vcardIndividualType = "http://www.w3.org/2006/vcard/ns#Organization";
	        }
	    }
	    config.addSparqlForAdditionalUrisInScope("vcard", individualVcardQuery);

	    config.setUrisOnForm("urlType");
	    config.setLiteralsOnForm(list("url","label","rank"));

	    config.addSparqlForExistingLiteral("url",    URL_QUERY);
	    config.addSparqlForExistingLiteral("label", ANCHOR_QUERY);
	    config.addSparqlForExistingLiteral("rank",   MAX_RANK_QUERY);
	    config.addSparqlForExistingUris("urlType", URLTYPE_QUERY);

	    config.addField(new FieldVTwo().
	            setName("url").
	            setValidators(list("nonempty", "datatype:"+XSD.anyURI.toString(), "httpUrl")).
	            setRangeDatatypeUri(XSD.anyURI.toString()));

	    config.addField( new FieldVTwo().
	            setName("urlType").
	            setValidators( list("nonempty") ).
	            setOptions(
	                new ChildVClassesWithParent("http://www.w3.org/2006/vcard/ns#URL")));

	    config.addField(new FieldVTwo().
	            setName("label"));

	    config.addField(new FieldVTwo().
	            setName("rank").
	            setRangeDatatypeUri(XSD.xint.toString()));

	    config.addFormSpecificData("newRank",
	            getMaxRank( EditConfigurationUtils.getObjectUri(vreq),
	                        EditConfigurationUtils.getSubjectUri(vreq), vreq )
	                    + 1 );

	    config.addValidator(new AntiXssValidation());

	    //might be null
	    config.addFormSpecificData("subjectName", getName( config, vreq));
    	return config;
    }

    /** may be null */
    private Object getName(EditConfigurationVTwo config, VitroRequest vreq) {
        Individual ind = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(config.getSubjectUri());
        if( ind == null )
            return null;
        else
            return ind.getName();
    }

    /* ********* N3 Assertions *********** */
    static String N3_FOR_WEBPAGE =
        "?subject ?webpageProperty ?vcard . \n"+
        "?vcard ?inverseProperty ?subject . \n"+
        "?vcard a ?vcardType . \n" +
        "?vcard <http://www.w3.org/2006/vcard/ns#hasURL> ?link ."+
        "?link a <http://www.w3.org/2006/vcard/ns#URL> . \n" +
        "?link ?linkUrlPredicate ?url .";

    static String N3_FOR_URLTYPE =
        "?link a ?urlType .";

    static String N3_FOR_ANCHOR =
        "?link ?linkLabelPredicate ?label .";

    static String N3_FOR_RANK =
        "?link ?rankPredicate ?rank .";

    /* *********** SPARQL queries for existing values ************** */

    static String URL_QUERY =
        "SELECT ?urlExisting WHERE { ?link ?linkUrlPredicate ?urlExisting }";

    static String URLTYPE_QUERY =
        "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?linkClassExisting WHERE { ?link vitro:mostSpecificType ?linkClassExisting }";

    static String ANCHOR_QUERY =
        "SELECT ?labelExisting WHERE { ?link ?linkLabelPredicate ?labelExisting }";

    static String RANK_QUERY =
        "SELECT ?rankExisting WHERE { ?link ?rankPredicate ?rankExisting }";

    static String core = "http://vivoweb.org/ontology/core#";

    static String individualVcardQuery =
        "SELECT ?existingVcard WHERE { \n" +
        "?subject <http://purl.obolibrary.org/obo/ARG_2000028>  ?existingVcard . \n" +
        "}";

    /* Note on ordering by rank in sparql: if there is a non-integer value on a link, that will be returned,
     * since it's ranked highest. Preventing that would require getting all the ranks and sorting in Java,
     * throwing out non-int values.
     */
    private static String MAX_RANK_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"
        + "SELECT DISTINCT ?rank WHERE { \n"
        + "    ?subject <http://purl.obolibrary.org/obo/ARG_2000028> ?vcard . \n"
        + "    ?vcard vcard:hasURL ?link . \n"
        + "    ?link core:rank ?rank .\n"
        + "} ORDER BY DESC(?rank) LIMIT 1";

    private int getMaxRank(String objectUri, String subjectUri, VitroRequest vreq) {

        int maxRank = 0; // default value
        if (objectUri == null) { // adding new webpage
            String queryStr = QueryUtils.subUriForQueryVar(this.getMaxRankQueryStr(), "subject", subjectUri);
            log.debug("Query string is: " + queryStr);
            try {
                ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
                if (results != null && results.hasNext()) { // there is at most one result
                    QuerySolution soln = results.next();
                    RDFNode node = soln.get("rank");
                    if (node != null && node.isLiteral()) {
                        // node.asLiteral().getInt() won't return an xsd:string that
                        // can be parsed as an int.
                        int rank = Integer.parseInt(node.asLiteral().getLexicalForm());
                        if (rank > maxRank) {
                            log.debug("setting maxRank to " + rank);
                            maxRank = rank;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                log.error("Invalid rank returned from query: not an integer value.");
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        return maxRank;
    }

    protected String getTemplate() {
    	return formTemplate;
    }

    protected String getMaxRankQueryStr() {
    	return MAX_RANK_QUERY;
    }

    protected String getN3ForWebpage() {
    	return N3_FOR_WEBPAGE;
    }

	private String getUrlPatternToReturnTo(VitroRequest vreq) {
		String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
		String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
		//Also add domain and range uris if they exist to enable cancel to work properly
		String domainUri = (String) vreq.getParameter("domainUri");
		String rangeUri = (String) vreq.getParameter("rangeUri");
		String generatorName = "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.ManageWebpagesForIndividualGenerator";
		String editUrl = EditConfigurationUtils.getEditUrlWithoutContext(vreq);
		String returnPath =  editUrl + "?subjectUri=" + UrlBuilder.urlEncode(subjectUri) +
		"&predicateUri=" + UrlBuilder.urlEncode(predicateUri) +
		"&editForm=" + UrlBuilder.urlEncode(generatorName);
		if(domainUri != null && !domainUri.isEmpty()) {
			returnPath += "&domainUri=" + UrlBuilder.urlEncode(domainUri);
		}
		if(rangeUri != null && !rangeUri.isEmpty()) {
			returnPath += "&rangeUri=" + UrlBuilder.urlEncode(rangeUri);
		}
		return returnPath;

	}

	private String getLinkUri(VitroRequest vreq) {
	    String linkUri = vreq.getParameter("linkUri");

		return linkUri;
	}
}
