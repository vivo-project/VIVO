package edu.cornell.mannlib.vivo.orcid.controller;

import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vivo.orcid.util.OrcidInternalOperationsUtil;

public class OrcidDisallowPushHandler extends OrcidAbstractHandler {

    public OrcidDisallowPushHandler(VitroRequest vreq) {
        super(vreq);
    }

    public RedirectResponseValues exec() {
        String individualUri = vreq.getParameter("profileUri");

        OrcidInternalOperationsUtil.setAllowPushStatusForIndividual(individualUri, false);

        return new RedirectResponseValues(
            occ.getSetting(OrcidClientContext.Setting.WEBAPP_BASE_URL) + "individual?uri=" + individualUri);
    }
}
