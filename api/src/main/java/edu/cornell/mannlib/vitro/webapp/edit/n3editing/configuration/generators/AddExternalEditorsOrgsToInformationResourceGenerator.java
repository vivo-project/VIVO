/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
//import edu.cornell.mannlib.vitro.webapp.edit.n3editing.FirstAndLastNameValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.PublicationHasAuthorValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
//import edu.cornell.mannlib.vitro.webapp.i18n.I18n;

/**
 * This is a slightly unusual generator that is used by Manage Editors on
 * information resources.
 *
 * It is intended to always be an add, and never an update.
 */
public class AddExternalEditorsOrgsToInformationResourceGenerator extends VivoBaseGenerator implements EditConfigurationGenerator {
    public static Log log = LogFactory.getLog(AddExternalEditorsOrgsToInformationResourceGenerator.class);

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) {
        EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
        initBasics(editConfiguration, vreq);
        initPropertyParameters(vreq, session, editConfiguration);

        //Overriding URL to return to
        setUrlToReturnTo(editConfiguration, vreq);

        //set variable names
        editConfiguration.setVarNameForSubject("infoResource");
        editConfiguration.setVarNameForPredicate("predicate");
        editConfiguration.setVarNameForObject("editorshipUri");

        // Required N3
        editConfiguration.setN3Required( list( getN3NewEditorship() ) );

        // Optional N3
        editConfiguration.setN3Optional( generateN3Optional());

		// Changes here for creating vcards for external editors
        editConfiguration.addNewResource("editorshipUri", DEFAULT_NS_TOKEN);
        //editConfiguration.addNewResource("newPerson", DEFAULT_NS_TOKEN);
		editConfiguration.addNewResource("newOrg", DEFAULT_NS_TOKEN);
        editConfiguration.addNewResource("vcardPerson", DEFAULT_NS_TOKEN);
        editConfiguration.addNewResource("vcardName", DEFAULT_NS_TOKEN);

        //In scope
        setUrisAndLiteralsInScope(editConfiguration, vreq);

        //on Form
        setUrisAndLiteralsOnForm(editConfiguration, vreq);

        //Sparql queries
        setSparqlQueries(editConfiguration, vreq);

        //set fields
        setFields(editConfiguration, vreq, EditConfigurationUtils.getPredicateUri(vreq));

        //template file
        editConfiguration.setTemplate("addExternalEditorsOrgsToInformationResource.ftl");
        //add validators
		editConfiguration.addValidator(new PublicationHasAuthorValidator());
		//Added here the FirstAndLastNameValidator
		//editConfiguration.addValidator(new FirstAndLastNameValidator("personUri"));

        //Adding additional data, specifically edit mode
        addFormSpecificData(editConfiguration, vreq);

        editConfiguration.addValidator(new AntiXssValidation());

        //NOITCE this generator does not run prepare() since it
        //is never an update and has no SPARQL for existing

        return editConfiguration;
    }

	private void setUrlToReturnTo(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		editConfiguration.setUrlPatternToReturnTo(EditConfigurationUtils.getFormUrlWithoutContext(vreq));
	}

	/***N3 strings both required and optional***/

	public String getN3PrefixString() {
		return "@prefix core: <" + vivoCore + "> .\n" +
		 "@prefix foaf: <" + foaf + "> .  \n"   ;
	}

	private String getN3NewEditorship() {
		return getN3PrefixString() +
		"?editorshipUri a core:Editorship ;\n" +
        "  core:relates ?infoResource .\n" +
        "?infoResource core:relatedBy ?editorshipUri .";
	}

	private String getN3EditorshipRank() {
		return getN3PrefixString() +
        "?editorshipUri core:rank ?rank .";
	}

	//first name, middle name, last name, and new perseon for new editor being created, and n3 for existing person
	//if existing person selected as editor
	public List<String> generateN3Optional() {
		return list(
		        getN3NewPersonFirstName() ,
                getN3NewPersonMiddleName(),
                getN3NewPersonLastName(),
                getN3NewPerson(),
                getN3EditorshipRank(),
                getN3ForExistingPerson(),
				getN3NewOrg(),
                getN3ForExistingOrg());
	}

	// Changes here for creating vcards for external editors
	private String getN3NewPersonFirstName() {
		return getN3PrefixString() +
            "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
            //"?newPerson <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardPerson . \n" +
            //"?vcardPerson <http://purl.obolibrary.org/obo/ARG_2000029>  ?newPerson . \n" +
            "?vcardPerson a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
            "?vcardPerson vcard:hasName  ?vcardName . \n" +
            "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n" +
            "?vcardName vcard:givenName ?firstName .";
	}

	private String getN3NewPersonMiddleName() {
		return getN3PrefixString() +
            "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
            //"?newPerson <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardPerson . \n" +
            //"?vcardPerson <http://purl.obolibrary.org/obo/ARG_2000029>  ?newPerson . \n" +
            "?vcardPerson a vcard:Individual . \n" +
            "?vcardPerson vcard:hasName  ?vcardName . \n" +
            "?vcardName a vcard:Name . \n" +
            "?vcardName <http://vivoweb.org/ontology/core#middleName> ?middleName .";
	}

	private String getN3NewPersonLastName() {
		return getN3PrefixString() +
            "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
            //"?newPerson <http://purl.obolibrary.org/obo/ARG_2000028>  ?vcardPerson . \n" +
            //"?vcardPerson <http://purl.obolibrary.org/obo/ARG_2000029>  ?newPerson . \n" +
            "?vcardPerson a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
            "?vcardPerson vcard:hasName  ?vcardName . \n" +
            "?vcardName a <http://www.w3.org/2006/vcard/ns#Name> . \n" +
            "?vcardName vcard:familyName ?lastName .";
	}
	
	// Changes here for creating vcards for external editors
	private String getN3NewPerson() {
		return  getN3PrefixString() +
        "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .  \n" +
        //"?newPerson a foaf:Person ;\n" +
		"?vcardPerson a vcard:Individual ;\n" +
        "<" + RDFS.label.getURI() + "> ?label .\n" +
        "?editorshipUri core:relates ?vcardPerson .\n" +
        "?vcardPerson core:relatedBy ?editorshipUri . ";
	}

	private String getN3ForExistingPerson() {
		return getN3PrefixString() +
		"?editorshipUri core:relates ?personUri .\n" +
		"?personUri core:relatedBy ?editorshipUri .";
	}

	private String getN3NewOrg() {
		return  getN3PrefixString() +
        "?newOrg a foaf:Organization ;\n" +
        "<" + RDFS.label.getURI() + "> ?orgName .\n" +
        "?editorshipUri core:relates ?newOrg .\n" +
        "?newOrg core:relatedBy ?editorshipUri . ";
	}

	private String getN3ForExistingOrg() {
		return getN3PrefixString() +
		"?editorshipUri core:relates ?orgUri .\n" +
		"?orgUri core:relatedBy ?editorshipUri .";
	}

	/**  Get new resources	 */
	//A new editorship uri will always be created when an editor is added
	//A new person may be added if a person not in the system will be added as editor
	 private Map<String, String> generateNewResources(VitroRequest vreq) {


			HashMap<String, String> newResources = new HashMap<String, String>();
			newResources.put("editorshipUri", DEFAULT_NS_TOKEN);
			newResources.put("newPerson", DEFAULT_NS_TOKEN);
			newResources.put("vcardPerson", DEFAULT_NS_TOKEN);
			newResources.put("vcardName", DEFAULT_NS_TOKEN);
			newResources.put("newOrg", DEFAULT_NS_TOKEN);
			return newResources;
		}

	/** Set URIS and Literals In Scope and on form and supporting methods	 */
    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	//Uris in scope always contain subject and predicate
    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
    	urisInScope.put(editConfiguration.getVarNameForSubject(),
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(),
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	editConfiguration.setUrisInScope(urisInScope);
    	//no literals in scope
    }

    public void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();
    	//If an existing person is being used as an editor, need to get the person uri
    	urisOnForm.add("personUri");
		urisOnForm.add("orgUri");
    	editConfiguration.setUrisOnform(urisOnForm);

    	//for person who is not in system, need to add first name, last name and middle name
    	//Also need to store editorship rank and label of editor
    	List<String> literalsOnForm = list("firstName",
    			"middleName",
    			"lastName",
    			"rank",
				"orgName",
    			"label");
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }

    /** Set SPARQL Queries and supporting methods. */
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
        //Sparql queries are all empty for existing values
    	//This form is different from the others that it gets multiple editors on the same page
    	//and that information will be queried and stored in the additional form specific data
        HashMap<String, String> map = new HashMap<String, String>();
    	editConfiguration.setSparqlForExistingUris(new HashMap<String, String>());
    	editConfiguration.setSparqlForExistingLiterals(new HashMap<String, String>());
    	editConfiguration.setSparqlForAdditionalUrisInScope(new HashMap<String, String>());
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    }

    /**
	 *
	 * Set Fields and supporting methods
	 */

	public void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	setLabelField(editConfiguration);
    	setFirstNameField(editConfiguration);
    	setMiddleNameField(editConfiguration);
    	setLastNameField(editConfiguration);
    	setRankField(editConfiguration);
    	setPersonUriField(editConfiguration);
		setOrgUriField(editConfiguration);
    	setOrgNameField(editConfiguration);
    }

	private void setLabelField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("label").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}


	private void setFirstNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("firstName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}


	private void setMiddleNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("middleName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}

	private void setLastNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("lastName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}

	private void setRankField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("rank").
				setValidators(list("nonempty")).
				setRangeDatatypeUri(XSD.xint.toString())
				);
	}


	private void setPersonUriField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("personUri")
				//.setObjectClassUri(personClass)
				);
	}

	private void setOrgUriField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("orgUri")
				//.setObjectClassUri(orgClass)
				);
	}

	private void setOrgNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("orgName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
	}

	//Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		//Get the existing editorships
		formSpecificData.put("existingEditorInfo", getExistingEditorships(editConfiguration.getSubjectUri(), vreq));
		formSpecificData.put("newRank", getMaxRank(editConfiguration.getSubjectUri(), vreq) + 1);
		formSpecificData.put("rankPredicate", "http://vivoweb.org/ontology/core#rank");
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	// Changes here for external editors
	private static String EDITORSHIPS_MODEL = " \n"
			+ "PREFIX core: <http://vivoweb.org/ontology/core#>\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
			+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"
			+ "CONSTRUCT\n"
			+ "{\n"
			+ "    ?subject core:relatedBy ?editorshipURI .\n"
			+ "    ?editorshipURI a core:Editorship .\n"
			+ "    ?editorshipURI core:relates ?editorURI .\n"
			+ "    ?editorshipURI core:rank ?rank.\n"
			+ "    ?editorURI a ?type .\n"
			+ "    ?editorURI rdfs:label ?editorName .\n"
			+ "	   ?editorURI vcard:hasName ?vName .\n"
			+ "    ?vName vcard:givenName ?firstName .\n"
			+ "    ?vName vcard:familyName ?lastName .\n"
			+ "    ?vName core:middleName ?middleName .\n"
			+ "}\n"
			+ "WHERE\n"
			+ "{\n"
			+ "    {\n"
			+ "        ?subject core:relatedBy ?editorshipURI .\n"
			+ "        ?editorshipURI a core:Editorship .\n"
			+ "        ?editorshipURI core:relates ?editorURI .\n"
			+ "        ?editorURI a foaf:Agent .\n"
			+ "		   ?editorURI a ?type .\n"
			+ "    }\n"
			+ "    UNION\n"
			+ "    {\n"
			+ "        ?subject core:relatedBy ?editorshipURI .\n"
			+ "        ?editorshipURI a core:Editorship .\n"
			+ "        ?editorshipURI core:relates ?editorURI .\n"
			+ "        ?editorURI a foaf:Agent .\n"
			+ "        ?editorURI rdfs:label ?editorName .\n"
			+ "    }\n"
			+ "    UNION\n"
			+ "    {\n"
			+ "        ?subject core:relatedBy ?editorshipURI .\n"
			+ "        ?editorshipURI a core:Editorship .\n"
			+ "        ?editorshipURI core:rank ?rank.\n"
			+ "    }\n"
			+ "    UNION\n"
			+ "    {\n"
			+ "        ?subject core:relatedBy ?editorshipURI .\n"
			+ "        ?editorshipURI a core:Editorship .\n"
			+ "        ?editorshipURI core:relates ?editorURI .\n"
			+ "        ?editorURI a vcard:Individual .\n"
			+ "        ?editorURI a ?type .\n"
			+ "        ?editorURI vcard:hasName ?vName .\n"
			+ "        ?vName vcard:givenName ?firstName .\n"
			+ "        ?vName vcard:familyName ?lastName .\n"
			+ "    }\n"
			+ "    UNION\n"
			+ "    {\n"
			+ "         ?subject core:relatedBy ?editorshipURI .\n"
			+ "         ?editorshipURI a core:Editorship .\n"
			+ "         ?editorshipURI core:relates ?editorURI .\n"
			+ "         ?editorURI a vcard:Individual .\n"
			+ "         ?editorURI a ?type .\n"
			+ "         ?editorURI vcard:hasName ?vName .\n"
			+ "         ?vName core:middleName ?middleName .\n"
			+ "    }\n"
			+ "}\n";
    // Changes here for external editors
    private static String EDITORSHIPS_QUERY = " \n"
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
		+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#> \n"
        + "SELECT ?editorshipURI (REPLACE(STR(?editorshipURI),\"^.*(#)(.*)$\", \"$2\") AS ?editorshipName) ?editorURI ?editorName ?rank \n"
        + "WHERE { { \n"
        + "?subject core:relatedBy ?editorshipURI . \n"
        + "?editorshipURI a core:Editorship . \n"
        + "?editorshipURI core:relates ?editorURI . \n"
        + "?editorURI a foaf:Agent . \n"
        + "OPTIONAL { ?editorURI rdfs:label ?editorName } \n"
        + "OPTIONAL { ?editorshipURI core:rank ?rank } \n"
		+ "} UNION {  \n"
	    + "	 ?subject core:relatedBy ?editorshipURI .  \n"
	    + "	 ?editorshipURI a core:Editorship .  \n"
	    + "	 ?editorshipURI core:relates ?editorURI .  \n"
	    + "	 ?editorURI a vcard:Individual .  \n"
	    + "	 ?editorURI vcard:hasName ?vName . \n"
	    + "	 ?vName vcard:givenName ?firstName . \n"
	    + "	 ?vName vcard:familyName ?lastName . \n"
	    + "	 OPTIONAL { ?vName core:middleName ?middleName . } \n"
	    + "	 OPTIONAL { ?editorshipURI core:rank ?rank }  \n"
	    + "	 bind ( COALESCE(?firstName, \"\") As ?firstName1) . \n"
	    + "	 bind ( COALESCE(?middleName, \"\") As ?middleName1) . \n"
	    + "	 bind ( COALESCE(?lastName, \"\") As ?lastName1) . \n"
	    + "	 bind (concat(str(str(?lastName1) + \", \"),str(str(?middleName1) + \" \"),str(?firstName1)) as ?editorName) . \n"
        + "} } ORDER BY ?rank";


    private List<EditorshipInfo> getExistingEditorships(String subjectUri, VitroRequest vreq) {
		RDFService rdfService = vreq.getRDFService();

		List<Map<String, String>> editorships = new ArrayList<Map<String, String>>();
		try {
			String constructStr = QueryUtils.subUriForQueryVar(EDITORSHIPS_MODEL, "subject", subjectUri);

			Model constructedModel = ModelFactory.createDefaultModel();
			rdfService.sparqlConstructQuery(constructStr, constructedModel);

			String queryStr = QueryUtils.subUriForQueryVar(this.getEditorshipsQuery(), "subject", subjectUri);
			log.debug("Query string is: " + queryStr);

			QueryExecution qe = QueryExecutionFactory.create(queryStr, constructedModel);
			try {
				ResultSet results = qe.execSelect();
				while (results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					RDFNode node = soln.get("editorshipURI");
					if (node.isURIResource()) {
						editorships.add(QueryUtils.querySolutionToStringValueMap(soln));
					}
				}
			} finally {
				qe.close();
			}
        } catch (Exception e) {
            log.error(e, e);
        }
        log.debug("editorships = " + editorships);
        return getEditorshipInfo(editorships);
    }

    private static String MAX_RANK_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "SELECT DISTINCT ?rank WHERE { \n"
        + "    ?subject core:relatedBy ?editorship . \n"
        + "    ?editorship a core:Editorship . \n"
        + "    ?editorship core:rank ?rank .\n"
        + "} ORDER BY DESC(?rank) LIMIT 1";

    private int getMaxRank(String subjectUri, VitroRequest vreq) {

        int maxRank = 0; // default value
        String queryStr = QueryUtils.subUriForQueryVar(this.getMaxRankQueryStr(), "subject", subjectUri);
        log.debug("maxRank query string is: " + queryStr);
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
        log.debug("maxRank is: " + maxRank);
        return maxRank;
    }

	private List<EditorshipInfo> getEditorshipInfo(
			List<Map<String, String>> editorships) {
		List<EditorshipInfo> info = new ArrayList<EditorshipInfo>();
	 	String editorshipUri =  "";
	 	String editorshipName = "";
	 	String editorUri = "";
	 	String editorName = "";

		for ( Map<String, String> editorship : editorships ) {
		    for (Entry<String, String> entry : editorship.entrySet() ) {
		            if ( entry.getKey().equals("editorshipURI") ) {
		                editorshipUri = entry.getValue();
		            }
		            else if ( entry.getKey().equals("editorshipName") ) {
		                editorshipName = entry.getValue();
		            }
		            else if ( entry.getKey().equals("editorURI") ) {
		                editorUri = entry.getValue();
		            }
		            else if ( entry.getKey().equals("editorName") ) {
		                editorName = entry.getValue();
		            }
			 }

			 EditorshipInfo aaInfo = new EditorshipInfo(editorshipUri, editorshipName, editorUri, editorName);
		    info.add(aaInfo);
		 }
		 log.debug("info = " + info);
		 return info;
	}

	//This is the information about editors the form will require
	public class EditorshipInfo {
		//This is the editorship node information
		private String editorshipUri;
		private String editorshipName;
		//Editor information for editorship node
		private String editorUri;
		private String editorName;

		public EditorshipInfo(String inputEditorshipUri,
				String inputEditorshipName,
				String inputEditorUri,
				String inputEditorName) {
			editorshipUri = inputEditorshipUri;
			editorshipName = inputEditorshipName;
			editorUri = inputEditorUri;
			editorName = inputEditorName;

		}

		//Getters - specifically required for Freemarker template's access to POJO
		public String getEditorshipUri() {
			return editorshipUri;
		}

		public String getEditorshipName() {
			return editorshipName;
		}

		public String getEditorUri() {
			return editorUri;
		}

		public String getEditorName() {
			return editorName;
		}
	}

	static final String DEFAULT_NS_TOKEN=null; //null forces the default NS

    protected String getMaxRankQueryStr() {
    	return MAX_RANK_QUERY;
    }

    protected String getEditorshipsQuery() {
    	return EDITORSHIPS_QUERY;
    }

}
