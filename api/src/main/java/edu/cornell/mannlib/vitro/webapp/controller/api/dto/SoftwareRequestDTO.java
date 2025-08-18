package edu.cornell.mannlib.vitro.webapp.controller.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SoftwareRequestDTO {

    public String internalIdentifier;

    @JsonProperty(required = true)
    public String name;

    public String datePublished;

    public List<AuthorDTO> authors = new ArrayList<>();

    public List<String> fundings = new ArrayList<>();

    public List<FunderRequestDTO> funders = new ArrayList<>();

    public String version;

    public String description;

    public List<String> identifiers = new ArrayList<>();

    public String keywords;
}
