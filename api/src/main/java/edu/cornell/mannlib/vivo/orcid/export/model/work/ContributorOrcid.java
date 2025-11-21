package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContributorOrcid {

    private String uri;

    private String path;

    private String host;
}
