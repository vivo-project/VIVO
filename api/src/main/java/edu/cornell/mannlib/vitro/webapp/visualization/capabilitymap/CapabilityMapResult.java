/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.capabilitymap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
class CapabilityMapResult {
    @JsonProperty
    String[] clusters;

    @JsonProperty("md_1")
    String profileId;

    @JsonProperty("md_2")
    String description;

    @JsonProperty("md_3")
    String thumbNail;

    @JsonProperty("md_4")
    String department;

    @JsonProperty("md_5")
    String overview;

    @JsonProperty("md_6")
    String geographicFocus;

    @JsonProperty("md_7")
    String geographicLocation;

    @JsonProperty("md_8")
    String[] grants;

    @JsonProperty("md_A")
    String firstName;

    @JsonProperty("md_B")
    String lastName;

    @JsonProperty("md_F")
    String fax;

    @JsonProperty("md_G")
    String email;

    @JsonProperty("md_H")
    String availableForSupervision;

    @JsonProperty("md_I")
    String homepage;

    @JsonProperty("md_L")
    String phoneNumber;

    @JsonProperty("md_U")
    String[] publications;

    @JsonProperty("md_X")
    String[] researchOverview;

    @JsonProperty("md_Y")
    String[] subjectArea;

    @JsonProperty("md_Z")
    String preferredTitle;

    @JsonProperty
    String query;
}
