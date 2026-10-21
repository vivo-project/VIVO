package edu.cornell.mannlib.vivo.harvest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportModule;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportParameter;
import org.apache.commons.fileupload.FileItem;

public class CommandAssembler {

    private CommandAssembler() {
    }

    public static List<String> assemble(ExportModule module, VitroRequest vreq, boolean isScheduledTask,
                                        Map<String, String> taskParameters) {
        List<String> command = new ArrayList<>();
        command.add("./run-fetch.sh");
        command.add("sparql");

        module.getParameters().forEach(parameter -> {
            StringBuilder paramBuilder = new StringBuilder();

            String value = null;

            if ("file".equals(parameter.getType())) {
                Map<String, List<FileItem>> fileItems = vreq.getFiles();
                List<FileItem> items = fileItems != null ? fileItems.get(parameter.getSymbol()) : null;
                FileItem item = items != null && !items.isEmpty() ? items.get(0) : null;

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
                taskParameters.put(parameter.getSymbol(), value);
            }

            if (value != null && !value.trim().isEmpty()) {
                paramBuilder.append(value);

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
                                taskParameters.put(sf.getSymbol(), val);

                                if (val != null && !val.trim().isEmpty()) {
                                    queryParts.add(sf.getSymbol() + "=" + val.trim());
                                }
                            }

                        } else {
                            List<String> groupItems = fields.stream()
                                .map(sf -> {
                                    String val = getParameterValue(sf, isScheduledTask, vreq);
                                    taskParameters.put(sf.getSymbol(), val);

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
                            .append(String.join("&", queryParts));
                    }
                }
            }

            command.add(paramBuilder.toString());
        });

        return command;
    }

    private static String getParameterValue(ExportParameter parameter, boolean isScheduledTask, VitroRequest vreq) {
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
