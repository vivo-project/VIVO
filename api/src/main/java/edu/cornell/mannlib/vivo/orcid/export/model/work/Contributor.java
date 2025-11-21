package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Contributor {

    @JsonProperty("contributor-orcid")
    private ContributorOrcid contributorOrcid;

    @JsonProperty("credit-name")
    private ContentValue creditName;

    @JsonProperty("contributor-email")
    private String contributorEmail;

    @JsonProperty("contributor-attributes")
    private ContributorAttributes contributorAttributes;
}
