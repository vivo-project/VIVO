/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.capabilitymap;

import com.google.gson.annotations.SerializedName;

class CapabilityMapResult {
    String[] clusters;

    @SerializedName("md_1")
    String profileId;

    @SerializedName("md_2")
    String description;

    @SerializedName("md_3")
    String thumbNail;

    @SerializedName("md_4")
    String department;

    @SerializedName("md_5")
    String overview;

    @SerializedName("md_6")
    String geographicFocus;

    @SerializedName("md_7")
    String geographicLocation;

    @SerializedName("md_8")
    String[] grants;

    @SerializedName("md_A")
    String firstName;

    @SerializedName("md_B")
    String lastName;

    @SerializedName("md_F")
    String fax;

    @SerializedName("md_G")
    String email;

    @SerializedName("md_H")
    String availableForSupervision;

    @SerializedName("md_I")
    String homepage;

    @SerializedName("md_L")
    String phoneNumber;

    @SerializedName("md_U")
    String[] publications;

    @SerializedName("md_X")
    String[] researchOverview;

    @SerializedName("md_Y")
    String[] subjectArea;

    @SerializedName("md_Z")
    String preferredTitle;

    String query;
}
