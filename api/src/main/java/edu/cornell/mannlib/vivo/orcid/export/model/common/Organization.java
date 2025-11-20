package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Organization {

    private String name;

    private Address address;

    @JsonProperty("disambiguated-organization")
    private DisambiguatedOrganization disambiguatedOrganization;
}
