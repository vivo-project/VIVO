package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.util.ArrayList;
import java.util.List;

public class SoftwareResponseDTO {

    public String internalIdentifier;

    public String name;

    public String datePublished;

    public List<AuthorDTO> authors = new ArrayList<>();

    public List<String> fundings = new ArrayList<>();

    public List<FunderDTO> funders = new ArrayList<>();

    public String version;

    public String description;

    public List<String> identifiers = new ArrayList<>();

    public String sameAs;

    public String url;

    public String keywords;

    public String isPartOf;

    public String hasPart;
}
