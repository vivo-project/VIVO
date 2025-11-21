package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Citation {

    @JsonProperty("citation-type")
    private String citationType;

    @JsonProperty("citation-value")
    private String citationValue;
}
