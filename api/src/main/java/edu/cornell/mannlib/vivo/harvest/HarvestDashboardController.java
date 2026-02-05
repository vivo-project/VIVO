package edu.cornell.mannlib.vivo.harvest;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.config.ContextPath;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;

@WebServlet(name = "harvest", urlPatterns = {"/harvest"})
public class HarvestDashboardController extends FreemarkerHttpServlet {

    private static final String TEMPLATE_NAME = "harvest-dashboard.ftl";


    @Override
    protected ResponseValues processRequest(VitroRequest vreq) throws Exception {
        Map<String, Object> dataContext = new HashMap<>();
        setCommonValues(dataContext, vreq);

        if (vreq.getMethod().equalsIgnoreCase("GET")) {
            return showForm(dataContext);
        }

        return handlePostRequest(vreq, dataContext);
    }


    private void setCommonValues(Map<String, Object> dataContext, VitroRequest vreq) {
        dataContext.put("contextPath", ContextPath.getPath(vreq));
        dataContext.put("modules", HarvestContext.modules);
    }

    private ResponseValues showForm(Map<String, Object> dataContext) {
        return new TemplateResponseValues(TEMPLATE_NAME, dataContext);
    }

    private ResponseValues handlePostRequest(VitroRequest vreq, Map<String, Object> dataContext) {
        Map<String, String[]> parameters = vreq.getParameterMap();
        HarvestContext.modules.stream()
            .filter(module -> module.getName().equals(parameters.get("moduleName")[0])).findFirst()
            .ifPresent(module -> {

                StringBuilder commandBuilder =
                    new StringBuilder("\"")
                        .append(Paths.get(ConfigurationProperties.getInstance().getProperty("harvester.directory"),
                            module.getPath(), "run-fetch.sh").normalize())
                        .append("\"");

                module.getParameters().forEach(parameter -> {

                    String value = vreq.getParameter(parameter.getSymbol());

                    if (value != null && !value.trim().isEmpty()) {
                        commandBuilder.append(" ")
                            .append(parameter.getSymbol())
                            .append(" \"")
                            .append(value);

                        if (parameter.getSubfields() != null && !parameter.getSubfields().isEmpty()) {
                            commandBuilder.append("?");

                            parameter.getSubfields().forEach(subfield -> {
                                commandBuilder
                                    .append(subfield.getSymbol())
                                    .append("=")
                                    .append(vreq.getParameter(subfield.getSymbol()));
                            });
                        }

                        commandBuilder.append("\"");
                    }
                });

                module.setRunning(true);
                HarvestJobExecutor.runAsync(module.getName(), commandBuilder.toString());
            });


        return new TemplateResponseValues(TEMPLATE_NAME, dataContext);
    }
}
