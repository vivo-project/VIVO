package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

    private String city;

    private String region;

    private String country;
}
