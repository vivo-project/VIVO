package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateValue {

    private String value;
}
