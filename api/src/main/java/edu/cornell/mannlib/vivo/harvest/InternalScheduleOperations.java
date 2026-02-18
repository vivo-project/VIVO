package edu.cornell.mannlib.vivo.harvest;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportModule;
import edu.cornell.mannlib.vivo.harvest.configmodel.ScheduledTaskMetadata;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.StatementImpl;

public class InternalScheduleOperations {

    public static final String START_DATE_PLACEHOLDER = "<START>";
    public static final String END_DATE_PLACEHOLDER = "<END>";
    public static final String REFERENCE_TIMESTAMP_PROPERTY = "http://vivoweb.org/ontology/core#referenceTimestamp";
    public static final String RECURRENCE_PROPERTY = "http://vivoweb.org/ontology/core#recurrence";
    public static final String COMMAND_PROPERTY = "http://vivoweb.org/ontology/core#scheduledCommand";
    public static final String BELONGS_TO_MODULE_PROPERTY = "http://vivoweb.org/ontology/core#belongsToModule";
    public static final String HAS_PARAMETERS = "http://vivoweb.org/ontology/core#hasParameters";
    public static final String SCHEDULED_TASK_URI = "http://vivoweb.org/scheduledTask/";
    private static final Log log = LogFactory.getLog(InternalScheduleOperations.class);

    private static OntModel getOntModel() {
        ContextModelAccess cma = ModelAccess.getInstance();
        return cma.getOntModel(ModelNames.SCHEDULED_WORKFLOWS);
    }

