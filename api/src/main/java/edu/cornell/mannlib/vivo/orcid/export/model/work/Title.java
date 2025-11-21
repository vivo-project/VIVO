package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Title {

    private ContentValue title;

    private ContentValue subtitle;

    @JsonProperty("translated-title")
    private ContentValue translatedTitle;
}
