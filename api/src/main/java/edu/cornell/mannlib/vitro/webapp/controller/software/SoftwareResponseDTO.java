package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.util.ArrayList;
import java.util.List;

public class SoftwareResponseDTO {

    public String internalIdentifier;

    public String label;

    public String datePublished;

    public List<AuthorResponseDTO> authors = new ArrayList<>();

    public List<FundingResponseDTO> funding = new ArrayList<>();

    public String version;

    public String description;

    public String identifier;

    public String sameAs;

    public String url;

    public String keywords;

    public String isPartOf;

    public String hasPart;
}
