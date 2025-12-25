package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.cornell.mannlib.vivo.orcid.export.model.common.ContentValue;

public class Contributor {

    @JsonProperty("contributor-orcid")
    private ContributorOrcid contributorOrcid;

    @JsonProperty("credit-name")
    private ContentValue creditName;

    @JsonProperty("contributor-email")
    private String contributorEmail;

    @JsonProperty("contributor-attributes")
    private ContributorAttributes contributorAttributes;


    public ContributorOrcid getContributorOrcid() {
        return contributorOrcid;
    }

    public void setContributorOrcid(ContributorOrcid contributorOrcid) {
        this.contributorOrcid = contributorOrcid;
    }

    public ContentValue getCreditName() {
        return creditName;
    }

    public void setCreditName(ContentValue creditName) {
        this.creditName = creditName;
    }

    public String getContributorEmail() {
        return contributorEmail;
    }

    public void setContributorEmail(String contributorEmail) {
        this.contributorEmail = contributorEmail;
    }

    public ContributorAttributes getContributorAttributes() {
        return contributorAttributes;
    }

    public void setContributorAttributes(
        ContributorAttributes contributorAttributes) {
        this.contributorAttributes = contributorAttributes;
    }
}
