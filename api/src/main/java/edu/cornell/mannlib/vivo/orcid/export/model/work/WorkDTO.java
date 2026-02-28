package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.BaseEntityDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ExternalIds;

public class WorkDTO extends BaseEntityDTO {

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


    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public ContentValue getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(ContentValue journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Citation getCitation() {
        return citation;
    }

    public void setCitation(Citation citation) {
        this.citation = citation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DateDTO getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(DateDTO publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public ContentValue getUrl() {
        return url;
    }

    public void setUrl(ContentValue url) {
        this.url = url;
    }

    public Contributors getContributors() {
        return contributors;
    }

    public void setContributors(Contributors contributors) {
        this.contributors = contributors;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public ContentValue getCountry() {
        return country;
    }

    public void setCountry(ContentValue country) {
        this.country = country;
    }
}
