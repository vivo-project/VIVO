package edu.cornell.mannlib.vitro.webapp.controller.individual;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import org.vivoweb.webapp.controller.freemarker.CreateAndLinkResourceController;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

public class VIVOIndividualResponseBuilderExtension implements IndividualResponseBuilder.ExtendedResponse {
    public static class Setup implements ServletContextListener {
        @Override
        public void contextInitialized(ServletContextEvent sce) {
            IndividualResponseBuilder.registerExtendedResponse(new VIVOIndividualResponseBuilderExtension());
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {

        }
    }

    @Override
    public void addOptions(VitroRequest vreq, Map<String, Object> body) {
        addAltMetricOptions(vreq, body);
        addPlumPrintOptions(vreq, body);
        addEnabledClaimingSources(vreq, body);
    }

    private void addEnabledClaimingSources(VitroRequest vreq, Map<String, Object> body) {
        ConfigurationProperties props = ConfigurationProperties.getBean(vreq);
        body.put("claimSources", CreateAndLinkResourceController.getEnabledProviders(props));

    }

    private void addAltMetricOptions(VitroRequest vreq, Map<String, Object> body) {
        ConfigurationProperties properties = ConfigurationProperties.getBean(vreq);

        if (properties != null) {
            String enabled        = properties.getProperty("resource.altmetric", "enabled");
            String displayTo      = properties.getProperty("resource.altmetric.displayto", "right");
            String badgeType      = properties.getProperty("resource.altmetric.badge-type", "donut");
            String badgeHideEmpty = properties.getProperty("resource.altmetric.hide-no-mentions", "true");
            String badgePopover   = properties.getProperty("resource.altmetric.badge-popover", "right");
            String badgeDetails   = properties.getProperty("resource.altmetric.badge-details");

            if (!"disabled".equalsIgnoreCase(enabled)) {
                body.put("altmetricEnabled", true);

                body.put("altmetricDisplayTo", displayTo);
                body.put("altmetricBadgeType", badgeType);
                if ("true".equalsIgnoreCase(badgeHideEmpty)) {
                    body.put("altmetricHideEmpty", true);
                }
                body.put("altmetricPopover", badgePopover);
                body.put("altmetricDetails", badgeDetails);
            }
        }
    }

    private void addPlumPrintOptions(VitroRequest vreq, Map<String, Object> body) {
        ConfigurationProperties properties = ConfigurationProperties.getBean(vreq);

        if (properties != null) {
            String enabled = properties.getProperty("resource.plum-print", "enabled");
            String displayTo = properties.getProperty("resource.plum-print.displayto", "right");
            String printHideEmpty = properties.getProperty("resource.plum-print.hide-when-empty", "true");
            String printPopover = properties.getProperty("resource.plum-print.popover", "right");
            String printSize = properties.getProperty("resource.plum-print.size", "medium");

            if (!"disabled".equalsIgnoreCase(enabled)) {
                body.put("plumPrintEnabled", true);

                body.put("plumPrintDisplayTo", displayTo);
                body.put("plumPrintHideEmpty", "true".equalsIgnoreCase(printHideEmpty) ? "true" : "false");
                body.put("plumPrintPopover", printPopover);
                body.put("plumPrintSize", printSize);
            }
        }
    }
}
