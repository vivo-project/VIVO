/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
/*
 * This preprocessor is used to set the language attribute on the label based on the user selection
 * on the manage labels page when adding a new label.
 */
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;

public class ManageLabelsForPersonPreprocessor extends ManageLabelsForIndividualPreprocessor {




	public ManageLabelsForPersonPreprocessor(EditConfigurationVTwo editConfig) {
		super(editConfig);

	}

	@Override
	public void preprocess(MultiValueEditSubmission inputSubmission, VitroRequest vreq) {
		//Use the ManageLabelsForIndividualPreprocessor in addition to this code specific for person
		super.preprocess(inputSubmission, vreq);
		//First name and last name would also have a language selected so make sure those literals are also
		//correctly typed
		//Middle name is optional
		if(inputSubmission.hasLiteralValue("firstName") && inputSubmission.hasLiteralValue("lastName") && inputSubmission.hasLiteralValue("newLabelLanguage")) {
			Map<String, List<Literal>> literalsFromForm = inputSubmission.getLiteralsFromForm();
			List<Literal> newLabelLanguages = literalsFromForm.get("newLabelLanguage");
			List<Literal> firstNames = literalsFromForm.get("firstName");
			List<Literal> lastNames = literalsFromForm.get("lastName");
			List<Literal> middleNames = new ArrayList<Literal>();
			if(inputSubmission.hasLiteralValue("middleName")) {
				middleNames = literalsFromForm.get("middleName");
			}


			//Expecting only one language
			if(firstNames.size() > 0 && lastNames.size() > 0 && newLabelLanguages.size() > 0) {
				Literal newLabelLanguage = newLabelLanguages.get(0);
				Literal firstNameLiteral = firstNames.get(0);
				Literal lastNameLiteral = lastNames.get(0);

				//Get the string
				String lang = this.getLanguage(newLabelLanguage.getString());
				String firstNameValue = firstNameLiteral.getString();
				String lastNameValue = lastNameLiteral.getString();

				//Now add the language category to the literal
				Literal firstNameWithLanguage = inputSubmission.createLiteral(firstNameValue,
						null,
						lang);
				Literal lastNameWithLanguage = inputSubmission.createLiteral(lastNameValue,
						null,
						lang);

				firstNames = new ArrayList<Literal>();
				lastNames = new ArrayList<Literal>();
				firstNames.add(firstNameWithLanguage);
				lastNames.add(lastNameWithLanguage);
				//replace the label with one with language, again assuming only one label being returned
				literalsFromForm.put("firstName", firstNames);
				literalsFromForm.put("lastName", lastNames);

				//Middle name handling
				if(middleNames.size() > 0) {
					Literal middleNameLiteral = middleNames.get(0);
					String middleNameValue = middleNameLiteral.getString();
					Literal middleNameWithLanguage = inputSubmission.createLiteral(middleNameValue,
							null,
							lang);
					middleNames = new ArrayList<Literal>();
					middleNames.add(middleNameWithLanguage);
					literalsFromForm.put("middleName", middleNames);
				}

				//Set literals
				inputSubmission.setLiteralsFromForm(literalsFromForm);
			}
		}

	}


}
