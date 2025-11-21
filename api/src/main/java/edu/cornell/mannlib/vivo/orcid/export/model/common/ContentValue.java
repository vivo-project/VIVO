package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContentValue {

    private String value;

    @JsonProperty("language-code")
    private String languageCode;


    public ContentValue() {
    }

    public ContentValue(String value) {
        this.value = value;
    }

    public ContentValue(String languageCode, String value) {
        this.languageCode = languageCode;
        this.value = value;
    }
}
