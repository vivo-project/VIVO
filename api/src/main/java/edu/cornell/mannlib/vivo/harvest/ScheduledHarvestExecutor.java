package edu.cornell.mannlib.vivo.harvest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vivo.harvest.configmodel.ScheduledTaskMetadata;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;
import edu.cornell.mannlib.vivo.scheduler.Scheduled;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class ScheduledHarvestExecutor {

    private final DateTimeFormatter dateFilterPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Scheduled(cron = "${workflow.scheduler.scan}")
    public void runScheduledWorkflowsForToday() {
        getScheduledWorkflows().forEach((scheduledTaskUri, commandString) -> {
            List<String> commandToRun =
                Arrays.stream(commandString.split("§")).collect(Collectors.toList());

            HarvestContext.modules.stream()
                .filter(module -> module.getScheduledTasks().containsKey(scheduledTaskUri))
                .findFirst().ifPresentOrElse(module -> {
                    InternalScheduleOperations.removeScheduledTask(scheduledTaskUri);
                    ScheduledTaskMetadata taskMetadata = module.getScheduledTasks().get(scheduledTaskUri);

                    Path modulePath =
                        Paths.get(ConfigurationProperties.getInstance().getProperty("harvester.directory"),
                            module.getPath()).normalize();

                    HarvestJobExecutor.runAsync(
                        InternalScheduleOperations.sanitizeModuleName(module.getName()),
                        commandToRun, modulePath, taskMetadata.getTaskName()
                    );

                    InternalScheduleOperations.saveScheduledTask(
                        module,
                        taskMetadata.getTaskName(),
                        RecurrenceType.valueOf(taskMetadata.getRecurrenceType()),
                        commandString);
                }, () -> InternalScheduleOperations.removeScheduledTask(scheduledTaskUri));
        });
    }

    private Map<String, String> getScheduledWorkflows() {
        OntModel displayModel = getOntModel();

        Property commandProp =
            ResourceFactory.createProperty(InternalScheduleOperations.COMMAND_PROPERTY);

        Property referenceProp =
            ResourceFactory.createProperty(InternalScheduleOperations.REFERENCE_TIMESTAMP_PROPERTY);

        Property recurrenceProp =
            ResourceFactory.createProperty(InternalScheduleOperations.RECURRENCE_PROPERTY);

        Map<String, String> workflowsThatShouldRunToday = new HashMap<>();

        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        StmtIterator iter =
            displayModel.listStatements(null, commandProp, (RDFNode) null);

        while (iter.hasNext()) {

            Statement commandStmt = iter.nextStatement();
            Resource workflow = commandStmt.getSubject();

            if (workflow == null) {
                continue;
            }

            Statement referenceStmt =
                displayModel.getProperty(workflow, referenceProp);

            Statement recurrenceStmt =
                displayModel.getProperty(workflow, recurrenceProp);

            if (referenceStmt == null || recurrenceStmt == null) {
                continue;
            }

            long referenceEpoch =
                referenceStmt.getObject().asLiteral().getLong();

            String recurrenceStr =
                recurrenceStmt.getObject().asLiteral().getString();

            RecurrenceType recurrence =
                RecurrenceType.valueOf(recurrenceStr);

            if (recurrence == RecurrenceType.ONCE) {
                continue;
            }

            LocalDate referenceDate =
                Instant.ofEpochSecond(referenceEpoch)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            boolean shouldRun = false;

            switch (recurrence) {
                case DAILY:
                    shouldRun = !today.isBefore(referenceDate);
                    break;

                case WEEKLY:
                    shouldRun =
                        !today.isBefore(referenceDate) &&
                            ChronoUnit.WEEKS.between(referenceDate, today) >= 1;
                    break;

                case MONTHLY:
                    shouldRun =
                        !today.isBefore(referenceDate) &&
                            ChronoUnit.MONTHS.between(referenceDate, today) >= 1;
                    break;

                case QUARTERLY:
                    shouldRun =
                        !today.isBefore(referenceDate) &&
                            ChronoUnit.MONTHS.between(referenceDate, today) >= 3;
                    break;
            }

            if (shouldRun) {
                String startDate = referenceDate.format(dateFilterPattern);
                String endDate = today.format(dateFilterPattern);
                String command =
                    commandStmt.getObject().asLiteral().getString()
                        .replace(InternalScheduleOperations.START_DATE_PLACEHOLDER, startDate)
                        .replace(InternalScheduleOperations.END_DATE_PLACEHOLDER, endDate);

                workflowsThatShouldRunToday.put(
                    workflow.getURI(),
                    command
                );
            }
        }

        return workflowsThatShouldRunToday;
    }

    private OntModel getOntModel() {
        ContextModelAccess cma = ModelAccess.getInstance();
        return cma.getOntModel(ModelNames.SCHEDULED_WORKFLOWS);
    }
}
