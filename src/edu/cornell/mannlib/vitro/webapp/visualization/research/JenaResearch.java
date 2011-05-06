package edu.cornell.mannlib.vitro.webapp.visualization.research;

import java.io.Writer;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.output.StringBuilderWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryConstants;

public class JenaResearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		String mainEntityURI = "http://vivo-trunk.indiana.edu/individual/n2185";
		String coAuthorEntityURI = "http://vivo-trunk.indiana.edu/individual/n3080";
		
		Model collaboratorModel = ModelFactory.createDefaultModel();

		collaboratorModel.setNsPrefixes(QueryConstants.getPrefixToNameSpace());
		collaboratorModel.setNsPrefix("qb", "http://purl.org/linked-data/cube#");
		collaboratorModel.setNsPrefix("know", "http://xcite.hackerceo.org/vocab/histograms#");
		collaboratorModel.setNsPrefix("xcite", "http://xcite.hackerceo.org/instance/"
												+ UUID.randomUUID().toString() + "#");

		Property rdfType = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("rdf") + "type");
		Property rdfsLabel = collaboratorModel.createProperty(collaboratorModel.getNsPrefixURI("rdfs") + "label");

		Resource knowCollaborator = collaboratorModel
										.createProperty(collaboratorModel.getNsPrefixURI("know") + "collaborator");
		
		knowCollaborator.addProperty(
				rdfType, 
				collaboratorModel.getNsPrefixURI("qb") + "DimensionProperty");
		
		Resource knowTally = collaboratorModel
								.createProperty(collaboratorModel.getNsPrefixURI("know") + "tally");
		knowTally.addProperty(
				rdfType, 
				collaboratorModel.getNsPrefixURI("qb") + "DimensionProperty");	
		
		Resource knowBehindTally = collaboratorModel
										.createProperty(collaboratorModel.getNsPrefixURI("know") + "behind-tally");
		
		knowBehindTally.addProperty(
				rdfType, 
				collaboratorModel.getNsPrefixURI("qb") + "DimensionProperty");
		
		////
		
		Resource collaborator = collaboratorModel.createResource(coAuthorEntityURI);
		collaborator.addProperty(rdfType, collaboratorModel.getNsPrefixURI("foaf") + "Person");
		collaborator.addProperty(rdfsLabel, "Tendulkar, Sachin");
		
		Resource observation = collaboratorModel.createResource(collaboratorModel.getNsPrefixURI("xcite") + 1);
			
		observation.addProperty(
				rdfType, 
				collaboratorModel.getNsPrefixURI("qb") + "Observation");
		
		observation.addProperty(
				(Property) knowCollaborator, 
				collaborator);
		
		observation.addProperty(
				(Property) knowTally, 
				"2");
		
		Resource publication = collaboratorModel.createResource("http://vivo-trunk.indiana.edu/individual/PUB1");
		
		observation.addProperty(
				(Property) knowBehindTally, 
				publication);
		
		publication = collaboratorModel.createResource("http://vivo-trunk.indiana.edu/individual/PUB2");
		
		observation.addProperty(
				(Property) knowBehindTally, 
				publication);
		
		Writer newWrite = new StringBuilderWriter();
		
		collaboratorModel.write(newWrite);
		
		System.out.println(" >>> " + newWrite.toString());
		

	}

}
