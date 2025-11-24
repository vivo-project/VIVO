package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;

public class Title {

    private ContentValue title;

    private ContentValue subtitle;

    @JsonProperty("translated-title")
    private ContentValue translatedTitle;


    public Title() {
    }

    public Title(ContentValue title, ContentValue subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public ContentValue getTitle() {
        return title;
    }

    public void setTitle(ContentValue title) {
        this.title = title;
    }

    public ContentValue getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(ContentValue subtitle) {
        this.subtitle = subtitle;
    }

    public ContentValue getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(ContentValue translatedTitle) {
        this.translatedTitle = translatedTitle;
    }
}
