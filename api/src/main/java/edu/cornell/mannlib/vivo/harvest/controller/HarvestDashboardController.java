package edu.cornell.mannlib.vivo.harvest.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.config.ContextPath;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vivo.harvest.HarvestJobExecutor;
import edu.cornell.mannlib.vivo.harvest.RoleCheckUtility;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;

@WebServlet(name = "etlWorkflows", urlPatterns = {"/etlWorkflows"})
public class HarvestDashboardController extends FreemarkerHttpServlet {

    private static final String TEMPLATE_NAME = "harvest-dashboard.ftl";


    @Override
    protected ResponseValues processRequest(VitroRequest vreq) throws Exception {
        Map<String, Object> dataContext = new HashMap<>();

        UserAccount acc = LoginStatusBean.getCurrentUser(vreq);
        if (acc == null || !RoleCheckUtility.isAdmin(acc)) {
            return new TemplateResponseValues("login.ftl", dataContext);
        }

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
        String moduleName = parameters.get("moduleName")[0];

        HarvestContext.modules.stream()
            .filter(module -> module.getName().equals(moduleName)).findFirst()
            .ifPresent(module -> {
                Path modulePath = Paths.get(ConfigurationProperties.getInstance().getProperty("harvester.directory"),
                    module.getPath()).normalize();

                List<String> command = new ArrayList<>();
                command.add("./run-fetch.sh");
                command.add("sparql");

                module.getParameters().forEach(parameter -> {
                    StringBuilder paramBuilder = new StringBuilder();

                    String value = vreq.getParameter(parameter.getSymbol());

                    if (value != null && !value.trim().isEmpty()) {
//                        if (!parameter.getSymbol().trim().isEmpty()) {
//                            commandBuilder.append(parameter.getSymbol()).append(" ");
//                        }

                        paramBuilder.append("\"").append(value);

                        if (parameter.getSubfields() != null && !parameter.getSubfields().isEmpty()) {
                            paramBuilder.append("?");

                            parameter.getSubfields().forEach(subfield -> {
                                paramBuilder
                                    .append(subfield.getSymbol())
                                    .append("=")
                                    .append(vreq.getParameter(subfield.getSymbol()));
                            });
                        }

                        paramBuilder.append("\"");
                    }

                    command.add(paramBuilder.toString());
                });

                module.setRunning(true);

                WorkflowOutputLogController.resetLogPosition(moduleName);

                HarvestJobExecutor.runAsync(module.getName(), command, modulePath);
            });


        return new TemplateResponseValues(TEMPLATE_NAME, dataContext);
    }
}
