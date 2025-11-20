package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateDTO {

    private DateValue year;

    private DateValue month;

    private DateValue day;
}
