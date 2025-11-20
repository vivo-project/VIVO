package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalId {

    @JsonProperty("external-id-type")
    private String externalIdType;

    @JsonProperty("external-id-value")
    private String externalIdValue;

    @JsonProperty("external-id-url")
    private Url externalIdUrl;

    @JsonProperty("external-id-relationship")
    private String externalIdRelationship;
}
