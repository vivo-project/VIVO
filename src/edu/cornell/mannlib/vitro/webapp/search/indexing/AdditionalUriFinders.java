/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.search.indexing;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.search.beans.StatementToURIsToUpdate;

/**
 * Make a list of StatementToURIsToUpdate objects for use by the
 * IndexBuidler.
 */
public class AdditionalUriFinders {

	public static List<StatementToURIsToUpdate> getList(OntModel jenaOntModel,
			IndividualDao indDao) {
		List<StatementToURIsToUpdate> uriFinders = new ArrayList<>();
		uriFinders.add(new AdditionalURIsForDataProperties());
		uriFinders.add(new AdditionalURIsForObjectProperties(jenaOntModel));
		uriFinders.add(new AdditionalURIsForContextNodes(jenaOntModel));
		uriFinders.add(new AdditionalURIsForTypeStatements());
		uriFinders.add(new URIsForClassGroupChange(indDao));
		return uriFinders;
	}

}
