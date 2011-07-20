package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.RefreshVisualizationCacheAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseMiscellaneousCuratorPages;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class SiteAdminController extends BaseSiteAdminController {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SiteAdminController.class);
    
    protected Map<String, String> getIndexCacheRebuildUrls(VitroRequest vreq) {
        
        Map<String, String> urls = super.getIndexCacheRebuildUrls(vreq);

        if (PolicyHelper.isAuthorizedForActions(vreq, new RefreshVisualizationCacheAction())) {
            urls.put("rebuildVisCache", UrlBuilder.getUrl("/vis/tools"));            
        }
        
        return urls;
    }
    
    protected Map<String, String> getSiteConfigUrls(VitroRequest vreq) {

        Map<String, String> urls = super.getSiteConfigUrls(vreq);
        
        if (PolicyHelper.isAuthorizedForActions(vreq, new UseMiscellaneousCuratorPages())) {
            urls.put("internalClass", UrlBuilder.getUrl("/processInstitutionalInternalClass"));
        }
        
        return urls;
    }
}
