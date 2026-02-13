package edu.cornell.mannlib.vivo.harvest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.modelaccess.ContextModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportModule;
import edu.cornell.mannlib.vivo.harvest.configmodel.ScheduledTaskMetadata;
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

    public static final String SCHEDULED_TASK_URI = "http://vivoweb.org/scheduledTask/";


    private static OntModel getOntModel() {
        ContextModelAccess cma = ModelAccess.getInstance();
        return cma.getOntModel(ModelNames.SCHEDULED_WORKFLOWS);
    }

    public static void saveScheduledTask(ExportModule exportModule, String taskName,
                                         RecurrenceType recurrenceType, String command) {
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
                    nextRuntimeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
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

        Property referenceProp =
            ResourceFactory.createProperty(InternalScheduleOperations.REFERENCE_TIMESTAMP_PROPERTY);

        Property recurrenceProp =
            ResourceFactory.createProperty(InternalScheduleOperations.RECURRENCE_PROPERTY);

        Property belongsToModuleProp =
            ResourceFactory.createProperty(InternalScheduleOperations.BELONGS_TO_MODULE_PROPERTY);

        List<ScheduledTaskMetadata> result = new ArrayList<>();

        StmtIterator iter =
            model.listStatements(null, belongsToModuleProp, (RDFNode) null);

        while (iter.hasNext()) {

            Statement moduleStmt = iter.nextStatement();
            Resource workflow = moduleStmt.getSubject();

            if (workflow == null) {
                continue;
            }

            String moduleValue =
                moduleStmt.getObject().asLiteral().getString();

            if (!moduleName.equals(moduleValue)) {
                continue;
            }

            Statement referenceStmt =
                model.getProperty(workflow, referenceProp);

            Statement recurrenceStmt =
                model.getProperty(workflow, recurrenceProp);

            if (referenceStmt == null || recurrenceStmt == null) {
                continue;
            }

            long referenceEpoch =
                referenceStmt.getObject().asLiteral().getLong();

            String recurrenceStr =
                recurrenceStmt.getObject().asLiteral().getString();

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

            String taskName =
                workflow.getURI()
                    .substring(workflow.getURI().lastIndexOf("/") + 1);

            result.add(
                new ScheduledTaskMetadata(
                    taskName,
                    workflow.getURI(),
                    recurrenceStr,
                    nextRuntimeDate.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                )
            );
        }

        return result;
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
}
