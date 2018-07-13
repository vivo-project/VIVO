/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.auth.permissions;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayDataProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayObjectProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class EntityDisplayPermission extends EntityPermission {
    private static final Log log = LogFactory.getLog(EntityDisplayPermission.class);

    public EntityDisplayPermission(String uri) {
        super(uri);
    }

    @Override
    public boolean isAuthorized(List<String> userUris, RequestedAction whatToAuth) {
        boolean result = false;

        if (whatToAuth instanceof DisplayDataProperty) {
            String predicateUri = ((DisplayDataProperty)whatToAuth).getDataProperty().getURI();
            result = isAuthorizedFor(new Property(predicateUri));
        } else if (whatToAuth instanceof DisplayObjectProperty) {
            result = isAuthorizedFor(((DisplayObjectProperty)whatToAuth).getObjectProperty());
        } else if (whatToAuth instanceof DisplayDataPropertyStatement) {
            DataPropertyStatement stmt = ((DisplayDataPropertyStatement)whatToAuth).getDataPropertyStatement();
            // Check subject as resource
            // String subjectUri = stmt.getIndividualURI();

            String predicateUri = stmt.getDatapropURI();
            result = isAuthorizedFor(new Property(predicateUri));
        } else if (whatToAuth instanceof DisplayObjectPropertyStatement) {
            // Check subject as resource
//            String subjectUri = ((DisplayObjectPropertyStatement)whatToAuth).getSubjectUri();
            // Check object as resource
//            String objectUri = ((DisplayObjectPropertyStatement)whatToAuth).getObjectUri();

            Property op = ((DisplayObjectPropertyStatement)whatToAuth).getProperty();
            result = isAuthorizedFor(op);
            //p.getDomainVClassURI(), p.getURI(), p.getRangeVClassURI()
        }

        if (result) {
            log.debug(this + " authorizes " + whatToAuth);
        } else {
            log.debug(this + " does not authorize " + whatToAuth);
        }

        return result;
    }
}
