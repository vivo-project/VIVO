package edu.cornell.mannlib.vivo.harvest;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.WhichService.CONTENT;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.jena.BlankNodeFilteringModelMaker;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vivo.harvest.configmodel.ExportConfig;
import edu.cornell.mannlib.vivo.harvest.configmodel.ScheduledTaskMetadata;
import edu.cornell.mannlib.vivo.harvest.contextmodel.HarvestContext;
import edu.cornell.mannlib.vivo.scheduler.SchedulerManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HarvestContextSetup implements ServletContextListener {

    private static final Log log = LogFactory.getLog(HarvestContextSetup.class);


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        loadModules(ctx);

        SchedulerManager.scheduleTasks(
            new ScheduledHarvestExecutor()
        );
    }

    private void loadModules(ServletContext ctx) {
        String harvesterDirectory = ConfigurationProperties.getInstance().getProperty("harvester.directory");
        String harvesterConfigurationPath =
            ConfigurationProperties.getInstance().getProperty("harvester.configuration");
        Path fullPath = Paths.get(harvesterDirectory, harvesterConfigurationPath).normalize();

        try (InputStream is = Files.newInputStream(fullPath)) {
            ObjectMapper mapper = new ObjectMapper();
            ExportConfig config = mapper.readValue(is, ExportConfig.class);

            HarvestContext.modules = config.getExportModules();

            RDFService rdfService = ModelAccess.on(ctx).getRDFService(CONTENT);
            List<String> models = new BlankNodeFilteringModelMaker(rdfService, ModelAccess.on(
                ctx).getModelMaker(CONTENT)).listModels().toList();

            HarvestContext.modules.forEach(module -> {
                module.getParameters().stream()
                    .filter(param -> "graph".equals(param.getType()))
                    .forEach(param -> param.setOptions(models));

                Map<String, ScheduledTaskMetadata> tasks =
                    InternalScheduleOperations
                        .getScheduledTasksForModule(module.getName())
                        .stream()
                        .collect(Collectors.toMap(
                            ScheduledTaskMetadata::getTaskName,
                            Function.identity(),
                            (a, b) -> b
                            // safety in case of duplicate task names, should never happen
                        ));

                module.getScheduledTasks().putAll(tasks);
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load export modules config", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        HarvestContext.modules.clear();
    }
}