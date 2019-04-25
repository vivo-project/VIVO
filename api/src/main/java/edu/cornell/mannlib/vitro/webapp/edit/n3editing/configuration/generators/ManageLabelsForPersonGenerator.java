/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import static edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction.SOME_LITERAL;
import static edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction.SOME_PREDICATE;
import static edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction.SOME_URI;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.FoafNameToRdfsLabelPreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.ManageLabelsForPersonPreprocessor;
import edu.cornell.mannlib.vitro.webapp.i18n.selection.SelectedLocale;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.DataPropertyStatementTemplateModel;

/**
 * This generator is specifically for handling labels for a FOAF Person individual and is an object property form.
 *This allows the page to show all the labels for a particular individual and sets up code
 *enabling the addition of a new label.  Links on the page will allow for removal or editing of a given label.
 */
public class ManageLabelsForPersonGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {
    public static Log log = LogFactory.getLog(ManageLabelsForIndividualGenerator.class);
    private static String template = "manageLabelsForIndividual.ftl";
    private HashMap<String, List<LabelInformation>> labelsSortedByLanguage = null;
    private List<Literal> existingLabelLiterals = null;

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {

        EditConfigurationVTwo config = new EditConfigurationVTwo();
        config.setTemplate(this.getTemplate());

        initBasics(config, vreq);
        initPropertyParameters(vreq, session, config);
        //This form is technically not an object property form in the true sense
        //or a data property form because it is used to list the various labels
        //and allow for adding new labels, but since the generator will
        //be employed when the 'add' button is used, we will set this is an object property form
        //although label will mean we need to add a data property statement as well
        //URL to return to is the same page once addition is complete
        initObjectPropForm(config, vreq);


        this.setUrlToReturnTo(config, vreq);

        config.setSubjectUri(EditConfigurationUtils.getSubjectUri(vreq));

        setVarNames(config);
        //config.setDatapropKey( EditConfigurationUtils.getDataHash(vreq) );
        //Add n3, fields, etc. in the case where the user wants to add a label
        //N3 required should be empty since the addition of a label is optional in this case
        config.setN3Required(this.generateN3Required(vreq));
        config.setN3Optional(this.generateN3Optional(vreq));
        this.addNewResources(config);
        this.setUrisAndLiteralsOnForm(config, vreq);
    	this.setUrisAndLiteralsInScope(config);
        this.setFields(config, vreq, EditConfigurationUtils
				.getPredicateUri(vreq));

        //Get existing labels
        //this.initExistingLabels(config, vreq);

        //Add form specific data used to populate template
        addFormSpecificData(config, vreq);
        //This preprocessor handles getting the correct label language and putting the attribute on the label
 	   config.addEditSubmissionPreprocessor(
			   new ManageLabelsForPersonPreprocessor(config));
 	   //This will handle generating the label from the first name, middle, and last names and also make sure to associate
 	   //a language with that label
       config.addModelChangePreprocessor(new FoafNameToRdfsLabelPreprocessor());

        prepare(vreq, config);
        return config;
    }

    /**With ISF Changes**/
    //For addition of a label, with ISF changes, the name is now linked to a vcard which  in turn is linked to a "fullname" that then has first/middle/last names

    private void addNewResources(EditConfigurationVTwo config) {
    	config.addNewResource("fullName", DEFAULT_NS_FOR_NEW_RESOURCE);
        config.addNewResource("individualVcard", DEFAULT_NS_FOR_NEW_RESOURCE);
    }

    private void setUrlToReturnTo(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		editConfiguration.setUrlPatternToReturnTo(EditConfigurationUtils.getFormUrlWithoutContext(vreq));
	}

    private void setVarNames(EditConfigurationVTwo editConfiguration) {
		  editConfiguration.setVarNameForSubject("subject");
	      editConfiguration.setVarNameForPredicate("predicate");

	}


    private List<String> generateN3Required(VitroRequest vreq) {
		List<String> n3Required = new ArrayList<String>();
		return n3Required;
	}

