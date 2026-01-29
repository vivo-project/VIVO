package edu.cornell.mannlib.vivo.orcid.export.model.common;

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

    public ContentValue getYear() {
        return year;
    }

    public void setYear(ContentValue year) {
        this.year = year;
    }

    public ContentValue getMonth() {
        return month;
    }

    public void setMonth(ContentValue month) {
        this.month = month;
    }

    public ContentValue getDay() {
        return day;
    }

    public void setDay(ContentValue day) {
        this.day = day;
    }
}
