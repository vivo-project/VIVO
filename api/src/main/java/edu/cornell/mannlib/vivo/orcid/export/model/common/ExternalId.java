package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalId {

    @JsonProperty("external-id-type")
    private String externalIdType;

    @JsonProperty("external-id-value")
    private String externalIdValue;

    @JsonProperty("external-id-url")
    private ContentValue externalIdUrl;

    @JsonProperty("external-id-relationship")
    private String externalIdRelationship;


    public ExternalId() {}

    public ExternalId(String externalIdType, String externalIdValue, ContentValue externalIdUrl,
                      String externalIdRelationship) {
        this.externalIdType = externalIdType;
        this.externalIdValue = externalIdValue;
        this.externalIdUrl = externalIdUrl;
        this.externalIdRelationship = externalIdRelationship;
    }

    public String getExternalIdType() {
        return externalIdType;
    }

    public void setExternalIdType(String externalIdType) {
        this.externalIdType = externalIdType;
    }

    public String getExternalIdValue() {
        return externalIdValue;
    }

    public void setExternalIdValue(String externalIdValue) {
        this.externalIdValue = externalIdValue;
    }

    public ContentValue getExternalIdUrl() {
        return externalIdUrl;
    }

    public void setExternalIdUrl(ContentValue externalIdUrl) {
        this.externalIdUrl = externalIdUrl;
    }

    public String getExternalIdRelationship() {
        return externalIdRelationship;
    }

    public void setExternalIdRelationship(String externalIdRelationship) {
        this.externalIdRelationship = externalIdRelationship;
    }
}
