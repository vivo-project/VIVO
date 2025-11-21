package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContributorAttributes {

    @JsonProperty("contributor-sequence")
    private String contributorSequence;

    @JsonProperty("contributor-role")
    private String contributorRole;
}
