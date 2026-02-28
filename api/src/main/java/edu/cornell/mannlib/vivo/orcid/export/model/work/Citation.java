package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Citation {

    @JsonProperty("citation-type")
    private String citationType;

    @JsonProperty("citation-value")
    private String citationValue;


    public String getCitationType() {
        return citationType;
    }

    public void setCitationType(String citationType) {
        this.citationType = citationType;
    }

    public String getCitationValue() {
        return citationValue;
    }

    public void setCitationValue(String citationValue) {
        this.citationValue = citationValue;
    }
}
