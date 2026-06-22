package edu.cornell.mannlib.vivo.harvest;

import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.PermissionSets;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;

public class RoleCheckUtility {

    public static boolean isAdmin(UserAccount acc) {
        if (acc.isRootUser()) {
            return true;
        }
        Set<String> roles = acc.getPermissionSetUris();
        return (roles.contains(PermissionSets.URI_DBA));
    }
}