	private List<String> generateN3Optional(VitroRequest vreq) {
		List<String> n3Optional = new ArrayList<String>();

		String personFullNameN3 = this.N3_PREFIX +
		 "?subject <http://purl.obolibrary.org/obo/ARG_2000028>  ?individualVcard . \n" +
	        "?individualVcard a <http://www.w3.org/2006/vcard/ns#Individual> . \n" +
	        "?individualVcard <http://purl.obolibrary.org/obo/ARG_2000029> ?subject . \n" +
	        "?individualVcard <http://www.w3.org/2006/vcard/ns#hasName> ?fullName . \n" +
	        "?fullName a <http://www.w3.org/2006/vcard/ns#Name> .";
		String personFirstNameN3 =
	        "?fullName <http://www.w3.org/2006/vcard/ns#givenName> ?firstName . ";
		String personLastNameN3 =
	        "?fullName <http://www.w3.org/2006/vcard/ns#familyName> ?lastName .";
		String personMiddleNameN3 = "?subject <http://vivoweb.org/ontology/core#middleName> ?middleName .";
  		n3Optional.add(personFullNameN3 + "\n " + personFirstNameN3 + "\n " + personLastNameN3);
		n3Optional.add(personMiddleNameN3);
		return n3Optional;
	}



	private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	editConfiguration.setFields(fields);

    	editConfiguration.addField(new FieldVTwo(
    			).setName("newLabelLanguage"));
    	//no validators since all of this is optional
    	//there should be error-checking client side though
    	editConfiguration.addField(new FieldVTwo().
    	        setName("firstName").
    	        setValidators(getFirstNameValidators(vreq, editConfiguration)));
    	editConfiguration.addField(new FieldVTwo().
                setName("middleName").
                setValidators(getMiddleNameValidators(vreq, editConfiguration)));

    	editConfiguration.addField(new FieldVTwo().
                setName("lastName").
                setValidators(getLastNameValidators(vreq, editConfiguration)));

