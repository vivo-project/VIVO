/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.Map;

public class N3TransitionToV2Mapping extends HashMap<String, String>{
    public N3TransitionToV2Mapping(){
        Map<String,String> map = this;

        map.put("defaultAddMissingIndividualForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.VIVODefaultAddMissingIndividualFormGenerator.class.getName());

        // vivo forms:

        map.put("addAuthorsToInformationResource.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddAuthorsToInformationResourceGenerator.class.getName());
        map.put("manageWebpagesForIndividual.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.ManageWebpagesForIndividualGenerator.class.getName());
        map.put("newIndividualForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.VIVONewIndividualFormGenerator.class.getName());
        map.put("organizationHasPositionHistory.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.OrganizationHasPositionHistoryGenerator.class.getName());
        map.put("personHasEducationalTraining.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.PersonHasEducationalTraining.class.getName());
        map.put("personHasPositionHistory.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.PersonHasPositionHistoryGenerator.class.getName());
        map.put("addGrantRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddGrantRoleToPersonGenerator.class.getName());
        map.put("addEditWebpageForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddEditWebpageFormGenerator.class.getName());
        map.put("addPublicationToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddPublicationToPersonGenerator.class.getName());

//        map.put("terminologyAnnotation.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.TerminologyAnnotationGenerator.class.getName());
//
//        map.put("redirectToPublication.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.RedirectToPublicationGenerator.class.getName());
//        map.put("unsupportedBrowserMessage.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.UnsupportedBrowserMessage.class.getName());
//
        // vivo 2 stage role forms:

        map.put("addAttendeeRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddAttendeeRoleToPersonGenerator.class.getName());
        map.put("addClinicalRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddClinicalRoleToPersonGenerator.class.getName());
        map.put("addEditorRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddEditorRoleToPersonGenerator.class.getName());
        map.put("addHeadOfRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddHeadOfRoleToPersonGenerator.class.getName());
        map.put("addMemberRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddMemberRoleToPersonGenerator.class.getName());
        map.put("addOrganizerRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddOrganizerRoleToPersonGenerator.class.getName());
        map.put("addOutreachRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddOutreachProviderRoleToPersonGenerator.class.getName());
        map.put("addPresenterRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddPresenterRoleToPersonGenerator.class.getName());
        map.put("addResearcherRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddResearcherRoleToPersonGenerator.class.getName());
        map.put("addReviewerRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddReviewerRoleToPersonGenerator.class.getName());
        map.put("addRoleToPersonTwoStage.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddRoleToPersonTwoStageGenerator.class.getName());
        map.put("addServiceProviderRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddServiceProviderRoleToPersonGenerator.class.getName());
        map.put("addTeacherRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddTeacherRoleToPersonGenerator.class.getName());

    }
}
