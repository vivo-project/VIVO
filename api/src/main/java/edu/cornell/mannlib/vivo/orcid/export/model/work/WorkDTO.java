package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ExternalIds;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WorkDTO {

    private Title title;

    @JsonProperty("journal-title")
    private ContentValue journalTitle;

    @JsonProperty("short-description")
    private String shortDescription;

    private Citation citation;

    private String type;

    @JsonProperty("publication-date")
    private DateDTO publicationDate;

    @JsonProperty("external-ids")
    private ExternalIds externalIds;

    private ContentValue url;

    private Contributors contributors;

    @JsonProperty("language-code")
    private String languageCode;

    private ContentValue country;
}