    	//With ISF Changes, also include middle name

    }

	//first and last name have validators if is person is true
    private List<String> getFirstNameValidators(VitroRequest vreq, EditConfigurationVTwo config) {
		List<String> validators = new ArrayList<String>();
			validators.add("nonempty");

		return validators;
	}

    private List<String> getMiddleNameValidators(VitroRequest vreq, EditConfigurationVTwo config) {
		List<String> validators = new ArrayList<String>();

		return validators;
	}

	private List<String> getLastNameValidators(VitroRequest vreq, EditConfigurationVTwo config) {
		List<String> validators = new ArrayList<String>();
			validators.add("nonempty");

		return validators;
	}





	private void setUrisAndLiteralsOnForm(EditConfigurationVTwo config,
			VitroRequest vreq) {
		List<String> literalsOnForm = new ArrayList<String>();
		literalsOnForm.add("newLabelLanguage");
		//optional for person
		literalsOnForm.add("firstName");
		literalsOnForm.add("lastName");
		literalsOnForm.add("middleName");
		config.setLiteralsOnForm(literalsOnForm);

	}


	private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration) {
    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
    	//note that at this point the subject, predicate, and object var parameters have already been processed
    	urisInScope.put(editConfiguration.getVarNameForSubject(),
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(),
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	editConfiguration.setUrisInScope(urisInScope);
    	//Uris in scope include subject, predicate, and object var

    	editConfiguration.setLiteralsInScope(new HashMap<String, List<Literal>>());
    }

	private void initExistingLabels(EditConfigurationVTwo config,
			VitroRequest vreq) {
		this.existingLabelLiterals = this.getExistingLabels(config.getSubjectUri(), vreq);

	}


	private List<String> getExistingSortedLanguageNamesList() {
		HashSet<String> existingLanguages = new HashSet<String>();
		for(Literal l: this.existingLabelLiterals) {
			String language = l.getLanguage();
			if(!existingLanguages.contains(language)) {
				existingLanguages.add(language);
			}
		}
		List<String> sortedNames =  new ArrayList<String>(existingLanguages);
		//sort alphabetically
		Collections.sort(sortedNames);
		return sortedNames;
	}


	private void addFormSpecificData(EditConfigurationVTwo config,
			VitroRequest vreq) {
		//Get all language codes/labels in the system, and this list is sorted by language name
        List<HashMap<String, String>> locales = this.getLocales(vreq);
        //Get code to label hashmap - we use this to get the language name for the language code returned in the rdf literal
        HashMap<String, String> localeCodeToNameMap = this.getFullCodeToLanguageNameMap(locales);
		//the labels already added by the user
		ArrayList<Literal> existingLabels = this.getExistingLabels(config.getSubjectUri(), vreq);
		int numberExistingLabels = existingLabels.size();
		//existing labels keyed by language name and each of the list of labels is sorted by language name
		HashMap<String, List<LabelInformation>> existingLabelsByLanguageName = this.getLabelsSortedByLanguageName(existingLabels, localeCodeToNameMap, config, vreq);
		//Get available locales for the drop down for adding a new label, also sorted by language name
		HashSet<String> existingLanguageNames = new HashSet<String>(existingLabelsByLanguageName.keySet());
		List<HashMap<String, String>> availableLocalesForAdd = getAvailableLocales(locales, existingLanguageNames);


		//Save all locales
		 config.addFormSpecificData("selectLocaleFullList", locales);
		 //Save labels sorted by language name, untyped have "untyped" as the language name value
		 config.addFormSpecificData("labelsSortedByLanguageName", existingLabelsByLanguageName);
		 config.addFormSpecificData("selectLocale",availableLocalesForAdd);
		 config.addFormSpecificData("displayRemoveLink", (numberExistingLabels > 1));


        //How do we edit? Will need to see
        config.addFormSpecificData("deleteWebpageUrl", "/edit/primitiveDelete");


        Individual subject = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(config.getSubjectUri());
        if( subject != null && subject.getName() != null ){
            config.addFormSpecificData("subjectName", subject.getName());
        }else{
            config.addFormSpecificData("subjectName", null);
        }

  		config.addFormSpecificData("isPersonType", "true");
  		//Include whether or not editable to enable edit/remove links and add to show up
  		config.addFormSpecificData("editable", isEditable(vreq, config));
	}



	//Based on what locales have already been selected for labels, return a list of
	//locales for which new labels can be added and have these sorted by the name of the language
	private List<HashMap<String, String>> getAvailableLocales(List<HashMap<String, String>> allLocales,
			HashSet<String> existingLabelsLanguageNames) {
		List<HashMap<String, String>> availableLocales = new ArrayList<HashMap<String, String>>();
		for(HashMap<String, String> localeInfo: allLocales) {
			String languageName = (String) localeInfo.get("label");
			//If this language label is NOT in the labels sorted by language, then available
			//for selection when creating a new label
			//The assumption here is we don't want to allow the user to add a new label when a label
			//already exists in that language
			if(!"untyped".equals(languageName) && !existingLabelsLanguageNames.contains(languageName)) {
				availableLocales.add(localeInfo);
			}
		}
		//Sort list by language label and return
		availableLocales.sort(new Comparator<HashMap<String, String>>() {
            public int compare(HashMap<String, String> h1, HashMap<String, String> h2) {
                String languageName1 = (String) h1.get("label");
                String languageName2 = (String) h2.get("label");
                return languageName1.compareTo(languageName2);
            }
        });

		return availableLocales;
	}


	private Object isEditable(VitroRequest vreq, EditConfigurationVTwo config) {
		Individual individual = EditConfigurationUtils.getIndividual(vreq, config.getSubjectUri());
		AddDataPropertyStatement adps = new AddDataPropertyStatement(
				vreq.getJenaOntModel(), individual.getURI(),
				SOME_URI, SOME_LITERAL);

		AddObjectPropertyStatement aops = new AddObjectPropertyStatement(
				vreq.getJenaOntModel(), individual.getURI(),
				SOME_PREDICATE, SOME_URI);
    	return PolicyHelper.isAuthorizedForActions(vreq, adps.or(aops));
	}


	//Copied from NewIndividualFormGenerator
	public String getFOAFPersonClassURI() {
		return "http://xmlns.com/foaf/0.1/Person";
	}

	public boolean isPersonType(VitroRequest vreq, EditConfigurationVTwo config) {
		WebappDaoFactory wdf = vreq.getWebappDaoFactory();
		Boolean isPersonType = Boolean.FALSE;
		String foafPersonType = getFOAFPersonClassURI();
		List<VClass> vclasses = this.getVClasses(config, vreq);
	    if( vclasses != null ){
	    	for( VClass v: vclasses){
	    		String typeUri = v.getURI();
	    		if( foafPersonType.equals(typeUri)) {
	    			isPersonType = Boolean.TRUE;
	    			break;
	    		}
	    	}
	    }
	    return isPersonType;
	}

	//how to get the type of the individual in question
	public List<VClass> getVClasses(EditConfigurationVTwo config, VitroRequest vreq) {
		Individual subject = EditConfigurationUtils.getIndividual(vreq, config.getSubjectUri());
		//Get the vclasses appropriate for this subject
		return subject.getVClasses();
	}

	//Languages sorted by language name
	private HashMap<String, List<LabelInformation>> getLabelsSortedByLanguageName(List<Literal> labels, Map<String, String> localeCodeToNameMap, EditConfigurationVTwo config,
			VitroRequest vreq) {
		String subjectUri = config.getSubjectUri();
		String propertyUri = config.getPredicateUri();
		Property prop = new Property();
		prop.setURI(propertyUri);
		//Iterate through the labels and create a hashmap
		HashMap<String, List<LabelInformation>> labelsHash= new HashMap<String, List<LabelInformation>>();

		for(Literal l: labels) {
			String languageTag = l.getLanguage();
			String languageName = "";
			if(StringUtils.isEmpty(languageTag)) {
				languageName = "untyped";
			}
			else if(localeCodeToNameMap.containsKey(languageTag)) {
				languageName = localeCodeToNameMap.get(languageTag);
			} else {
				log.warn("This language tag " + languageTag + " does not have corresponding name in the system and was not processed");
			}

			if(!StringUtils.isEmpty(languageName)) {
				if(!labelsHash.containsKey(languageName)) {
					labelsHash.put(languageName, new ArrayList<LabelInformation>());
				}
				ArrayList<LabelInformation> labelsList = (ArrayList<LabelInformation>)labelsHash.get(languageName);
				//This should put the label in the list
				//Create label information instance with the required information
				//To generate link

				DataPropertyStatementTemplateModel dpstm = new DataPropertyStatementTemplateModel(subjectUri, prop, l,
			            template, vreq);
				labelsList.add(new LabelInformation(
						l, dpstm.getEditUrl(), dpstm.getDeleteUrl(), languageTag, languageName));
			}
		}

		//Sort each label list
		LabelInformationComparator lic = new LabelInformationComparator();
		for(String languageName: labelsHash.keySet()) {
			List<LabelInformation> labelInfo = labelsHash.get(languageName);
			labelInfo.sort(lic);
		}
		return labelsHash;

	}


	public static class LabelInformationComparator implements Comparator<LabelInformation> {

		public int compare(LabelInformation l1, LabelInformation l2) {
			return l1.getLabelStringValue().compareTo(l2.getLabelStringValue());
		}
	}


	private static String LABEL_QUERY = ""
	        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
	        + "SELECT DISTINCT ?label WHERE { \n"
	        + "    ?subject rdfs:label ?label \n"
	        + "} ORDER BY ?label";


    private ArrayList<Literal>  getExistingLabels(String subjectUri, VitroRequest vreq) {
        String queryStr = QueryUtils.subUriForQueryVar(LABEL_QUERY, "subject", subjectUri);
        log.debug("queryStr = " + queryStr);

        ArrayList<Literal>  labels = new ArrayList<Literal>();
        try {
        	//We want to get the labels for all the languages, not just the display language
            ResultSet results = QueryUtils.getLanguageNeutralQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Literal nodeLiteral = soln.get("label").asLiteral();
                labels.add(nodeLiteral);


            }
        } catch (Exception e) {
            log.error(e, e);
        }
       return labels;
}



    //Putting this into a method allows overriding it in subclasses
    protected String getEditForm() {
    	return null;
    	//return AddEditWebpageFormGenerator.class.getName();
    }


    protected String getTemplate() {
    	return template;
    }



    //get locales
    public List<HashMap<String, String>> getLocales(VitroRequest vreq) {
    	List<Locale> selectables = SelectedLocale.getSelectableLocales(vreq);
		if (selectables.isEmpty()) {
			return Collections.emptyList();
		}
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Locale currentLocale = SelectedLocale.getCurrentLocale(vreq);
		for (Locale locale : selectables) {
			try {
				list.add(buildLocaleMap(locale, currentLocale));
			} catch (FileNotFoundException e) {
				log.warn("Can't show the Locale selector for '" + locale
						+ "': " + e);
			}
		}

		return list;
    }



    public HashMap<String, String> getFullCodeToLanguageNameMap(List<HashMap<String, String>> localesList) {
    	HashMap<String, String> codeToLanguageMap = new HashMap<String, String>();
    	for(Map<String, String> locale: localesList) {
    		String code = (String) locale.get("code");
    		String label = (String) locale.get("label");
    		if(!codeToLanguageMap.containsKey(code)) {
    			codeToLanguageMap.put(code, label);
    		}
    		else {
    			log.warn("Language code " + code + " for " + label  + " was not associated in map becayse label already exists");
    		}
    	}
    	return codeToLanguageMap;
    }

    public List<String> getFullLanguagesNamesSortedList(List<Map<String, Object>> localesList) {
    	HashSet<String> languageNamesSet = new HashSet<String>();
    	for(Map<String, Object> locale: localesList) {
    		String label = (String) locale.get("label");
    		if(!languageNamesSet.contains(label)) {
    			languageNamesSet.add(label);
    		}

    	}
    	List<String> languageNames =  new ArrayList<String>(languageNamesSet);
    	Collections.sort(languageNames);
    	return languageNames;
    }

    //copied from locale selection data getter but don't need all this information
    private HashMap<String, String> buildLocaleMap(Locale locale,
			Locale currentLocale) throws FileNotFoundException {
		HashMap<String, String> map = new HashMap<String, String>();
		//Replacing the underscore with a hyphen because that is what is represented in the actual literals
		map.put("code", locale.toString().replace("_", "-"));
		map.put("label", locale.getDisplayName(currentLocale));
		return map;
	}

    //Class used to store the information needed for the template, such as the labels, their languages, their edit links
    public class LabelInformation {
    	private Literal labelLiteral = null;
    	private String editLinkURL;
    	private String deleteLinkURL;
    	private String languageCode; //languageCode
    	private String languageName;
    	public LabelInformation(Literal inputLiteral, String inputEditLinkURL, String inputDeleteLinkURL, String inputLanguageCode, String inputLanguageName) {
    		this.labelLiteral = inputLiteral;
    		this.editLinkURL = inputEditLinkURL;
    		this.deleteLinkURL = inputDeleteLinkURL;
    		this.languageCode = inputLanguageCode;
    		this.languageName = inputLanguageName;
    	}


    	public Literal getLabelLiteral() {
    		return this.labelLiteral;
    	}

    	public String getLabelStringValue() {
    		return this.labelLiteral.getString();
    	}

    	public String getEditLinkURL() {
    		return this.editLinkURL;
    	}

    	public String getDeleteLinkURL() {
    		return this.deleteLinkURL;
    	}
    	public String getLanguageCode() {
    		return this.languageCode;
    	}

    	public String getLanguageName() {
    		return this.languageName;
    	}
    }

	private String N3_PREFIX = "@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n";

}
