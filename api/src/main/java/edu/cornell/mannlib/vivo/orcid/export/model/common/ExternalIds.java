package edu.cornell.mannlib.vivo.orcid.export.model.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExternalIds {

    @JsonProperty("external-id")
    private List<ExternalId> externalId;
}
