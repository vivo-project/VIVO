package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DateDTO {

    private ContentValue year;

    private ContentValue month;

    private ContentValue day;


    public DateDTO() {
    }

    public DateDTO(ContentValue year, ContentValue month, ContentValue day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
