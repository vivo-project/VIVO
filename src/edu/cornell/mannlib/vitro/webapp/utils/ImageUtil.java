/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Some static methods that help in dealing with image files.
 * 
 * So far, we only have methods that obtain the placeholder image for an
 * Individual that has no image of its own.
 * 
 */
public class ImageUtil {
	private static final String DEFAULT_IMAGE_PATH = "/images/placeholders/thumbnail.jpg";

	private static final Map<String, String> DEFAULT_IMAGE_PATHS_BY_TYPE = initImagePaths();

	private static Map<String, String> initImagePaths() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("http://xmlns.com/foaf/0.1/Person",
				"/images/placeholders/person.thumbnail.jpg");
		map.put(VitroVocabulary.USERACCOUNT,
				"/images/placeholders/person.thumbnail.jpg");
		return Collections.unmodifiableMap(map);
	}

	/**
	 * If we have a placeholder image for this exact type, return it. Otherwise,
	 * return the default.
	 */
	public static String getPlaceholderImagePathForType(String typeUri) {
		for (Entry<String, String> entry : DEFAULT_IMAGE_PATHS_BY_TYPE
				.entrySet()) {
			if (typeUri.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return DEFAULT_IMAGE_PATH;
	}

	/**
	 * If there is a placeholder image for any type that this Individual
	 * instantiates, return that image. Otherwise, return the default.
	 */
	public static String getPlaceholderImagePathForIndividual(
			VitroRequest vreq, String individualUri) {
		IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
		for (Entry<String, String> entry : DEFAULT_IMAGE_PATHS_BY_TYPE
				.entrySet()) {
			if (indDao.isIndividualOfClass(entry.getKey(), individualUri)) {
				return entry.getValue();
			}
		}
		return DEFAULT_IMAGE_PATH;
	}

	/** Never need to instantiate this -- all methods are static. */
	private ImageUtil() {
		// Nothing to do
	}
}
