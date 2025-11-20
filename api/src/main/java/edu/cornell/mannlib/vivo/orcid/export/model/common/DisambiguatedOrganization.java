package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DisambiguatedOrganization {

    @JsonProperty("disambiguated-organization-identifier")
    private String disambiguatedOrganizationIdentifier;

    @JsonProperty("disambiguation-source")
    private String disambiguationSource;
}
