/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.bo;


import java.util.List;


public class ConceptInfo extends SemanticServicesInfoBase {

    private List<Concept> conceptList;

    /**
     *
     */
    public ConceptInfo() {
        super();
    }

    /**
     * @return the vivoDepartmentList
     */
    public List<Concept> getConceptList() {
        return conceptList;
    }

    /**
     * @param vivoDepartmentList the vivoDepartmentList to set
     */
    public void setConceptList(List<Concept> inputConceptList) {
        this.conceptList = inputConceptList;
    }

}
