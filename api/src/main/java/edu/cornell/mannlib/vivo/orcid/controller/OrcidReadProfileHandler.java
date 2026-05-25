/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.AUTHENTICATE;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.DENIED_AUTHENTICATE;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.FAILED_AUTHENTICATE;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.GOT_PROFILE;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidConfirmationState.Progress.ID_ALREADY_PRESENT;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vivo.orcid.util.OrcidInternalOperationsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * We should now know the user's ORCID, so read the user's public ORCID profile.
 */
public class OrcidReadProfileHandler extends OrcidAbstractHandler {
    private static final Log log = LogFactory
        .getLog(OrcidReadProfileHandler.class);

    private AuthorizationStatus status;
    private OrcidProfile profile;

    protected OrcidReadProfileHandler(VitroRequest vreq) {
        super(vreq);
    }

    public ResponseValues exec() throws OrcidClientException {
        List<String> uris = Authenticator.getInstance(vreq)
            .getAssociatedIndividualUris(LoginStatusBean.getCurrentUser(vreq));
        status = auth.getAuthorizationStatus(AUTHENTICATE);
        if (status.isSuccess()) {
            readProfile();

            onSuccessfulRead();

            return showConfirmationPage();
        } else if (status.isDenied()) {
            return showConfirmationPage(DENIED_AUTHENTICATE);
        } else if (!uris.isEmpty() && OrcidInternalOperationsUtil.hasConfiguredPushCredentials(uris.get(0))) {
            String profilePage = UrlBuilder.getIndividualProfileUrl(uris.get(0), vreq);
            state.reset(uris.get(0), profilePage);

            String orcidId = OrcidInternalOperationsUtil.readOrcidIdForUser(uris.get(0));
            if (orcidId == null) {
                orcidId = "";
            }

            profile = manager.createReadPublicBioAction().execute(
                orcidId.replace("https://orcid.org/", "")
            );

            onSuccessfulRead();

            HashSet<String> existingIds = new HashSet<>();
            existingIds.add(orcidId);
            state.setExistingOrcids(existingIds);

            return showConfirmationPage();
        } else {
            return showConfirmationPage(FAILED_AUTHENTICATE);
        }
    }

    private void readProfile() throws OrcidClientException {
        profile = manager.createReadPublicBioAction().execute(status.getAccessToken().getOrcid());
        log.debug("Read profile");
    }

    private void onSuccessfulRead() {
        state.progress(GOT_PROFILE, profile);

        recordConfirmation();

        if (state.getVivoId() != null) {
            state.progress(ID_ALREADY_PRESENT);
        }
    }

}
