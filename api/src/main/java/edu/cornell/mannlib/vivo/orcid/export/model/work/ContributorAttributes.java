package edu.cornell.mannlib.vivo.orcid.export.model.work;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContributorAttributes {

    @JsonProperty("contributor-sequence")
    private String contributorSequence;

    @JsonProperty("contributor-role")
    private String contributorRole;


    public ContributorAttributes() {
    }

    public ContributorAttributes(String contributorSequence, String contributorRole) {
        this.contributorSequence = contributorSequence;
        this.contributorRole = contributorRole;
    }

    public String getContributorSequence() {
        return contributorSequence;
    }

    public void setContributorSequence(String contributorSequence) {
        this.contributorSequence = contributorSequence;
    }

    public String getContributorRole() {
        return contributorRole;
    }

    public void setContributorRole(String contributorRole) {
        this.contributorRole = contributorRole;
    }
}
