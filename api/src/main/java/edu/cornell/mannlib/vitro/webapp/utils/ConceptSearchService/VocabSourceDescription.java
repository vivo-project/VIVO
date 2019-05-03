/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

public class VocabSourceDescription {
	private String url;
	private String label;
	private String schema;
	private String description;

	public VocabSourceDescription(String inputLabel, String inputSchema, String inputUrl, String inputDescription) {
		url = inputUrl;
		label = inputLabel;
		schema = inputSchema;
		description = inputDescription;
	}

	public String getUrl() {
		return url;
	}

	public String getLabel() {
		return label;
	}

	public String getSchema() {
		return schema;
	}

	public String getDescription() {
		return description;
	}
}
