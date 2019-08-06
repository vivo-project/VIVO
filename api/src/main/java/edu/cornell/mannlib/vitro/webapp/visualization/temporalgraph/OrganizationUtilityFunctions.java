/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.temporalgraph;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.visualization.constants.QueryFieldLabels;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Entity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.GenericQueryMap;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.GenericQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;

public class OrganizationUtilityFunctions {

	public static String getHighestLevelOrganizationURI(ResultSet resultSet, Map<String, String> fieldLabelToOutputFieldLabel) {
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.nextSolution();

			RDFNode organizationNode = solution.get(fieldLabelToOutputFieldLabel.get("organization"));

			if (organizationNode != null) {
				return organizationNode.toString();
			}
		}

		return "";
	}

	public static String getHighestLevelOrganizationURI(Log log, Dataset dataset)
			throws MalformedQueryParametersException {

		Map<String, String> fieldLabelToOutputFieldLabel = new HashMap<String, String>();
		fieldLabelToOutputFieldLabel.put("organization",
				QueryFieldLabels.ORGANIZATION_URL);
		fieldLabelToOutputFieldLabel.put("organizationLabel",
				QueryFieldLabels.ORGANIZATION_LABEL);

		String aggregationRules = "(count(?organization) AS ?numOfChildren)";

		String whereClause = "?organization rdf:type foaf:Organization ;"
						+ " rdfs:label ?organizationLabel . \n"
				+ "OPTIONAL { ?organization <http://purl.obolibrary.org/obo/BFO_0000051> ?subOrg . \n"
			    + "           ?subOrg rdf:type foaf:Organization } . \n"
				+ "OPTIONAL { ?organization <http://purl.obolibrary.org/obo/BFO_0000050> ?parent . \n"
		    + "               ?parent rdf:type foaf:Organization } . \n"
				+ "FILTER ( !bound(?parent) ). \n";

		String groupOrderClause = "GROUP BY ?organization ?organizationLabel \n"
				+ "ORDER BY DESC(?numOfChildren)\n" + "LIMIT 1\n";

		QueryRunner<ResultSet> highestLevelOrganizationQueryHandler = new GenericQueryRunner(
				fieldLabelToOutputFieldLabel, aggregationRules, whereClause,
				groupOrderClause, dataset);

		String highestLevelOrgURI = OrganizationUtilityFunctions
				.getHighestLevelOrganizationURI(
						highestLevelOrganizationQueryHandler.getQueryResult(),
						fieldLabelToOutputFieldLabel);
		return highestLevelOrgURI;
	}

	public static String getEntityLabelFromDAO(VitroRequest vitroRequest,
			String entityURI) {

		IndividualDao iDao = vitroRequest.getWebappDaoFactory().getIndividualDao();
        Individual ind = iDao.getIndividualByURI(entityURI);

        String organizationLabel = "Unknown Organization";

        if (ind != null) {
        	organizationLabel = ind.getName();
        }
		return organizationLabel;
	}

	public static String getStaffProvidedOrComputedHighestLevelOrganization(Log log,
			Dataset dataset, VitroRequest vitroRequest)
			throws MalformedQueryParametersException {

		String staffProvidedHighestLevelOrganization = ConfigurationProperties.getBean(vitroRequest)
					.getProperty("visualization.topLevelOrg");

		/*
		 * First checking if the staff has provided highest level organization in runtime.properties
		 * if so use to temporal graph vis.
		 */
		if (StringUtils.isNotBlank(staffProvidedHighestLevelOrganization)) {

			/*
			 * To test for the validity of the URI submitted.
			 */
			IRIFactory iRIFactory = IRIFactory.jenaImplementation();
			IRI iri = iRIFactory.create(staffProvidedHighestLevelOrganization);


			if (!iri.hasViolation(false)) {
		    	return staffProvidedHighestLevelOrganization;
		    }
		}

		/*
		 * If the provided value was not proper compute it yourself.
		 * */
		return OrganizationUtilityFunctions.getHighestLevelOrganizationURI(log, dataset);
	}

	public static Entity mergeEntityIfShareSameURI(Entity entityA, Entity entityB) {

		if (StringUtils.equalsIgnoreCase(entityA.getEntityURI(), entityB.getEntityURI())) {

			Entity mergedEntity = new Entity(entityA.getEntityURI());

			if (StringUtils.isNotBlank(entityA.getEntityLabel())) {

				mergedEntity.setEntityLabel(entityA.getEntityLabel());

			} else if (StringUtils.isNotBlank(entityB.getEntityLabel())) {

				mergedEntity.setEntityLabel(entityB.getEntityLabel());
			}

			mergedEntity.addSubEntitities(entityA.getSubEntities());
			mergedEntity.addSubEntitities(entityB.getSubEntities());

			mergedEntity.addParents(entityA.getParents());
			mergedEntity.addParents(entityB.getParents());

			return mergedEntity;

		} else {
			return null;
		}

	}

}
