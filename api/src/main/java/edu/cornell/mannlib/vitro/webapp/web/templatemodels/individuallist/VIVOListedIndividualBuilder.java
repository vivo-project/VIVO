package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class VIVOListedIndividualBuilder implements ListedIndividualBuilder.ILIstedIndividualBuilder {
    @Override
    public ListedIndividual build(Individual individual, VitroRequest vreq) {
        return new VIVOListedIndividual(individual, vreq);
    }
}
