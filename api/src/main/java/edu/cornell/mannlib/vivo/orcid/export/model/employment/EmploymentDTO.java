package edu.cornell.mannlib.vivo.orcid.export.model.employment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ExternalIds;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Organization;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Url;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmploymentDTO {

    @JsonProperty("department-name")
    private String departmentName;

    @JsonProperty("role-title")
    private String roleTitle;

    @JsonProperty("start-date")
    private DateDTO startDate;

    @JsonProperty("end-date")
    private DateDTO endDate;

    private Organization organization;

    private Url url;

    @JsonProperty("external-ids")
    private ExternalIds externalIds;
}
