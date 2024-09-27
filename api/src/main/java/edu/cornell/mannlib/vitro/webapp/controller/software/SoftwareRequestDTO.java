package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.util.ArrayList;
import java.util.List;

public class SoftwareRequestDTO {

    public String internalIdentifier;

    public String label;

    public String datePublished;

    public List<AuthorDTO> authors = new ArrayList<>();

    public List<FundingDTO> funding = new ArrayList<>();

    public String version;

    public String description;

    public String identifier;

    public String keywords;
}
