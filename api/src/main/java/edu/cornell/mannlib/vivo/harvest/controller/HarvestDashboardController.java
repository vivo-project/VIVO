package edu.cornell.mannlib.vivo.harvest.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
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
import edu.cornell.mannlib.vivo.harvest.CommandAssembler;
import edu.cornell.mannlib.vivo.harvest.HarvestContextSetup;
import edu.cornell.mannlib.vivo.harvest.HarvestJobExecutor;
import edu.cornell.mannlib.vivo.harvest.InternalScheduleOperations;
import edu.cornell.mannlib.vivo.harvest.RecurrenceType;
import edu.cornell.mannlib.vivo.harvest.RoleCheckUtility;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;

@WebServlet(name = "etlWorkflows", urlPatterns = {"/etlWorkflows"})
public class HarvestDashboardController extends FreemarkerHttpServlet {

    private static final String TEMPLATE_NAME = "harvest-dashboard.ftl";

    private static final String TEMPLATE_NOT_CONFIGURED = "harvest-not-configured.ftl";


    @Override
    protected ResponseValues processRequest(VitroRequest vreq) throws Exception {
        Map<String, Object> dataContext = new HashMap<>();

        UserAccount acc = LoginStatusBean.getCurrentUser(vreq);
        if (acc == null || !RoleCheckUtility.isAdmin(acc)) {
            return new TemplateResponseValues("login.ftl", dataContext);
        }

        if (!HarvestContext.configured) {
            dataContext.put("contextPath", ContextPath.getPath(vreq));
            dataContext.put("missingProperties", HarvestContextSetup.findMissingProperties());
            return new TemplateResponseValues(TEMPLATE_NOT_CONFIGURED, dataContext);
        }

        if ("true".equals(vreq.getParameter("refreshConfig"))) {
            HarvestContextSetup.loadModules(vreq.getServletContext());
            return null;
        }

        setCommonValues(dataContext, vreq);

        if (vreq.getMethod().equalsIgnoreCase("GET")) {
            dataContext.put("allScheduledTasks", InternalScheduleOperations.getAllScheduledTasks());
            return showForm(dataContext);
        }

        return handlePostRequest(vreq, dataContext);
    }

    private void setCommonValues(Map<String, Object> dataContext, VitroRequest vreq) {
        dataContext.put("contextPath", ContextPath.getPath(vreq));

        InternalScheduleOperations.reloadTaskMetadataAndLogs();
        HarvestContextSetup.refreshGraphOptions(vreq.getServletContext());
        HarvestContext.resolveLabelsBasedOnLocale(vreq);

        dataContext.put("modules", HarvestContext.modules);
    }

    private ResponseValues showForm(Map<String, Object> dataContext) {
        return new TemplateResponseValues(TEMPLATE_NAME, dataContext);
    }

    private ResponseValues handlePostRequest(VitroRequest vreq, Map<String, Object> dataContext) {
        Map<String, String[]> parameters = vreq.getParameterMap();
        String moduleName = parameters.get("moduleName")[0];
        Map<String, String> taskParameters = new HashMap<>();

        HarvestContext.modules.stream()
            .filter(module -> module.getName().equals(moduleName)).findFirst()
            .ifPresent(module -> {
                boolean isScheduledTask;

                String recurrence = vreq.getParameter("recurrenceType");
                if (module.isSchedulable()) {
                    if (recurrence == null || recurrence.trim().isEmpty()) {
                        return;
                    }

                    isScheduledTask = !(RecurrenceType.ONCE.name()).equals(recurrence);
                } else {
                    isScheduledTask = false;
                }

                Path modulePath = Paths.get(ConfigurationProperties.getInstance().getProperty("harvester.directory"),
                    module.getPath()).normalize();

                List<String> command =
                    CommandAssembler.assemble(module, vreq, isScheduledTask, taskParameters);

                if (isScheduledTask) {
                    module.setRunning(true);

                    WorkflowOutputLogController.resetLogPosition(moduleName);

                    InternalScheduleOperations.saveScheduledTask(
                        module,
                        vreq.getParameter("scheduledTaskName"),
                        RecurrenceType.valueOf(recurrence),
                        String.join("§", command),
                        taskParameters);
                } else {
                    boolean started = HarvestJobExecutor.runAsync(
                        InternalScheduleOperations.sanitizeModuleName(module.getName()),
                        command, modulePath, null
                    );

                    if (started) {
                        module.setRunning(true);

                        WorkflowOutputLogController.resetLogPosition(moduleName);
                    }
                }
            });

        dataContext.put("modules", HarvestContext.modules);
        dataContext.put("allScheduledTasks", InternalScheduleOperations.getAllScheduledTasks());
        return new TemplateResponseValues(TEMPLATE_NAME, dataContext);
    }

}
