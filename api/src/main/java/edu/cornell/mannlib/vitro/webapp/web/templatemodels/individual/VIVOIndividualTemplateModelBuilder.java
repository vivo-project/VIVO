package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class VIVOIndividualTemplateModelBuilder implements IndividualTemplateModelBuilder.IIndividualTemplateModelBuilder {
    @Override
    public VIVOIndividualTemplateModel build(Individual individual, VitroRequest vreq) {
        return new VIVOIndividualTemplateModel(individual, vreq);
    }
}
