/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.START;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.PATH_AUTH_EXTERNAL_ID;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.PATH_AUTH_AUTHENTICATE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.model.ExternalIdentifier;
import edu.cornell.mannlib.orcidclient.model.OrcidBio;
import edu.cornell.mannlib.orcidclient.model.OrcidId;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;

/**
 * Keep track of where we are in the Orcid confirmation process; what has been
 * requested, and what has been returned.
 */
class OrcidConfirmationState {
	private static final Log log = LogFactory
			.getLog(OrcidConfirmationState.class);


	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	private static final String ATTRIBUTE_NAME = OrcidConfirmationState.class
			.getName();

	static OrcidConfirmationState fetch(HttpServletRequest req) {
		HttpSession session = req.getSession();
		Object o = session.getAttribute(ATTRIBUTE_NAME);
		if (o instanceof OrcidConfirmationState) {
			return (OrcidConfirmationState) o;
		} else {
			OrcidConfirmationState ocs = new OrcidConfirmationState();
			session.setAttribute(ATTRIBUTE_NAME, ocs);
			return ocs;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	public enum Progress {
		START, DENIED_AUTHENTICATE, FAILED_AUTHENTICATE, GOT_PROFILE, ID_ALREADY_PRESENT, DENIED_ID, FAILED_ID, ADDED_ID
	}

	private static final Set<Progress> requiresMessage = EnumSet.of(
			Progress.GOT_PROFILE, Progress.ADDED_ID);

	private Progress progress;
	private String individualUri;
	private Set<String> existingOrcids;
	private OrcidProfile profile;
	private String profilePageUrl;

	public void reset(String uri, String profileUrl) {
		progress = START;
		individualUri = uri;
		existingOrcids = Collections.emptySet();
		profile = null;
		profilePageUrl = profileUrl;
	}

	public void setExistingOrcids(Set<String> existing) {
		existingOrcids = new HashSet<>(existing);
	}

	public void progress(Progress p, OrcidProfile... profiles) {
		progress = p;

		if (requiresMessage.contains(p)) {
			if (profiles.length != 1) {
				throw new IllegalStateException("Progress to " + p
						+ " requires an OrcidMessage");
			}
			profile = profiles[0];
		} else {
			if (profiles.length != 0) {
				throw new IllegalStateException("Progress to " + p
						+ " does not accept an OrcidMessage");
			}
		}
	}

	// ----------------------------------------------------------------------
	// Convenience methods for extracting information from the profile.
	// ----------------------------------------------------------------------

	public String getProgress() {
		return progress.toString();
	}

	public String getProgressUrl() {
		switch (progress) {
		case START:
			return UrlBuilder.getUrl(PATH_AUTH_AUTHENTICATE);
		case GOT_PROFILE:
			return UrlBuilder.getUrl(PATH_AUTH_EXTERNAL_ID);
		default:
			return null;
		}
	}

	public String getIndividualUri() {
		return individualUri;
	}

	public String getProfilePageUrl() {
		return profilePageUrl;
	}

	public String getOrcid() {
		return getElementFromOrcidIdentifier("path");

	}

	public String getOrcidUri() {
		return getElementFromOrcidIdentifier("uri");
	}

	public ExternalIdentifier getVivoId() {
		for (ExternalIdentifier id : getExternalIds()) {
			if (individualUri.equals(id.getExternalIdUrl())) {
				return id;
			}
		}
		return null;
	}

	public List<ExternalIdentifier> getExternalIds() {
		OrcidProfile orcidProfile = getOrcidProfile();
		if (orcidProfile == null) {
			return Collections.emptyList();
		}

		OrcidBio bio = orcidProfile.getOrcidBio();
		if (bio == null) {
			return Collections.emptyList();
		}

		if (bio.getExternalIdentifiers() == null) {
			return Collections.emptyList();
		}

		return bio.getExternalIdentifiers();
	}

	private String getElementFromOrcidIdentifier(String elementName) {
		OrcidProfile orcidProfile = getOrcidProfile();
		if (orcidProfile == null) {
			return "";
		}

		OrcidId id = orcidProfile.getOrcidIdentifier();
		if (id == null) {
			log.warn("There is no ORCID Identifier in the profile.");
			return "";
		}

		if ("path".equalsIgnoreCase(elementName)) {
			return id.getPath();
		} else if ("uri".equalsIgnoreCase(elementName)) {
			return id.getUri();
		}

		log.warn("Didn't find the element '' in the ORCID Identifier");
		return "";
	}

	private OrcidProfile getOrcidProfile() {
		if (profile == null) {
			return null;
		}

		return profile;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("progress", progress.toString());
		map.put("individualUri", individualUri);
		map.put("profilePage", profilePageUrl);
		map.put("orcid", getOrcid());
		map.put("orcidUri", getOrcidUri());
		map.put("hasVivoId", getVivoId() == null);
		map.put("externalIds", formatExternalIds());
		map.put("existingOrcids", existingOrcids);

		String progressUrl = getProgressUrl();
		if (progressUrl == null) {
			map.put("progressUrl", "");
		} else {
			map.put("progressUrl", progressUrl);
		}

		return map;
	}

	private List<Map<String, String>> formatExternalIds() {
		List<Map<String, String>> list = new ArrayList<>();
		for (ExternalIdentifier id : getExternalIds()) {
			Map<String, String> map = new HashMap<>();
			map.put("commonName", id.getExternalIdCommonName());
			map.put("reference", id.getExternalIdReference());
			map.put("uri", id.getExternalIdUrl());
			list.add(map);
		}
		return list;
	}
}
