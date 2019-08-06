/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.ModelConstructorUtilities;

@SuppressWarnings("deprecation")
public class ConstructedModel {

	private String uri;
	private String individualLabel;
	private String type;
	private String humanReadableType;

	public ConstructedModel(String type, String uri) {
		this.type = type;
		this.humanReadableType = ModelConstructorUtilities.modelTypeToHumanReadableName.get(type);
		this.uri = uri == null ? "" : uri;
	}

	public String getUri() {
		return uri;
	}

	public String getType() {
		return type;
	}

	public String getHumanReadableType() {
		return humanReadableType;
	}

	public void setIndividualLabel(String indiviualLabel) {
		this.individualLabel = indiviualLabel;
	}

	public String getIndividualLabel() {
		return individualLabel;
	}

}
