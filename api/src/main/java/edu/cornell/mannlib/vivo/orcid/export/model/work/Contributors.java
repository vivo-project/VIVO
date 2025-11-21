package edu.cornell.mannlib.vivo.orcid.export.model.work;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Contributors {

    private List<Contributor> contributor;
}
