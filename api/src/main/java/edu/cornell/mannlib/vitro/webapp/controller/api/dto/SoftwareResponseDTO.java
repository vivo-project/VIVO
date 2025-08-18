package edu.cornell.mannlib.vitro.webapp.controller.api.dto;

import java.util.ArrayList;
import java.util.List;

public class SoftwareResponseDTO extends InformationContentEntityResponseDTO {

    public String version;

    public String description;

    public List<String> identifiers = new ArrayList<>();

    public String sameAs;

    public String url;

    public String keywords;

    public String isPartOf;

    public String hasPart;
}
