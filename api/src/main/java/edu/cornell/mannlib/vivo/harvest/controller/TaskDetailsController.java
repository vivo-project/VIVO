package edu.cornell.mannlib.vivo.harvest.controller;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.config.ContextPath;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;
import edu.cornell.mannlib.vivo.harvest.InternalScheduleOperations;
import edu.cornell.mannlib.vivo.harvest.RecurrenceType;
import edu.cornell.mannlib.vivo.harvest.RoleCheckUtility;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportModule;
import edu.cornell.mannlib.vivo.harvest.configmodel.ScheduledTaskMetadata;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;

@WebServlet(name = "taskDetails", urlPatterns = {"/taskDetails"})
public class TaskDetailsController extends FreemarkerHttpServlet {

    private static final String TEMPLATE_NAME = "task-details.ftl";


    @Override
    protected ResponseValues processRequest(VitroRequest vreq) throws Exception {
        Map<String, Object> dataContext = new HashMap<>();

        UserAccount acc = LoginStatusBean.getCurrentUser(vreq);
        if (acc == null || !RoleCheckUtility.isAdmin(acc)) {
            return new TemplateResponseValues("login.ftl", dataContext);
        }

        setCommonValues(dataContext, vreq);

        if (!dataContext.containsKey("scheduledTask") || !dataContext.containsKey("module")) {
            return new TemplateResponseValues("error-titled.ftl", dataContext);
        }

        if (vreq.getMethod().equalsIgnoreCase("GET")) {
            return showForm(dataContext);
        }

        return handlePostRequest(vreq, dataContext);
    }

    private void setCommonValues(Map<String, Object> dataContext, VitroRequest vreq) {
        dataContext.put("contextPath", ContextPath.getPath(vreq));
        InternalScheduleOperations.reloadTaskMetadataAndLogs();

        String taskUri = vreq.getParameter("taskUri");

        if (taskUri == null || taskUri.trim().isEmpty()) {
            return;
        }

        ScheduledTaskMetadata taskMetadata = InternalScheduleOperations.getScheduledTaskForTaskUri(taskUri);

        if (taskMetadata == null) {
            return;
        }

        Optional<ExportModule> module =
            HarvestContext.modules.stream().filter(m -> m.getName().equals(taskMetadata.getModuleName()))
                .findFirst();

        if (!module.isPresent()) {
            return;
        }

        File tmpDir = new File(HarvestContext.logFileLocation);
        String prefix = "harvest-" + InternalScheduleOperations.sanitizeModuleName(taskMetadata.getModuleName()) + "-" +
            taskMetadata.getTaskName();

        File[] matchingScheduledFiles = tmpDir.listFiles((dir, name) -> name.startsWith(prefix));
        if (matchingScheduledFiles != null && matchingScheduledFiles.length > 0) {
            module.get().getLogFiles().clear();
            module.get().getLogFiles().addAll(Arrays.stream(matchingScheduledFiles)
                .map(File::getName)
                .collect(Collectors.toList())
            );
        }

        dataContext.put("scheduledTask", taskMetadata);
        dataContext.put("module", module.get());
    }

    private ResponseValues showForm(Map<String, Object> dataContext) {
        return new TemplateResponseValues(TEMPLATE_NAME, dataContext);
    }

    private ResponseValues handlePostRequest(VitroRequest vreq, Map<String, Object> dataContext) {
        ScheduledTaskMetadata taskMetadata = (ScheduledTaskMetadata) dataContext.get("scheduledTask");
        taskMetadata.getParameters().keySet().forEach(symbol ->
            taskMetadata.getParameters().put(symbol, vreq.getParameter(symbol)));

        InternalScheduleOperations.removeScheduledTask(taskMetadata.getTaskUri());
        taskMetadata.setTaskName(vreq.getParameter("scheduledTaskName"));

        String savedTaskUri = InternalScheduleOperations.saveScheduledTask(
            (ExportModule) dataContext.get("module"),
            taskMetadata.getTaskName(),
            RecurrenceType.valueOf(vreq.getParameter("recurrenceType")),
            taskMetadata.getCommand(),
            taskMetadata.getParameters());

        String redirectUrl = "/taskDetails?taskUri=" + URLEncoder.encode(savedTaskUri);

        return new RedirectResponseValues(redirectUrl);
    }
}
