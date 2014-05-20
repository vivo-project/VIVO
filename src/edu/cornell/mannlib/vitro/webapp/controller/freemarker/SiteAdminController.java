/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.visualization.tools.ToolsRequestHandler;

public class SiteAdminController extends BaseSiteAdminController {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SiteAdminController.class);
    
    @Override
	protected Map<String, Object> getSiteMaintenanceUrls(VitroRequest vreq) {
        
        Map<String, Object> urls = super.getSiteMaintenanceUrls(vreq);

        if (PolicyHelper.isAuthorizedForActions(vreq, ToolsRequestHandler.REQUIRED_ACTIONS)) {
            urls.put("rebuildVisCache", UrlBuilder.getUrl("/vis/tools"));            
        }
        
        return urls;
    }
    
    @Override
	protected Map<String, Object> getSiteConfigData(VitroRequest vreq) {

        Map<String, Object> data = super.getSiteConfigData(vreq);
        
        if (PolicyHelper.isAuthorizedForActions(vreq, InstitutionalInternalClassController.REQUIRED_ACTIONS)) {
            data.put("internalClass", UrlBuilder.getUrl("/processInstitutionalInternalClass"));
        }
        
        return data;
    }
}
