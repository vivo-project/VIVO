/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.semservices.bo;


import java.util.List;


public class ConceptInfo extends SemanticServicesInfoBase {

    private List<?> conceptList;
    /**
     *
     */
    public ConceptInfo() {
        super();
    }

    /**
     * @return the conceptList
     */
    public List<?> getConceptList() {
        return conceptList;
    }

    /**
     * @param conceptList the conceptList to set
     */
    public void setConceptList(List<?> conceptList) {
        this.conceptList = conceptList;
    }
}
