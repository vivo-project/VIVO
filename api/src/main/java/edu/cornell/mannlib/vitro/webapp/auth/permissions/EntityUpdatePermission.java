/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.auth.permissions;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractDataPropertyStatementAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractObjectPropertyStatementAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.resource.AbstractResourceAction;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.rdf.model.impl.Util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EntityUpdatePermission extends EntityPermission {
    private static final Log log = LogFactory.getLog(EntityUpdatePermission.class);

    private static final Collection<String> PROHIBITED_NAMESPACES = Arrays
            .asList(new String[] { VitroVocabulary.vitroURI, "" });

    private static final Collection<String> PERMITTED_EXCEPTIONS = Arrays
            .asList(new String[] { VitroVocabulary.MONIKER,
                    VitroVocabulary.MODTIME, VitroVocabulary.IND_MAIN_IMAGE,
                    VitroVocabulary.LINK, VitroVocabulary.PRIMARY_LINK,
                    VitroVocabulary.ADDITIONAL_LINK,
                    VitroVocabulary.LINK_ANCHOR, VitroVocabulary.LINK_URL });

    public EntityUpdatePermission(String uri) {
        super(uri);
    }

    @Override
    public boolean isAuthorized(List<String> userUris, RequestedAction whatToAuth) {
        boolean isAuthorized = false;

        if (whatToAuth instanceof AbstractDataPropertyStatementAction) {
            // Check resource
            String subjectUri = ((AbstractDataPropertyStatementAction)whatToAuth).getSubjectUri();
            if (isModifiable(subjectUri)) {
                Property predicate = ((AbstractDataPropertyStatementAction)whatToAuth).getPredicate();
                if (isModifiable(predicate.getURI())) {
                    isAuthorized = isAuthorizedFor(predicate);
                }
            }

            if (isAuthorized) {
                isAuthorized = isAuthorizedFor((AbstractPropertyStatementAction) whatToAuth, userUris);
            }
        } else if (whatToAuth instanceof AbstractObjectPropertyStatementAction) {
            String subjectUri = ((AbstractObjectPropertyStatementAction)whatToAuth).getSubjectUri();
            String objectUri = ((AbstractObjectPropertyStatementAction)whatToAuth).getObjectUri();
            if (isModifiable(subjectUri) && isModifiable(objectUri)) {
                Property predicate = ((AbstractObjectPropertyStatementAction)whatToAuth).getPredicate();
                if (isModifiable(predicate.getURI())) {
                    isAuthorized = isAuthorizedFor(predicate);
                }
            }

            if (isAuthorized) {
                isAuthorized = isAuthorizedFor((AbstractPropertyStatementAction) whatToAuth, userUris);
            }
        } else if (whatToAuth instanceof AbstractResourceAction) {
            String subjectUri = ((AbstractObjectPropertyStatementAction)whatToAuth).getSubjectUri();
            isAuthorized = isModifiable(subjectUri);
        }

        if (isAuthorized) {
            log.debug(this + " authorizes " + whatToAuth);
        } else {
            log.debug(this + " does not authorize " + whatToAuth);
        }

        return isAuthorized;
    }

    private boolean isModifiable(String uri) {
        if (PROHIBITED_NAMESPACES.contains(namespace(uri))) {
            if (PERMITTED_EXCEPTIONS.contains(uri)) {
                return true;
            } else {
                return false;
            }
        }

        return true;

    }

    private String namespace(String uri) {
        return uri.substring(0, Util.splitNamespaceXML(uri));
    }
}
