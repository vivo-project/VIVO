/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasProfile;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasProxyEditingRights;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.IsRootUser;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.DataGetter;
import edu.cornell.mannlib.vitro.webapp.utils.sparqlrunner.ResultSetParser;
import edu.cornell.mannlib.vitro.webapp.utils.sparqlrunner.SparqlQueryRunner;
import edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController;

/**
 * This data getter should be assigned to the template that renders the list
 * view for ORCID IDs.
 *
 * Find out whether the user is authorized to confirm the ORCID IDs on this
 * page. Find the list of ORCID IDs, and whether each has already been
 * confirmed.
 *
 * The information is stored in the values map like this:
 *
 * <pre>
 *    orcidInfo = map {
 *        authorizedToConfirm: boolean
 *        orcids: map of String to boolean [
 *            orcid: String
 *            confirm: boolean
 *        ]
 *    }
 * </pre>
 */
public class OrcidIdDataGetter implements DataGetter {
	private static final Log log = LogFactory.getLog(OrcidIdDataGetter.class);

	private static final Map<String, Object> EMPTY_RESULT = Collections
			.emptyMap();
	public static final String ORCID_ID = "http://vivoweb.org/ontology/core#orcidId";
	public static final String ORCID_IS_CONFIRMED = "http://vivoweb.org/ontology/core#confirmedOrcidId";
	private static final String QUERY_TEMPLATE = "SELECT ?orcid ?confirmed \n"
			+ "WHERE { \n" //
			+ "    <%s> <%s> ?orcid . \n" //
			+ "    OPTIONAL { \n" //
			+ "       ?orcid <%s> ?confirmed . \n" //
			+ "       }  \n" //
			+ "}\n";

	private final VitroRequest vreq;

	public OrcidIdDataGetter(VitroRequest vreq) {
		this.vreq = vreq;
	}

	@Override
	public Map<String, Object> getData(Map<String, Object> valueMap) {
		try {
			String individualUri = findIndividualUri(valueMap);
			if (individualUri == null) {
				return EMPTY_RESULT;
			}

			boolean isAuthorizedToConfirm = figureIsAuthorizedtoConfirm(individualUri);
			List<OrcidInfo> orcids = runSparqlQuery(individualUri);
			return buildMap(isAuthorizedToConfirm, orcids, individualUri);
		} catch (Exception e) {
			log.warn("Failed to get orcID information", e);
			return EMPTY_RESULT;
		}
	}

	private String findIndividualUri(Map<String, Object> valueMap) {
		try {
			String uri = (String) valueMap.get("individualURI");

			if (uri == null) {
				log.warn("valueMap has no individualURI. Keys are: "
						+ valueMap.keySet());
				return null;
			} else {
				return uri;
			}
		} catch (Exception e) {
			log.debug("has a problem finding the individualURI", e);
			return null;
		}

	}

	/**
	 * You are authorized to confirm an orcId only if you are a self-editor or
	 * root.
	 */
	private boolean figureIsAuthorizedtoConfirm(String individualUri) {
		IdentifierBundle ids = RequestIdentifiers.getIdBundleForRequest(vreq);
		boolean isSelfEditor = HasProfile.getProfileUris(ids).contains(
				individualUri);
		boolean isProxyEditor = HasProxyEditingRights.getProxiedPageUris(ids)
				.contains(individualUri);
		boolean isRoot = IsRootUser.isRootUser(ids);
		return isRoot || isProxyEditor || isSelfEditor;
	}

	private List<OrcidInfo> runSparqlQuery(String individualUri) {
		String queryStr = String.format(QUERY_TEMPLATE, individualUri,
				ORCID_ID, ORCID_IS_CONFIRMED);
		return SparqlQueryRunner
				.createSelectQueryContext(vreq.getJenaOntModel(), queryStr)
				.execute().parse(new OrcidResultParser());
	}

	private Map<String, Object> buildMap(boolean isAuthorizedToConfirm,
			List<OrcidInfo> orcids, String individualUri) {
		Map<String, Boolean> confirmationMap = new HashMap<>();
		for (OrcidInfo oInfo : orcids) {
			confirmationMap.put(oInfo.getOrcid(), oInfo.isConfirmed());
		}

		Map<String, Object> orcidInfoMap = new HashMap<>();
		orcidInfoMap.put("authorizedToConfirm", isAuthorizedToConfirm);
		orcidInfoMap.put("orcidUrl", UrlBuilder.getUrl(
				OrcidIntegrationController.PATH_DEFAULT, "individualUri",
				individualUri));
		orcidInfoMap.put("orcids", confirmationMap);

		Map<String, Object> map = new HashMap<>();
		map.put("orcidInfo", orcidInfoMap);

		log.debug("Returning these values:" + map);
		return map;
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	/**
	 * Parse the results of the SPARQL query.
	 */
	private static class OrcidResultParser extends ResultSetParser<List<OrcidInfo>> {
		@Override
		protected List<OrcidInfo> defaultValue() {
			return Collections.emptyList();
		}

		@Override
		protected List<OrcidInfo> parseResults(String queryStr,
				ResultSet results) {
			List<OrcidInfo> orcids = new ArrayList<>();

			while (results.hasNext()) {
				try {
					QuerySolution solution = results.next();
					Resource orcid = solution.getResource("orcid");
					RDFNode cNode = solution.get("confirmed");
					log.debug("Result is orcid=" + orcid + ", confirmed="
							+ cNode);

					if (orcid != null && orcid.isURIResource()) {
						boolean confirmed = (cNode != null);
						orcids.add(new OrcidInfo(orcid.getURI(), confirmed));
					}
				} catch (Exception e) {
					log.warn("Failed to parse the query result: " + queryStr, e);
				}
			}

			return orcids;
		}
	}

	/**
	 * A bean to hold info for each ORCID.
	 */
	static class OrcidInfo {
		private final String orcid;
		private final boolean confirmed;

		public OrcidInfo(String orcid, boolean confirmed) {
			this.orcid = orcid;
			this.confirmed = confirmed;
		}

		public String getOrcid() {
			return orcid;
		}

		public boolean isConfirmed() {
			return confirmed;
		}

		@Override
		public String toString() {
			return "OrcidInfo[orcid=" + orcid + ", confirmed=" + confirmed
					+ "]";
		}

	}

}
