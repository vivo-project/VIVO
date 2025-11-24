package edu.cornell.mannlib.vivo.orcid.export.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseEntityDTO {

    @JsonProperty("put-code")
    private Integer putCode;


    public BaseEntityDTO() {}

    public Integer getPutCode() {
        return putCode;
    }

    public void setPutCode(Integer putCode) {
        this.putCode = putCode;
    }
}
