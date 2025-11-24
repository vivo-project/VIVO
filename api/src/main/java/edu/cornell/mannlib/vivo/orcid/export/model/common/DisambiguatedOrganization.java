package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DisambiguatedOrganization {

    @JsonProperty("disambiguated-organization-identifier")
    private String disambiguatedOrganizationIdentifier;

    @JsonProperty("disambiguation-source")
    private String disambiguationSource;


    public DisambiguatedOrganization() {
    }

    public DisambiguatedOrganization(String disambiguatedOrganizationIdentifier, String disambiguationSource) {
        this.disambiguatedOrganizationIdentifier = disambiguatedOrganizationIdentifier;
        this.disambiguationSource = disambiguationSource;
    }

    public String getDisambiguatedOrganizationIdentifier() {
        return disambiguatedOrganizationIdentifier;
    }

    public void setDisambiguatedOrganizationIdentifier(String disambiguatedOrganizationIdentifier) {
        this.disambiguatedOrganizationIdentifier = disambiguatedOrganizationIdentifier;
    }

    public String getDisambiguationSource() {
        return disambiguationSource;
    }

    public void setDisambiguationSource(String disambiguationSource) {
        this.disambiguationSource = disambiguationSource;
    }
}
