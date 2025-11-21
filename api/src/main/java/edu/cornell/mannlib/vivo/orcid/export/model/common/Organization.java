package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Organization {

    private String name;

    private Address address;

    @JsonProperty("disambiguated-organization")
    private DisambiguatedOrganization disambiguatedOrganization;


    public Organization() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public DisambiguatedOrganization getDisambiguatedOrganization() {
        return disambiguatedOrganization;
    }

    public void setDisambiguatedOrganization(
        DisambiguatedOrganization disambiguatedOrganization) {
        this.disambiguatedOrganization = disambiguatedOrganization;
    }
}
