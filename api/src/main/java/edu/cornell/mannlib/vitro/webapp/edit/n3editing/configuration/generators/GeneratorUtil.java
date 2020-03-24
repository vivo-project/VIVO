package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ConstantFieldOptions;
import edu.cornell.mannlib.vitro.webapp.i18n.I18n;
import edu.cornell.mannlib.vitro.webapp.i18n.I18nBundle;
import edu.cornell.mannlib.vitro.webapp.i18n.selection.SelectedLocale;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;

public class GeneratorUtil {
	private static String GET_LABEL_QUERY = " "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "SELECT DISTINCT ?uri ?label  \n"
			+ "WHERE {  \n"
			+ "    ?uri <http://www.w3.org/2000/01/rdf-schema#label> ?label  \n"
			//			+ "    FILTER (lang(?label) = 'LANGUAGE' ) "
			+ "} \n"
			+ "        ORDER BY ?label \n"
			;
	/*
	 * UQAM Help to generate the labels of scrollDowm list in proper language	
	 */
	static public ConstantFieldOptions buildConstantFieldOptions(VitroRequest vreq, String DESCRIBE_QUERY) throws Exception {

		List<List<String>> options = builFieldOptionsList(vreq, DESCRIBE_QUERY);
		ConstantFieldOptions filedOptions = new  ConstantFieldOptions("" , options);	
		return filedOptions;
	}
	/*
	 * UQAM Help to generate the labels of scrollDowm list in proper language	
	 */
	static public List<List<String>> builFieldOptionsList(VitroRequest vreq, String DESCRIBE_QUERY) throws Exception {

		I18nBundle i18n = I18n.bundle(vreq);
		String i18nSelectType = i18n.text("select_type");
		String selectType = (i18nSelectType == null || i18nSelectType.isEmpty()) ? "Select type" : i18nSelectType ;
		Locale lang = SelectedLocale.getCurrentLocale(vreq);
		List<List<String>> options = new ArrayList<List<String>>();
		List<String> pair = new ArrayList<String>(2);
		pair.add("");
		pair.add(selectType);
		options.add(pair);
		RDFService rdfService = vreq.getRDFService();

		Model constructedModel = RDFServiceUtils.parseModel(
				rdfService.sparqlDescribeQuery(DESCRIBE_QUERY, RDFService.ModelSerializationFormat.N3),  
				RDFService.ModelSerializationFormat.N3);

		Query query = QueryFactory.create(GET_LABEL_QUERY.replaceAll("LANGUAGE", lang.toString())) ;
		QueryExecution qe = QueryExecutionFactory.create(query, constructedModel);
		try {
			ResultSet results = qe.execSelect();

			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String uriId = soln.getResource("uri").getURI();
				String label = soln.getLiteral("label").getLexicalForm();
				pair = new ArrayList<String>(2);
				pair.add(uriId);
				pair.add(label);
				options.add(pair);
			}
		} finally {
			qe.close();
		}
		return options;
	}
}
