package edu.cornell.mannlib.vivo.orcid.export.model.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalIds {

    @JsonProperty("external-id")
    private List<ExternalId> externalId;


    public ExternalIds() {}

    public ExternalIds(List<ExternalId> externalId) {
        this.externalId = externalId;
    }

    public List<ExternalId> getExternalId() {
        return externalId;
    }

    public void setExternalId(List<ExternalId> externalId) {
        this.externalId = externalId;
    }
}
