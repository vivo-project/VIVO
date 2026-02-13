package edu.cornell.mannlib.vivo.harvest.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import edu.cornell.mannlib.vivo.harvest.InternalScheduleOperations;
import edu.cornell.mannlib.vivo.harvest.RecurrenceType;
import edu.cornell.mannlib.vivo.harvest.RoleCheckUtility;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportParameter;
import edu.cornell.mannlib.vivo.harvest.configmodel.ScheduledTaskMetadata;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;
import org.apache.commons.fileupload.FileItem;

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

        File tmpDir = new File("/tmp");

        HarvestContext.modules.forEach(module -> {
            Map<String, ScheduledTaskMetadata> tasks =
                InternalScheduleOperations
                    .getScheduledTasksForModule(module.getName())
                    .stream()
                    .collect(Collectors.toMap(
                        ScheduledTaskMetadata::getTaskUri,
                        Function.identity(),
                        (a, b) -> b
                        // safety in case of duplicate task names, should never happen
                    ));

            module.getScheduledTasks().clear();
            module.getScheduledTasks().putAll(tasks);

            String safeModuleName = InternalScheduleOperations.sanitizeModuleName(module.getName());
            String prefix = "harvest-" + safeModuleName + "-";
            String exactFile = "harvest-" + safeModuleName + ".log";

            File[] matchingScheduledFiles = tmpDir.listFiles((dir, name) -> name.startsWith(prefix));
            File[] matchingManualRunFiles = tmpDir.listFiles((dir, name) -> name.equals(exactFile));

            if (matchingScheduledFiles != null && matchingScheduledFiles.length > 0) {
                module.getLogFiles().clear();
                module.getLogFiles().addAll(Arrays.stream(matchingScheduledFiles)
                    .map(File::getName)
                    .collect(Collectors.toList())
                );
            }

            if (matchingManualRunFiles != null && matchingManualRunFiles.length > 0) {
                module.setManualRunLogExists(true);
            }
        });

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

                List<String> command = new ArrayList<>();
                command.add("./run-fetch.sh");
                command.add("sparql");

                module.getParameters().forEach(parameter -> {
                    StringBuilder paramBuilder = new StringBuilder();

                    String value = null;

                    if ("file".equals(parameter.getType())) {
                        Map<String, List<FileItem>> fileItems = vreq.getFiles();
                        FileItem item = fileItems != null ? fileItems.get(parameter.getSymbol()).get(0) : null;

                        if (item != null && !item.isFormField() && item.getSize() > 0) {
                            String fileName = Paths.get(item.getName()).getFileName().toString();
                            Path tmpPath = Paths.get(parameter.getTmpLocation(), fileName);

                            try (InputStream in = item.getInputStream()) {
                                Files.copy(in, tmpPath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            value = tmpPath.toString();
                        }
                    } else {
                        value = getParameterValue(parameter, isScheduledTask, vreq);
                    }

                    if (value != null && !value.trim().isEmpty()) {
                        paramBuilder.append("\"").append(value);

                        if ("url".equals(parameter.getType())
                            && parameter.getSubfields() != null
                            && !parameter.getSubfields().isEmpty()) {

                            Map<String, List<ExportParameter>> grouped =
                                parameter.getSubfields().stream()
                                    .collect(Collectors.groupingBy(
                                        sf -> sf.getGroup() != null ? sf.getGroup() : "",
                                        LinkedHashMap::new,
                                        Collectors.toList()
                                    ));

                            List<String> queryParts = new ArrayList<>();

                            for (Map.Entry<String, List<ExportParameter>> entry : grouped.entrySet()) {
                                String group = entry.getKey();
                                List<ExportParameter> fields = entry.getValue();

                                if (group.isEmpty()) {
                                    for (ExportParameter sf : fields) {
                                        String val = getParameterValue(sf, isScheduledTask, vreq);
                                        if (val != null && !val.trim().isEmpty()) {
                                            queryParts.add(sf.getSymbol() + "=" + val.trim());
                                        }
                                    }

                                } else {
                                    List<String> groupItems = fields.stream()
                                        .map(sf -> {
                                            String val = getParameterValue(sf, isScheduledTask, vreq);
                                            if (val == null || val.trim().isEmpty()) {
                                                return null;
                                            }
                                            return sf.getSymbol() + sf.getKvSep() + val.trim();
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());

                                    if (!groupItems.isEmpty()) {
                                        queryParts.add(group + "=" +
                                            String.join(fields.get(0).getGroupSep(), groupItems));
                                    }
                                }
                            }

                            if (!queryParts.isEmpty()) {
                                paramBuilder.append("?")
                                    .append(String.join("\\&", queryParts)); // escaped &
                            }
                        }

                        paramBuilder.append("\"");
                    }

                    command.add(paramBuilder.toString());
                });

                module.setRunning(true);

                WorkflowOutputLogController.resetLogPosition(moduleName);

                if (isScheduledTask) {
                    InternalScheduleOperations.saveScheduledTask(
                        module,
                        vreq.getParameter("scheduledTaskName"),
                        RecurrenceType.valueOf(recurrence),
                        String.join("§", command));
                } else {
                    HarvestJobExecutor.runAsync(
                        InternalScheduleOperations.sanitizeModuleName(module.getName()),
                        command, modulePath, null
                    );
                }
            });

        dataContext.put("modules", HarvestContext.modules);
        return new TemplateResponseValues(TEMPLATE_NAME, dataContext);
    }

    private String getParameterValue(ExportParameter parameter, boolean isScheduledTask, VitroRequest vreq) {
        if (!isScheduledTask) {
            return vreq.getParameter(parameter.getSymbol());
        }

        if (parameter.isStartDateAttribute()) {
            return InternalScheduleOperations.START_DATE_PLACEHOLDER;
        } else if (parameter.isEndDateAttribute()) {
            return InternalScheduleOperations.END_DATE_PLACEHOLDER;
        }

        return vreq.getParameter(parameter.getSymbol());
    }
}
