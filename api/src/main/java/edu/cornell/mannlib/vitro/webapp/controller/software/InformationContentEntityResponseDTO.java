package edu.cornell.mannlib.vitro.webapp.controller.software;

import java.util.ArrayList;
import java.util.List;

public class InformationContentEntityResponseDTO {

    public String internalIdentifier;

    public String name;

    public String datePublished;

    public List<AuthorDTO> authors = new ArrayList<>();

    public List<String> fundings = new ArrayList<>();

    public List<FunderResponseDTO> funders = new ArrayList<>();
}
