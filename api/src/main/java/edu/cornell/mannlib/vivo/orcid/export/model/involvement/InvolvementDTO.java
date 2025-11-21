package edu.cornell.mannlib.vivo.orcid.export.model.involvement;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;
import edu.cornell.mannlib.vivo.orcid.export.model.common.DateDTO;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ExternalIds;
import edu.cornell.mannlib.vivo.orcid.export.model.common.Organization;

public class InvolvementDTO {

    @JsonProperty("department-name")
    private String departmentName;

    @JsonProperty("role-title")
    private String roleTitle;

    @JsonProperty("start-date")
    private DateDTO startDate;

    @JsonProperty("end-date")
    private DateDTO endDate;

    private Organization organization;

    private ContentValue url;

    @JsonProperty("external-ids")
    private ExternalIds externalIds;


    public InvolvementDTO() {
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public DateDTO getStartDate() {
        return startDate;
    }

    public void setStartDate(DateDTO startDate) {
        this.startDate = startDate;
    }

    public DateDTO getEndDate() {
        return endDate;
    }

    public void setEndDate(DateDTO endDate) {
        this.endDate = endDate;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public ContentValue getUrl() {
        return url;
    }

    public void setUrl(ContentValue url) {
        this.url = url;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }
}