    public static String saveScheduledTask(ExportModule exportModule, String taskName,
                                           RecurrenceType recurrenceType, String command,
                                           Map<String, String> taskParameters) {
        OntModel ontologyModel = getOntModel();
        Resource scheduledTaskResource =
            ResourceFactory.createResource(
                SCHEDULED_TASK_URI + taskName.toLowerCase().replaceAll("\\s", "_")
            );

        long taskCreationTime = Instant.now().getEpochSecond();
        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                scheduledTaskResource,
                ResourceFactory.createProperty(REFERENCE_TIMESTAMP_PROPERTY),
                ResourceFactory.createTypedLiteral(taskCreationTime)
            ));

        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                scheduledTaskResource,
                ResourceFactory.createProperty(RECURRENCE_PROPERTY),
                ResourceFactory.createTypedLiteral(recurrenceType.name())
            ));

        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                scheduledTaskResource,
                ResourceFactory.createProperty(COMMAND_PROPERTY),
                ResourceFactory.createTypedLiteral(command)
            ));

        saveModelStatement(
            ontologyModel,
            new StatementImpl(
                scheduledTaskResource,
                ResourceFactory.createProperty(BELONGS_TO_MODULE_PROPERTY),
                ResourceFactory.createTypedLiteral(exportModule.getName())
            ));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serializedParameters = objectMapper.writeValueAsString(taskParameters);
            saveModelStatement(
                ontologyModel,
                new StatementImpl(
                    scheduledTaskResource,
                    ResourceFactory.createProperty(HAS_PARAMETERS),
                    ResourceFactory.createTypedLiteral(serializedParameters)
                ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LocalDate nextRuntimeDate =
            Instant.ofEpochSecond(taskCreationTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        switch (recurrenceType) {
            case DAILY:
                nextRuntimeDate = nextRuntimeDate.plusDays(1);
                break;
            case WEEKLY:
                nextRuntimeDate = nextRuntimeDate.plusDays(7);
                break;
            case MONTHLY:
                nextRuntimeDate = nextRuntimeDate.plusMonths(1);
                break;
            case QUARTERLY:
                nextRuntimeDate = nextRuntimeDate.plusMonths(3);
                break;
        }

        exportModule.getScheduledTasks()
            .put(scheduledTaskResource.getURI(),
                new ScheduledTaskMetadata(taskName, scheduledTaskResource.getURI(), recurrenceType.name(),
                    nextRuntimeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), exportModule.getName(),
                    taskParameters, command));

        return scheduledTaskResource.getURI();
    }

    public static void removeScheduledTask(String scheduledTaskUri) {
        OntModel ontologyModel = getOntModel();
        Resource scheduledTaskResource = ResourceFactory.createResource(scheduledTaskUri);

        ontologyModel.removeAll(
            scheduledTaskResource,
            ResourceFactory.createProperty(REFERENCE_TIMESTAMP_PROPERTY),
            null
        );

        ontologyModel.removeAll(
            scheduledTaskResource,
            ResourceFactory.createProperty(RECURRENCE_PROPERTY),
            null
        );

        ontologyModel.removeAll(
            scheduledTaskResource,
            ResourceFactory.createProperty(COMMAND_PROPERTY),
            null
        );

        ontologyModel.removeAll(
            scheduledTaskResource,
            null,
            null
        );
    }

    public static List<ScheduledTaskMetadata> getScheduledTasksForModule(String moduleName) {
        OntModel model = getOntModel();

        Property belongsToModuleProp =
            ResourceFactory.createProperty(InternalScheduleOperations.BELONGS_TO_MODULE_PROPERTY);

        List<ScheduledTaskMetadata> result = new ArrayList<>();

        StmtIterator iter =
            model.listStatements(null, belongsToModuleProp, (RDFNode) null);

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource workflow = stmt.getSubject();

            if (workflow == null) {
                continue;
            }

            String moduleValue = stmt.getObject().asLiteral().getString();

            if (!moduleName.equals(moduleValue)) {
                continue;
            }

            ScheduledTaskMetadata metadata =
                buildMetadataFromStatement(model, workflow, moduleValue);

            if (metadata != null) {
                result.add(metadata);
            }
        }

        return result;
    }

    public static ScheduledTaskMetadata getScheduledTaskForTaskUri(String taskUri) {
        OntModel model = getOntModel();

        if (taskUri == null || taskUri.trim().isEmpty()) {
            return null;
        }

        Resource workflow = model.getResource(taskUri);

        if (workflow == null || !model.containsResource(workflow)) {
            return null;
        }

        Property belongsToModuleProp =
            ResourceFactory.createProperty(
                InternalScheduleOperations.BELONGS_TO_MODULE_PROPERTY
            );

        Statement moduleStmt =
            model.getProperty(workflow, belongsToModuleProp);

        if (moduleStmt == null) {
            return null;
        }

        String moduleName =
            moduleStmt.getObject().asLiteral().getString();

        return buildMetadataFromStatement(model, workflow, moduleName);
    }

    public static List<ScheduledTaskMetadata> getAllScheduledTasks() {
        OntModel model = getOntModel();

        Property belongsToModuleProp =
            ResourceFactory.createProperty(InternalScheduleOperations.BELONGS_TO_MODULE_PROPERTY);

        List<ScheduledTaskMetadata> result = new ArrayList<>();

        StmtIterator iter =
            model.listStatements(null, belongsToModuleProp, (RDFNode) null);

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource workflow = stmt.getSubject();

            if (workflow == null) {
                continue;
            }

            String moduleValue = stmt.getObject().asLiteral().getString();

            ScheduledTaskMetadata metadata =
                buildMetadataFromStatement(model, workflow, moduleValue);

            if (metadata != null) {
                result.add(metadata);
            }
        }

        return result;
    }

    private static ScheduledTaskMetadata buildMetadataFromStatement(OntModel model, Resource workflow,
                                                                    String moduleName) {
        Property referenceProp =
            ResourceFactory.createProperty(InternalScheduleOperations.REFERENCE_TIMESTAMP_PROPERTY);
        Property recurrenceProp =
            ResourceFactory.createProperty(InternalScheduleOperations.RECURRENCE_PROPERTY);
        Property parametersProp =
            ResourceFactory.createProperty(InternalScheduleOperations.HAS_PARAMETERS);
        Property commandProp =
            ResourceFactory.createProperty(InternalScheduleOperations.COMMAND_PROPERTY);

        Statement referenceStmt =
            model.getProperty(workflow, referenceProp);
        Statement recurrenceStmt =
            model.getProperty(workflow, recurrenceProp);
        Statement parametersStmt =
            model.getProperty(workflow, parametersProp);
        Statement commandStmt =
            model.getProperty(workflow, commandProp);

        if (referenceStmt == null || recurrenceStmt == null || commandStmt == null) {
            return null;
        }

        String taskName =
            workflow.getURI()
                .substring(workflow.getURI().lastIndexOf("/") + 1);

        long referenceEpoch =
            referenceStmt.getObject().asLiteral().getLong();

        String recurrenceStr =
            recurrenceStmt.getObject().asLiteral().getString();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> taskParameters = new HashMap<>();
        if (parametersStmt != null) {
            try {
                taskParameters = objectMapper.readValue(
                    parametersStmt.getObject().asLiteral().getString(), new TypeReference<>() {
                    }
                );
            } catch (JsonProcessingException e) {
                log.error("Unable to parse task parameters for task " + taskName + ". Reason: " + e.getMessage());
            }
        }

        String commandStr =
            commandStmt.getObject().asLiteral().getString();

        RecurrenceType recurrence =
            RecurrenceType.valueOf(recurrenceStr);

        LocalDate nextRuntimeDate =
            Instant.ofEpochSecond(referenceEpoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        switch (recurrence) {
            case DAILY:
                nextRuntimeDate = nextRuntimeDate.plusDays(1);
                break;
            case WEEKLY:
                nextRuntimeDate = nextRuntimeDate.plusDays(7);
                break;
            case MONTHLY:
                nextRuntimeDate = nextRuntimeDate.plusMonths(1);
                break;
            case QUARTERLY:
                nextRuntimeDate = nextRuntimeDate.plusMonths(3);
                break;
            default:
                break;
        }

        return new ScheduledTaskMetadata(
            taskName,
            workflow.getURI(),
            recurrenceStr,
            nextRuntimeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            moduleName,
            taskParameters,
            commandStr
        );
    }

    private static void saveModelStatement(OntModel ontologyModel, StatementImpl statement) {
        ontologyModel.removeAll(
            statement.getSubject(),
            statement.getPredicate(),
            null
        );

        ontologyModel.add(
            statement
        );
    }

    public static String sanitizeModuleName(String moduleName) {
        return moduleName.replaceAll("[^a-zA-Z0-9_-]", "");
    }

    public static void reloadTaskMetadataAndLogs() {
        File tmpDir = new File(HarvestContext.logFileLocation);

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
    }
}
